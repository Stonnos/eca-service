Модуль eca-oauth
========================================

Описание
----------------------------------------
Данный модуль выполняет роль сервера авторизации c использованием протокола OAuth2.
Он отвечает за проверку подлинности пользовательской информации и создание авторизационных токенов
для web - приложения. С помощью авторизационных токенов web - приложение будет осуществлять доступ к ресурсам.

Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven => 3.3.9
* Postgres Database для хранения информации => 9.6

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-oauth находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) common - общие настройки
   * common.maxPageSize - максимальное число элементов на странице (используется для запросов с пагинацией)
3) open-api - настройки Swagger
   * open-api.tokenBaseUrl - базовый url - сервера авторизации
   * open-api.projectVersion - версия API
   * open-api.title - краткое название API
   * open-api.description - описание API
   * open-api.author - автор
   * open-api.email - email для связи
   * open-api.basePath - базовый префикс для API
   * open-api.apiAuth - настройки авторизации
   * open-api.apiAuth.scopes - список scopes
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
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-oauth.war

Примеры запросов для получения токенов
-------------------------------------------------------

1. Пример запроса на получение access token:

    curl eca_web:web_secret@localhost:8080/eca-oauth/oauth/token -d grant_type=password -d username=admin -d password=secret
    
    где eca_web:web_secret - пара clientId и clientSecret

    Запрос возвращает пару access_token/refresh_token:
    
    {"access_token":"cf4cd583-b9f1-4dce-8dcc-afefd1510974","token_type":"bearer","refresh_token":"7321ab3a-ee63-4e2e-bdfd-58aaca9fb263","expires_in":1799,"scope":"web"}
    
    В случае, если настроена двухфакторная аутентификация, то запрос возвращает 403 ошибку с телом ответа:
    
    {"error":"tfa_required","error_description":"Two-factor authentication required","expires_in":"120"}
    
    где expires_in - время действия одноразового пароля, который должен прийти по почте, привязанной к учетной записи пользователя

2. Пример запроса на обновление access token на основе refresh token:

    curl eca_web:web_secret@localhost:8080/eca-oauth/oauth/token -d grant_type=refresh_token -d refresh_token=7321ab3a-ee63-4e2e-bdfd-58aaca9fb263d

3. Пример запроса на получение токена с использованием двухфакторной аутентификации:

    curl eca_web:web_secret@localhost:8080/eca-oauth/oauth/token -d grant_type=tfa_code -d tfa_code=hFgU5G
    
    где tfa_code - одноразовый пароль для двухфакторной аутентификации
