#!/usr/bin/env bash


set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "${SCRIPT_DIR}/00-config.sh"

LOGIN_SERVER=$(az acr show --name "$ACR_NAME" --resource-group "$RESOURCE_GROUP" --query loginServer --output tsv)
ACR_USERNAME=$(az acr credential show --name "$ACR_NAME" --resource-group "$RESOURCE_GROUP" --query username --output tsv)
ACR_PASSWORD=$(az acr credential show --name "$ACR_NAME" --resource-group "$RESOURCE_GROUP" --query "passwords[0].value" --output tsv)

DB_PASSWORD=$(az keyvault secret show --vault-name "$KEY_VAULT_NAME" --name db-password --query value --output tsv)
JWT_SECRET=$(az keyvault secret show --vault-name "$KEY_VAULT_NAME" --name jwt-secret --query value --output tsv)

STORAGE_KEY=$(az storage account keys list \
  --resource-group "$RESOURCE_GROUP" \
  --account-name "$STORAGE_ACCOUNT_NAME" \
  --query "[0].value" \
  --output tsv)

YAML_FILE="$(mktemp --suffix=.yaml)"
trap 'rm -f "$YAML_FILE"' EXIT

cat > "$YAML_FILE" << YAMLEOF
apiVersion: "2021-10-01"
location: ${LOCATION}
name: ${CONTAINER_GROUP_NAME}
properties:
  containers:
    - name: mysql
      properties:
        image: ${LOGIN_SERVER}/${MYSQL_REPOSITORY}:${MYSQL_TAG}
        ports:
          - port: ${MYSQL_PORT}
        environmentVariables:
          - name: MYSQL_DATABASE
            value: "${MYSQL_DATABASE}"
          - name: MYSQL_USER
            value: "${MYSQL_APP_USER}"
          - name: MYSQL_ROOT_PASSWORD
            secureValue: "${DB_PASSWORD}"
          - name: MYSQL_PASSWORD
            secureValue: "${DB_PASSWORD}"
        resources:
          requests:
            cpu: 1
            memoryInGB: 1.5
        volumeMounts:
          - name: mysql-data
            mountPath: /var/lib/mysql
    - name: api
      properties:
        image: ${LOGIN_SERVER}/${API_REPOSITORY}:${API_TAG}
        ports:
          - port: ${API_PORT}
        environmentVariables:
          - name: SPRING_DATASOURCE_URL
            value: "jdbc:mysql://localhost:${MYSQL_PORT}/${MYSQL_DATABASE}"
          - name: SPRING_DATASOURCE_USERNAME
            value: "${MYSQL_APP_USER}"
          - name: SPRING_DATASOURCE_PASSWORD
            secureValue: "${DB_PASSWORD}"
          - name: JWT_SECRET
            secureValue: "${JWT_SECRET}"
        resources:
          requests:
            cpu: 1
            memoryInGB: 1
  imageRegistryCredentials:
    - server: ${LOGIN_SERVER}
      username: ${ACR_USERNAME}
      password: ${ACR_PASSWORD}
  osType: Linux
  ipAddress:
    type: Public
    ports:
      - port: ${API_PORT}
        protocol: tcp
  restartPolicy: Always
  volumes:
    - name: mysql-data
      azureFile:
        shareName: ${FILE_SHARE_NAME}
        storageAccountName: ${STORAGE_ACCOUNT_NAME}
        storageAccountKey: ${STORAGE_KEY}
tags: {}
type: Microsoft.ContainerInstance/containerGroups
YAMLEOF

echo "==> Implantando o container group ${CONTAINER_GROUP_NAME} <=="
az container create \
  --resource-group "$RESOURCE_GROUP" \
  --file "$YAML_FILE" \
  --output none

API_IP=$(az container show \
  --resource-group "$RESOURCE_GROUP" \
  --name "$CONTAINER_GROUP_NAME" \
  --query ipAddress.ip \
  --output tsv)

echo ""
echo "==> Deploy solicitado com sucesso <=="
echo "API:     http://${API_IP}:${API_PORT}"
echo "Swagger: http://${API_IP}:${API_PORT}/swagger-ui.html"

