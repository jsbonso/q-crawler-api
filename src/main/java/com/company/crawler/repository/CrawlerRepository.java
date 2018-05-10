package com.company.crawler.repository;

import com.company.crawler.exceptions.CrawlerException;
import com.company.crawler.model.Crawler;
import com.company.crawler.service.CrawlerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * @Author: Jon Bonso
 */
@RestController
@EnableCaching
public class CrawlerRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerRepository.class);

    @Autowired
    private CrawlerService crawlerService;

    @Value("${CRAWLER_MAX_DEPTH : 3}")
    private int MAX_DEPTH;


    /**
     * Web Crawler GET Endpoint
     * @param url - URL to crawl
     * @param maxDepth - Optional Parameter
     * @return
     * @throws IOException
     */
    @RequestMapping("/web-crawler")
    @ResponseBody
    public HttpEntity<Crawler> crawl(@RequestParam(value = "url") URL url,
                                     @RequestParam("maxDepth") Optional<Integer>  maxDepth) throws IOException, CrawlerException {

        LOGGER.info("[CRAWL-START] URL = {}", url);

        if (maxDepth.isPresent())
            LOGGER.info("maxDepth = {}", maxDepth.get());

        Crawler crawler = crawlerService.crawlURL(url, maxDepth.isPresent() ? maxDepth.get() : MAX_DEPTH);

        LOGGER.info("[CRAWL-COMPLETED] URL = {} ", url);

        return new ResponseEntity<>(crawler, HttpStatus.OK);

    }



}
