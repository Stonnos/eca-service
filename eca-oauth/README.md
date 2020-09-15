Модуль eca-oauth
========================================

Описание
----------------------------------------
Данный модуль выполняет роль сервера авторизации c использованием протокола OAuth2.
Он отвечает за проверку подлинности пользовательской информации и создание авторизационных токенов
для web - приложения. С помощью авторизационных токенов web - приложение будет осуществлять доступ к ресурсам.

Необходимый софт
----------------------------------------
* jdk 1.8
* maven => 3.3.9
* Postgres Database для хранения информации => 9.6
* Контейнер сервлетов Tomcat 8

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-oauth находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) common - общие настройки
   * common.maxPageSize - максимальное число элементов на странице (используется для запросов с пагинацией)
3) swagger2 - настройки Swagger
   * swagger2.tokenBaseUrl - базовый url - сервера авторизации
   * swagger2.clientId - идентификатор клиента
   * swagger2.secret - пароль клиента
   * swagger2.groups - Groups map с мета информацией для swagger
4) password - настройки для генератора паролей
   * password.length - длина пароля
   * password.useDigits - использовать цифры?
   * password.useLowerCaseSymbols - использовать латинские буквы нижнего регистра?
   * password.useUpperCaseSymbols - использовать латинские буквы верхнего регистра?
5) reset-password - настройки для восстановления пароля
   * reset-password.validityMinutes - время действия токена в мин.
   * reset-password.baseUrl - базовый url для формирования ссылки для восстановления пароля


Инструкция по развертыванию
----------------------------------------

1. Для запуска модуля необходимо собрать проект с помощью команды:
    
   mvn clean install
    
2. Развернуть target/eca-oauth.war на одном из контейнеров сервлетов (например, Tomcat 8) с контекстом /eca-oauth.

Инструкция по развертыванию в Docker
-------------------------------------------------------

1. Для Windows достаточно скачать и установить дистрибутив Docker Desktop (https://www.docker.com/products/docker-desktop).

2. Далее для сборки проекта и создания образа проекта нужно выполнить команду:

    mvn clean install dockerfile:build

3. Используя пакетный менеджер docker-compose, создать docker контейнеры с помощью команды:

    docker-compose up -d

ВАЖНО! Данную команду необходимо выполнять из корневой папки проекта.

Примеры запросов для получения токенов
-------------------------------------------------------

1. Пример запроса на получение access token:

    curl eca_web:web_secret@localhost:8080/eca-oauth/oauth/token -d grant_type=password -d username=admin -d password=secret
    
    где eca_web:web_secret - пара clientId и clientSecret

2. Пример запроса на обновление access token на основе refresh token:

    curl eca_web:web_secret@localhost:8080/eca-oauth/oauth/token -d grant_type=refresh_token -d refresh_token=7321ab3a-ee63-4e2e-bdfd-58aaca9fb263d

3. Пример запроса на получение токена с использованием двухфакторной аутентификации:

    curl eca_web:web_secret@localhost:8080/eca-oauth/oauth/token -d grant_type=tfa_code -d tfa_code=AJTUa2
    
   Важно! Сначала необходимо вызвать метод из п.1