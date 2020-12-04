ECA external API
========================================

Описание
----------------------------------------
Сервис предоставляет внешний REST API для построения различных моделей классификаторов, а также
расчёта их основных показателей точности. Сервис поддерживает построение как
одиночных, так и ансамблевых моделей.

Необходимый софт
----------------------------------------
* jdk 1.8
* maven => 3.3.9
* Rabbit MQ => 3
* eca-core 6.8
* Postgres Database для хранения информации => 9.6
* Docker, Docker compose

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-external-api находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) spring.tokendatasource - настройки БД для хранения авторизационных токенов
3) external-api - основные настройки модуля. Ниже приведено описание основных настроек:
   * external-api.batchSize - размер страницы (используется для постраничной обработки)
   * external-api.requestTimeoutMinutes - таймаут запроса в мин.
   * external-api.requestCacheDurationMinutes - таймаут кэша запросов в мин.
   * external-api.classifiersPath - путь к папке с моделями классификаторов
   * external-api.trainDataPath - путь к папке с обучающими выборками
   * external-api.downloadBaseUrl - внешний url для скачивания моделей
   * external-api.numberOfDaysForStorage - число дней для хранения файлов с моделями и обучающими выборками.
   * external-api.removeClassifiersCron - крон выражение для удаления файлов с моделями
   * external-api.removeDataCron - крон выражение для удаления файлов с обучающими выборками
4) queue - настройки очередей rabbit mq
   * queue.evaluationRequestQueue - очередь запросов на построение классификаторов
   * queue.evaluationRequestReplyToQueue - очередь ответов от eca - server
5) swagger2 - настройки Swagger
   * swagger2.tokenBaseUrl - базовый url - сервера авторизации
   * swagger2.clientId - идентификатор клиента
   * swagger2.secret - пароль клиента
   * swagger2.groups - Groups map с мета информацией для swagger

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install
    
2. Развернуть target/eca-external-api.war на одном из контейнеров сервлетов (например, Tomcat 8) с контекстом /eca-external-api.
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-external-api/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.