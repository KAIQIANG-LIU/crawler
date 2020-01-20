package org.github.zaker;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ElasticSearchEngine {
    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("Please input a search keyword:");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String keyword = reader.readLine();

            search(keyword);
        }
    }

    private static void search(String keyword) throws IOException {
        String search = "{" +
                "  \"query\": {" +
                "    \"bool\": {" +
                "      \"must\": [" +
                "        { \"match\": { \"content\":   \"" + keyword + "\" }}" +
                "      ]" +
                "    }" +
                "  }" +
                "}";
        Search build = new Search.Builder(search).addIndex("zaker").addType("news").build();
        try {
            SearchResult result = SearchClient.getClient().execute(build);
            System.out.println(result.getFirstHit(News.class).source.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
