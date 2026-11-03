# Prepara as variáveis que o Terraform precisa, neste terminal.
# Dentro da pasta terraform, usar este comando: source preparar-variaveis.sh
# Precisa ser "source", pois com "bash preparar-variaveis.sh" as variáveis somem quando o script termina.

printf "Senha do MySQL (Enter para gerar uma automaticamente): "
stty -echo
read senha
stty echo
echo

if [ -z "$senha" ]; then
  senha="Cv$(openssl rand -hex 12)#9"
  echo "Senha gerada: $senha"
  echo "ANOTE essa senha agora (ex.: gerenciador de senhas). Ela não será mostrada de novo."
fi

export TF_VAR_mysql_admin_password="$senha"
unset senha

export TF_VAR_meu_ip="$(curl -4 -s https://ifconfig.me)"

echo "Pronto: senha do MySQL definida e IP liberado no firewall = $TF_VAR_meu_ip"