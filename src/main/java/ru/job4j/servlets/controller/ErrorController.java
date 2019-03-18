package ru.job4j.servlets.controller;

import ru.job4j.servlets.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Error page controller.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 26/02/2019
 */
public class ErrorController extends HttpServlet {
    /**
     * Forwards to error JSP-page.
     *
     * @param req - HTTP request.
     * @param resp - HTTP response.
     * @throws ServletException - NOP
     * @throws IOException - NOP
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(Constants.PAGE_JSP_ERROR).forward(req, resp);
    }
}