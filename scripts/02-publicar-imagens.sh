#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "${SCRIPT_DIR}/00-config.sh"


if [[ -z "${ACR_LOGIN_SERVER:-}" ]]; then
  read -r -p "ACR_LOGIN_SERVER (ex.: ${ACR_NAME}.azurecr.io): " ACR_LOGIN_SERVER
fi

if [[ -z "${ACR_USERNAME:-}" ]]; then
  read -r -p "ACR_USERNAME: " ACR_USERNAME
fi

if [[ -z "${ACR_PASSWORD:-}" ]]; then
  read -r -s -p "ACR_PASSWORD: " ACR_PASSWORD
  echo ""
fi

echo "==> Autenticando o Podman no ACR privado <=="
podman login "$ACR_LOGIN_SERVER" \
  --username "$ACR_USERNAME" \
  --password "$ACR_PASSWORD"

echo "==> Baixando as imagens de origem <=="
podman pull "$PUBLIC_API_IMAGE"
podman pull "$PUBLIC_ORACLE_IMAGE"

echo "==> Retagueando para o ACR privado <=="
podman tag "$PUBLIC_API_IMAGE" "${ACR_LOGIN_SERVER}/${API_REPOSITORY}:${API_TAG}"
podman tag "$PUBLIC_ORACLE_IMAGE" "${ACR_LOGIN_SERVER}/${ORACLE_REPOSITORY}:${ORACLE_TAG}"

echo "==> Publicando no ACR privado <=="
podman push "${ACR_LOGIN_SERVER}/${API_REPOSITORY}:${API_TAG}"
podman push "${ACR_LOGIN_SERVER}/${ORACLE_REPOSITORY}:${ORACLE_TAG}"

echo ""
echo "==> Publicado com sucesso <=="
echo "  ${ACR_LOGIN_SERVER}/${API_REPOSITORY}:${API_TAG}"
echo "  ${ACR_LOGIN_SERVER}/${ORACLE_REPOSITORY}:${ORACLE_TAG}"
