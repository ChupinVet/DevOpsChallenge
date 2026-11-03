output "acr_login_server" {
  value = azurerm_container_registry.acr.login_server
}

output "webapp_name" {
  value = azurerm_linux_web_app.webapp.name
}

output "webapp_url" {
  value = "https://${azurerm_linux_web_app.webapp.default_hostname}"
}

output "mysql_fqdn" {
  value = azurerm_mysql_flexible_server.mysql.fqdn
}

output "spring_datasource_url" {
  value = "jdbc:mysql://${azurerm_mysql_flexible_server.mysql.fqdn}:3306/chupinvet?sslMode=REQUIRED"
}