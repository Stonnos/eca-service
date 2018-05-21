ECA service v2.0
========================================

Описание
----------------------------------------
1. Данный сервис предназначен для построения различных моделей классификаторов, а также
расчёта их основных показателей точности. Сервис поддерживает построение как
одиночных, так и ансамблевых моделей.
2. Сервис также предоставляет REST - API Data Miner, основным предназначением которого
является автоматический подбор оптимальных параметров для одиночных и ансамблевых алгоритмов
на основе серии экспериментов.
3. Интеграция с внешним сервисом evaluation-results-service. API предоставляет возможность
отправки результатов классификации во внешних сервис для последующего анализа.

Необходимый софт
----------------------------------------
* jdk 1.8
* maven => 3.3.9
* eca-core 5.1
* DB для хранения информации.
* Контейнер сервлетов (например Tomcat 7)

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-service находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) cross-validation — настройки параметров для метода k * V блочной кросс - проверки
   на тестовой выборке:
   cross-validation.numFolds - число блоков
   cross-validation.numTests - число тестов
3) experiment - настройки параметров модуля Data Miner. Ниже приведено описание
   основных настроек:
   experiment.resultSize - число наилучших конфигураций классификаторов
   experiment.numIterations - число итераций эксперимента
   experiment.storagePath - путь к папке на файловой системе для хранения файлов с историей экспериментов
   experiment.individualClassifiersStoragePath - путь к папке в ресурсах для хранения json - конфигураций классификаторов,
   которые впоследствии будут использоваться при построении эксперимента
   experiment.downloadUrl - url ссылки на скачивание файла с результатами эксперимента
   experiment.maximumFractionDigits - число десятичных знаков после запятой
   experiment.timeout - время таймаута эксперимента в часах.
   experiment.pageSize - размер страницы для постраничной обработки экспериментов.
   experiment.resultSizeToSend - число лучших моделей классификаторов в одном эксперименте, результаты которых впоследствии
   будут отправлены в сервис evaluation-results-service
   experiment.delay - интервал между запусками scheduler для обработки экспериметов
   experiment.data.storagePath - путь к папке на файловой системе для хранения файлов с исходныи данными (обучающая выборка)
   experiment.ensemble.numIterations - число итераций для ансамблевых алгоритмов
   experiment.ensemble.numBestClassifiers - число наилучших по точности базовых классификаторов, которые впоследствии
   будут использоваться при построении ансамбля
   experiment.ensemble.multiThreadModeEnabled - многопоточный режим для ансамблевых алгоритмов (вкл./выкл.)
   experiment.ensemble.numThreads - число используемых потоков
   experiment.ensemble.numFoldsForStacking - число блоков V - блочной кросс - проверки для алгоритма Stacking
   experiment.mail.from - email отправителя
   experiment.mail.subject - тема письма
   experiment.mail.maxFailedAttemptsToSent - максимальной число попыток для отправки письма
4) spring.mail - настройки smtp сервера для отправки результатов экспериментов по email
5) evaluationResultsServiceConfig - настройки интеграции с сервисом evaluation-results-service
   evaluationResultsServiceConfig.url - url конечной точки сервиса
   evaluationResultsServiceConfig.enabled - выключатель для отправки результатов классификации (вкл./выкл.)
   evaluationResultsServiceConfig.threadPoolSize - максимальный размер пула потоков для асинхронной отправки
   результатов классификации

Инструкция по развертыванию
----------------------------------------

1. Для запуска сервиса сначала необходимо собрать проект. Ниже приведен пример команды:
    
   mvn clean install -P[experiment-profile],[db-profile] -Dexperiment.storagePath=/home/roman/experiment/
           -Dexperiment.data.storagePath=/home/roman/experiment/data/ -Dmail.host=smtp.yandex.com
           -Dmail.port=25 -Dmail.username=test@yandex.ru -Dmail.password=password
           -Djdbc.url=url -Djdbc.user=user -Djdbc.password=pass -Djdbc.dllAuto=update 
    
   Ниже приведен перечень основных параметров:
   
   a) experiment-profile - профиль для параметров модуля Data Miner. Может принимать одно из следующих значений:
        * experiment-linux - используется для машин с ОС семейства Linux.
        * experiment-windows - используется для машин с ОС семейства Windows.
   b) db-profile - профиль для настройки параметров конкретной БД. Может принимать одно из следующих значений:
        * postgres - профиль для параметров СУБД PostgreSQL
        * mysql - профиль для параметров СУБД MySQL
        * oracle - профиль для параметров СУБД Oracle
        * h2 - профиль для параметров СУБД H2
   c) Параметры БД:
        * jdbc.url - url для подключения к БД
        * jdbc.user - логин
        * jdbc.password - пароль
        * jdbc.ddlAuto - параметр, который может принимать одно из четырех значений:
   
            validate - не вносить изменения в базу данных.
            update - обновить схему базы данных.
            create - создать базу данных, уничтожив предыдущие данные.
            create-drop - создать базу данных. После завершения работы приложения, созданная
                        база данных будет удалена.
   d) Параметры модуля Data Miner:
        * experiment.storagePath - путь к папке на файловой системе для хранения файлов с историей экспериментов
        * experiment.data.storagePath - путь к папке на файловой системе для хранения файлов с
            исходныи данными (обучающая выборка)
        * mail - настройки smtp - сервера для отправки результатов эксперимента по email
    
2. Развернуть target/eca-service-2.0.war на одном из контейнеров сервлетов (например, Tomcat) с контекстом /eca-service.
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-service/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
    