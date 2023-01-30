create table account(
id BIGSERIAL not null primary key,
accountNumber bigint not null,
balanceMoney bigint not null,
amount bigint not null,
createdData timestamp
);
