package org.github.zaker;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MyBatisCrawlerDao implements CrawlerDao {
    SqlSessionFactory sqlSessionFactory;

    public MyBatisCrawlerDao() {
        String resource = "db/mybatis/config.xml";
        try {
            InputStream resourceAsStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNextLinkThenDelete() throws IOException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String url = session.selectOne("NewsMapper.selectNextLink");
            if (url != null) {
                session.delete("NewsMapper.deleteLink", url);
            }
            return url;
        }
    }

    @Override
    public void insertIntoNewsDataBase(String url, String title, String content) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("NewsMapper.insetNews", new News(title, content, url));
        }
    }

    @Override
    public void insertIntoToBeProcessed(String url) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "links_already_processed");
        param.put("link", url);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("NewsMapper.insertLink", param);
        }
    }

    @Override
    public void insertIntoHasProcessed(String url) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "link_to_be_processed");
        param.put("link", url);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("NewsMapper.insertLink", param);
        }
    }

    @Override
    public boolean hasProcessedLink(String link) throws SQLException {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            int count = session.selectOne("NewsMapper.isLinkProcessed", link);
            return count != 0;
        }
    }
}
