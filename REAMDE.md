ECA service
========================================

Описание
----------------------------------------
1. Данный сервис предназначен для построения различных моделей классификаторов, а также
расчёта их основных показателей точности. Сервис поддерживает посроение как
одиночных, так и ансамблевых моделей.
2. Сервис также предоставляет REST - API Data Miner, основным предназначением которого
является автоматический подбор оптимальных параметров для одиночных и ансамблевых алгоритмов
на основе серии экспериментов.

Необходимый софт
----------------------------------------
* jdk 1.8
* maven 3.3.9
* eca-core 5.0
* DB для хранения информации.

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-service находятся в application.properties. Основные параметы:
1) spring.datasource - настройки БД для хранения информации
2) server.port — номер порта сервера
3) cross.validation — настройки параметров для метода k * V блочной кросс - проверки
   на тестовой выборке:
   numFolds - число блоков
   numTests - число тестов
4) experiment - настройки параметров модуля Data Miner. Ниже приведено описание
   основных настроек:
   resultSize - число наилучших конфигураций классификаторов
   numIterations - число итераций эксперимента
   storagePath - путь к папке в файловой системе для хранения файлов с историей экспериментов
   downloadUrl - url ссылки на скачивание файла с результатами эксперимента
   maximumFractionDigits - число десятичных знаков после запятой
   timeout - время таймаута в часах.
   delay - интервал между запусками scheduler для обработки экспериметов
   data.storagePath - путь к папке в файловой системе для хранения файлов с исходныи данными (обучающая выборка)
   ensemble.numIterations - число итераций для ансамблевых алгоритмов
   mail.from - email отправителя
   mail.subject - тема письма
   mail.maxFailedAttemptsToSent - максимальной число попыток для отправки письма
5) spring.mail - настройки smtp сервера для отправки результатов экспериментов по email 

Инструкция
----------------------------------------

1. Для запуска сервиса с использованием БД MySQL необходимо:
    Собрать проект с использованием профиля mysql:
    
    mvn clean install -Pmysql -Djdbc.url=url -Djdbc.user=user -Djdbc.password=pass -Djdbc.dllAuto=update
    
   Для запуска сервиса с использованием БД PostreSQL необходимо:
    Собрать проект с использованием профиля postgres:
       
    mvn clean install -Ppostgres -Djdbc.url=url -Djdbc.user=user -Djdbc.password=pass -Djdbc.dllAuto=update
       
   Для запуска сервиса с использованием БД OracleSQL необходимо:
    Собрать проект с использованием профиля oracle:
       
    mvn clean install -Poracle -Djdbc.url=url -Djdbc.user=user -Djdbc.password=pass -Djdbc.dllAuto=update
 
   Для запуска сервиса с использованием БД H2 необходимо:
    Собрать проект с использованием профиля h2:
       
    mvn clean install -Ph2
    
   где:
   url - url для подключения к БД
   user - логин
   pass - пароль
   ddlAuto - параметр, который может принимать одно из четырех значений:
   
        validate - не вносить изменения в базу данных.
        update - обновить схему базы данных.
        create - создать базу данных, уничтожив предыдущие данные.
        create-drop - создать базу данных. После завершения работы приложения, созданная
                        база данных будет удалена.
   
       
2. Запустить приложение из КОРНЕВОЙ папки проекта

    java -jar target/eca-service-1.2.jar
    
3. Для запуска вместе с модулем DataMiner необходимо собрать приложение с профилем experiment, указав
основные параметры. Ниже приведен пример:

    mvn clean install -Pexperiment, postgres -Dexperiment.storagePath=/home/roman/experiment/
        -Dexperiment.data.storagePath=/home/roman/experiment/data/ -Dspring.mail.host=smtp.yandex.com
        -Dspring.mail.port=25 -Dspring.mail.username=test@yandex.ru -Dspring.mail.password=password
        -Dmail.from=test@yandex.ru
    