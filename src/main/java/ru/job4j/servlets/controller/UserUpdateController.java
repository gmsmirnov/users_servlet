package ru.job4j.servlets.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import javax.servlet.http.HttpSession;
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
     * The logger.
     */
    private static final Logger LOG = LogManager.getLogger(UserUpdateController.class.getName());

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
        User user = new User(
                Integer.parseInt(req.getParameter(User.PARAM_ID)),
                req.getParameter(User.PARAM_LOGIN),
                req.getParameter(User.PARAM_EMAIL),
                req.getParameter(User.PARAM_PASSWORD),
                req.getParameter(User.PARAM_COUNTRY),
                req.getParameter(User.PARAM_CITY),
                req.getParameter(User.PARAM_ROLE)
        );
        Dispatcher dispatcher = new Dispatcher(user);
        dispatcher.sent(Constants.ACTION_UPDATE);
        UserUpdateController.LOG.info(String.format("Current user: '%s' update user '%s'",
                req.getSession().getAttribute(Constants.ATTR_LOGIN), req.getParameter(User.PARAM_LOGIN)));
        HttpSession session = req.getSession();
        if (session.getAttribute(Constants.ATTR_LOGIN).equals(req.getParameter(User.PARAM_LOGIN))) {
            session.setAttribute(Constants.ATTR_CURRENT_USER, user);
        }
        this.doGet(req, resp);
    }
}