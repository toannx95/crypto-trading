# Crypto Trading System (Spring Boot + H2)

This is a crypto trading system built with **Spring Boot** and **H2 in-memory database**.  

---
## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 2.4.4**
- **H2 Database**
- **Flyway**
- **MapStruct**
- **Lombok**
- **SLF4J Simple**
- **Maven**

---
## 📊 Database Design

- **user**: stores user information.
- **aggregated_price**: stores the latest best bid/ask per symbol.
- **wallet_balance**: stores user balances (USDT, BTC, ETH).
- **trade**: records executed BUY/SELL transactions.

### 📝 Notes
- For this demo, initialize 2 users in the **user** table first.
- And initialize wallet balance of **50,000 USDT** for each user.

### 🖼️ DB Diagram
![Database Diagram](/docs/DB%20diagram.PNG)

---
## 🚀 Features Implemented

### 1. Price Aggregation
- Scheduled job (every 10 seconds) fetches prices from:
    - Binance → [Binance API](https://api.binance.com/api/v3/ticker/bookTicker)
    - Huobi → [Huobi API](https://api.huobi.pro/market/tickers)
- Aggregates best Bid and best Ask:
    - **Best Bid** = highest bid (used for SELL orders).
    - **Best Ask** = lowest ask (used for BUY orders).
- Stores the latest aggregated prices into the database.

### 2. Latest Prices API
- Returns the list of the latest aggregated prices
  - Supports filtering by symbol (ETHUSDT/BTCUSDT)
  - Supports page number, page size, sort field, and sort order (asc/desc)
- Returns the latest aggregated price for a specific symbol.

### 3. Trading API
- Allows users to BUY/SELL crypto based on the latest best price.
- Validates wallet balances before executing trades.
- Executes trades transactionally:
    - Updates wallet balances (USDT, BTC, ETH).
    - Records each trade in the transaction history.

### 4. Wallet Balance API
- Retrieves the user’s current crypto balances.

### 5. Trading History API
- Returns the list of trades executed by a user.
- Supports filtering by symbol (ETHUSDT/BTCUSDT) and trade type (BUY/SELL).
- Supports page number, page size, sort field, and sort order (asc/desc).

---
## 📖 API Specification
- Detailed API documentation is available in here: [API Spec](./docs/API Spec.md)

---
## 🔮 Possible Enhancements

- **Add Idempotency-Key support for POST /trades** → avoid duplicate trades on client retries due to network issues or timeouts.
- **Use Quartz Scheduler instead of @Scheduled** → prevents overlapping executions, with clustering (only one node runs the job in multi-node deployments).
- **Circuit-breaker for each exchange** → protects the system from failures if one exchange is slow or unavailable. And circuit breaker temporarily stops calling the failing exchange after repeated errors.
- **Generate API docs with Swagger / OpenAPI** → auto-generate clear API documentation for easier testing/integration.
- **Apply with Microservices**
- **Add Unit Test**
- **Upgrade Java 21, Spring Bot 3.x.x**

---
## ✅ How to Run

#### Build
```bash
  mvn clean install
````  

#### Run
```bash
  mvn spring-boot:run
```

#### H2 DB console
```
  http://localhost:8080/h2-console
```
