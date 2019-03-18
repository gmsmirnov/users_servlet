package ru.job4j.servlets.dao.impl;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.servlets.dao.UserDao;
import ru.job4j.servlets.dao.exception.DaoSystemException;
import ru.job4j.servlets.dao.exception.NoSuchIdException;
import ru.job4j.servlets.model.User;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of a Postrgres database storage.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.2
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

    public static final String COLUMN_LABEL_ROLE = "role";

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
     * Puts the specified value into the storage.
     *
     * @param user - the specified value.
     * @throws DaoSystemException if SQLException occurs.
     */
    @Override
    public void add(User user) throws DaoSystemException, NoSuchIdException {
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            this.insertIntoTables(connection, user);
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

    private void insertIntoTables(Connection connection, User user) throws DaoSystemException, NoSuchIdException {
        try {
            connection.setAutoCommit(false);
            this.insertToUsersTable(connection, user);
            this.insertToUsersRolesTable(connection, user.getId(), this.findRoleId(user.getRole()));
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

    private void insertToUsersTable(Connection connection, User user) throws DaoSystemException {
        try (PreparedStatement statement = connection.prepareStatement(
                     "insert into users(id, login, email, password, country, city)"
                             + " values(?, ?, ?, ?, ?, ?);")) {
            statement.setInt(1, user.getId());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getCountry());
            statement.setString(6, user.getCity());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

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

    private int findRoleId(String role) throws DaoSystemException, NoSuchIdException {
        int id;
        try (Connection connection = UserDaoDb.SOURCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "select id from roles where role = ?;"
             )) {
            statement.setString(1, role);
            try (ResultSet rslSet = statement.executeQuery()) {
                if (rslSet.next()) {
                    id = rslSet.getInt("id");
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
     * @param user - the specified value.
     * @throws DaoSystemException if SQLException occurs.
     */
    @Override
    public void update(User user) throws DaoSystemException, NoSuchIdException {
        try (Connection connection = UserDaoDb.SOURCE.getConnection()) {
            this.updateTables(connection, user);
        } catch (SQLException e) {
            throw new DaoSystemException(e.getMessage(), e);
        }
    }

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
                            String.format("%d", rslSet.getInt(User.PARAM_ID)),
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
                            String.format("%d", id),
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
                            String.format("%d", rslSet.getInt(User.PARAM_ID)),
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