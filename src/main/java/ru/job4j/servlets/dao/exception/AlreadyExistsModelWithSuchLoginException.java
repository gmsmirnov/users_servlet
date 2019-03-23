package ru.job4j.servlets.dao.exception;

/**
 * There is another model with the specified login in the storage.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 23/03/2019
 */
public class AlreadyExistsModelWithSuchLoginException extends DaoBusinessException {
    /**
     * Creates checked exception, based on message.
     *
     * @param message - the specified message.
     */
    public AlreadyExistsModelWithSuchLoginException(String message) {
        super(message);
    }

    /**
     * Creates checked exception, based on another checked exception and message.
     *
     * @param message - the specified message.
     * @param cause - another checked exception.
     */
    public AlreadyExistsModelWithSuchLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
