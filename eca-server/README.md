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
* Openjdk 1.11
* maven => 3.3.9
* Rabbit MQ => 3
* eca-core 6.9.0
* Postgres Database для хранения информации => 9.6

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-server находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) cross-validation — настройки параметров для метода k * V блочной кросс - проверки
   на тестовой выборке:
   * cross-validation.numFolds - число блоков
   * cross-validation.numTests - число тестов
   * cross-validation.seed - начальное значение для генератора псевдослучайных чисел
   * cross-validation.timeout - таймаут в сек. для оценки точности классификатора
3) experiment - настройки параметров модуля Data Miner. Ниже приведено описание
   основных настроек:
   * experiment.resultSize - число наилучших конфигураций классификаторов
   * experiment.numIterations - число итераций эксперимента
   * experiment.individualClassifiersStoragePath - путь к папке в ресурсах для хранения json - конфигураций классификаторов,
   которые впоследствии будут использоваться при построении эксперимента
   * experiment.maximumFractionDigits - число десятичных знаков после запятой
   * experiment.numberOfDaysForStorage - кол-во дней для хранения результатов экспериментов
   * experiment.removeExperimentCron - крон выражение для удаления моделей с результатами экспериментов
   * experiment.timeout - время таймаута эксперимента в часах.
   * experiment.delaySeconds - интервал между запусками scheduler для обработки экспериметов
   * experiment.pageSize - размер страницы для постраничной обработки заявок
   * experiment.experimentDownloadUrlExpirationDays - время жизни ссылки на скачивание результатов эксперимента
   * experiment.shortLifeUrlExpirationMinutes - время короткоживущей ссылки на получение результатов эксперимента
   * experiment.ensemble.numIterations - число итераций для ансамблевых алгоритмов
   * experiment.ensemble.numBestClassifiers - число наилучших по точности базовых классификаторов, которые впоследствии
   будут использоваться при построении ансамбля
   * experiment.ensemble.multiThreadModeEnabled - многопоточный режим для ансамблевых алгоритмов (вкл./выкл.)
   * experiment.ensemble.numThreads - число используемых потоков
   * experiment.ensemble.numFoldsForStacking - число блоков V - блочной кросс - проверки для алгоритма Stacking
   * experiment.lock.registryKey - ключ реестра для блокировок
   * experiment.lock.expireAfter - время жизни блокировки
4) ers - настройки интеграции с сервисом eca-ers
   * ers.useClassifierOptionsCache - вкл./выкл. кеширование оптимальных настроек классификатора
   * ers.classifierOptionsCacheDurationInDays - период хранения оптимальных настроек классификатора, полученных от сервиса ERS
5) app - общие настройки приложения
   * app.threadPoolSize - число потоков для асинхронных задач
   * app.maxPageSize - максимальное число элементов на странице (используется для запросов с пагинацией)
   * app.notifications.webPushesEnabled - вкл./выкл. отправки web пушей 
6) cache.specs - настройки spring cache
7) open-api - настройки Swagger
   * open-api.tokenBaseUrl - базовый url - сервера авторизации
   * open-api.projectVersion - версия API
   * open-api.title - краткое название API
   * open-api.description - описание API
   * open-api.author - автор
   * open-api.email - email для связи
   * open-api.basePath - базовый префикс для API
   * open-api.apiAuth - настройки авторизации
   * open-api.apiAuth.scopes - список scopes
8) queue - настройки очередей
   * queue.evaluationRequestQueue - входящая очередь для запросов на построение модели классификатора
   * queue.evaluationOptimizerRequestQueue - входящая очередь для запросов на построение оптимального классификатора
   * queue.experimentRequestQueue - входящая очередь для запросов на построение эксперимента
9) auth-server - настройки интеграции с eca-oauth
   * baseUrl - базовый url eca-oauth
   * clientId - идентификатор клиента
   * clientSecret - пароль клиента
10) lock.enabled - вкл./выкл. использование блокировок
11) redis-lock - настройки блокировок с использованием redis
   * registryKey - ключ реестра
   * expireAfter - время жизни блокировки
12) audit.enabled - вкл./выкл. отправки событий аудита
13) rabbit.enabled - вкл./выкл. подключения к очередям rabbit MQ
14) mail.client - настройки библиотеки отправки email сообщений
15) redelivery.enabled - вкл./выкл. библиотеки redelivery

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-server.war

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
