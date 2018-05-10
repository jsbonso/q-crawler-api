package com.company.crawler.exceptions;

/**
 * Exception Class for the Crawler
 *
 * @Author: Jon Bonso
 */
public class CrawlerException extends Exception {

    public CrawlerException(String message) {
        super(message);
    }

    public CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrawlerException(Throwable cause) {
        super(cause);
    }
}
