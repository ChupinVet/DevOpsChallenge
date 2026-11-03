variable "location" {
  description = "Região da Azure"
  type        = string
  default     = "mexicocentral"
}

variable "resource_group_name" {
  description = "Nome do grupo de recursos"
  type        = string
  default     = "rg-chupinvet-sprint4"
}

variable "sufixo" {
  description = "Sufixo para nomes que precisam ser únicos no Azure (RM)"
  type        = string
  default     = "566052"
}

variable "app_service_sku" {
  description = "Plano do App Service. B1 evita o limite diário de CPU do F1 e permite always_on"
  type        = string
  default     = "B1"
}

variable "mysql_admin_login" {
  description = "Usuário administrador do MySQL (a API se conecta com ele)"
  type        = string
  default     = "chupinvet"
}

variable "mysql_admin_password" {
  description = "Senha do administrador do MySQL. Informada por TF_VAR_mysql_admin_password, nunca em arquivo"
  type        = string
  sensitive   = true

  validation {
    condition = length(var.mysql_admin_password) >= 8 && length(var.mysql_admin_password) <= 128 && (
    (can(regex("[A-Z]", var.mysql_admin_password)) ? 1 : 0) +
    (can(regex("[a-z]", var.mysql_admin_password)) ? 1 : 0) +
    (can(regex("[0-9]", var.mysql_admin_password)) ? 1 : 0) +
    (can(regex("[^A-Za-z0-9]", var.mysql_admin_password)) ? 1 : 0)
    ) >= 3
    error_message = "A senha precisa ter de 8 a 128 caracteres e pelo menos 3 destes tipos: maiúscula, minúscula, número e símbolo."
  }
}

variable "meu_ip" {
  description = "IP público de quem administra o banco (rodar o script e os SELECTs do vídeo). Informado por TF_VAR_meu_ip"
  type        = string

  validation {
    condition     = can(regex("^\\d{1,3}(\\.\\d{1,3}){3}$", var.meu_ip))
    error_message = "O firewall do MySQL só aceita IPv4 (ex.: 200.150.10.20). Use: curl -4 -s https://ifconfig.me"
  }
}

