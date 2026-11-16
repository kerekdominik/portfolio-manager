export const environment = {
  production: false,
  // Relative, so the app works on any host: nginx (container) and `ng serve` (proxy.conf.json) forward /api to the backend
  apiBaseUrl: '/api'
};
