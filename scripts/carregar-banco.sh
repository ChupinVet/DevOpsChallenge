#!/usr/bin/env bash
# Cria as tabelas e os dados de exemplo (script_bd.sql) no MySQL da nuvem.
# Rodar uma vez, logo depois do primeiro "terraform apply".
# Uso (da raiz do repositório):
#   source terraform/preparar-variaveis.sh    # a MESMA senha usada no apply
#   ./scripts/carregar-banco.sh
set -euo pipefail
raiz="$(cd "$(dirname "$0")/.." && pwd)"

if [ -z "${TF_VAR_mysql_admin_password:-}" ]; then
  echo "Senha não definida. Rode antes: source terraform/preparar-variaveis.sh"
  exit 1
fi

fqdn="$(terraform -chdir="$raiz/terraform" output -raw mysql_fqdn)"
usuario="${MYSQL_ADMIN_LOGIN:-chupinvet}"

echo "Carregando script_bd.sql em $fqdn ..."
podman run --rm -i -e MYSQL_PWD="$TF_VAR_mysql_admin_password" docker.io/library/mysql:8.4 \
  mysql -h "$fqdn" -u "$usuario" --ssl-mode=REQUIRED < "$raiz/script_bd.sql"

echo "Pronto. Tabelas criadas:"
podman run --rm -e MYSQL_PWD="$TF_VAR_mysql_admin_password" docker.io/library/mysql:8.4 \
  mysql -h "$fqdn" -u "$usuario" --ssl-mode=REQUIRED chupinvet -e "SHOW TABLES;"