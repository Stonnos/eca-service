export const environment = {
  production: true,
  serverUrl: `http://${window.location.hostname}:${window.location.port}/eca-server`,
  oauthUrl: `http://${window.location.hostname}:${window.location.port}/eca-oauth/oauth/token`,
  clientId: "eca_web",
  secret: "web_secret"
};
