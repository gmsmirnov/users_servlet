package ru.job4j.servlets;

import ru.job4j.servlets.dao.UserDao;
import ru.job4j.servlets.dao.exception.*;
import ru.job4j.servlets.dao.impl.UserDaoMemory;
import ru.job4j.servlets.model.User;

import java.util.Collection;

public class ValidateServiceStub implements Validate {
    /**
     * The singleton instance of a validate service.
     */
    private static Validate singletonValidateServiceStubInstance = new ValidateServiceStub();

    /**
     * The storage singleton instance.
     */
    private UserDao memory = UserDaoMemory.getSingletonMemoryInstance();

    /**
     * ID's counter.
     */
    private int ids = 1;

    /**
     * Default constructor.
     */
    public ValidateServiceStub() {
    }

    /**
     * Eager initialization of a validate service.
     *
     * @return the singleton instance of a validate service.
     */
    public static Validate getSingletonValidateServiceStubInstance() {
        return ValidateServiceStub.singletonValidateServiceStubInstance;
    }

    /**
     * Checks if the specified user is already exists in the storage. If not, then puts it into storage.
     *
     * @param user - the specified user.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NullArgumentException if the specified user is null.
     * @throws AlreadyExistsModelWithSuchLoginException if there already is another user with such login in database.
     * @throws NoSuchIdException if there is no role with such id in database.
     */
    @Override
    public void add(User user) throws DaoSystemException, NullArgumentException, AlreadyExistsModelWithSuchLoginException, NoSuchIdException {
        this.checkUser(user);
        if (!this.memory.containsLogin(user)) {
            user.setId(this.ids++);
            this.memory.add(user);
        } else {
            throw new AlreadyExistsModelWithSuchLoginException(String.format("User with such login already exists in database: %s.", user.getLogin()));
        }
    }

    /**
     * Checks if the specified user is in the storage. If it is, then updates it.
     *
     * @param user - the specified user.
     * @return true if successful.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NullArgumentException if the specified user is null.
     * @throws NoSuchIdException if there is no role with the specified id in the database.
     */
    @Override
    public boolean update(User user) throws DaoSystemException, NullArgumentException, NoSuchIdException {
        this.checkUser(user);
        boolean result = false;
        if (this.memory.containsKey(user)) {
            this.memory.update(user);
            result = true;
        }
        return result;
    }

    /**
     * Checks if the specified user is in the storage. If it is, then deletes it.
     *
     * @param user - the specified user.
     * @return true if successful.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NullArgumentException if the specified user is null.
     */
    @Override
    public boolean delete(User user) throws DaoSystemException, NullArgumentException {
        this.checkUser(user);
        boolean result = false;
        if (this.memory.containsKey(user)) {
            this.memory.delete(user);
            this.ids--;
            result = true;
        }
        return result;
    }

    /**
     * Gets all users from the container.
     *
     * @return a collection of all users.
     * @throws DaoSystemException - generates from SQLException.
     */
    @Override
    public Collection<User> findAll() throws DaoSystemException {
        return this.memory.findAll();
    }

    /**
     * Checks if the user with the specified id is in the storage. If it is, then gets it.
     *
     * @param id - the specified id.
     * @return a user which is mapped to the specified id. Never return null.
     * @throws DaoSystemException - generates from SQLException.
     * @throws NoSuchModelException - generates if there is no user with such id.
     */
    @Override
    public User findById(int id) throws DaoSystemException, NoSuchModelException {
        User result = null;
        if (this.memory.containsKey(id)) {
            result = this.memory.findById(id);
        }
        if (result == null) {
            throw new NoSuchModelException(String.format("No user with id: %d", id));
        }
        return result;
    }

    /**
     * Checks if the user with specified login and password is in the storage.
     *
     * @param login - the specified user's login.
     * @param password - the specified user's password.
     * @return true if the user with specified login and password is in the storage.
     */
    @Override
    public boolean isCredential(String login, String password) throws DaoSystemException {
        boolean result = false;
        for (User user : this.memory.findAll()) {
            if (login.equals(user.getLogin()) && password.equals(user.getPassword())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * If specified user is null throws DaoBusiness exception.
     *
     * @param user - the specified exception.
     * @throws NullArgumentException if the specified user is null.
     */
    private void checkUser(User user) throws NullArgumentException {
        if (user == null) {
            throw new NullArgumentException("Null user argument.");
        }
    }
}