package ru.job4j.servlets.filter;

import ru.job4j.servlets.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Auth in filter.
 *
 * @author Gregory Smirnov (artress@ngs.ru)
 * @version 1.0
 * @since 26/02/2019
 */
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        if (req.getRequestURI().contains(Constants.PAGE_LOGIN)) {
            filterChain.doFilter(req, resp);
        } else {
            HttpSession session = req.getSession();
            synchronized (session) {
                if (session.getAttribute("login") == null) {
                    resp.sendRedirect(String.format("%s%s", req.getContextPath(), Constants.PAGE_LOGIN));
                    return;
                }
            }
            filterChain.doFilter(req, resp);
        }
    }
}