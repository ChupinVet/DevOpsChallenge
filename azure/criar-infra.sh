#!/bin/bash

set -euo pipefail

trap 'echo "Erro na linha $LINENO ao executar: $BASH_COMMAND" >&2' ERR

echo "Iniciando provisionamento ChupinVet..."

RG="rg-chupinvet-devops"
LOCATION="canadacentral"
VM_NAME="vm-chupinvet-app"
ADMIN_USER="azureuser"
IMAGE="Ubuntu2204"
SIZE="Standard_B2s_v2"
APP_PORT=8080
DB_PORT=1521
REPO_URL="https://github.com/ChupinVet/DevOpsChallenge.git"
REPO_DIR="DevOpsChallenge"

echo "Criando Resource Group..."
az group create \
  --name "$RG" \
  --location "$LOCATION" \
  >/dev/null

echo "Criando VM..."
az vm create \
  --resource-group "$RG" \
  --name "$VM_NAME" \
  --image "$IMAGE" \
  --size "$SIZE" \
  --admin-username "$ADMIN_USER" \
  --generate-ssh-keys \
  >/dev/null

echo "Abrindo porta da API $APP_PORT..."
az vm open-port \
  --resource-group "$RG" \
  --name "$VM_NAME" \
  --port "$APP_PORT" \
  >/dev/null

echo "Obtendo IP público..."
PUBLIC_IP=$(az vm show \
  --resource-group "$RG" \
  --name "$VM_NAME" \
  -d \
  --query publicIps \
  -o tsv)

if [ -z "$PUBLIC_IP" ]; then
  echo "Não foi possível obter o IP público da VM."
  exit 1
fi

echo "Aguardando SSH ficar disponível..."
for i in {1..30}; do
  if ssh -o StrictHostKeyChecking=no -o ConnectTimeout=5 "$ADMIN_USER@$PUBLIC_IP" "echo ok" >/dev/null 2>&1; then
    echo "SSH disponível."
    break
  fi
  echo "Tentativa $i/30: aguardando SSH..."
  sleep 5
done

echo "Instalando Docker, Git e ferramentas..."
az vm run-command invoke \
  --resource-group "$RG" \
  --name "$VM_NAME" \
  --command-id RunShellScript \
  --scripts "
    curl -fsSL https://get.docker.com | sh &&
    sudo apt-get install -y git nano &&
    sudo systemctl enable --now docker &&
    sudo usermod -aG docker $ADMIN_USER
  " \
  >/dev/null

echo "Clonando repositório e subindo containers..."
ssh -o StrictHostKeyChecking=no "$ADMIN_USER@$PUBLIC_IP" "
  if [ ! -d \"$REPO_DIR\" ]; then
    git clone \"$REPO_URL\"
  fi

  cd \"$REPO_DIR\"

  sudo docker compose down || true
  sudo docker compose up -d --quiet-pull > /dev/null 2>&1
"

echo "Aguardando aplicação responder..."
for i in {1..60}; do
  if curl -fsS "http://$PUBLIC_IP:$APP_PORT/responsaveis" >/dev/null 2>&1; then
    echo "Aplicação respondeu com sucesso."
    break
  fi
  echo "Tentativa $i/60: aguardando aplicação..."
  sleep 10
done

if ! curl -fsS "http://$PUBLIC_IP:$APP_PORT/responsaveis" >/dev/null 2>&1; then
  echo "A aplicação não respondeu em http://$PUBLIC_IP:$APP_PORT/responsaveis"
  echo "Acesse a VM e verifique: sudo docker ps -a && sudo docker logs chupinvet-api"
  exit 1
fi


echo "Finalizado com sucesso!"
echo "API: http://$PUBLIC_IP:$APP_PORT"
echo "Swagger: http://$PUBLIC_IP:$APP_PORT/swagger-ui.html"
