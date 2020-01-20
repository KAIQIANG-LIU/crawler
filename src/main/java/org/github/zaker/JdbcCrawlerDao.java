package org.github.zaker;

import java.sql.*;

public class JdbcCrawlerDao implements CrawlerDao {
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "root";
    private final Connection connection;

    @SuppressWarnings("DMI_CONSTANT_DB_PASSWORD")
    public JdbcCrawlerDao() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:C:/Users/liukaiqiang/IdeaProjects/zaker-crawler/news", USER_NAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNextLink(String sql) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return null;
    }

    @Override
    public String getNextLinkThenDelete() {
        try {
            String nextLink = getNextLink("SELECT LINK FROM LINKS_TO_BE_PROCESSED LIMIT 1");
            if (nextLink != null) {
                updateDatabase(nextLink);
                return nextLink;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateDatabase(String link) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM LINKS_TO_BE_PROCESSED WHERE LINK = ?")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertIntoNewsDataBase(String url, String title, String content) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO NEWS (TITLE, CONTENT, URL, CREATED_AT, MODIFIED_AT) VALUES ( ?,?,?,NOW(),NOW() )")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, content);
            preparedStatement.setString(3, url);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void insertIntoToBeProcessed(String url) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LINKS_TO_BE_PROCESSED (LINK) VALUES ( ? )")) {
            preparedStatement.setString(1, url);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertIntoHasProcessed(String url) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LINKS_ALREADY_PROCESSED (LINK) VALUES ( ? )")) {
            preparedStatement.setString(1, url);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasProcessedLink(String link) throws SQLException {
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT LINK FROM LINKS_ALREADY_PROCESSED WHERE LINK = ?")) {
            preparedStatement.setString(1, link);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return false;
    }
}
