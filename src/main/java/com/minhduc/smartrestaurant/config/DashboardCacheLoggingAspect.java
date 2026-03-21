package com.minhduc.smartrestaurant.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class DashboardCacheLoggingAspect {

    private final CacheManager cacheManager;

    @Around("execution(* com.minhduc.smartrestaurant.service.DashboardService.getDashboardStats(..))")
    public Object logCacheSource(ProceedingJoinPoint joinPoint) throws Throwable {
        Cache cache = cacheManager.getCache("dashboard_stats");
        Cache.ValueWrapper cacheValue = cache != null ? cache.get("all") : null;

        if (cacheValue != null) {
            log.info("Cache HIT dashboard_stats::all -> trả dữ liệu từ Redis");
        } else {
            log.info("Cache MISS dashboard_stats::all -> sẽ truy vấn Database");
        }

        return joinPoint.proceed();
    }
}
