#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "${SCRIPT_DIR}/00-config.sh"

echo "==> Criando Storage Account ${STORAGE_ACCOUNT_NAME} <=="
az storage account create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$STORAGE_ACCOUNT_NAME" \
  --location "$LOCATION" \
  --sku Standard_LRS \
  --kind StorageV2 \
  --output none

STORAGE_KEY=$(az storage account keys list \
  --resource-group "$RESOURCE_GROUP" \
  --account-name "$STORAGE_ACCOUNT_NAME" \
  --query "[0].value" \
  --output tsv)

echo "==> Criando File Share ${FILE_SHARE_NAME} <=="
az storage share create \
  --name "$FILE_SHARE_NAME" \
  --account-name "$STORAGE_ACCOUNT_NAME" \
  --account-key "$STORAGE_KEY" \
  --quota 5 \
  --output none

echo ""
echo "==> Storage Account e File Share criados com sucesso <=="
echo "Storage Account: $STORAGE_ACCOUNT_NAME"
echo "File Share:      $FILE_SHARE_NAME"
echo "Storage Key:     $STORAGE_KEY"
echo ""

