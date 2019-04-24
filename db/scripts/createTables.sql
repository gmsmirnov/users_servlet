-- main tables

create table if not exists users (
	id serial primary key,
	login varchar(30),
	email varchar(50),
	password varchar(30),
	country varchar(30),
	city varchar(30)
);

create table if not exists roles (
	id serial primary key,
	role varchar(30)
);

--help tables

create table if not exists users_roles (
	id serial primary key,
	user_id int references users(id),
	role_id int references roles(id)
);