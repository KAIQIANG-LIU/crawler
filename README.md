# 多线程爬虫与Elasticsearch实战
## 1.如何让项目跑起来
a.首先测试zaker-crawler下H2数据库news的连接情况，连接正常进行下一步

b.运行org.github.zaker包下的Main,该类会开启多线程去爬取网易上的新闻，并将其存
存储到数据库中，数据库中间层使用JDBC还是Mybatis可以自己选择

c.运行org.github.zaker包下的ElasticSearchDataGenerator，该类会将开启多个线程，将数据库中的新闻数据写入部署到远端的
ES服务器上

d.最后运行org.github.zaker包下的ElasticSearchEngine,按照操作提示，对感兴趣的内容进行检索
## 2.用到的知识和技术
a.使用集成的H2数据库

b.使用Elasticsearch做数据的索引

c.分别使用了JDBC的数据库直接中间件和Mybatis数据库中中间件作为持久层

d.使用了单例模式

e.在云端使用docker部署ES的服务器

f.使用了flyway进行数据库版本的控制
## 3.遇到的问题和挑战
a.遇到了各种各样的异常如SocketTimeoutException,SSLException,数据库读写异常等诸多异常
，并独立解决

b.遇到了各种中间件、服务器、数据库版本的兼容问题，导致按照预期调用的接口无法调用，尝试使用各种方法解决了兼容性问题

c.粗心大意造成了许多不应该犯的小错误
## 4.感谢
感谢张老师，感谢搜索引擎，希望自己能学到的更多。

