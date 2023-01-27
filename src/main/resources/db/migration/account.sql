create table account(
id BIGSERIAL not null primary key,
accountNumber bigint not null,
client_cpf bigint not null,
created_at timestamp
);

