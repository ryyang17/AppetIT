# ADR: R2DBC voor WebFlux + WebSockets

## Status
Geaccepteerd – 2025-11-25

## Context
We wilden de API klaar maken voor WebSockets. Die verbindingen blijven lang open, dus we willen zo weinig mogelijk blokkerende code zodat de server niet vastloopt op drukke momenten. In de code gebruiken we al overal `Flux`/`Mono` (WebFlux) en R2DBC repositories. Als we daar een blokkerende JDBC-driver tussen zetten, verliezen we het voordeel en staan threads alsnog te wachten op de database.

## Beslissing
We blijven bij **Spring Data R2DBC** (met de PostgreSQL R2DBC-driver) in plaats van JPA/JDBC. Zo blijft de hele keten niet-blokkerend: controller → service → repository → database. Hierdoor kan de server meer WebSocket-clients tegelijk aan zonder extra threads te verbruiken.

## Consequenties
- **Plus**
  - Alles blijft reactief, dus minder kans op blokkerende bottlenecks.
  - WebSockets schalen beter omdat queries geen threads meer vasthouden.
  - Minder latency als er veel events tegelijk lopen.
- **Min**
  - Minder tooling en voorbeelden dan bij JPA.
  - Flyway werkt niet op R2DBC, dus we hebben een aparte JDBC-config (zie `FlywayConfig`).
  - Debuggen en testen vraagt Reactor-kennis (StepVerifier etc.).
- **In de gaten houden**
  - Monitoring moet blokkerende calls kunnen spotten.
  - Nieuwe teamleden moeten wennen aan Reactor/backpressure.

## Alternatieven
1. **Spring MVC + JPA (JDBC)**  
   + Bekende stack, veel voorbeelden  
   − Blokkerend, dus slecht voor veel WebSocket-clients.
2. **WebFlux met coroutines + JDBC**  
   + Werkt prima met Kotlin  
   − JDBC blokkeert alsnog; je hebt extra threadpools nodig en verliest het voordeel.

## Referenties
- `build.gradle.kts` – dependencies voor WebFlux en R2DBC.
- `src/main/kotlin/.../ProductController.kt` – voorbeelden van `Flux`/`Mono` endpoints.
- `src/main/kotlin/.../config/FlywayConfig.kt` – aanvullende configuratie omdat Flyway JDBC vereist.


