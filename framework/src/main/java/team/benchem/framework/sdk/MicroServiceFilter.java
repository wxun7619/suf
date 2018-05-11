package team.benchem.framework.sdk;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.benchem.framework.service.TokenService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@WebFilter(urlPatterns = "/*", filterName = "userContextFilter")
public class MicroServiceFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(MicroServiceFilter.class);

    @Autowired
    TokenService tokenService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("MicroServiceFilter.init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("MicroServiceFilter.doFilter -> begin");

        UserContext usrCtx = UserContext.createUserContext();
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Suf-Token");
        if(token != null && token.length() > 0) {
            usrCtx.properties.put("Suf-Token", token);
            JSONObject tokenBody = tokenService.getToken(token);
            if(tokenBody != null) {
                for (Map.Entry<String, Object> item : tokenBody.entrySet()) {
                    usrCtx.properties.put(item.getKey(), item.getValue());
                }
            }
        }

        chain.doFilter(request, response);
        logger.debug("MicroServiceFilter.doFilter -> end");
    }

    @Override
    public void destroy() {
        logger.debug("MicroServiceFilter.destroy");

    }
}
