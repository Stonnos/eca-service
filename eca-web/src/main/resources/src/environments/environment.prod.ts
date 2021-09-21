export const environment = {
  production: true,
  serverUrl: `http://${window.location.hostname}:${window.location.port}/eca-server`,
  oauthUrl: `http://${window.location.hostname}:${window.location.port}/eca-oauth`,
  dsUrl: `http://${window.location.hostname}:${window.location.port}/eca-ds`,
  mailUrl: `http://${window.location.hostname}:${window.location.port}/eca-mail`,
  auditLogUrl: `http://${window.location.hostname}:${window.location.port}/eca-audit-log`,
  wsUrl: `ws://${window.location.hostname}:8099/socket`,
  clientId: "eca_web",
  secret: "web_secret"
};
