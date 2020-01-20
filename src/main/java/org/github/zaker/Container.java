package org.github.zaker;

import java.util.ArrayList;
import java.util.List;

public class Container {
    List<News> newsList;
    List<String> linkToBeProcessed;
    List<String> linkHasProcessed;
    final static List<String> standardLink = new ArrayList<>();

    static {
        standardLink.add("news.sina.cn");
        standardLink.add("mil.sina.cn");
        standardLink.add("finance.sina.cn");
        standardLink.add("sports.sina.cn");
        standardLink.add("ent.sina.cn");
        standardLink.add("tech.sina.cn");
        standardLink.add("games.sina.cn");
        standardLink.add("edu.sina.cn");
        standardLink.add("eladies.sina.cn");
        standardLink.add("gd.sina.cn");
        standardLink.add("sc.sina.cn");
        standardLink.add("henan.sina.cn");
        standardLink.add("fj.sina.cn");
        standardLink.add("jx.sina.cn");
        standardLink.add("hunan.sina.cn");
        standardLink.add("hb.sina.cn");
        standardLink.add("sh.sina.cn");
        standardLink.add("hlj.sina.cn");
        standardLink.add("jiangsu.sina.cn");
        standardLink.add("gx.sina.cn");
        standardLink.add("hainan.sina.cn");
        standardLink.add("shanxi.sina.cn");
        standardLink.add("jl.sina.cn");
        standardLink.add("zj.sina.cn");
        standardLink.add("sd.sina.cn");
        standardLink.add("yn.sina.cn");
    }

    public Container() {
        this.newsList = new ArrayList<News>();
        this.linkToBeProcessed = new ArrayList<String>();
        this.linkHasProcessed = new ArrayList<String>();
        linkToBeProcessed.add("https://sina.cn/?vt=4&pos=108");
    }
}
