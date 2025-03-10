Модуль eca-oauth
========================================

Описание
----------------------------------------
Данный модуль выполняет роль сервера авторизации c использованием протокола OAuth2.
Он отвечает за проверку подлинности пользовательской информации и создание авторизационных токенов
для web - приложения. С помощью авторизационных токенов web - приложение будет осуществлять доступ к ресурсам.

Необходимый софт
----------------------------------------
* Openjdk 21
* maven => 3.9.2
* Postgres Database для хранения информации 13.9

Описание ключевой конфигурации модуля
----------------------------------------
Настройки для проекта eca-oauth находятся в application.yml. Основные параметры:
1) spring.datasource - настройки БД для хранения информации
2) app - общие настройки приложения
   * app.webExternalBaseUrl - внешний url веб приложения
   * app.validUserPhotoFileExtensions - список доступных расширений для фотографий пользователя
   * app.resetPassword.validityMinutes - время действия токена для восстановления пароля в мин.
   * app.resetPassword.url - url для формирования ссылки для восстановления пароля
   * app.changePassword.validityMinutes - время действия токена для изменения пароля в мин.
   * app.changeEmail.validityMinutes - время действия токена для изменения email в мин.
   * app.security.whitelistUrls - список endpoint-ов не требующих авторизацию
   * app.security.writeTokenInCookie - получать access/refresh токен в куках
   * app.security.refreshTokenCookiePath - refresh токен куки path
   * app.security.tokenInCookieAvailableGrantTypes - список доступных схем (grant_type) для получения access/refresh токен в куках. По умолчанию: password, tfa_code, refresh_token
   * app.security.tokenInCookieClientIds - список доступных client id для получения access/refresh токен в куках
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
4) password - настройки для генератора паролей
   * password.length - длина пароля
5) audit.enabled - вкл./выкл. отправки событий аудита
6) tfa - настройки двухфакторной аутентификации
   * tfa.enabled - глобальное вкл./выкл. исполользование двухфакторной аутентификации
   * tfa.codeValiditySeconds - время действия одноразового пароля в сек.
   * tfa.codeLength - длина пароля
   * tfa.delaySeconds - интервал в сек. между запусками шедулера для очистки просроченных кодов подтверждения
7) mail.client - настройки библиотеки отправки email сообщений
8) redelivery.enabled - вкл./выкл. библиотеки redelivery
9) logging.mode - режим логирования
   * text - текстовый формат
   * json - логи в формате json
10) user-profile - дефолтные настройки для профиля пользователя
   * user-profile.emailEnabled - глобальный флаг вкл./выкл. отправки email сообщений
   * user-profile.webPushEnabled - глобальный флаг вкл./выкл. отправки пуш уведомлений в приложении
   * user-profile.notificationEventOptions - настройки событий уведомлений
   * user-profile.dataEventRetryIntervalSeconds - интервал в сек. между запуском scheduler для повторной отправки событий изменений настроек профиля пользователя
   * user-profile.rabbit.exchangeName - название exchange для отправки событий изменений настроек профиля пользователя

Инструкция по развертыванию
----------------------------------------

1. Для запуска модуля необходимо собрать проект с помощью команды:
    
   mvn clean install
    
2. Запустить проект с помощью команды:

    java -jar /target/eca-oauth.jar

