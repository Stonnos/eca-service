Модуль нагрузочного тестирования
========================================

Описание
----------------------------------------
Данный модуль предоставляет следующий функционал:

1. Запуск нагрузочных тестов (с заданными параметрами) на построение моделей классификаторов с помощью сервера eca - server. 
2. Построение сводных отчетов по нагрузочному тестированию в формате xlsx.

Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven => 3.3.9
* Rabbit MQ => 3
* eca-core 6.9.3
* Postgres Database для хранения информации => 13.9

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-load-tests находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) eca-load-tests — настройки параметров для тестов:
   * eca-load-tests.numRequests - число запросов в eca-server
   * eca-load-tests.numThreads - число потоков для параллельной отправки запросов
   * eca-load-tests.workerThreadTimeOutInSeconds - таймаут для отправки всех запросов
   * eca-load-tests.requestTimeoutInSeconds - таймаут для обработки одного запроса сервером eca - server
   * eca-load-tests.numFolds -  число блоков для метода k * V блочной кросс - проверки
   * eca-load-tests.numTests - число тестов для метода k * V блочной кросс - проверки
   * eca-load-tests.seed - начальное значение для генератора псевдослучайных чисел
   * eca-load-tests.trainingDataStoragePath - относительный путь к директории (в resources) с датасетами
   * eca-load-tests.classifiersStoragePath - относительный путь к директории (в resources) с настройками классификаторов
   * eca-load-tests.pageSize - размер страницы для постраничной обработки
   * eca-load-tests.delaySeconds - интервал между запусками scheduler для обработки тестов
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
   * queue.evaluationRequestQueue - очередь запросов на построение классификаторов
   * queue.replyToQueue - очередь ответов от eca - server

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install

2. Запустить проект с помощью команды:

    java -jar /target/eca-load-tests.war
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/ eca-load-tests/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
