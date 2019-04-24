insert into users (login, email, password, city, country)
values ('root', 'root@users.net', 'root', 'Moscow', 'Russia');

insert into users_roles (user_id, role_id) select users.id, roles.id from users, roles where users.login = 'root' and roles.role = 'admin';