package news.analytics.container.filter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        System.out.println("Inside authentication filter : " + req.getMethod() + " : " + req.getRequestURI());
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        if(cookies != null && cookies[0].getName().equals("SPRINGBOOT")) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendRedirect("/public/login?service=" + ((HttpServletRequest) request).getRequestURI());
        }

        System.out.println("Exiting authentication filter");
    }

    @Override
    public void destroy() {

    }
}
