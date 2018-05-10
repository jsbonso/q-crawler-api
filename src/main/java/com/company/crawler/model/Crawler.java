package com.company.crawler.model;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Model for the Crawler Node
 *
 * @Author: Jon Bonso
 */

public class Crawler {

    private String url;
    private String title;
    private List<Crawler> nodes;

    public Crawler() {
    }

    public Crawler(String url, String title, List<Crawler> nodes) {
        this.url = url;
        this.title = title;
        this.nodes = nodes;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public List<Crawler> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crawler crawler = (Crawler) o;
        return Objects.equals(url, crawler.url) &&
                Objects.equals(title, crawler.title) &&
                Objects.equals(nodes, crawler.nodes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url, title, nodes);
    }

    @Override
    public String toString() {
        return "Crawler{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
