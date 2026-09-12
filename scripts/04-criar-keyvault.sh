#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/00-config.sh"

echo "==> Registrando provider Microsoft.KeyVault <=="
az provider register --namespace Microsoft.KeyVault --wait

echo "==> Criando Key Vault ${KEY_VAULT_NAME} <=="
az keyvault create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$KEY_VAULT_NAME" \
  --location "$LOCATION" \
  --sku standard \
  --enable-rbac-authorization true \
  --output none

echo "==> Concedendo a role de Key Vault ADM"
az role assignment create \
  --assignee "$(az account show --query user.name -o tsv)" \
  --role "Key Vault Administrator" \
  --scope "/subscriptions/$(az account show --query id -o tsv)/resourceGroups/${RESOURCE_GROUP}/providers/Microsoft.KeyVault/vaults/${KEY_VAULT_NAME}" \
  --output none

# Só gera a senha se o segredo ainda não existir.
if ! az keyvault secret show --vault-name "$KEY_VAULT_NAME" --name oracle-password --output none 2>/dev/null; then
  echo "==> Gerando e armazenando a senha do Oracle no Key Vault <=="
  ORACLE_PASSWORD_GERADA=$(openssl rand -base64 24)
  az keyvault secret set \
    --vault-name "$KEY_VAULT_NAME" \
    --name oracle-password \
    --value "$ORACLE_PASSWORD_GERADA" \
    --output none
else
  echo "==> Segredo oracle-password já existe, não foi alterado <=="
fi

echo ""
echo "==> Key Vault pronto: ${KEY_VAULT_NAME} <=="
echo "Segredo 'oracle-password' disponível para o script de deploy."
