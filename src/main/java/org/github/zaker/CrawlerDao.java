package org.github.zaker;

import java.io.IOException;
import java.sql.SQLException;

public interface CrawlerDao {
    String getNextLinkThenDelete() throws IOException;

    void insertIntoNewsDataBase(String url, String title, String content) throws SQLException;

    void insertIntoToBeProcessed(String url);

    void insertIntoHasProcessed(String url);

    boolean hasProcessedLink(String link) throws SQLException;
}
