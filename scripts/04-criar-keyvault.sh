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

echo "==> Concedendo a role de Key Vault Administrator <=="
az role assignment create \
  --assignee "$(az account show --query user.name -o tsv)" \
  --role "Key Vault Administrator" \
  --scope "/subscriptions/$(az account show --query id -o tsv)/resourceGroups/${RESOURCE_GROUP}/providers/Microsoft.KeyVault/vaults/${KEY_VAULT_NAME}" \
  --output none

echo "==> Aguardando a propagação da permissão no Key Vault <=="
TENTATIVAS=10
for ((i = 1; i <= TENTATIVAS; i++)); do
  if az keyvault secret list --vault-name "$KEY_VAULT_NAME" --output none 2>/dev/null; then
    echo "==> Permissão propagada, seguindo em frente <=="
    break
  fi
  if [[ "$i" -eq "$TENTATIVAS" ]]; then
    echo "==> Ainda sem permissão, algo além da propagação normal pode estar errado."
    exit 1
  fi
  echo "  ainda propagando... (tentativa: ${i}/${TENTATIVAS})"
  sleep 10
done


gerar_segredo_se_nao_existir() {
  local nome_segredo="$1"
  local tamanho_bytes="$2"

  if ! az keyvault secret show --vault-name "$KEY_VAULT_NAME" --name "$nome_segredo" --output none 2>/dev/null; then
    echo "==> Gerando e armazenando '${nome_segredo}' no Key Vault <=="
    local valor_gerado
    valor_gerado=$(openssl rand -base64 "$tamanho_bytes")
    az keyvault secret set \
      --vault-name "$KEY_VAULT_NAME" \
      --name "$nome_segredo" \
      --value "$valor_gerado" \
      --output none
  else
    echo "==> Segredo '${nome_segredo}' já existe, não foi alterado <=="
  fi
}

gerar_segredo_se_nao_existir db-password 24
gerar_segredo_se_nao_existir jwt-secret 32

echo ""
echo "==> Key Vault pronto: ${KEY_VAULT_NAME} <=="
