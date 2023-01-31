create table client(
id BIGSERIAL primary key,
name varchar(100) not null,
cpf varchar(11) not null,
postal_code varchar(100) not null,
street varchar(100) not null,
state varchar(100) not null,
city varchar(100) not null,
created_data timestamp
);

ALTER TABLE client ADD CONSTRAINT cpf_unique UNIQUE (cpf)