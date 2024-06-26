Модуль eca-mail
========================================

Описание
----------------------------------------
   Eca mail представляет собой микросервис для отправки email сообщений.

Необходимый софт
----------------------------------------
* Openjdk 21
* maven >= 3.9.2
* База данных PostgreSQL 13.9 для хранения информации.

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта находятся в файле application.yml. Ниже приведены основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) mail-config - основные настройки модуля
    * maxFailedAttemptsToSent - максимальное число попыток для отправки email
    * pageSize - число писем для отправки за один раз
    * delaySeconds - интервал в сек. между отправками писем
    * sender - Email отправителя
    * encrypt - настройки для алгоритма шифрования AES
       * password - пароль для алгоритма PBKDF2
       * salt - соль для алгоритма PBKDF2
3) open-api - настройки Swagger
   * open-api.tokenBaseUrl - базовый url - сервера авторизации
   * open-api.projectVersion - версия API
   * open-api.title - краткое название API
   * open-api.description - описание API
   * open-api.author - автор
   * open-api.email - email для связи
   * open-api.basePath - базовый префикс для API
   * open-api.apiAuth - настройки авторизации
   * open-api.apiAuth.scopes - список scopes
4) logging.mode - режим логирования
   * text - текстовый формат
   * json - логи в формате json
    
Инструкция по развертыванию
----------------------------------------
       
1. Собрать проект с помощью системы сборки проекта maven. Ниже приведен пример команды:

   mvn clean install
   
2. Запустить проект с помощью команды:

    java -jar /target/eca-mail.jar

   
