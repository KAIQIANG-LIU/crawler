package org.github.zaker;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler extends Thread {
    Container container;
    //包含：
    //1.需要被处理的链接
    //2.已经处理的链接
    //3.存储的新闻
    CrawlerDao crawlerDao;

    public Crawler(Container container) {
        this.container = container;
    }

    public Crawler(CrawlerDao crawlerDao) {
        this.crawlerDao = crawlerDao;
    }

    @Override
    public void run() {
       /* while (!container.linkToBeProcessed.isEmpty()) {
            //拿链接-->解析链接-->查看内容-->存储新闻-->查看所含链接-->将目标链接存储到需要被处理的链接
            //-->将该链接从当前链接表中删除-->将目前该链接存储到已处理的链接中
            String link = container.linkToBeProcessed.get(container.linkToBeProcessed.size() - 1);
            if (container.linkHasProcessed.contains(link)) {
                container.linkToBeProcessed.remove(container.linkToBeProcessed.size() - 1);
                continue;
            }
            Document document = parseHtmlGetDoc(link);
            getNews(link, document);
            getLinks(document);
            container.linkHasProcessed.add(link);
            container.linkToBeProcessed.remove(container.linkToBeProcessed.size() - 1);
        }*/
        String link;
        //拿链接-->解析链接-->查看内容-->存储新闻-->查看所含链接-->将目标链接存储到需要被处理的链接
        //-->将该链接从当前链接表中删除-->将目前该链接存储到已处理的链接中
        try {
            while ((link = crawlerDao.getNextLinkThenDelete()) != null) {
                if (crawlerDao.hasProcessedLink(link)) {
                    continue;
                }
                Document document = parseHtmlGetDoc(link);
                if (document == null) {
                    continue;
                }
                getNews(link, document);
                getLinks(document);
                crawlerDao.insertIntoHasProcessed(link);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private Document parseHtmlGetDoc(String url) {
        try {
            Connection connect = Jsoup.connect(url);
            connect.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connect.header("Accept-Encoding", "gzip, deflate, sdch");
            connect.header("Accept-Language", "zh-CN,zh;q=0.8");
            connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            connect.timeout(100000);
            return connect.get();
        } catch (IOException e) {
            if(e instanceof HttpStatusException){
                return null;
            }
            throw new RuntimeException(e);
        }
    }

    private void getNews(String url, Document document) {
        //1.解析页面
        //2.获得链接中新闻标题
        //3.获得链接中新闻时间
        //4.获得链接中新闻内容
        //5.将新闻存储到Container
        if (isdocNewsDoc(document)) {
            Elements art_box = document.getElementsByClass("art_box");
            String title = art_box.select("h1").text();
            List<Element> select = art_box.select(".art_content p");
            String content = select.stream().map(Element::text).collect(Collectors.joining("\n"));
            try {
                crawlerDao.insertIntoNewsDataBase(url, title, content);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isdocNewsDoc(Document document) {
        return !document.getElementsByClass("art_box").isEmpty();
    }

    private boolean ishrefInteresting(String href) {
        //强筛选和弱筛选
        //强筛选：只选符合我们条件的，我就要
        //弱筛选：只要不是我们不想要的，我就要
        //这里使用强筛选
        for (String standard : Container.standardLink) {
            if (href.contains(standard) || href.equals("https://sina.cn")) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotTooLong(String href) {
        return href.length() < 1000;
    }

    private void getLinks(Document document) {
        //1.解析页面
        //2.获取链接中符合条件的链接
        //3.将新闻链接存储到Container中
        List<Element> elements = document.select("a");
        for (Element element : elements) {
            String href = element.attr("href");
            if (href.startsWith("//")) {
                href = "https:" + href;
            }
            if (href.contains("\\/")) {
                href = href.replace("\\/", "/");
            }
            if (ishrefInteresting(href) && isNotTooLong(href)) {
                crawlerDao.insertIntoToBeProcessed(href);
            }
        }
    }


}
