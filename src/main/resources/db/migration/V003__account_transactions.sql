create table account_transactions (
id BIGSERIAL not null primary key,
account_id BIGSERIAL not null,
value numeric not null,
transaction_type varchar(50) not null,

CONSTRAINT fk_account_id
FOREIGN KEY (account_id)
REFERENCES account(id)
);