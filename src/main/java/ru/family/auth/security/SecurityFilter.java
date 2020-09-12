package ru.family.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.family.auth.configuration.ASBusinessException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class SecurityFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        var excludeAuth = new ArrayList<>(Arrays.asList("api-docs", "configuration", "swagger", "webjars", "error"));
        var authHeader = req.getHeader("X-FAMILY-APP-ID");
        AtomicBoolean exclude = new AtomicBoolean(false);

        log.info("-------------------------------------------------------------------------------------------");
        log.info(" /" + req.getMethod());
        log.info(" Request: " + req.getRequestURI());
        log.info("-------------------------------------------------------------------------------------------");

        var uri = req.getRequestURI();

        excludeAuth.forEach(ex -> {
            if (uri.contains(ex)) {
                exclude.set(true);
            }
        });

        if (!exclude.get() && (authHeader == null || !Objects.equals(authHeader, "FAMILY"))) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json");
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Required headers not specified in the request or incorrect");
            throw new ASBusinessException("Required headers not specified in the request or incorrect");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Auth filter initialization");
    }
}