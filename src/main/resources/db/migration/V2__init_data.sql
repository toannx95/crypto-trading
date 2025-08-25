-- Insert user
INSERT INTO user (username, name, email, version)
VALUES ('toan', 'Toan Nguyen', 'toan@gmail.com', 0);

INSERT INTO user (username, name, email, version)
VALUES ('demo', 'Demo Account', 'demo@gmail.com', 0);

-- Ensure wallet_balance for that user has 50,000 USDT
INSERT INTO wallet_balance (user_id, currency, balance, version)
VALUES (
           (SELECT id FROM user WHERE username = 'toan'),
           'USDT',
           50000.00,
           0
       );

INSERT INTO wallet_balance (user_id, currency, balance, version)
VALUES (
           (SELECT id FROM user WHERE username = 'demo'),
           'USDT',
           50000.00,
           0
       );