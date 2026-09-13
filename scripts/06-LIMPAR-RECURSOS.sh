#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "${SCRIPT_DIR}/00-config.sh"

echo "Isto vai apagar PERMANENTEMENTE o Resource Group '${RESOURCE_GROUP}' e todos os recursos dentro dele."
echo ""
read -r -p "Digite o nome do Resource Group para confirmar: " CONFIRMACAO

if [[ "$CONFIRMACAO" != "$RESOURCE_GROUP" ]]; then
  echo "Confirmação incorreta. Nada foi apagado."
  exit 1
fi

echo "==> Apagando o Resource Group ${RESOURCE_GROUP} <=="
az group delete \
  --name "$RESOURCE_GROUP" \
  --yes \
  --no-wait

echo ""
echo "==> Exclusão solicitada <=="

while az group exists --name "$RESOURCE_GROUP" --output tsv | grep -q true; do
  sleep 10
  echo "  ainda apagando..."
done

echo "==> Resource Group apagado com sucesso <=="
echo ""
echo "==> Purgando o Key Vault ${KEY_VAULT_NAME} <=="
az keyvault purge \
  --name "$KEY_VAULT_NAME" \
  --location "$LOCATION" \
  --output none

echo ""
echo "==> Limpeza completa. Todos os recursos foram removidos <=="

