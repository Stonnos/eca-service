ECA server
========================================

Описание
----------------------------------------
1. Данный сервис предназначен для построения различных моделей классификаторов, а также
расчёта их основных показателей точности. Сервис поддерживает построение как
одиночных, так и ансамблевых моделей.
2. Сервис также предоставляет API Data Miner, основным предназначением которого
является автоматический подбор оптимальных параметров для одиночных и ансамблевых алгоритмов
на основе серии экспериментов.
3. Интеграция с сервисом ERS (Evaluation results service). API предоставляет возможность
отправки результатов классификации в сервис для последующего анализа, а также нахождение
оптимальных конфигураций классификаторов для конкретной обучающей выборки на
основе истории результатов классификации.

Необходимый софт
----------------------------------------
* Openjdk 11
* maven => 3.9.2
* Rabbit MQ => 3.8.14
* eca-core 6.9.5
* Postgres Database для хранения информации => 13.9

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-server находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) cross-validation — настройки параметров для метода k * V блочной кросс - проверки
   на тестовой выборке:
   * cross-validation.numFolds - число блоков
   * cross-validation.numTests - число тестов
   * cross-validation.seed - начальное значение для генератора псевдослучайных чисел
3) classifiers - настройка параметров для постороения моделей классификаторов
   * classifiers.timeout - таймаут в сек. для оценки точности классификатора
   * classifiers.delaySeconds - интервал между запусками scheduler для построения моделей
   * classifiers.maxRequestsPerJob - максимальное число моделей обрабатываемое в рамках одного вызова scheduler
4) experiment - настройки параметров модуля Data Miner. Ниже приведено описание
   основных настроек:
   * experiment.resultSize - число наилучших конфигураций классификаторов
   * experiment.numIterations - число итераций эксперимента
   * experiment.individualClassifiersStoragePath - путь к папке в ресурсах для хранения json - конфигураций классификаторов,
   которые впоследствии будут использоваться при построении эксперимента
   * experiment.maximumFractionDigits - число десятичных знаков после запятой
   * experiment.timeout - время таймаута эксперимента в часах.
   * experiment.delaySeconds - интервал между запусками scheduler для обработки экспериметов
   * experiment.ensemble.numIterations - число итераций для ансамблевых алгоритмов
   * experiment.ensemble.numBestClassifiers - число наилучших по точности базовых классификаторов, которые впоследствии
   будут использоваться при построении ансамбля
   * experiment.ensemble.multiThreadModeEnabled - многопоточный режим для ансамблевых алгоритмов (вкл./выкл.)
   * experiment.ensemble.numThreads - число используемых потоков
   * experiment.ensemble.numFoldsForStacking - число блоков V - блочной кросс - проверки для алгоритма Stacking
5) ers - настройки интеграции с сервисом eca-ers
   * ers.useClassifierOptionsCache - вкл./выкл. кеширование оптимальных настроек классификатора
   * ers.classifierOptionsCacheDurationInDays - период хранения оптимальных настроек классификатора, полученных от сервиса ERS
6) app - общие настройки приложения
   * app.threadPoolSize - число потоков для асинхронных задач
   * app.push.enabled - вкл./выкл. отправки web пушей
   * app.push.async - вкл./выкл. асинхронной отправки web пушей
   * app.removeModelCron - крон выражение для удаления моделей экспериментов/классификаторов
   * app.numberOfDaysForStorage - кол-во дней для хранения моделей экспериментов/классификаторов
   * app.pageSize - размер страницы для постраничной обработки заявок
   * app.modelDownloadUrlExpirationDays - время жизни ссылки на скачивание модели эксперимента/классификатора
   * app.shortLifeUrlExpirationMinutes - время короткоживущей ссылки на получение модели эксперимента/классификатора
7) cache.specs - настройки spring cache
8) open-api - настройки Swagger
   * open-api.tokenBaseUrl - базовый url - сервера авторизации
   * open-api.projectVersion - версия API
   * open-api.title - краткое название API
   * open-api.description - описание API
   * open-api.author - автор
   * open-api.email - email для связи
   * open-api.basePath - базовый префикс для API
   * open-api.apiAuth - настройки авторизации
   * open-api.apiAuth.scopes - список scopes
9) queue - настройки очередей
   * queue.evaluationRequestQueue - входящая очередь для запросов на построение модели классификатора
   * queue.evaluationOptimizerRequestQueue - входящая очередь для запросов на построение оптимального классификатора
   * queue.experimentRequestQueue - входящая очередь для запросов на построение эксперимента
10) auth-server - настройки интеграции с eca-oauth
   * baseUrl - базовый url eca-oauth
   * clientId - идентификатор клиента
   * clientSecret - пароль клиента
11) lock.enabled - настройки блокировок
   * lock.enabled - вкл./выкл. использование блокировок
   * lock.registry-type - тип блокировок REDIS, IN_MEMORY
   * lock.redis.registryKey - ключ реестра для Redis
   * lock.redis.expireAfter - время жизни блокировки для Redis
12) audit.enabled - вкл./выкл. отправки событий аудита
13) rabbit.enabled - вкл./выкл. подключения к очередям rabbit MQ
14) mail.client - настройки библиотеки отправки email сообщений
15) redelivery.enabled - вкл./выкл. библиотеки redelivery 
16) logging.mode - режим логирования
   * text - текстовый формат
   * json - логи в формате json

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-server.jar

    С помощью следующих настроек можно отключить все внешние интеграции:
    
    * lock.enabled=false
      (Отключение распределенных блокировок)
    * audit.enabled=false
      (Отключение отправки событий аудита)
    * rabbit.enabled=false
      (Отключение rabbit MQ)
    * mail.client.enabled=false
      (Отключение отправки email сообщений)
     * app.notifications.webPushesEnabled=false
      (Отключение отправки web пушей )
    * spring.cache.type=caffeine
      (Использование in-memory кеша caffeine)
    * eureka.client.registerWithEureka=false
      (Отключение регистрации сервиса в discovery server)
    * eureka.client.fetchRegistry=false
      (Отключение получения данных от discovery server)
    * management.health.rabbit.enabled=false
      (Отключение spring actuator health check для rabbit mq)
    * management.health.redis.enabled=false
      (Отключение spring actuator health check для redis)
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-server/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
