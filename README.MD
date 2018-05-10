
# Web Crawler API

Author: Jon Bonso


## App Details:

You are required to design and create a simple web crawler. It needs to take
a URL as a parameter and create a tree of child pages linked to the URL. It’s
expected that your application provides deep-crawling solution, meaning that
it goes through multiple levels in link hierarchy.

Create a simple API endpoint that would take URL as a parameter and
return json representing the tree described above. Each node should have at
least following fields: url, title, nodes.


## Running the application:

`./gradlew build && java -jar build/libs/q-crawler-api-0.0.1-SNAPSHOT.jar`


## How to perform the web crawl

1. Open your browser
2. Go to this link:
`http://localhost:8080/web-crawler?url=http://www.goole.com`
3. You can also specify the optional max depth of the crawl:
`http://localhost:8080/web-crawler?url=http://www.goole.me&maxDepth=2`


## Assumptions:

1. For the Result, the provided sample response is an array (enclosed with [] ). I assume that the response should be a valid JSON (enclosed with {})
2. I assume that the protocol (HTTP, HTTPS) is required
3. Basic Caching is implemented
4. MAX Depth is only 3 levels. It can be increased but note that processing time also increases.


