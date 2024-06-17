Модуль автотестов для внешнего API
========================================

Описание
----------------------------------------
Данный модуль предоставляет следующий функционал:

1. Запуск пакета автотестов для внешнего API. 
2. Построение сводных отчетов по автотестам в формате csv.
3. Возможность добавления новых автотестов в директорию src/main/resources

Необходимый софт
----------------------------------------
* Openjdk 21
* maven => 3.9.2

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-external-api-tests находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) external-api-tests — настройки параметров для тестов:
   * external-api-tests.numThreads - число потоков для параллельного выполнения тестов
   * external-api-tests.workerThreadTimeOutInSeconds - таймаут для выполнения пакета тестов
   * external-api-tests.pageSize - размер страницы для постраничной обработки
   * external-api-tests.delaySeconds - интервал между запусками scheduler для обработки тестов
   * external-api-tests.url - endpoint для внешнего API
   * external-api-tests.evaluationTestDataPath - относительный путь к директории (в resources) с автотестами для построения моделей классификаторов
   * external-api-tests.experimentTestDataPath - относительный путь к директории (в resources) с автотестами для построения экспериментов
   * external-api.expectedExperimentNumModels - ожидаемое число моделей в истории эксперимента
   * external-api.auth.clientId - идентификатор клиента
   * external-api.auth.clientSecret - секретный ключ или пароль клиента
   * external-api.auth.tokenUrl - endpoint для получения access token
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
4) process - настройки бизнес - процесса
   * ids - идентификаторы бизнес - процессов

Инструкция по развертыванию
----------------------------------------

1. Собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

java --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.sql/java.sql=ALL-UNNAMED -jar target/eca-external-api-tests.jar
         
3. Страница с документацией swagger находится по адресу http://[host]:[port]/eca-external-api-tests/swagger-ui.html, где host и port
соответственно адрес машины и порт на котором развернуто приложение.
