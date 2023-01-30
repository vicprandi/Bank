create table client(
id bigint primary key,
name varchar(100) not null,
cpf varchar(11) not null,
postalCode varchar(100) not null,
street varchar(100) not null,
state varchar(100) not null,
city varchar(100) not null,
createdData timestamp
);
