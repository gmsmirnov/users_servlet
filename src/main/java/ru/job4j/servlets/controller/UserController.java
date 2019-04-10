package ru.job4j.servlets.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.servlets.Constants;
import ru.job4j.servlets.Validate;
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
 * User controller. Checks user for any errors and redirects to appropriate page.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.1
 * @since 24/02/2019
 */
public class UserController extends HttpServlet {
    /**
     * The logger.
     */
    private static final Logger LOG = LogManager.getLogger(UserController.class.getName());

    /**
     * The logic singleton instance.
     */
    private final Validate logic = ValidateService.getSingletonValidateServiceInstance();

    /**
     * Shows user's profile (forwards request to jsp view page).
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException - NOP
     * @throws IOException - NOP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter(User.PARAM_ID);
        if (idParam != null) {
            try {
                req.setAttribute(Constants.ATTR_USER, this.logic.findById(Integer.parseInt(idParam)));
                UserController.LOG.info(String.format("Current user: '%s' requests the profile of user '%s'",
                        req.getSession().getAttribute(Constants.ATTR_LOGIN), ((User) req.getAttribute(Constants.ATTR_USER)).getLogin()));
                req.getRequestDispatcher(Constants.PAGE_JSP_USER).forward(req, resp);
            } catch (DaoSystemException | NoSuchModelException e) {
                resp.sendRedirect(String.format("%s%s", req.getContextPath(), Constants.PAGE_ERROR));
            }
        } else {
            resp.sendRedirect(String.format("%s%s", req.getContextPath(), Constants.PAGE_ERROR));
        }
    }
}