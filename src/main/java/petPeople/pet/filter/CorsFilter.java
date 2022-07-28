package petPeople.pet.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");

        String method = ((HttpServletRequest) req).getMethod();

        String requestURI = request.getRequestURI();
        StringBuffer requestURL = request.getRequestURL();

        log.info("***************");
        log.info("requestURI = {}", requestURI);
        log.info("requestURL = {}", requestURL);
        log.info("***************");

        log.info("***************");
        log.info("method = {}", method);
        log.info("***************");

        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("host : " + req.getRemoteHost());
            log.info("addr : " + req.getRemoteAddr());
            log.info("port : " + req.getRemotePort());

            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("method = {}", method);
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            response.setStatus(HttpServletResponse.SC_OK);
        }else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {

    }
}