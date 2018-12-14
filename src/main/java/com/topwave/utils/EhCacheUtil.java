package com.topwave.utils;

import com.topwave.synch.BuildingSynch;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by xxx on 2017/7/29.
 */
public class EhCacheUtil {
    //ehcache.xml 保存在src/main/resources/
    Logger logger = Logger.getLogger(EhCacheUtil.class);
    private static final String path = "C:\\ehcache.xml";

    private URL url;

    private CacheManager manager;

    private static EhCacheUtil ehCache;

    private EhCacheUtil(String path) {
//        url = getClass().getResource(path);
        File file = new File(path);
//        url = getClass().getResource(path);
        try {
            url = file.toURL();
            logger.info("ehcache配置文件 url： " + url);
            manager = CacheManager.create(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public static EhCacheUtil getInstance() {
        if (ehCache == null) {
            ehCache = new EhCacheUtil(path);
        }
        return ehCache;
    }

    public void put(String cacheName, String key, Object value) {
        Cache cache = manager.getCache(cacheName);
        Element element = new Element(key, value);
        cache.put(element);
    }

    public Object get(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        Element element = cache.get(key);
        return element == null ? null : element.getObjectValue();
    }

    public Cache get(String cacheName) {
        return manager.getCache(cacheName);
    }

    public void remove(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        cache.remove(key);
    }

    public void flush(String cacheName) {
        Cache cache = manager.getCache(cacheName);
        cache.flush();
    }
}