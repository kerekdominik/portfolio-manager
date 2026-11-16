# syntax=docker/dockerfile:1

# ---- build: Angular production bundle -----------------------------------------------------------
FROM node:20-alpine AS build
WORKDIR /app

# Lockfile first, so the npm layer is reused until the dependencies change
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build -- --configuration production

# ---- runtime: unprivileged nginx serves the files and proxies /api to the backend --------------
FROM nginxinc/nginx-unprivileged:alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/portfolio-frontend/browser /usr/share/nginx/html

EXPOSE 8080
# 127.0.0.1 on purpose: "localhost" can resolve to ::1 while nginx only listens on IPv4
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/healthz || exit 1
