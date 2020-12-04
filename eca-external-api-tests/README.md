Модуль автотестов для внешнего API
========================================

Описание
----------------------------------------
Данный модуль предоставляет следующий функционал:

1. Запуск пакета автотестов для внешнего API. 
2. Построение сводных отчетов по автотестам в формате csv.
3. Возможность добавления новых автотестов в директорию src/main/resources/requests

Необходимый софт
----------------------------------------
* jdk 1.8
* maven => 3.3.9
* Контейнер сервлетов Tomcat 8

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-external-api-tests находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) external-api-tests — настройки параметров для тестов:
   * external-api-tests.numThreads - число потоков для параллельного выполнения тестов
   * external-api-tests.workerThreadTimeOutInSeconds - таймаут для выполнения пакета тестов
   * external-api-tests.pageSize - размер страницы для постраничной обработки
   * external-api-tests.delaySeconds - интервал между запусками scheduler для обработки тестов
   * external-api-tests.url - endpoint для внешнего API
   * external-api-tests.downloadBaseUrl - базовый endpoint для скачивания модели
   * external-api-tests.testDataPath - относительный путь к директории (в resources) с автотестами
3) security.oauth2.client - настройки клиента для oauth2 авторизации
   * security.oauth2.client.clientId - идентификатор клиента
   * security.oauth2.client.clientSecret - секретный ключ или пароль клиента
   * security.oauth2.client.accessTokenUri - endpoint для получения access token
   * security.oauth2.client.grant-type - тип авторизации
4) swagger2 - настройки Swagger
   * swagger2.groups - Groups map с мета информацией для swagger

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install
    
2. Развернуть target/eca-external-api-tests.war на одном из контейнеров сервлетов (например, Tomcat 8) с контекстом /eca-external-api-tests.
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-external-api-tests/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.