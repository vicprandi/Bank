create table account (
id BIGSERIAL not null primary key,
account_number bigint not null,
customer_id BIGSERIAL not null,
balance_money numeric not null,
created_data timestamp,

CONSTRAINT fk_customer_id
FOREIGN KEY (customer_id)
REFERENCES customer(id)
);