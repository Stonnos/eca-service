Модуль eca-data-storage
========================================

Описание
----------------------------------------
   Данный модуль предоставляет следующий функционал:

1. API для сохранения файлов с обучающей выборкой в базу данных в виде таблиц.
При этом, система поддерживает файлы следующих форматов: arff, xls, xlsx, csv, json, txt, data, xml.
2. API для работы с таблицами данных через веб приложение: выбор атрибутов для классификации, выбор класса и т.д.

Необходимый софт
----------------------------------------
* Openjdk 21
* maven => 3.9.2
* eca-core 7.0
* База данных PostgreSQL 13.9 для хранения информации.

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-data-storage находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) eca-ds — основные настройки модуля:
   * eca-ds.dateFormat - формат даты для атрибутов типа "Дата"
   * eca-ds.batchSize - размер блока для транзакционного сохранения данных в базу данных
   * eca-ds.reportsPath - пусть к файлу в resources с конфигурациями для отчетов
   * eca-ds.supportedDataFileExtensions - поддерживаемы форматы обучающих выборок
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
5) audit.enabled - вкл./выкл. отправки событий аудита
6) redelivery.enabled - вкл./выкл. библиотеки redelivery
7) logging.mode - режим логирования
   * text - текстовый формат
   * json - логи в формате json
    
Инструкция по развертыванию
----------------------------------------

1. Для запуска модуля необходимо собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-data-storage.jar

Страница с документацией swagger находится по адресу http://[host]:[port]/eca-data-storage/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
