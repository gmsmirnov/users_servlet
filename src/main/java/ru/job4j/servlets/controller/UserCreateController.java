package ru.job4j.servlets.controller;

import ru.job4j.servlets.Constants;
import ru.job4j.servlets.Dispatcher;
import ru.job4j.servlets.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for creating a new user.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.2
 * @since 16/02/2019
 */
public class UserCreateController extends HttpServlet {
    /**
     * Shows empty form to create a new user. The GET request.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException NOP
     * @throws IOException NOP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(Constants.PAGE_JSP_CREATE).forward(req, resp);
    }

    /**
     * Creates a new user request. The POST request. After creation forwards to jsp page.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException NOP
     * @throws IOException NOP
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Dispatcher dispatcher = new Dispatcher(new User(
                req.getParameter(User.PARAM_ID),
                req.getParameter(User.PARAM_LOGIN),
                req.getParameter(User.PARAM_EMAIL),
                req.getParameter(User.PARAM_PASSWORD),
                req.getParameter(User.PARAM_COUNTRY),
                req.getParameter(User.PARAM_CITY),
                req.getParameter(User.PARAM_ROLE)
        ));
        dispatcher.sent(Constants.ACTION_CREATE);
        this.doGet(req, resp);
    }
}