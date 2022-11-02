ECA external API
========================================

Описание
----------------------------------------
Сервис предоставляет внешний REST API для построения различных моделей классификаторов, а также
расчёта их основных показателей точности. Сервис поддерживает построение как
одиночных, так и ансамблевых моделей.

Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven => 3.3.9
* Rabbit MQ => 3
* eca-core 6.9.2
* Postgres Database для хранения информации => 9.6
* Docker, Docker compose

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-external-api находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) external-api - основные настройки модуля. Ниже приведено описание основных настроек:
   * external-api.batchSize - размер страницы (используется для постраничной обработки)
   * external-api.requestTimeoutSeconds - таймаут запроса в сек.
   * external-api.evaluationRequestTimeoutMinutes - таймаут в мин. для построения моделей
   * external-api.experimentRequestTimeoutMinutes - таймаут в мин. для построения экспериментов
   * external-api.classifierDownloadUrlExpirationDays - время жизни ссылки на скачивание моделей
   * external-api.numberOfDaysForStorage - число дней для хранения файлов с моделями и обучающими выборками.
   * external-api.removeClassifiersCron - крон выражение для удаления файлов с моделями
   * external-api.removeDataCron - крон выражение для удаления файлов с обучающими выборками
3) queue - настройки очередей rabbit mq
   * queue.evaluationRequestQueue - очередь для запросов на построение классификаторов
   * queue.experimentRequestQueue - очедерь для запросов на построение экспериментов
   * queue.optimalEvaluationRequestQueue - очередь для запросов на построение классификаторов с использованием оптимальных параметров
   * queue.evaluationResponseQueue - очередь ответов от eca - server на построение классификаторов
   * queue.experimentResponseQueue - очередь ответов от eca - server на построение экспериментов
4) open-api - настройки Swagger
   * open-api.tokenBaseUrl - базовый url - сервера авторизации
   * open-api.projectVersion - версия API
   * open-api.title - краткое название API
   * open-api.description - описание API
   * open-api.author - автор
   * open-api.email - email для связи
   * open-api.basePath - базовый префикс для API
   * open-api.apiAuth - настройки авторизации
   * open-api.apiAuth.scopes - список scopes
5) auth-server - настройки интеграции с eca-oauth
   * baseUrl - базовый url eca-oauth
   * clientId - идентификатор клиента
   * clientSecret - пароль клиента
6) logging.mode - режим логирования
   * text - текстовый формат
   * json - логи в формате json

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-external-api.war
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-external-api/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
