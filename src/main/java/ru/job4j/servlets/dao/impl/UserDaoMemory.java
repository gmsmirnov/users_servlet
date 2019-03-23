package ru.job4j.servlets.dao.impl;

import ru.job4j.servlets.dao.UserDao;
import ru.job4j.servlets.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a memory storage. The memory storage is a hash-map.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.3
 * @since 07/02/2019
 */
public class UserDaoMemory implements UserDao {
    /**
     * Thread sage hash map is a storage for users.
     */
    private final Map<Integer, User> storage = new ConcurrentHashMap<Integer, User>();

    /**
     * The singleton instance of the storage.
     */
    private static UserDaoMemory singletonMemoryInstance = new UserDaoMemory();

    /**
     * Default constructor.
     */
    private UserDaoMemory() {
    }

    /**
     * Eager initialization of the storage singleton instance.
     *
     * @return the singleton instance of the storage.
     */
    public static UserDaoMemory getSingletonMemoryInstance() {
        return UserDaoMemory.singletonMemoryInstance;
    }

    /**
     * Puts the specified value into the storage.
     *
     * @param user - the specified value.
     */
    @Override
    public void add(User user) {
        this.storage.put(user.getId(), user);
    }

    /**
     * Replaces the specified value in the storage.
     *
     * @param user - the specified value.
     */
    @Override
    public void update(User user) {
        this.storage.put(user.getId(), user);
    }

    /**
     * Deletes the specified user in the storage.
     *
     * @param user - the specified user.
     */
    @Override
    public void delete(User user) {
        this.storage.remove(user.getId());
    }

    /**
     * Gets a collection of all users in the storage.
     *
     * @return a collection of all users in the storage.
     */
    @Override
    public Collection<User> findAll() {
        return this.storage.values();
    }

    /**
     * Finds the user in the store by the specified id.
     *
     * @param id - the specified id.
     * @return the user which is mapped to the specified id.
     */
    @Override
    public User findById(int id) {
        return this.storage.get(id);
    }

    /**
     * Checks if the specified user is in the container.
     *
     * @param user - the specified user.
     * @return true if the specified user exists in the container.
     */
    @Override
    public boolean contains(User user) {
        return this.storage.containsValue(user);
    }

    /**
     * Checks if the specified user's key is used in the map.
     *
     * @param user - the specified user, which id checks.
     * @return true if the user's id is used like a key.
     */
    @Override
    public boolean containsKey(User user) {
        return this.storage.containsKey(user.getId());
    }

    /**
     * Checks if the specified id is used in the map.
     *
     * @param id - the specified id.
     * @return true if the id is used like a key.
     */
    @Override
    public boolean containsKey(int id) {
        return this.storage.containsKey(id);
    }

    /**
     * Checks if the user's login is already used.
     *
     * @param user - the specified user which login checks.
     * @return true if the user's login is used.
     */
    @Override
    public boolean containsLogin(User user) {
        return false;
    }

    /**
     * Finds user by login.
     *
     * @param login - the specified login.
     * @return the user with the specified login.
     */
    @Override
    public User findByLogin(String login) {
        return null;
    }
}