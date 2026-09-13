#!/usr/bin/env bash

LOCATION="mexicocentral"
SUFFIXO="566052"

RESOURCE_GROUP="rg-chupinvet-aci-${SUFFIXO}"

# ACR
ACR_NAME="chupinvetacr${SUFFIXO}"
PUBLIC_API_IMAGE="docker.io/vitordalmagro/chupinvet-api:mysql-v1"
API_REPOSITORY="chupinvet/api"
API_TAG="v1"

# Trocado de Oracle para MySQL, Oracle não funcionou com o volume do Azure Files (erro ORA-03113 no teste isolado).
MYSQL_REPOSITORY="chupinvet/mysql"
MYSQL_TAG="8.4"
PUBLIC_MYSQL_IMAGE="docker.io/vitordalmagro/chupinvet-mysql:v1"

# Storage Account (persistência do banco)
STORAGE_ACCOUNT_NAME="stchupinvet${SUFFIXO}"
FILE_SHARE_NAME="mysql-data"
MYSQL_VOLUME_NAME="mysql-chupinvet-volume"

# Key Vault
KEY_VAULT_NAME="kv-chupinvet-${SUFFIXO}"

CONTAINER_GROUP_NAME="aci-chupinvet-${SUFFIXO}"

MYSQL_PORT="3306"
API_PORT="8080"

# Banco (nomes e não segredos, a senha de app fica no Key Vault)
MYSQL_DATABASE="chupinvet"
MYSQL_APP_USER="chupinvet"