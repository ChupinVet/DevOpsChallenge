#!/usr/bin/env bash


RM_1="rm566052"
RM_2="rm561507"
RM_3="rm561190"
RM_4="rm554794"

# Usando rm para garantir unicidade global dos recursos que exigem isso no Azure

LOCATION="mexicocentral"
SUFFIXO="rm566052"

RESOURCE_GROUP="rg-chupinvet-aci-${SUFFIXO}"

#ACR
ACR_NAME="chupinvetacr${SUFFIXO}"
PUBLIC_API_IMAGE="docker.io/vitordalmagro/chupinvet-api:v2"
API_REPOSITORY="chupinvet/api"
API_TAG="v1"
ORACLE_REPOSITORY="chupinvet/oracle-xe"
ORACLE_TAG="21-slim"
PUBLIC_ORACLE_IMAGE="gvenzl/oracle-xe:21-slim"


#Storage Account
STORAGE_ACCOUNT_NAME="stchupinvet${SUFFIXO}"
FILE_SHARE_NAME="oracle-data"
ORACLE_VOLUME_NAME="oracle-chupinvet-volume"

# Key Vault
KEY_VAULT_NAME="kv-chupinvet-${SUFFIXO}"

#Containers
ORACLE_CONTAINER_GROUP="aci-chupinvet-oracle-${SUFFIXO}"
API_CONTAINER_GROUP="aci-chupinvet-api-${SUFFIXO}"

ORACLE_PORT="1521"
API_PORT="8080"

#Banco (nomes e não segredos. A senha/usuário do app ficam no Key Vault)
ORACLE_APP_USER="chupinvet"
ORACLE_PDB_NAME="XEPDB1"
