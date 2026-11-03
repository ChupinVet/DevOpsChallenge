resource "azurerm_resource_group" "this" {
  name     = var.resource_group_name
  location = var.location

  tags = {
    projeto        = "chupinvet"
    sprint         = "4"
    gerenciado_por = "terraform"
  }
}

# ---------- Registro das imagens ----------
resource "azurerm_container_registry" "acr" {
  name                = "acrchupinvet${var.sufixo}"
  resource_group_name = azurerm_resource_group.this.name
  location            = var.location
  sku                 = "Basic"
  admin_enabled       = true
}

# ---------- Web App (container) ----------
resource "azurerm_service_plan" "plan" {
  name                = "asp-chupinvet-${var.sufixo}"
  resource_group_name = azurerm_resource_group.this.name
  location            = var.location
  os_type             = "Linux"
  sku_name            = var.app_service_sku
}

resource "azurerm_linux_web_app" "webapp" {
  name                = "app-chupinvet-${var.sufixo}"
  resource_group_name = azurerm_resource_group.this.name
  location            = var.location
  service_plan_id     = azurerm_service_plan.plan.id
  https_only          = true

  identity {
    type = "SystemAssigned"
  }

  site_config {
    always_on                               = var.app_service_sku != "F1"
    container_registry_use_managed_identity = true
  }

  # Só o que não é segredo. Banco e JWT são definidos pela pipeline (CD),
  # a partir de variáveis secretas do Azure DevOps.
  app_settings = {
    WEBSITES_PORT                       = "8080"
    WEBSITES_CONTAINER_START_TIME_LIMIT = "600"
  }

  lifecycle {
    # A imagem e os app settings passam a ser controlados pela pipeline.
    ignore_changes = [
      site_config[0].application_stack,
      app_settings,
    ]
  }
}

# O Web App puxa a imagem do ACR com a própria identidade, sem senha.
resource "azurerm_role_assignment" "acr_pull" {
  scope                = azurerm_container_registry.acr.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_linux_web_app.webapp.identity[0].principal_id
}

# ---------- Banco: Azure Database for MySQL Flexible Server (PaaS) ----------
resource "azurerm_mysql_flexible_server" "mysql" {
  name                   = "mysql-chupinvet-${var.sufixo}"
  resource_group_name    = azurerm_resource_group.this.name
  location               = var.location
  administrator_login    = var.mysql_admin_login
  administrator_password = var.mysql_admin_password
  sku_name               = "B_Standard_B1ms"
  version                = "8.4"
  backup_retention_days  = 7

  storage {
    size_gb = 20
  }

  lifecycle {
    ignore_changes = [zone]
  }
}

resource "azurerm_mysql_flexible_database" "chupinvet" {
  name                = "chupinvet"
  resource_group_name = azurerm_resource_group.this.name
  server_name         = azurerm_mysql_flexible_server.mysql.name
  charset             = "utf8mb4"
  collation           = "utf8mb4_unicode_ci"
}

# 0.0.0.0 = libera o acesso a partir de serviços do Azure (o Web App).
resource "azurerm_mysql_flexible_server_firewall_rule" "servicos_azure" {
  name                = "permitir-servicos-azure"
  resource_group_name = azurerm_resource_group.this.name
  server_name         = azurerm_mysql_flexible_server.mysql.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"
}

# Acesso de quem administra o banco (script inicial e SELECTs do vídeo).
resource "azurerm_mysql_flexible_server_firewall_rule" "meu_ip" {
  name                = "permitir-meu-ip"
  resource_group_name = azurerm_resource_group.this.name
  server_name         = azurerm_mysql_flexible_server.mysql.name
  start_ip_address    = var.meu_ip
  end_ip_address      = var.meu_ip
}