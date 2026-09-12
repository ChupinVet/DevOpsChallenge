#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "${SCRIPT_DIR}/00-config.sh"

echo "==> Criando Resource Group ${RESOURCE_GROUP} <=="
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --output none

echo "==> Registrando provider Microsoft.ContainerRegistry (idempotente) <=="
az provider register --namespace Microsoft.ContainerRegistry --wait

echo "==> Criando o Azure Container Registry ${ACR_NAME} <=="
az acr create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$ACR_NAME" \
  --sku Basic \
  --location "$LOCATION" \
  --public-network-enabled true \
  --admin-enabled true \
  --output none

LOGIN_SERVER=$(az acr show \
  --name "$ACR_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --query loginServer \
  --output tsv)

ADMIN_USERNAME=$(az acr credential show \
  --name "$ACR_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --query username \
  --output tsv)

ADMIN_PASSWORD=$(az acr credential show \
  --name "$ACR_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --query "passwords[0].value" \
  --output tsv)

echo ""
echo "==> ACR criado com sucesso <=="
echo "Login Server: $LOGIN_SERVER"
echo "Username:     $ADMIN_USERNAME"
echo "Password:     $ADMIN_PASSWORD"
echo ""
echo "Guarde esses três valores para autenticar o Docker localmente"
echo "(próximo passo: 02-publicar-imagens.sh, rodado na sua máquina)."
echo "Não cole a senha em nenhum arquivo do repositório."
