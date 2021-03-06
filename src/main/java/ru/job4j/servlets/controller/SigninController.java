package ru.job4j.servlets.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.servlets.Constants;
import ru.job4j.servlets.ValidateService;
import ru.job4j.servlets.dao.exception.DaoSystemException;
import ru.job4j.servlets.dao.impl.UserDaoDb;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Sign in controller.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.1
 * @since 26/02/2019
 */
public class SigninController extends HttpServlet {
    /**
     * The logger.
     */
    private static final Logger LOG = LogManager.getLogger(SigninController.class.getName());

    /**
     * Forwards to login JSP-page.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException - NOP
     * @throws IOException - NOP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(Constants.PAGE_JSP_LOGIN).forward(req, resp);
    }

    /**
     * Checks user's login and password pair and redirects to the list of all users page. In case when login/password pair
     * is not valid sets 'error' attribute and through 'doGet()' forwards to login page.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException - NOP
     * @throws IOException - NOP
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        try {
            if (ValidateService.getSingletonValidateServiceInstance().isCredential(login, password)) {
                HttpSession session = req.getSession();
                session.setAttribute(Constants.ATTR_CURRENT_USER, UserDaoDb.getDBStoreInstance().findByLogin(login));
                session.setAttribute(Constants.ATTR_LOGIN, login);
                SigninController.LOG.info(String.format("User: '%s' entered the system.", session.getAttribute(Constants.ATTR_LOGIN)));
                resp.sendRedirect(String.format("%s%s", req.getContextPath(), Constants.PAGE_LIST));
            } else {
                req.setAttribute("error", "Credential invalid");
                SigninController.LOG.info("Credential invalid.");
                this.doGet(req, resp);
            }
        } catch (DaoSystemException e) {
            SigninController.LOG.error(e.getMessage(), e);
        }
    }
}