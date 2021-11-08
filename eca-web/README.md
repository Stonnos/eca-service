Модуль eca-web
========================================

Описание
----------------------------------------
Модуль реализует web - клиент для работы с заявками на построение моделей и состоит из следующих частей:

* Angular приложение, обернутое в spring boot
* REST API для получения конфигурации web приложения

Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven => 3.3.9
* nodejs => 12.14.x

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для angular приложения находятся в файле environment.ts или environment.prod.ts. Ниже приведены основные параметры:

* serverUrl - url REST API сервера eca-server
* oauthUrl - url сервера авторизации
* dsUrl - url модуля eca-data-storage
* mailUrl - url модуля eca-mail
* auditLogUrl - url модуля eca-audit-log
* webAppUrl - url модуля для получения конфигурации web - приложения
* wsUrl - url сервера веб - сокетов (модуль eca-web-push)
* clientId - идентификатор клиента
* secret - секретный ключ или пароль клиента

Настройки для серверной части находятся в application.yml. Основные параметры:
1) open-api - настройки Swagger
   * open-api.tokenBaseUrl - базовый url - сервера авторизации
   * open-api.projectVersion - версия API
   * open-api.title - краткое название API
   * open-api.description - описание API
   * open-api.author - автор
   * open-api.email - email для связи
   * open-api.basePath - базовый префикс для API
   * open-api.apiAuth - настройки авторизации
   * open-api.apiAuth.scopes - список scopes
2) auth-server - настройки интеграции с eca-oauth
   * baseUrl - базовый url eca-oauth
   * clientId - идентификатор клиента
   * clientSecret - пароль клиента

Инструкция по развертыванию
----------------------------------------

1. Сначала необходимо установить node.js (https://nodejs.org/en/download/)

2. Для запуска модуля необходимо собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-web.war
