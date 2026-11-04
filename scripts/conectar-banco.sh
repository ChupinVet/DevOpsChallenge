#!/usr/bin/env bash
# Abre o cliente do MySQL conectado ao banco da nuvem (para os SELECTs).
# Uso (da raiz do repositório):
#   source terraform/preparar-variaveis.sh    # a MESMA senha usada no apply
#   ./scripts/conectar-banco.sh                          # abre o prompt mysql>
#   ./scripts/conectar-banco.sh "SELECT * FROM pet;"     # roda só esse comando
set -euo pipefail
raiz="$(cd "$(dirname "$0")/.." && pwd)"

if [ -z "${TF_VAR_mysql_admin_password:-}" ]; then
  echo "Senha não definida. Rode antes: source terraform/preparar-variaveis.sh"
  exit 1
fi

fqdn="$(terraform -chdir="$raiz/terraform" output -raw mysql_fqdn)"
usuario="${MYSQL_ADMIN_LOGIN:-chupinvet}"

if [ $# -gt 0 ]; then
  podman run --rm -e MYSQL_PWD="$TF_VAR_mysql_admin_password" docker.io/library/mysql:8.4 \
    mysql -h "$fqdn" -u "$usuario" --ssl-mode=REQUIRED -t chupinvet -e "$1"
else
  podman run --rm -it -e MYSQL_PWD="$TF_VAR_mysql_admin_password" docker.io/library/mysql:8.4 \
    mysql -h "$fqdn" -u "$usuario" --ssl-mode=REQUIRED chupinvet
fi