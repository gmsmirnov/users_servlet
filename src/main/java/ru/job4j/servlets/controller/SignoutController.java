package ru.job4j.servlets.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.servlets.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Sign out controller.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 08/03/2019
 */
public class SignoutController extends HttpServlet {
    /**
     * The logger.
     */
    private static final Logger LOG = LogManager.getLogger(SignoutController.class.getName());

    /**
     * Closes current session and forward to login-page.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException - NOP
     * @throws IOException - NOP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        SignoutController.LOG.info(String.format("User: '%s' leaved the system.", session.getAttribute(Constants.ATTR_LOGIN)));
        session.invalidate();
        req.getRequestDispatcher(Constants.PAGE_JSP_LOGIN).forward(req, resp);
    }
}