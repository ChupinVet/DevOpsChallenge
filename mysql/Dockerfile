FROM mysql:8.4

COPY mysql/native-aio-off.cnf /etc/mysql/conf.d/native-aio-off.cnf

COPY script_bd.sql /docker-entrypoint-initdb.d/script_bd.sql
