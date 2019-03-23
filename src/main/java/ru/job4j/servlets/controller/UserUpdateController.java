package ru.job4j.servlets.controller;

import ru.job4j.servlets.Constants;
import ru.job4j.servlets.Dispatcher;
import ru.job4j.servlets.ValidateService;
import ru.job4j.servlets.dao.exception.DaoSystemException;
import ru.job4j.servlets.dao.exception.NoSuchModelException;
import ru.job4j.servlets.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for updating a user.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.3
 * @since 16/02/2019
 */
public class UserUpdateController extends HttpServlet {
    /**
     * The logic singleton instance.
     */
    private final ValidateService logic = ValidateService.getSingletonValidateServiceInstance();

    /**
     * Shows filled form to update the specified user. The GET request. Forwards the request to jsp view page.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException NOP
     * @throws IOException NOP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter(User.PARAM_ID);
        if (idParam != null) {
            try {
                req.setAttribute(Constants.ATTR_USER, this.logic.findById(Integer.parseInt(idParam)));
                req.getRequestDispatcher(Constants.PAGE_JSP_UPDATE).forward(req, resp);
            } catch (DaoSystemException | NoSuchModelException e) {
                resp.sendRedirect(String.format("%s%s", req.getContextPath(), Constants.PAGE_ERROR));
            }
        }
    }

    /**
     * Update a user request. The POST request.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException NOP
     * @throws IOException NOP
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Dispatcher dispatcher = new Dispatcher(new User(
                Integer.parseInt(req.getParameter(User.PARAM_ID)),
                req.getParameter(User.PARAM_LOGIN),
                req.getParameter(User.PARAM_EMAIL),
                req.getParameter(User.PARAM_PASSWORD),
                req.getParameter(User.PARAM_COUNTRY),
                req.getParameter(User.PARAM_CITY),
                req.getParameter(User.PARAM_ROLE)
        ));
        dispatcher.sent(Constants.ACTION_UPDATE);
        this.doGet(req, resp);
    }
}