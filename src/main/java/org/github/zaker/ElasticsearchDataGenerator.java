package org.github.zaker;

import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchDataGenerator {
    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory;
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<News> newsFromDatabase = getNewsFromH2(sqlSessionFactory);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> writetoEls(newsFromDatabase)).start();
        }
    }

    private static void writetoEls(List<News> newsFromDatabase) {
        Bulk.Builder bulkOfNews = new Bulk.Builder().defaultIndex("news");
        for (News news : newsFromDatabase) {
            Map<String, Object> data = new HashMap<>();
            data.put("content", news.getContent().length() > 10 ? news.getContent().substring(0, 10) : news.getContent());
            data.put("url", news.getUrl());
            data.put("title", news.getTitle());
            data.put("createdAt", news.getCreatedAt());
            data.put("modifiedAt", news.getModifiedAt());
            data.put("id", news.getId());
            bulkOfNews.addAction(new Index.Builder(data).index("zaker").type("news").id(String.valueOf(news.getId())).build());
        }
        Bulk build = bulkOfNews.build();
        try {
            SearchClient.getClient().execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<News> getNewsFromH2(SqlSessionFactory sqlSessionFactory) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.selectList("NewsMapper.selectNews");
        }
    }

}
