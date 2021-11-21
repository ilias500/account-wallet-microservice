package com.ilias.syrros.wallet.interceptor;

import com.ilias.syrros.wallet.annotation.Audit;
import com.ilias.syrros.wallet.service.contracts.IAuditService;
import com.ilias.syrros.wallet.service.models.AuditDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuditInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);

    @Autowired
    private IAuditService auditService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            final Audit audit = ((HandlerMethod) handler).getMethod().getAnnotation((Audit.class));
            if (audit != null) {
                long startTime = System.currentTimeMillis();
                logger.info("Request URL :: " + request.getRequestURL().toString()
                        + ":: Start Time = " + System.currentTimeMillis());
                request.setAttribute("startTime", startTime);
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        if (handler instanceof HandlerMethod) {
            final Method restMethod = ((HandlerMethod) handler).getMethod();
            final Audit audit = ((HandlerMethod) handler).getMethod().getAnnotation((Audit.class));
            if (audit != null) {
                logger.info("Inside audit method");
                logger.info("Method :: " + restMethod.getName());
                String pathUrl = request.getRequestURL().toString();
                System.out.println("Request URL::" + request.getRequestURL().toString()
                        + " Sent to Handler :: Current Time = " + System.currentTimeMillis());
                Map<String, List<String>> headersMap = Collections.list(request.getHeaderNames())
                        .stream()
                        .collect(Collectors.toMap(
                                Function.identity(),
                                h -> Collections.list(request.getHeaders(h))
                        ));
                String requestPayload = null;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                request.getInputStream().transferTo(baos);
                InputStream cloneInput = new ByteArrayInputStream(baos.toByteArray());
                byte[] body = StreamUtils.copyToByteArray(cloneInput);
                if (body.length > 0) {
                    requestPayload = new String(body, 0, body.length);
                }

                logger.info("Request Payload :: " + requestPayload);

                ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
                String responsePayload = null;
                if (wrapper != null) {
                    byte[] buf = wrapper.getContentAsByteArray();
                    if (buf.length > 0) {
                        responsePayload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                    }
                }
                logger.info("Response Payload :: " + responsePayload);
                if (request.getAttribute("startTime") != null) {
                    long startTime = (Long) request.getAttribute("startTime");
                    logger.info("Request URL ::" + request.getRequestURL().toString()
                            + ":: End Time = " + System.currentTimeMillis());
                    long callDuration = System.currentTimeMillis() - startTime;
                    logger.info("Request URL :: " + request.getRequestURL().toString()
                            + ":: Time Taken = " + callDuration);

                    auditService.createAuditEntry(AuditDTO.builder().requestMethod(restMethod.getName())
                            .requestHeader(headersMap.toString())
                            .requestPayload(requestPayload)
                            .responsePayload(responsePayload)
                            .urlPath(pathUrl)
                            .callDuration(callDuration)
                            .build()
                    );
                }
            }
        }
    }

}
