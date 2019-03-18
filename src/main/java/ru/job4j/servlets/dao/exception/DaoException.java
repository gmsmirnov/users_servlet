package ru.job4j.servlets.dao.exception;

/**
 * Basic exception for DAO.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 24/02/2019
 */
public class DaoException extends Exception {
    /**
     * Creates checked exception, based on message.
     *
     * @param message - the specified message.
     */
    public DaoException(String message) {
        super(message);
    }

    /**
     * Creates checked exception, based on another checked exception and message.
     *
     * @param message - the specified message.
     * @param cause - another checked exception.
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}