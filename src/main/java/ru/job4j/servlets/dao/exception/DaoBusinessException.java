package ru.job4j.servlets.dao.exception;

/**
 * Business problems with data access object.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 24/02/2019
 */
public class DaoBusinessException extends DaoException {
    /**
     * Creates checked exception, based on message.
     *
     * @param message - the specified message.
     */
    public DaoBusinessException(String message) {
        super(message);
    }

    /**
     * Creates checked exception, based on another checked exception and message.
     *
     * @param message - the specified message.
     * @param cause - another checked exception.
     */
    public DaoBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}