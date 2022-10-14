export const environment = {
  production: true,
  serverUrl: `http://${window.location.hostname}:${window.location.port}/eca-server`,
  oauthUrl: `http://${window.location.hostname}:${window.location.port}/eca-oauth`,
  dsUrl: `http://${window.location.hostname}:${window.location.port}/eca-ds`,
  mailUrl: `http://${window.location.hostname}:${window.location.port}/eca-mail`,
  auditLogUrl: `http://${window.location.hostname}:${window.location.port}/eca-audit-log`,
  webAppUrl: `http://${window.location.hostname}:${window.location.port}`,
  webPushUrl: `http://${window.location.hostname}:${window.location.port}/eca-web-push`,
  wsUrl: `ws://${window.location.hostname}:8099/socket`,
  clientId: "eca_web",
  secret: "web_secret",
  pushQueue: "/push",
  pushLifeTimeMillis: 12000,
  debug: false
};
