create table account (
id BIGSERIAL not null primary key,
account_number bigint not null,
client_id BIGSERIAL not null,
balance_money numeric not null,
created_data timestamp,

CONSTRAINT fk_client_id
FOREIGN KEY (client_id)
REFERENCES client(id)
);