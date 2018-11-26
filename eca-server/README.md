ECA service v4.0
========================================

Описание
----------------------------------------
1. Данный сервис предназначен для построения различных моделей классификаторов, а также
расчёта их основных показателей точности. Сервис поддерживает построение как
одиночных, так и ансамблевых моделей.
2. Сервис также предоставляет REST - API Data Miner, основным предназначением которого
является автоматический подбор оптимальных параметров для одиночных и ансамблевых алгоритмов
на основе серии экспериментов.
3. Интеграция с внешним сервисом ERS (Evaluation results service). API предоставляет возможность
отправки результатов классификации во внешних сервис для последующего анализа, а также нахождение
оптимальных конфигураций классификаторов для конкретной обучающей выборки на
основе истории результатов классификации.
4. Интеграция с внешним сервисом Notification service для отправки почты. 

Необходимый софт
----------------------------------------
* jdk 1.8
* maven => 3.3.9
* eca-core 6.4
* DB для хранения информации.
* Контейнер сервлетов (например Tomcat 8)

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-service находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) cross-validation — настройки параметров для метода k * V блочной кросс - проверки
   на тестовой выборке:
   * cross-validation.numFolds - число блоков
   * cross-validation.numTests - число тестов
   * cross-validation.seed - начальное значение для генератора псевдослучайных чисел
   * cross-validation.timeout - таймаут в сек. для оценки точности классификатора
   * cross-validation.classifierOptionsCacheDurationInDays - период хранения оптимальных настроек классификатора
   полученных от внешнего сервиса ERS
3) experiment - настройки параметров модуля Data Miner. Ниже приведено описание
   основных настроек:
   * experiment.resultSize - число наилучших конфигураций классификаторов
   * experiment.numIterations - число итераций эксперимента
   * experiment.storagePath - путь к папке на файловой системе для хранения файлов с историей экспериментов
   * experiment.individualClassifiersStoragePath - путь к папке в ресурсах для хранения json - конфигураций классификаторов,
   которые впоследствии будут использоваться при построении эксперимента
   * experiment.downloadUrl - url ссылки на скачивание файла с результатами эксперимента
   * experiment.maximumFractionDigits - число десятичных знаков после запятой
   * experiment.timeout - время таймаута эксперимента в часах.
   * experiment.pageSize - размер страницы для постраничной обработки экспериментов.
   * experiment.resultSizeToSend - число лучших моделей классификаторов в одном эксперименте, результаты которых впоследствии
   будут отправлены в сервис evaluation-results-service
   * experiment.delay - интервал между запусками scheduler для обработки экспериметов
   * experiment.data.storagePath - путь к папке на файловой системе для хранения файлов с исходныи данными (обучающая выборка)
   * experiment.ensemble.numIterations - число итераций для ансамблевых алгоритмов
   * experiment.ensemble.numBestClassifiers - число наилучших по точности базовых классификаторов, которые впоследствии
   будут использоваться при построении ансамбля
   * experiment.ensemble.multiThreadModeEnabled - многопоточный режим для ансамблевых алгоритмов (вкл./выкл.)
   * experiment.ensemble.numThreads - число используемых потоков
   * experiment.ensemble.numFoldsForStacking - число блоков V - блочной кросс - проверки для алгоритма Stacking
   * experiment.mail.from - email отправителя
   * experiment.mail.subject - тема письма
   * experiment.mail.serviceUrl - url конечной точки Notification service
4) ers-config - настройки интеграции с сервисом evaluation-results-service
   * ers-config.url - url конечной точки ERS сервиса
   * ers-config.enabled - выключатель для отправки результатов классификации (вкл./выкл.)
   * ers-config.threadPoolSize - максимальный размер пула потоков для асинхронной отправки
   результатов классификации

Инструкция по развертыванию
----------------------------------------

1. Для запуска сервиса сначала необходимо собрать проект. Ниже приведен пример команды:
    
   mvn clean install -P[experiment-profile],[db-profile] -Dexperiment.storagePath=/home/roman/experiment/
           -Dexperiment.data.storagePath=/home/roman/experiment/data/ 
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
    
2. Развернуть target/eca-service.war на одном из контейнеров сервлетов (например, Tomcat 8) с контекстом /eca-service.
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-service/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.

Интеграционные тесты
------------------------------------------------------
Для запуска всех интеграционыых тестов необходимо выполнить команду (указав профиль quality):

mvn clean install -Pquality

С параметрами:
-Ders-config.url=http://localhost:8089/evaluation-results-service/ws/
-Ders-config.enabled=true
-Ders-config.threadPoolSize=10

Инструкция по развертыванию в Docker
-------------------------------------------------------

1. Для Windows достаточно скачать и установить дистрибутив Docker Desktop (https://www.docker.com/products/docker-desktop).

2. Далее для сборки проекта и создания образа проекта нужно выполнить команду:

mvn clean install dockerfile:build

3. Используя пакетный менеджер docker-compose, создать docker контейнеры с помощью команды:

docker-compose up

ВАЖНО! Данную команду необходимо выполнять из корневой папки проекта.

Для развертывания окружения на production используется команда:

mvn clean install dockerfile:build -Ppostgres,experiment-linux -Djdbc.url=jdbc:postgresql://eca-db:5432/eca

При этом в файле application.yml необходимо установить следующие параметры:

  * ers-config.url=http://evaluation-results-service:8080/evaluation-results-service/ws/
  * experiment.mail.serviceUrl=http://notification-service:8080/notification-service/ws/
  * oauth2-resource.oauthUrl=http://eca-oauth:8080/eca-oauth
  * swagger2.oauthUrl=http://localhost:8083/eca-oauth

ВАЖНО! В конфиге swagger2.oauthUrl необходимо указывать внешний url docker контейнера

Для удаления всех контейнеров и image-ов необходимо выполнить команду:

docker-compose down --rmi all

Основные команды Docker
-------------------------------------------------------

Для просмотра image-ов, необходимо выполнить команду:

docker images

Для просмотра запущенных контейнеров, необходимо выполнить команду:

docker ps

Для удаления контейнера, необходимо выполнить команду:

docker rm container_name

Для удаления image, необходимо выполнить команду:

docker image rm image_id

Для просотра доступных сетей, необходимо выполнить команду:

docker network ls

Для того чтобы подключится к контейнеру, необходимо выполнить команду:

docker exec -it container_name bash

Для копирования новой версии war файла в контейнер (container_name), необходимо выполнить команду:

docker cp application.war container_name:/usr/local/tomcat/webapps/

ВАЖНО! Данную команду необходимо выполнять из директории, в которой лежит war файл.
Либо можно указать абсолютный путь к war - файлу.