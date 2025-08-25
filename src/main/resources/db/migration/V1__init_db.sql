create table if not exists user (
    id                  bigint auto_increment primary key,
    username            varchar(64)  not null,
    name                varchar(100) not null,
    email               varchar(254) not null,
    version             bigint       not null,
    created_at          timestamp    not null default current_timestamp,
    updated_at          timestamp    not null default current_timestamp,
    constraint uk_user_username unique (username),
    constraint uk_user_email unique (email)
    );

create table if not exists aggregated_price (
    id                  bigint auto_increment primary key,
    symbol              varchar(16)     not null,
    best_bid            decimal(38,10)  not null,
    best_ask            decimal(38,10)  not null,
    best_bid_source     varchar(16),
    best_ask_source     varchar(16),
    version             bigint      not null,
    created_at          timestamp   not null default current_timestamp,
    updated_at          timestamp   not null default current_timestamp,
    constraint uk_agg_symbol unique (symbol)
    );

create table if not exists wallet_balance (
    id                  bigint auto_increment primary key,
    user_id             bigint          not null,
    currency            varchar(16)     not null,
    balance             decimal(38,10)  not null default 0,
    version             bigint          not null,
    created_at          timestamp       not null default current_timestamp,
    updated_at          timestamp       not null default current_timestamp,
    constraint uk_wallet_user_currency unique (user_id, currency),
    constraint fk_wallet_user foreign key (user_id) references user(id)
    );

create index if not exists idx_wallet_user on wallet_balance(user_id);

create table if not exists trade (
    id                  bigint auto_increment primary key,
    user_id             bigint          not null,
    symbol              varchar(16)     not null,
    type                varchar(8)      not null,
    price               decimal(38,10)  not null,
    quantity            decimal(38,10)  not null,
    total               decimal(38,10)  not null,
    version             bigint          not null,
    created_at          timestamp       not null default current_timestamp,
    updated_at          timestamp       not null default current_timestamp,
    constraint fk_trade_user foreign key (user_id) references user(id)
    );

-- support filtering by user_id and then order by created_at
create index if not exists idx_trade_user_created on trade(user_id, created_at desc);

-- support filtering by user + symbol + created_at
create index if not exists idx_trade_user_symbol_created on trade(user_id, symbol, created_at desc);

-- support filtering by user + type + created_at
create index if not exists idx_trade_user_type_created on trade(user_id, type, created_at desc);
