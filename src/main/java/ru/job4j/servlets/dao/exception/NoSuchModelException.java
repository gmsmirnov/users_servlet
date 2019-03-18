package ru.job4j.servlets.dao.exception;

/**
 * There is no model with specified id in the storage.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 24/02/2019
 */
public class NoSuchModelException extends DaoBusinessException {
    /**
     * Creates checked exception, based on message.
     *
     * @param message - the specified message.
     */
    public NoSuchModelException(String message) {
        super(message);
    }

    /**
     * Creates checked exception, based on another checked exception and message.
     *
     * @param message - the specified message.
     * @param cause - another checked exception.
     */
    public NoSuchModelException(String message, Throwable cause) {
        super(message, cause);
    }
}