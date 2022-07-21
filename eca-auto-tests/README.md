Модуль автотестов
========================================

Описание
----------------------------------------
Данный модуль предоставляет следующий функционал:

1. Запуск авто тестов на построение экспериментов с контролем основных точек процесса.
2. Запуск авто тестов на построение моделей классификаторов. 
3. Построение сводных отчетов по автоматическому тестированию в формате csv.

Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven => 3.3.9
* Rabbit MQ => 3
* eca-core 6.9.0
* Postgres Database для хранения информации => 9.6

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-auto-tests находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) auto-tests — настройки параметров для тестов:
   * auto-tests.requestTimeoutInSeconds - таймаут для обработки одного запроса сервером eca - server
   * auto-tests.classifiersDataPath - относительный путь к директории (в resources) с тестовыми данными для моделей классификаторов
   * auto-tests.experimentsDataPath - относительный путь к директории (в resources) с тестовыми данными для экспериментов
   * auto-tests.pageSize - размер страницы для постраничной обработки
   * auto-tests.delaySeconds - интервал между запусками scheduler для обработки тестов
   * auto-tests.schedulerPoolSize - размер пула потоков для шедулера
   * auto-tests.ecaErsBaseUrl - базовый url eca-ers
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
4) queue - настройки очередей rabbit mq
   * queue.experimentRequestQueue - очередь запросов на создание заявок на эксперимент
   * queue.experimentReplyToQueue - очередь ответов от eca - server для экспериментов
   * queue.evaluationRequestQueue - очередь запросов на построение моделей классификаторов
   * queue.evaluationReplyToQueue - очередь ответов от eca - server для моделей классификаторов
5) mail - настройки интеграции с почтой
  * mail.userName - логин пользователя
  * mail.password - пароль пользователя
  * mail.enabled - вкл./выкл. получение email сообщений

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install

2. Запустить проект с помощью команды:

    java -jar /target/eca-auto-tests.war
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-auto-tests/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
