package com.ilias.syrros.wallet.filter;

import com.ilias.syrros.wallet.wrapper.CachedBodyHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ResponseWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingResponseWrapper responseCacheWrapperObject = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);
        //ContentCachingRequestWrapper requestCacheWrapperObject = new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest((HttpServletRequest) servletRequest);
        filterChain.doFilter(cachedBodyHttpServletRequest, responseCacheWrapperObject);

        responseCacheWrapperObject.copyBodyToResponse();
    }
}
