package ru.job4j.servlets.dao.impl;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.servlets.dao.UserDao;
import ru.job4j.servlets.dao.exception.DaoSystemException;
import ru.job4j.servlets.dao.exception.NoSuchIdException;
import ru.job4j.servlets.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of a Postrgres database storage.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.3
 * @since 21/02/2019
 */
public class UserDaoDb implements UserDao {
    /**
     * The connections' pool.
     */
    private static final BasicDataSource SOURCE = new BasicDataSource();

    /**
     * The singleton instance of the storage.
     */
    private static final UserDaoDb INSTANCE = new UserDaoDb();

    /**
     * The constant for result table column, named 'role'.
     */
    public static final String COLUMN_LABEL_ROLE = "role";

    /**
     * The constant for result table column, named 'id'.
     */
    public static final String COLUMN_LABEL_ID = "id";

    /**
     * Default constructor.
     */
    public UserDaoDb() {
        UserDaoDb.SOURCE.setUrl("jdbc:postgresql://127.0.0.1:5432/users");
        UserDaoDb.SOURCE.setUsername("postgres");
        UserDaoDb.SOURCE.setPassword("postgres");
        UserDaoDb.SOURCE.setDriverClassName("org.postgresql.Driver");
        UserDaoDb.SOURCE.setMinIdle(5);
        UserDaoDb.SOURCE.setMaxIdle(10);
        UserDaoDb.SOURCE.setMaxOpenPreparedStatements(100);
        if (!this.isStructure()) {
            this.createStructure();
            try {
                this.initDataBase();
            } catch (DaoSystemException | NoSuchIdException e) {
                /*NOP*/
            }
        }
    }

    /**
     * Eager initialization of the storage singleton instance.
     *
     * @return the singleton instance of the storage.
     */
    public static UserDaoDb getDBStoreInstance() {
        return UserDaoDb.INSTANCE;
    }

