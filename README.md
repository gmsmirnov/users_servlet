[![Build Status](https://travis-ci.org/gmsmirnov/users_servlet.svg?branch=features)](https://travis-ci.org/gmsmirnov/users_servlet)
[![codecov](https://codecov.io/gh/gmsmirnov/users_servlet/branch/features/graph/badge.svg)](https://codecov.io/gh/gmsmirnov/users_servlet)
# users_servlet
Простое приложение для управления пользователями.

Реализован вход через логи/пароль и доступ к базе данных пользователей. 
Для роли "Администратор" доступны действия с добавлением, просмотром, модификацией и удалением любых пользователей.
Обычные пользователи не могут модифицировать роль, не могут добавлять администраторов, удалить могут только себя.
При удалении самого себя происходит закрытие сессии и редирект на страницу входа.
Все пользователи хранятся в базе данных PostgresSQL.
