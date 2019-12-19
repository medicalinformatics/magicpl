package de.mainzelliste.paths.webservice.filter;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsResponseFilter implements Filter {
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String origin = httpRequest.getHeader("Origin");
            String thisHostAndScheme = httpRequest.getScheme() + "://" + httpRequest.getHeader("Host");
            if (origin != null) {
                // TODO: Change this to part of config !!! Really fast solution
                if (origin.equals(thisHostAndScheme) || System.getenv("MAGICPL_ALLOWED_ORIGINS").contains(origin)) {
                    logger.debug("Allowing cross domain request from origin " + origin);
                    httpResponse.addHeader("Access-Control-Allow-Origin", origin);
                } else {
                    logger.info("Rejecting cross domain request from origin " + origin);
                    // For illegal origin, cancel request with 403 Forbidden.
                    HttpServletResponse resp = (HttpServletResponse) response;
                    resp.setStatus(403);
                    resp.getWriter().println("Rejecting cross domain request from origin " + origin);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