    /**
     * Checks database structure.
     *
     * @return true if the structure exists.
     */
    public boolean isStructure() {
        boolean result = false;
        ArrayList<String> tables = new ArrayList<String>();
        try (Connection connection = UserDaoDb.SOURCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                "select table_name from information_schema.tables "
                        + "where table_schema='public' order by table_name;")) {
            try (ResultSet rslSet = statement.executeQuery()) {
                while (rslSet.next()) {
                    tables.add(rslSet.getString("table_name"));
                }
            }
            if (tables.contains("roles") && tables.contains("users") && tables.contains("users_roles")) {
                result = true;
            }
        } catch (SQLException e) {
            /*NOP*/
        }
        return result;
    }

    /**
     * Creates tables structure if it not exists.
     */
    private void createStructure() {
        try (Connection connection = UserDaoDb.SOURCE.getConnection();
             PreparedStatement tableUsers = connection.prepareStatement(
                     "create table if not exists users("
                             + "id serial primary key,"
                             + "login varchar(30),"
                             + "email varchar(50),"
                             + "password varchar(30),"
                             + "country varchar(30),"
                             + "city varchar(30)"
                             + ");"
             );
                PreparedStatement tableRoles = connection.prepareStatement(
                        "create table if not exists roles("
                                + "id serial primary key,"
                                + "role varchar(30)"
                                + ");"
                );
             PreparedStatement helpTable = connection.prepareStatement(
                     "create table if not exists users_roles("
                             + "id serial primary key,"
                             + "user_id int references users(id),"
                             + "role_id int references roles(id)"
                             + ");"
             )) {
            tableUsers.executeUpdate();
            tableRoles.executeUpdate();
            helpTable.executeUpdate();
        } catch (SQLException e) {
            /*NOP*/
        }
    }

    /**
     * Initiates empty database. Inserts roles 'admin', 'user'. Inserts 'root'-user with password 'root'.
     *
     * @throws DaoSystemException if SQLException occurs.
     * @throws NoSuchIdException if there is no role with such id in database.
     */
    private void initDataBase() throws DaoSystemException, NoSuchIdException {
        this.addRole("admin");
        this.addRole("user");
        this.add(new User("root", "root@root.net", "root", "RF", "Moscow", "admin"));
    }

    /**
     * Adds a role into role's table of the database.
     *
     * @param role - the specified role's name.
     * @throws DaoSystemException if SQLException occurs.
     */
    public void addRole(String role) throws DaoSystemException {
        try (Connection connection = UserDaoDb.SOURCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "insert into roles (role) values (?);"
             )) {
            statement.setString(1, role);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Puts the specified user into the storage.
     *
     * @param user - the specified value.
     * @throws DaoSystemException if SQLException occurs.
     * @throws NoSuchIdException if there is no role with such id in database.
     */
    @Override
    public void add(User user) throws DaoSystemException, NoSuchIdException {
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            this.insertIntoTables(connection, user);
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'add'-method. Inserts values into user table and into help-table ('users_roles').
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param user - the specified user.
     * @throws DaoSystemException if SQLException occurs.
     * @throws NoSuchIdException if there is no role with such id in database.
     */
    private void insertIntoTables(Connection connection, User user) throws DaoSystemException, NoSuchIdException {
        try {
            connection.setAutoCommit(false);
            int id = this.insertToUsersTable(connection, user);
            this.insertToUsersRolesTable(connection, id, this.findRoleId(user.getRole()));
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new DaoSystemException(e1.getMessage(), e1);
            }
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'insertIntoTables'-method. Inserts value into 'users' table.
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param user - the specified user.
     * @return new user's id.
     * @throws DaoSystemException if SQLException occurs.
     */
    private int insertToUsersTable(Connection connection, User user) throws DaoSystemException {
        int result = -1;
        try (PreparedStatement statement = connection.prepareStatement(
                     "insert into users(login, email, password, country, city)"
                             + " values(?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getCountry());
            statement.setString(5, user.getCity());
            statement.executeUpdate();
            try (ResultSet rslSet = statement.getGeneratedKeys()) {
                if (rslSet.next()) {
                    result = rslSet.getInt(UserDaoDb.COLUMN_LABEL_ID);
                }
            }
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
        if (result == -1) {
            throw new DaoSystemException("Users id id '-1'.");
        }
        return result;
    }

    /**
     * Called from 'insertIntoTables'-method. Inserts value to help-table ('users_roles' table). The relation
     * between users and roles.
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param userId - the specified user's id.
     * @param roleId - the specified role's id.
     * @throws DaoSystemException if SQLException occurs.
     */
    private void insertToUsersRolesTable(Connection connection, int userId, int roleId) throws DaoSystemException {
        try (PreparedStatement statement = connection.prepareStatement(
                     "insert into users_roles(user_id, role_id) values(?, ?)")) {
            statement.setInt(1, userId);
            statement.setInt(2, roleId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Help method which finds the specified role's id in the 'roles' table. Never returns '0' or other wrong id.
     *
     * @param role - the specified role.
     * @return the specified role's id.
     * @throws DaoSystemException if SQLException occurs.
     * @throws NoSuchIdException if there is no role with such id in the 'roles'-table.
     */
    private int findRoleId(String role) throws DaoSystemException, NoSuchIdException {
        int id;
        try (Connection connection = UserDaoDb.SOURCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select id from roles where role = ?;"
             )) {
            statement.setString(1, role);
            try (ResultSet rslSet = statement.executeQuery()) {
                if (rslSet.next()) {
                    id = rslSet.getInt(UserDaoDb.COLUMN_LABEL_ID);
                } else {
                    throw new NoSuchIdException("No role with such id");
                }
            }
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
        return id;
    }

    /**
     * Replaces the specified value in the storage.
     *
     * @param user - the specified updated user.
     * @throws DaoSystemException if SQLException occurs.
     * @throws NoSuchIdException if there is no role with such id in database.
     */
    @Override
    public void update(User user) throws DaoSystemException, NoSuchIdException {
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            this.updateTables(connection, user);
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'update'-method. Updates values in the users table and in the help-table ('users_roles').
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param user - the specified, updated user.
     * @throws DaoSystemException if SQLException occurs.
     * @throws NoSuchIdException if there is no role with such id in database.
     */
    private void updateTables(Connection connection, User user) throws DaoSystemException, NoSuchIdException {
        try {
            connection.setAutoCommit(false);
            this.updateUsersTable(connection, user);
            this.updateUsersRolesTable(connection, user.getId(), this.findRoleId(user.getRole()));
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new DaoSystemException(e1.getMessage(), e1);
            }
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'updateTables'-method. Updates value in the 'users' table.
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param user - the specified user.
     * @throws DaoSystemException if SQLException occurs.
     */
    private void updateUsersTable(Connection connection, User user) throws DaoSystemException {
        try (PreparedStatement statement = connection.prepareStatement(
                "update users set login = ?, email = ?, password = ?, country = ?, city = ? where id = ?;")) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getCountry());
            statement.setString(5, user.getCity());
            statement.setInt(6, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'updateTables'-method. Updates value of help-table ('users_roles' table). The relation
     * between users and roles.
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param userId - the specified user's id.
     * @param roleId - the specified role's id.
     * @throws DaoSystemException if SQLException occurs.
     */
    private void updateUsersRolesTable(Connection connection, int userId, int roleId) throws DaoSystemException {
        try (PreparedStatement statement = connection.prepareStatement(
                "update users_roles set role_id = ? where user_id = ?")) {
            statement.setInt(1, roleId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Deletes the specified user in the storage.
     *
     * @param user - the specified user.
     * @throws DaoSystemException if SQLException occurs.
     */
    @Override
    public void delete(User user) throws DaoSystemException {
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            this.deleteFromTables(connection, user);
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'delete'-method. Deletes value from user table and from help-table ('users_roles').
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param user - the specified user to delete.
     * @throws DaoSystemException if SQLException occurs.
     */
    private void deleteFromTables(Connection connection, User user) throws DaoSystemException {
        try {
            connection.setAutoCommit(false);
            this.deleteFromUsersRolesTable(connection, user.getId());
            this.deleteFromUsersTable(connection, user);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new DaoSystemException(e1.getMessage(), e1);
            }
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'deleteFromTables'-method. Deletes value of help-table ('users_roles' table). The relation
     * between users and roles.
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param id - the specified user's id.
     * @throws DaoSystemException if SQLException occurs.
     */
    private void deleteFromUsersRolesTable(Connection connection, int id) throws DaoSystemException {
        try (PreparedStatement statement = connection.prepareStatement(
                    "delete from users_roles where user_id = ?;"
            )) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Called from 'deleteFromTables'-method. Deletes value in the 'users' table.
     *
     * @param connection - the specified connection, to rollback the transaction if an error occurs.
     * @param user - the specified user.
     * @throws DaoSystemException if SQLException occurs.
     */
    private void deleteFromUsersTable(Connection connection, User user) throws DaoSystemException {
        try (PreparedStatement statement = connection.prepareStatement(
                    "delete from users where id = ?;"
            )) {
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    /**
     * Gets a collection of all users in the storage.
     *
     * @return a collection of all users in the storage. Never return null.
     * @throws DaoSystemException if SQLException occurs.
     */
    @Override
    public Collection<User> findAll() throws DaoSystemException {
        List<User> result = new LinkedList<User>();
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("select * from users;");
            try (ResultSet rslSet = statement.getResultSet()) {
                while (rslSet.next()) {
                    result.add(new User(
                            rslSet.getInt(User.PARAM_ID),
                            rslSet.getString(User.PARAM_LOGIN),
                            rslSet.getString(User.PARAM_EMAIL),
                            rslSet.getString(User.PARAM_PASSWORD),
                            rslSet.getString(User.PARAM_COUNTRY),
                            rslSet.getString(User.PARAM_CITY),
                            this.findRoleByUserLogin(rslSet.getString(User.PARAM_LOGIN))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Finds the user in the store by the specified id.
     *
     * @param id - the specified id.
     * @return the user which is mapped to the specified id. Never return null.
     * @throws DaoSystemException - generates from SQLException.
     */
    @Override
    public User findById(int id) throws DaoSystemException {
        return this.tryGetUserFromDb(id);
    }

    /**
     * Finds the user in the store by the specified login.
     *
     * @param login - the specified login.
     * @return the user which is mapped to the specified login. Never return null.
     * @throws DaoSystemException - generates from SQLException.
     */
    @Override
    public User findByLogin(String login) throws DaoSystemException {
        return this.tryGetUserFromDb(login);
    }

    /**
     * Checks if the specified user is in the container.
     *
     * @param user - the specified user.
     * @return true if the specified user exists in the container.
     * @throws DaoSystemException if SQLException occurs.l.
     */
    @Override
    public boolean contains(User user) throws DaoSystemException {
        boolean result = false;
        if (user.equals(this.tryGetUserFromDb(user.getId()))) {
            result = true;
        }
        return result;
    }

    /**
     * Checks if the specified user's key is used in the storage.
     *
     * @param user - the specified user, which id checks.
     * @return true if the user's id is used like a key.
     * @throws DaoSystemException if SQLException occurs.
     */
    @Override
    public boolean containsKey(User user) throws DaoSystemException {
        return this.tryGetUserFromDb(user.getId()) != null;
    }

    /**
     * Checks if the specified id is used in the storage.
     *
     * @param id - the specified id.
     * @return true if the id is used like a key.
     * @throws DaoSystemException if SQLException occurs.
     */
    @Override
    public boolean containsKey(int id) throws DaoSystemException {
        return this.tryGetUserFromDb(id) != null;
    }

    /**
     * Checks if the user's login is used in the storage.
     *
     * @param user - the specified user.
     * @return true if the login is already used.
     * @throws DaoSystemException if SQLException occurs.
     */
    @Override
    public boolean containsLogin(User user) throws DaoSystemException {
        return this.tryGetUserFromDb(user.getLogin()) != null;
    }

    /**
     * Tries to get the user with the specified id from database.
     *
     * @param id - the specified user's id.
     * @return the user with the specified id or null.
     * @throws DaoSystemException if SQLException occurs.
     */
    private User tryGetUserFromDb(int id) throws DaoSystemException {
        User result = null;
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select login, email, password, country, city from users where id = ?;");
            statement.setInt(1, id);
            try (ResultSet rslSet = statement.executeQuery()) {
                if (rslSet.next()) {
                    result = new User(
                            id,
                            rslSet.getString(User.PARAM_LOGIN),
                            rslSet.getString(User.PARAM_EMAIL),
                            rslSet.getString(User.PARAM_PASSWORD),
                            rslSet.getString(User.PARAM_COUNTRY),
                            rslSet.getString(User.PARAM_CITY),
                            this.findRoleByUserLogin(rslSet.getString(User.PARAM_LOGIN))
                    );
                }
            }
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Tries to get the user with the specified login from database.
     *
     * @param login - the specified user's login.
     * @return the user with the specified id or null.
     * @throws DaoSystemException if SQLException occurs.
     */
    private User tryGetUserFromDb(String login) throws DaoSystemException {
        User result = null;
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("select id, login, email, password, country, city from users where login = ?;");
            statement.setString(1, login);
            try (ResultSet rslSet = statement.executeQuery()) {
                if (rslSet.next()) {
                    result = new User(
                            rslSet.getInt(User.PARAM_ID),
                            rslSet.getString(User.PARAM_LOGIN),
                            rslSet.getString(User.PARAM_EMAIL),
                            rslSet.getString(User.PARAM_PASSWORD),
                            rslSet.getString(User.PARAM_COUNTRY),
                            rslSet.getString(User.PARAM_CITY),
                            this.findRoleByUserLogin(rslSet.getString(User.PARAM_LOGIN))
                    );
                }
            }
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Finds user's role by its login.
     *
     * @param login - the specified login.
     * @return the user's role.
     * @throws DaoSystemException if SQLException occurs.
     */
    private String findRoleByUserLogin(String login) throws DaoSystemException {
        String result = "";
        try (Connection connection = UserDaoDb.SOURCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select role from\n"
                             + "(select roles.role, users.login from users_roles \n"
                             + "left join roles on users_roles.role_id = roles.id\n"
                             + "left join users on users_roles.user_id = users.id)\n"
                             + " as mapping where login = ?;"
             )) {
            statement.setString(1, login);
            try (ResultSet rslSet = statement.executeQuery()) {
                if (rslSet.next()) {
                    result = rslSet.getString(UserDaoDb.COLUMN_LABEL_ROLE);
                }
            }
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Finds list of role's users by its name.
     *
     * @param role - the specified role.
     * @return the list of role's users.
     * @throws DaoSystemException if SQLException occurs.
     */
    private List<User> findUsersByRoleName(String role) throws DaoSystemException {
        List<User> result = new LinkedList<User>();
        try (Connection connection = UserDaoDb.SOURCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select user from\n"
                             + "(select roles.role, users.login from users_roles \n"
                             + "left join roles on users_roles.role_id = roles.id\n"
                             + "left join users on users_roles.user_id = users.id)\n"
                             + " as mapping where role = ?;"
             )) {
            statement.setString(1, role);
            try (ResultSet rslSet = statement.executeQuery()) {
                while (rslSet.next()) {
                    result.add(new User(
                            String.format("%d", rslSet.getInt(User.PARAM_ID)),
                            rslSet.getString(User.PARAM_LOGIN),
                            rslSet.getString(User.PARAM_EMAIL),
                            rslSet.getString(User.PARAM_PASSWORD),
                            rslSet.getString(User.PARAM_COUNTRY),
                            rslSet.getString(User.PARAM_CITY)
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
        return result;
    }
}