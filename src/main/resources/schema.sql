create table if not exists "user"
(
    id integer not null,
    name varchar(255) not null,
    email_address varchar(255) not null,
    primary key(id)
);
