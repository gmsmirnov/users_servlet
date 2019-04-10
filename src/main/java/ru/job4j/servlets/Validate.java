package ru.job4j.servlets;

import ru.job4j.servlets.dao.exception.*;
import ru.job4j.servlets.model.User;

import java.util.Collection;

/**
 * Validate service interface for user servlet.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 02/04/2019
 */
public interface Validate {
    /**
     * Checks if the specified user is already exists in the storage. If not, then puts it into storage.
     *
     * @param user - the specified user.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NullArgumentException if the specified user is null.
     * @throws AlreadyExistsModelWithSuchLoginException if there already is another user with such login in database.
     * @throws NoSuchIdException if there is no role with such id in database.
     */
    void add(User user) throws DaoSystemException, NullArgumentException, AlreadyExistsModelWithSuchLoginException, NoSuchIdException;

    /**
     * Checks if the specified user is in the storage. If it is, then updates it.
     *
     * @param user - the specified user.
     * @return true if successful.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NullArgumentException if the specified user is null.
     * @throws NoSuchIdException if there is no role with the specified id in the database.
     */
    boolean update(User user) throws DaoSystemException, NullArgumentException, NoSuchIdException;

    /**
     * Checks if the specified user is in the storage. If it is, then deletes it.
     *
     * @param user - the specified user.
     * @return true if successful.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NullArgumentException if the specified user is null.
     */
    boolean delete(User user) throws DaoSystemException, NullArgumentException;

    /**
     * Gets all users from the container.
     *
     * @return a collection of all users.
     * @throws DaoSystemException - generates from SQLException.
     */
    Collection<User> findAll() throws DaoSystemException;

    /**
     * Checks if the user with the specified id is in the storage. If it is, then gets it.
     *
     * @param id - the specified id.
     * @return a user which is mapped to the specified id. Never return null.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NoSuchModelException - generates if there is no user with such id.
     */
    User findById(int id) throws DaoSystemException, NoSuchModelException;

    /**
     * Checks if the user with specified login and password is in the storage.
     *
     * @param login - the specified user's login.
     * @param password - the specified user's password.
     * @return true if the user with specified login and password is in the storage.
     */
    boolean isCredential(String login, String password) throws DaoSystemException;
}