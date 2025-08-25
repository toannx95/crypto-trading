# 📖 API Specification (Crypto Trading System)

---
## 1. Get All Latest Prices
- **Description**: Fetches the latest aggregated prices.
- **Endpoint**: `/api/prices`
- **Method**: GET
- **Request Parameters**:
  - symbol: String - Not required - Filter by a specific trading symbol
  - page: int - Not required - Default `0` - Page number
  - size: int - Not required - Default `20` - Number of records per page
  - sort: String - Not required - Default `createdAt` - Field name to sort by
  - order: String - Not required - Default `asc` - Sort direction (`asc` or `desc`)
- **Response**:
    - Success responses: 200 OK
        - example:
      ```json
      [
        {
          "id": 1,
          "symbol": "BTCUSDT",
          "bestBid": 114882.2800000000,
          "bestAsk": 114877.7000000000,
          "bestBidSource": "BINANCE",
          "bestAskSource": "HUOBI",
          "version": 76,
          "createdAt": "2025-08-24T14:55:39.163917",
          "updatedAt": "2025-08-24T15:13:42.383132"
        },
        {
          "id": 2,
          "symbol": "ETHUSDT",
          "bestBid": 4772.9300000000,
          "bestAsk": 4771.7400000000,
          "bestBidSource": "BINANCE",
          "bestAskSource": "HUOBI",
          "version": 103,
          "createdAt": "2025-08-24T14:55:39.188916",
          "updatedAt": "2025-08-24T15:14:03.090984"
        }
      ]
      ```
  - Error responses
      - 400 Bad Request
      - 500 Internal Server Error
          - example:
        ```json
        {
          "error_description": "Failed to get all prices latest"
        }
        ```
- **cURL Example**:
  ```bash
  curl --location 'localhost:8080/api/prices?page=0&size=20&sort=id&order=asc'
  ```

---
## 2. Get Price by Symbol
- **Description**: Fetches the price for a specific trading symbol.
- **Endpoint**: `/api/prices/{symbol}`
- **Method**: GET
- **Request Parameters**:
    - symbol: String - Required - Trading symbol
- **Response**:
    - Success responses: 200 OK
        - example:
          ```json
          {
            "id": 2,
            "symbol": "ETHUSDT",
            "bestBid": 4773.0700000000,
            "bestAsk": 4771.5500000000,
            "bestBidSource": "BINANCE",
            "bestAskSource": "HUOBI",
            "version": 95,
            "createdAt": "2025-08-24T14:55:39.188916",
            "updatedAt": "2025-08-24T15:12:40.522115"
          }
          ```
    - Error responses
      - 400 Bad Request
      - 500 Internal Server Error
          - example:
        ```json
        {
          "error_description": "Failed to get price by symbol"
        }
        ```
- **cURL Example**:
  ```bash
  curl --location 'localhost:8080/api/prices/ETHUSDT'
  ```

---
## 3. Execute Trade API
- **Description**: Executes a trade (BUY or SELL) for a given user.
- **Endpoint**: `/api/trades/{userId}`
- **Method**: POST
- **Request Parameters**:
    - **Path Parameter**:
        - userId: Long - Required - ID of the user executing the trade
    - **Request Body**:
      ```json
      {
        "symbol": "BTCUSDT",
        "tradeType": "BUY",
        "quantity": 0.01
      }
      ```
        - **symbol**: String - Required - Trading symbol
        - **tradeType**: String - Required - Trade type, must be either `BUY` or `SELL`
        - **quantity**: BigDecimal - Required - Amount of asset to trade

- **Response**:
    - Success responses: 200 OK
        - example:
          ```json
          {
            "id": 8,
            "symbol": "BTCUSDT",
            "tradeType": "BUY",
            "price": 114886.3000000000,
            "quantity": 0.01000000,
            "total": 1148.86300000,
            "version": 0,
            "createdAt": "2025-08-24T15:08:06.9532322",
            "updatedAt": "2025-08-24T15:08:06.9532322"
          }
          ```
    - Error responses
        - 400 Bad Request
        - 404 Not Found
        - 500 Internal Server Error
            - example:
          ```json
          {
            "error_description": "Failed to execute trade"
          }
          ```
- **cURL Example**:
  ```bash
  curl --location 'http://localhost:8080/api/trades/1' \
       --header 'Content-Type: application/json' \
       --data '{
         "symbol": "BTCUSDT",
         "tradeType": "BUY",
         "quantity": 0.01
       }'
  ```

---
## 4. Get Wallet Balances
- **Description**: Fetches all wallet balances for a given user.
- **Endpoint**: `/api/wallets/{userId}`
- **Method**: GET
- **Request Parameters**:
    - **Path Parameter**:
        - userId: Long - Required - ID of the user
- **Response**:
    - Success responses: 200 OK
        - example:
          ```json
          [
            {
              "id": 1,
              "currency": "USDT",
              "balance": 47606.9880000000
            },
            {
              "id": 10,
              "currency": "ETH",
              "balance": 0.0200000000
            },
            {
              "id": 11,
              "currency": "BTC",
              "balance": 0.0200000000
            }
          ]
          ```
    - Error responses
        - 400 Bad Request
        - 404 Not Found
        - 500 Internal Server Error
            - example:
          ```json
          {
            "error_description": "Failed to get wallet balance"
          }
          ```
- **cURL Example**:
  ```bash
  curl --location 'http://localhost:8080/api/wallets/1'
  ```

---
## 5. Trading History
- **Description**: Returns the list of trades executed by a user.
- **Endpoint**: `/api/trades`
- **Method**: GET
- **Request Parameters**:
    - **Query Parameters**:
        - userId: Long - Required - ID of the user
        - symbol: String - Optional - Filter by trading symbol
        - type: String - Optional - Filter by trade type, must be `BUY` or `SELL`
        - page: int - Optional - Default `0` - Page number
        - size: int - Optional - Default `20` - Number of records per page
        - sort: String - Optional - Default `createdAt` - Field to sort by
        - order: String - Optional - Default `asc` - Sort direction (`asc` or `desc`)

- **Response**:
    - Success responses: 200 OK
        - example:
          ```json
          [
            {
              "id": 5,
              "symbol": "ETHUSDT",
              "tradeType": "BUY",
              "price": 4764.3000000000,
              "quantity": 0.0100000000,
              "total": 47.6430000000,
              "version": 0,
              "createdAt": "2025-08-24T15:07:59.945431",
              "updatedAt": "2025-08-24T15:07:59.945431"
            },
            {
              "id": 6,
              "symbol": "ETHUSDT",
              "tradeType": "BUY",
              "price": 4764.3000000000,
              "quantity": 0.0100000000,
              "total": 47.6430000000,
              "version": 0,
              "createdAt": "2025-08-24T15:08:00.623937",
              "updatedAt": "2025-08-24T15:08:00.623937"
            }
          ]
          ```
    - Error responses
        - 400 Bad Request
        - 404 Not Found
        - 500 Internal Server Error
            - example:
          ```json
          {
            "error_description": "Failed to fetch trade history"
          }
          ```
- **cURL Example**:
  ```bash
  curl --location 'http://localhost:8080/api/trades?userId=1&page=0&size=20&sort=id&order=asc'
  ```