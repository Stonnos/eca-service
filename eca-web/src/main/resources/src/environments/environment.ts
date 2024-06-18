// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  serverUrl: `http://${window.location.hostname}:${window.location.port}/eca-server`,
  oauthUrl: `http://${window.location.hostname}:${window.location.port}/eca-oauth`,
  dsUrl: `http://${window.location.hostname}:${window.location.port}/eca-ds`,
  mailUrl: `http://${window.location.hostname}:${window.location.port}/eca-mail`,
  auditLogUrl: `http://${window.location.hostname}:${window.location.port}/eca-audit-log`,
  webPushUrl: `http://${window.location.hostname}:${window.location.port}/eca-web-push`,
  ersUrl: `http://${window.location.hostname}:${window.location.port}/eca-ers`,
  wsUrl: `ws://${window.location.hostname}:${window.location.port}/eca-web-push/socket`,
  clientId: "eca_web",
  secret: "web_secret",
  pushQueue: "/push",
  pushLifeTimeMillis: 12000,
  debug: true
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
