#!/usr/bin/env bash
# Cria as tabelas e os dados de exemplo (script_bd.sql) no MySQL da nuvem.
# Roda sozinho no "terraform apply" (recurso terraform_data.carga_banco), mas também pode ser rodado à mão, da raiz do repositório:
#   source terraform/preparar-variaveis.sh    # a MESMA senha usada no apply
#   ./scripts/carregar-banco.sh
# Se as tabelas já existirem, não faz nada (pode rodar mais de uma vez).
set -euo pipefail
raiz="$(cd "$(dirname "$0")/.." && pwd)"

if [ -z "${TF_VAR_mysql_admin_password:-}" ]; then
  echo "Senha não definida. Rode antes: source terraform/preparar-variaveis.sh"
  exit 1
fi

# Durante o apply, o Terraform informa o endereço em MYSQL_HOST.
# Rodando à mão, o endereço vem da saída do Terraform.
fqdn="${MYSQL_HOST:-$(terraform -chdir="$raiz/terraform" output -raw mysql_fqdn)}"
usuario="${MYSQL_ADMIN_LOGIN:-chupinvet}"

mysql_nuvem() {
  podman run --rm -i -e MYSQL_PWD="$TF_VAR_mysql_admin_password" docker.io/library/mysql:8.4 \
    mysql -h "$fqdn" -u "$usuario" --ssl-mode=REQUIRED "$@"
}

if [ -n "$(mysql_nuvem -N -s chupinvet -e "SHOW TABLES LIKE 'usuario';")" ]; then
  echo "Banco já carregado em $fqdn (tabela usuario existe). Nada a fazer."
  exit 0
fi

echo "Carregando script_bd.sql em $fqdn ..."
mysql_nuvem < "$raiz/script_bd.sql"

echo "Pronto. Tabelas criadas:"
mysql_nuvem chupinvet -e "SHOW TABLES;"