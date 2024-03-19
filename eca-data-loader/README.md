Модуль eca-data-loader
========================================

Описание
----------------------------------------
   Модуль предоставляет единое API для сохранения обучающих выборок во внутреннее хранилище S3.
   Сохраненные данные используются для последующей классификации. 

Необходимый софт
----------------------------------------
* Openjdk 17
* maven => 3.9.2
* eca-core 6.9.7
* База данных PostgreSQL 13.9 для хранения информации.
* Minio

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-data-loader находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) minio — настройки minio:
   * minio.url - URL для подключения
   * minio.accessKey - логин для подключения
   * minio.secretKey - пароль для подключения
   * minio.bucketName - имя бакета
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
4) auth-server - настройки интеграции с eca-oauth
   * baseUrl - базовый url eca-oauth
   * clientId - идентификатор клиента
   * clientSecret - пароль клиента
5) logging.mode - режим логирования
   * text - текстовый формат
   * json - логи в формате json
6) app - настройки приложения
   * app.instancesExpireDays - время жизни объекта обучающей выборки
   * app.dateFormat - формат даты для атрибутов типа "Дата"
   * app.batchSize - размер страницы для постраничной обработки данных
   * app.removeDataCron - крон выражения для очистки данных
    
Инструкция по развертыванию
----------------------------------------

1. Для запуска модуля необходимо собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-data-loader.jar

Страница с документацией swagger находится по адресу http://[host]:[port]/eca-data-loader/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
