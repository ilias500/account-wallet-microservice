package com.ilias.syrros.wallet.aspect;

import com.ilias.syrros.wallet.advice.RateLimiterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    @Autowired
    CacheManager cacheManager;

    @Pointcut("@annotation(rateLimit)")
    private void annotatedWithRateLimit(RateLimit rateLimit) {}

    @Pointcut("@within(org.springframework.stereotype.Controller)"
            + " || @within(org.springframework.web.bind.annotation.RestController)")
    private void controllerMethods() {}

    @Before("controllerMethods() && annotatedWithRateLimit(rateLimit)")
    public void rateLimitProcess(final JoinPoint joinPoint,
                                 RateLimit rateLimit) throws RateLimiterException {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Cache cache = cacheManager.getCache("ips");

        String ip = request.getRemoteHost();
        String url = request.getRequestURI();
        String key = String.format("req:lim:%s:%s", url, ip);
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper == null) {
            cache.putIfAbsent(key , 1);
        }
        else {
            Integer count = (Integer) valueWrapper.get();
            cache.put(key, ++count);
        }
        Integer count = (Integer) cache.get(key).get();

        if (count > rateLimit.limit()) {
            log.warn("Ip : {}, Try count : {}, url : {}, rateLimit : {}", ip, count, url, rateLimit.limit());
            throw new RateLimiterException("Too many requests within short period. Please wait and try again.", 429);
        }
    }

}