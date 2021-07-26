Evaluation results service
========================================

Описание
----------------------------------------
   Модуль предназначен для хранения и анализа результатов классификации разнотипных данных с использованием 
различных алгоритмов классификации, как одиночных так и ансамблевых. Сервис также предоставляет API для 
нахождения оптимальных параметров классификаторов для конкретной обучающей выборки на основе накопленной 
истории результатов классификации.


Необходимый софт
----------------------------------------
* Openjdk 1.11
* maven >= 3.3.9
* База данных PostgreSQL для хранения информации.

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта находятся в файле application.yml. Ниже приведены основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) open-api - настройки Swagger
   * open-api.tokenBaseUrl - базовый url - сервера авторизации
   * open-api.projectVersion - версия API
   * open-api.title - краткое название API
   * open-api.description - описание API
   * open-api.author - автор
   * open-api.email - email для связи
   * open-api.basePath - базовый префикс для API
   * open-api.apiAuth - настройки авторизации
   * open-api.apiAuth.scopes - список scopes
3) ers - основные настройки модуля
    * ers.resultSize - число наилучших конфигураций классификаторов

Бизнес метрики приложения
----------------------------------------
* ers.operation.save-evaluation-results.timed.seconds.max - Макс. время выполнения операции сохранения результатов классификации
* ers.operation.save-evaluation-results.timed.seconds.count - Общее число операций сохранения результатов классификации
* ers.operation.save-evaluation-results.timed.seconds.sum - Суммарное время для операций сохранения результатов классификации
* ers.operation.get-evaluation-results.timed.seconds.max - Макс. время выполнения операции получения результатов классификации
* ers.operation.get-evaluation-results.timed.seconds.count - Общее число операций получения результатов классификации
* ers.operation.get-evaluation-results.timed.seconds.sum - Суммарное время для операций получения результатов классификации
* ers.operation.get-optimal-classifier-options.timed.seconds.max - Макс. время выполнения операции поиска оптимальных конфигураций классификаторов
* ers.operation.get-optimal-classifier-options.seconds.count - Общее число операций поиска оптимальных конфигураций классификаторов
* ers.operation.get-optimal-classifier-options.timed.seconds.sum - Суммарное время для операций поиска оптимальных конфигураций классификаторов

Метрики приложения доступны по адресу http://[service.host]:[service.port]/actuator/prometheus

Инструкция по развертыванию
----------------------------------------
   
1. Собрать проект с помощью системы сборки проекта maven. Ниже приведен пример команды:

   mvn clean install
   
2. Запустить проект с помощью команды:

    java -jar /target/eca-ers.war
