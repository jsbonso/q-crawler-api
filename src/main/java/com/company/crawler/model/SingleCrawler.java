package com.company.crawler.model;

import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * SingleCrawler
 * @Author: Jon Bonso
 */

public class SingleCrawler {

    private URL url;
    private String title;
    private List<URL> links;

    public SingleCrawler(URL url, String title, List<URL> links) {
        this.url = url;
        this.title = title;
        this.links = links;
    }

    public URL getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public List<URL> getLinks() {
        return links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleCrawler that = (SingleCrawler) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(title, that.title) &&
                Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url, title, links);
    }

    @Override
    public String toString() {
        return "SingleCrawler{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", links=" + links +
                '}';
    }
}
