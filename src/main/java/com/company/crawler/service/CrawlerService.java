package com.company.crawler.service;

import com.company.crawler.exceptions.CrawlerException;
import com.company.crawler.model.Crawler;
import com.company.crawler.model.ICrawler;
import com.company.crawler.model.SingleCrawler;
import com.company.crawler.repository.SingleCrawlerCallable;
import org.springframework.beans.factory.annotation.Value;
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
public class CrawlerService implements ICrawler {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);
    private Set<URL> urlSet = ConcurrentHashMap.newKeySet();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${CRAWLER_TIMEOUT_MILLIS : 100000}")
    private int timeoutInMillis;

    /**
     *
     * @param url
     * @param maxDepth
     * @return
     * @throws IOException
     */
    @Override
    public Crawler crawlURL(URL url, int maxDepth) throws IOException, CrawlerException {
        if (!isValidURL(url)) {
            LOGGER.warn("url must be valid");
            throw new CrawlerException("Url must be valid");
        }

        Crawler crawler = crawlURL(singletonList(url), maxDepth);
        urlSet.clear();
        return crawler;
    }

    /**
     *
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
                LOGGER.info("ignoring url {} because it is either invalid or exists in set", String.valueOf(url));
                continue;
            }
            urlSet.add(url);
            LOGGER.info("Crawling url {} with depth {}", url, depth);

            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(url, timeoutInMillis);
            Future<SingleCrawler> singleCrawlerFuture = executorService.submit(singleCrawlerCallable);
            singleCrawlerFutures.add(singleCrawlerFuture);
        }

        for (Future<SingleCrawler> singleCrawlerFuture : singleCrawlerFutures) {
            SingleCrawler singleCrawler = null;
            try {
                singleCrawler = singleCrawlerFuture.get();
            } catch (Exception e) {
                LOGGER.error("[ERROR] Problem accessing url {}", String.valueOf(singleCrawler.getUrl()));
            }

            Crawler crawler = new Crawler(singleCrawler.getUrl().toString(), singleCrawler.getTitle(), new ArrayList<>());
            return crawlChildURLs(crawler, singleCrawler.getLinks(), depth - 1);
        }

        return null;
    }

    /**
     *
     * @param crawler
     * @param linkUrls
     * @param depth
     * @return
     * @throws IOException
     */
    private Crawler crawlChildURLs(Crawler crawler, List<URL> linkUrls, int depth) throws IOException {
        for (URL linkUrl : linkUrls) {
            if (!isValidURL(linkUrl) || urlSet.contains(linkUrl)) {
                LOGGER.info("Skipping url {}", String.valueOf(linkUrl));
                continue;
            }
            urlSet.add(linkUrl);
            SingleCrawlerCallable singleCrawlerCallable = new SingleCrawlerCallable(linkUrl, timeoutInMillis);
            Future<SingleCrawler> singleCrawlerFuture = executorService.submit(singleCrawlerCallable);
            try {
                SingleCrawler singleCrawler = singleCrawlerFuture.get();
                Crawler crawlerChild = new Crawler(singleCrawler.getUrl().toString(), singleCrawler.getTitle(), new ArrayList<>());
                Crawler c = crawlURL(singleCrawler.getLinks(), depth);
                if (c != null) {
                    crawlerChild.getNodes().add(c);
                }

                crawler.getNodes().add(crawlerChild);
            } catch (ExecutionException e) {
                LOGGER.warn("Problem accessing url {}, moving on to the next one", String.valueOf(linkUrl));
                continue;
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
        }

        return crawler;
    }

}