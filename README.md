Проект Eca - service
========================================

Описание
----------------------------------------
Представляет собой серверное решение для классификации разнотипных данных (количественных, порядковых, номинальных)
из любой предметной области на основе ансамбля алгоритмов.

Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven => 3.3.9
* Rabbit MQ => 3
* eca-core 6.9.3
* Postgres Database для хранения информации => 9.6
* Docker, Docker-compose

Описание модулей
----------------------------------------

* core-test - модуль содержит основные зависимости для тестов
* oauth2-test - модуль содержит конфигурацию spring security oauth2 для тестов
* common-error-model - модуль содержит модельные классы для ошибок
* common-web - модуль содержит общие классы, используемые во всех сервисах
* core-lock - библиотека для работы с блокирующими операциями
* redis-lock - библиотека для работы с блокирующими операциями с использованием redis
* eca-model - модуль содержит модель сообщений с классификаторами для передачи через rabbit mq
* eca-user-model - модуль содержит основные классы для работы с пользовательским контекстом
* eca-mail-transport - модуль содержит dto классы для сервиса eca-mail
* classifiers-options - модуль содержит модельные классы для настроек входных параметров классификаторов
* eca-ers-transport - модуль содержит dto классы для сервиса ERS
* eca-ds-transport - модуль содержит dto классы для сервиса Data storage
* eca-external-api-transport - модуль содержит dto классы для внешнего API
* eca-audit-transport - модуль содержит dto классы для сервиса журнала аудита
* eca-report-model - модуль содержит модельные классы для отчетов
* eca-web-transport - модуль содержит dto классы для REST API WEB
* core-redelivery - общая библиотека для поддержки механизмов redelivery
* core-audit - библиотека для отправки событий аудита
* core-filter - общая библиотека для фильтрации и сортировки
* core-form-template - общая библиотека для работы с шаблонами CRUD - форм
* swagger-core - модуль содержит конфигурационные классы для swagger
* oauth2-core - модуль содержит конфигурационные классы для oauth2 resource server 
* rabbit-core - модуль содержит конфигурационные классы для rabbit
* eca-mail-client - общая библиотека для отправки email сообщений
* eca-s3-minio-client - общая библиотека для работы с S3 minio storage
* classifiers-options-adapter - модуль для конвертации настроек классификаторов
* eca-report - модуль генерации отчетов
* eca-report-data - общая библиотека формирования данных для отчетов
* feign-metrics - общая библиотека провайдера метрик для feign client
* eca-tests-common - общая библиотека для модулей авто тестов
* eca-oauth - модуль авторизации
* eca-server - основной модуль, который принимает сообщения для обучения моделей классификаторов
* eca-mail - модуль для отправки почты
* eca-ers - сервис для хранения и анализа результатов классификации
* eca-data-storage - модуль для хранения обучающих выборок в виде таблиц БД
* eca-web-push - модуль для отправки веб - пушей с поддержкой web sockets
* eca-audit-log - модуль для сбора и хранения событий аудита
* eca-external-api - предоставляет внешний API (web proxy) для обучения моделей классификаторов
* eca-web - реализует web клиент для администрирования
* zuul-gate - Zuul proxy  (API gateway)
* discovery-server - Eureka discovery server
* eca-load-tests - модуль для нагрузочного тестирования eca-server
* eca-external-api-tests - модуль автоматических тестов для внешнего API
* eca-auto-tests - модуль автоматических тестов для запросов на построение моделей

Интеграционные тесты
------------------------------------------------------

Для запуска всех интеграционых тестов необходимо выполнить команду (указав профиль quality):

mvn clean install -Pquality

Инструкция по развертыванию в Docker
-------------------------------------------------------

1. Для Windows 10 достаточно скачать и установить дистрибутив Docker Desktop (https://www.docker.com/products/docker-desktop).
   
   Для Linux сначала необходимо установить Docker CE (https://docs.docker.com/install/linux/docker-ce/ubuntu/),
   затем Docker compose (https://docs.docker.com/compose/install/).

2. Далее для сборки проекта и создания образа проекта нужно выполнить команду:

    mvn clean install dockerfile:build

3. Используя пакетный менеджер docker-compose, создать docker контейнеры с помощью команды:

    docker-compose up -d

ВАЖНО! Данную команду необходимо выполнять из корневой папки проекта.

Для остановки приложения (удаления контейнеров) нужно выполнить команду:

docker-compose down

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

Для просмотра доступных сетей, необходимо выполнить команду:

docker network ls

Для того чтобы подключится к контейнеру, необходимо выполнить команду:

docker exec -it container_name bash

Для копирования новой версии war файла в контейнер (container_name), необходимо выполнить команду:

docker cp application.war container_name:/

ВАЖНО! Данную команду необходимо выполнять из директории, в которой лежит war файл. Либо можно указать абсолютный путь к war - файлу.

Обновление версии проекта
-------------------------------------------------------

Для поднятия версии всех модулей необходимо выполнить команду:

mvn versions:set -DnewVersion=version -DgenerateBackupPoms=false