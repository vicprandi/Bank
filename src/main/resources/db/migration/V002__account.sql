create table account (
id BIGSERIAL not null primary key,
account_number bigint not null,
client bigint not null,
balance_money bigint not null,
amount bigint not null,
created_data timestamp,

CONSTRAINT fk_client
FOREIGN KEY (client)
REFERENCES client(id)
)
