package com.tkmoya.springgradle.filter;

import com.tkmoya.springgradle.controller.MenuController;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginCheckFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.contains("edit_")) {

            HttpSession session = httpRequest.getSession();
            Object loginInfoObj = session.getAttribute(MenuController.SESSION_USER_ID);
            if (loginInfoObj == null) {
                httpResponse.sendRedirect(httpRequest.getContextPath());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
