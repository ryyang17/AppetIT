# AppetIT

A restaurant ordering platform: customers browse a menu, order from their table, and staff manage products, restaurants, tables, and orders in real time.

This repository is a monorepo combining the two AppetIT projects, each kept with its full original git history:

- [`backend/`](backend) — Kotlin + Spring WebFlux API
- [`frontend/`](frontend) — Next.js customer ordering app

## Tech stack

**Backend**
- Kotlin, Spring Boot (WebFlux, R2DBC)
- PostgreSQL with Flyway migrations
- Gradle
- Docker (deployed as `547988/appetit-api`)

**Frontend**
- Next.js 15 (App Router), React 19, TypeScript
- Tailwind CSS
- i18next (English / Dutch)
- Docker (deployed as `547988/appetit-user-app`)

## Getting started

### Backend

```bash
cd backend
./gradlew bootRun
```

Requires a local PostgreSQL instance (see [`application.properties`](backend/src/main/resources/application.properties) for connection settings, database `appetit`). Once running:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs at http://localhost:3000, and expects the backend API URL to be configured via `API_BASE_URL`.

## Deployment

Both services build Docker images and ship via `docker-compose` with separate `staging` and `prod` profiles (see `backend/docker-compose.yml` and `frontend/docker-compose.yml`).

## About

Built as part of the FSD5 project at Fontys (S3-CB, FSD5 InfoSupport, Group 2).
