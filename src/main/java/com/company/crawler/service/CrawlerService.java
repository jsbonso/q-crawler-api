package com.company.crawler.service;

import com.company.crawler.exceptions.CrawlerException;
import com.company.crawler.model.Crawler;
import com.company.crawler.model.ICrawler;
import com.company.crawler.model.SingleCrawler;
import com.company.crawler.repository.SingleCrawlerCallable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static com.company.crawler.utils.CrawlerUtils.isValidURL;
import static java.util.Collections.singletonList;

/**
 * Contains the REST endpoints for the crawler
 *
 * @Author: Jon Bonso
 */

@Service
@EnableCaching
public class CrawlerService implements ICrawler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);
    private Set<URL> urlSet = ConcurrentHashMap.newKeySet();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${CRAWLER_TIMEOUT_MILLIS : 100000}")
    private int timeoutInMillis;


    /**
     * crawl
     * @param url
     * @param maxDepth
     * @return
     */
    public Object crawl(URL url, int maxDepth){
        Crawler crawlResult = null;
        try {
            return crawlURL(url, maxDepth);
        }catch (Exception e){
            LOGGER.error("Error processing URL: {}", url);
        }

        return crawlResult;
    }

    /**
     * crawlURL
     * @param url
     * @param maxDepth
     * @return
     * @throws IOException
     */
    @Override
    public Crawler crawlURL(URL url, int maxDepth) throws IOException, CrawlerException {
        LOGGER.warn("Starting to crawl: {}", url);
        if (!isValidURL(url)) {
            LOGGER.warn("Invalid URL: {}", url);
            throw new CrawlerException("Url must be valid");
        }

        Crawler crawler = crawlURL(singletonList(url), maxDepth);
        urlSet.clear();
        return crawler;
    }

    /**
     * Crawl URL
     * @param urls
     * @param depth
     * @return
     * @throws IOException
     */
    private Crawler crawlURL(List<URL> urls, int depth) throws IOException {
        if (depth <= 0) {
            return null;
        }
        List<Future<SingleCrawler>> singleCrawlerFutures = new ArrayList<>();
        for (URL url : urls) {
            if (!isValidURL(url) || urlSet.contains(url)) {
                LOGGER.info("Not a valid URL: {}. Skipping...", String.valueOf(url));
                continue;
            }
            urlSet.add(url);

            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(url, timeoutInMillis);
            Future<SingleCrawler> singleCrawlerFuture = executorService.submit(singleCrawlerCallable);
            singleCrawlerFutures.add(singleCrawlerFuture);
        }

        for (Future<SingleCrawler> singleCrawlerFuture : singleCrawlerFutures) {
            SingleCrawler singleCrawler = null;
            try {
                singleCrawler = singleCrawlerFuture.get();
            } catch (ExecutionException | InterruptedException  e) {
                LOGGER.error("[ERROR] Can not process the URL {}", String.valueOf(singleCrawler.getUrl()));
            }

            Crawler crawler = new Crawler(singleCrawler.getUrl().toString(), singleCrawler.getTitle(), new ArrayList<>());
            return crawlChildURLs(crawler, singleCrawler.getLinks(), depth - 1);
        }

        return null;
    }

    /**
     * Crawl Children
     * @param crawler
     * @param linkUrls
     * @param depth
     * @return
     * @throws IOException
     */
    private Crawler crawlChildURLs(Crawler crawler, List<URL> linkUrls, int depth) throws IOException {

        for (URL linkUrl : linkUrls) {

            if (!isValidURL(linkUrl) || urlSet.contains(linkUrl)) {
                LOGGER.warn("URL is not valid:  {}", String.valueOf(linkUrl));
                continue;
            }

            urlSet.add(linkUrl);
            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(linkUrl, timeoutInMillis);
            Future<SingleCrawler> singleCrawlerFuture = executorService.submit(singleCrawlerCallable);

            try {

                SingleCrawler singleCrawler = singleCrawlerFuture.get();
                Crawler crawlerChild = new Crawler(singleCrawler.getUrl().toString(),
                        singleCrawler.getTitle(), new ArrayList<>());
                Crawler c = crawlURL(singleCrawler.getLinks(), depth);

                if (c != null)
                    crawlerChild.getNodes().add(c);

                crawler.getNodes().add(crawlerChild);
            } catch ( ExecutionException | InterruptedException io) {
                LOGGER.error("[ERROR] Can not process the URL {}", String.valueOf(linkUrl));
                continue;
            }
        }

        return crawler;
    }

}