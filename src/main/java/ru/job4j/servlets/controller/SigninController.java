package ru.job4j.servlets.controller;

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
 * @version 1.0
 * @since 26/02/2019
 */
public class SigninController extends HttpServlet {
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
                session.setAttribute("login", login);
                resp.sendRedirect(String.format("%s%s", req.getContextPath(), Constants.PAGE_LIST));
            } else {
                req.setAttribute("error", "Credential invalid");
                this.doGet(req, resp);
            }
        } catch (DaoSystemException e) {
            e.printStackTrace();
        }
    }
}