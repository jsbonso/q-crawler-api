package com.company.crawler;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleWebCrawlerApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }


    /**
     * Test the core crawl functionality
     */
    @Test
    public void performCrawlTestWithNoNodes() {
        String testUrl = "/web-crawler?url=http://www.didthanoskill.me";
        String body = this.restTemplate.getForObject(testUrl, String.class);

        JSONObject response = new JSONObject(body);
        JSONObject crawlResult = new JSONObject(response.toString());

        assertThat(crawlResult.get("url")).isEqualTo("http://www.didthanoskill.me");
        assertThat(crawlResult.get("title")).isEqualTo("Did Thanos Kill You?");

    }

    /**
     * Test the core crawl functionality
     */
    @Test
    public void performCrawlTestWithMultipleNodes() {
        String testUrl = "/web-crawler?url=http://www.goole.com&maxDepth=1";
        String body = this.restTemplate.getForObject(testUrl, String.class);

        JSONObject response = new JSONObject(body);
        JSONObject crawlResult = new JSONObject(response.toString());

        assertThat(crawlResult.get("url")).isEqualTo("http://www.goole.com");
        assertThat(crawlResult.get("title")).isEqualTo("Goole.com - Search the Net or visit Goole");
        assertThat(crawlResult.has("nodes"));
    }

    /**
     * Test with MaxDepth query parameter
     */
    @Test
    public void performCrawlWithMaxDepthTest() {
        // Append the maxDepth Query Parameter
        String testUrl = "/web-crawler?url=http://www.didthanoskill.me&maxDepth=1";

        String body = this.restTemplate.getForObject(testUrl, String.class);
        JSONObject response = new JSONObject(body);
        System.out.println("response = " + response.toString());
        JSONObject crawlResult = new JSONObject(response.toString());

        assertThat(crawlResult.get("url")).isEqualTo("http://www.didthanoskill.me");
        assertThat(crawlResult.get("title")).isEqualTo("Did Thanos Kill You?");
        assertThat(crawlResult.has("nodes"));
    }


    /**
     * Test with MaxDepth query parameter
     */
    @Test
    public void performCrawlWithMultipleNodesMaxDepthTest() {
        // Append the maxDepth Query Parameter
        String testUrl = "/web-crawler?url=http://www.goole.com&maxDepth=1";

        String body = this.restTemplate.getForObject(testUrl, String.class);
        JSONObject response = new JSONObject(body);
        System.out.println("response = " + response.toString());
        JSONObject crawlResult = new JSONObject(response.toString());

        assertThat(crawlResult.get("url")).isEqualTo("http://www.goole.com");
        assertThat(crawlResult.get("title")).isEqualTo("Goole.com - Search the Net or visit Goole");

    }



}
