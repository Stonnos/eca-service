Модуль eca-audit-log
========================================

Описание
----------------------------------------
   Данный модуль предоставляет следующий функционал:

1. Сбор и хранение событий аудита из других микросервисов.
2. REST API для просмотра журналов аудита с учетом параметров фильтрации и сортировки.

Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven >= 3.3.9
* База данных PostgreSQL для хранения информации.

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-audit-log находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) audit-log — основные настройки модуля:
   * audit-log.maxPageSize - максимальное число элементов на странице (используется для запросов с пагинацией)
3) swagger2 - настройки Swagger
   * swagger2.tokenBaseUrl - базовый url - сервера авторизации
   * swagger2.clientId - идентификатор клиента
   * swagger2.secret - пароль клиента
   * swagger2.groups - Groups map с мета информацией для swagger
4) auth-server - настройки интеграции с eca-oauth
   * baseUrl - базовый url eca-oauth
   * clientId - идентификатор клиента
   * clientSecret - пароль клиента
    
Инструкция по развертыванию
----------------------------------------

1. Для запуска модуля необходимо собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-audit-log.war

Страница с документацией swagger находится по адресу http://[host]:[port]/eca-audit-log/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
