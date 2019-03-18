package ru.job4j.servlets.dao;

import ru.job4j.servlets.dao.exception.*;
import ru.job4j.servlets.model.User;

import java.util.Collection;

/**
 * Data access object interface. Base CRUD operations.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.1
 * @since 07/02/2019
 */
public interface UserDao {
    /**
     * Adds the specified user to the container.
     *
     * @param user - the specified user.
     */
    void add(User user) throws DaoSystemException, NoSuchIdException;

    /**
     * Updates the specified user in the container.
     *
     * @param user - the specified user.
     */
    void update(User user) throws DaoSystemException, NoSuchIdException;

    /**
     * Deletes the specified user in the container.
     *
     * @param user - the specified user.
     */
    void delete(User user) throws DaoSystemException;

    /**
     * Gets a collection of all users in the storage.
     *
     * @return a collection of all users in the storage.
     */
    Collection<User> findAll() throws DaoSystemException;

    /**
     * Finds the user in the store by the specified id.
     *
     * @param id - the specified id.
     * @return the user which is mapped to the specified id.
     */
    User findById(int id) throws DaoSystemException;

    /**
     * Finds the user in the store by the specified login.
     *
     * @param login - the specified login.
     * @return the user which is mapped to the specified login.
     */
    User findByLogin(String login) throws DaoSystemException;

    /**
     * Checks if the specified user is in the container.
     *
     * @param user - the specified user.
     * @return true if the specified user exists in the container.
     */
    boolean contains(User user) throws DaoSystemException;

    /**
     * Checks if the specified user's key is used in the container.
     *
     * @param user - the specified user, which id checks.
     * @return true if the user's id is used like a key.
     */
    boolean containsKey(User user) throws DaoSystemException;

    /**
     * Checks if the specified id is used in the container.
     *
     * @param id - the specified id.
     * @return true if the id is used like a key.
     */
    boolean containsKey(int id) throws DaoSystemException;
}