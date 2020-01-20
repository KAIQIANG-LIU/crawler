package org.github.zaker;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class SearchClient {
    public static JestClient jestClient = null;

    private SearchClient() {
    }

    public static JestClient getClient() {
        if (jestClient == null) {
            JestClientFactory jestClientFactory = new JestClientFactory();
            jestClientFactory.setHttpClientConfig(
                    new HttpClientConfig.Builder("http://129.211.55.33:9200")
                            .multiThreaded(true)
                            .readTimeout(10000)
                            .defaultMaxTotalConnectionPerRoute(2)
                            .maxTotalConnection(10)
                            .build()
            );
            jestClient = jestClientFactory.getObject();
        }
        return jestClient;
    }
}
