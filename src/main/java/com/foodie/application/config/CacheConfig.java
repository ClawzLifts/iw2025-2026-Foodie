package com.foodie.application.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for Spring Cache with Caffeine backend.
 * This configuration enables caching for frequently accessed data
 * to improve application performance.
 *
 * Cache stores:
 * - menus: Menu and menu items (refreshes every 1 hour)
 * - ingredients: Ingredients (refreshes every 2 hours)
 * - allergens: Allergens (refreshes every 2 hours)
 * - users: User data (refreshes every 30 minutes)
 * - products: Products (refreshes every 1 hour)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Caffeine cache manager with different TTL for different caches
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "menus",
                "ingredients",
                "allergens",
                "users",
                "products",
                "roles"
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(1000));

        return cacheManager;
    }

    /**
     * Alternative configuration with different expiration times per cache
     * Uncomment to use if you want fine-grained cache control
     */
    /*
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("menus",
            Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build());

        cacheManager.registerCustomCache("ingredients",
            Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build());

        cacheManager.registerCustomCache("allergens",
            Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).build());

        cacheManager.registerCustomCache("users",
            Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build());

        cacheManager.registerCustomCache("products",
            Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build());

        return cacheManager;
    }
    */
}

