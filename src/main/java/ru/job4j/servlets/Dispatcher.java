package ru.job4j.servlets;

import ru.job4j.servlets.dao.exception.*;
import ru.job4j.servlets.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Dispatcher picks up the right logic action depending of type of POST request action param.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.1
 * @since 14/02/2019
 */
public class Dispatcher {
    /**
     * The logic singleton.
     */
    private final ValidateService logic = ValidateService.getSingletonValidateServiceInstance();

    /**
     * The POST request action params are mapped here into the logic actions handles.
     */
    private final Map<String, Function<String, Boolean>> dispatcher = new HashMap<String, Function<String, Boolean>>();

    /**
     * The handle to create the specified user.
     *
     * @param user - the specified user.
     * @return create user handle.
     */
    public Function<String, Boolean> create(User user) {
        return action -> {
            try {
                this.logic.add(user);
            } catch (DaoSystemException e) {
                e.printStackTrace();
            } catch (AlreadyExistsModelWithSuchIdException e) {
                e.printStackTrace();
            } catch (NullArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchIdException e) {
                e.printStackTrace();
            }
            return true;
        };
    }

    /**
     * The handle to update the specified user.
     *
     * @param user - the specified user.
     * @return update user handle.
     */
    public Function<String, Boolean> update(User user) {
        return action -> {
            try {
                this.logic.update(user);
            } catch (DaoSystemException e) {
                e.printStackTrace();
            } catch (NullArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchIdException e) {
                e.printStackTrace();
            }
            return true;
        };
    }

    /**
     * The handle to delete the specified user.
     *
     * @param user - the specified user.
     * @return delete user handle.
     */
    public Function<String, Boolean> delete(User user) {
        return action -> {
            try {
                this.logic.delete(user);
            } catch (DaoSystemException e) {
                e.printStackTrace();
            } catch (NullArgumentException e) {
                e.printStackTrace();
            }
            return true;
        };
    }

    /**
     * Maps the specified action to the specified handler.
     *
     * @param action - the specified action.
     * @param handle - the specified handler.
     */
    public void load(String action, Function<String, Boolean> handle) {
        this.dispatcher.put(action, handle);
    }

    /**
     * Creates the dispatcher for the specified user.
     *
     * @param user - the specified user.
     */
    public Dispatcher(User user) {
        this.load(Constants.ACTION_CREATE, create(user));
        this.load(Constants.ACTION_UPDATE, update(user));
        this.load(Constants.ACTION_DELETE, delete(user));
    }

    /**
     * Sends message to dispatcher.
     *
     * @param action - the specified action.
     * @return true if the action is in this dispatcher's map.
     */
    public boolean sent(String action) {
        return this.dispatcher.get(action).apply(action);
    }
}