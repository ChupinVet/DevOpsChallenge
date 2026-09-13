#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "${SCRIPT_DIR}/00-config.sh"

ACR_LOGIN_SERVER=$(az acr show --name "$ACR_NAME" --resource-group "$RESOURCE_GROUP" --query loginServer --output tsv)

ACR_USERNAME=$(az acr credential show --name "$ACR_NAME" --resource-group "$RESOURCE_GROUP" --query username --output tsv)
ACR_PASSWORD=$(az acr credential show --name "$ACR_NAME" --resource-group "$RESOURCE_GROUP" --query "passwords[0].value" --output tsv)

echo "==> Autenticando o Podman no ACR privado <=="
podman login "$ACR_LOGIN_SERVER" --username "$ACR_USERNAME" --password "$ACR_PASSWORD"

echo "==> Baixando as imagens dos repositórios do Docker Hub <=="
podman pull "$PUBLIC_API_IMAGE"
podman pull "$PUBLIC_MYSQL_IMAGE"

echo "==> Retag da API e Banco para o ACR <=="
podman tag "$PUBLIC_API_IMAGE" "${ACR_LOGIN_SERVER}/${API_REPOSITORY}:${API_TAG}"
podman tag "$PUBLIC_MYSQL_IMAGE" "${ACR_LOGIN_SERVER}/${MYSQL_REPOSITORY}:${MYSQL_TAG}"

echo "==> Publicando as duas imagens no ACR <=="
podman push "${ACR_LOGIN_SERVER}/${API_REPOSITORY}:${API_TAG}"
podman push "${ACR_LOGIN_SERVER}/${MYSQL_REPOSITORY}:${MYSQL_TAG}"

echo ""
echo "==> Publicado com sucesso <=="
echo "  ${ACR_LOGIN_SERVER}/${API_REPOSITORY}:${API_TAG}"
echo "  ${ACR_LOGIN_SERVER}/${MYSQL_REPOSITORY}:${MYSQL_TAG}"
