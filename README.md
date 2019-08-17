# 安装/配置docker
## 安装教程
[阿里云安装docker教程](https://help.aliyun.com/document_detail/60742.html)
## 镜像加速配置
阿里云搜索`容器镜像服务`，左侧镜像中心里面有镜像加速教程

# 下载镜像

## 下载elasticsearch镜像
```bash
docker pull elasticsearch:6.4.2
```

## redis下载
```bash
docker pull redis:5.0.5
```

## mysql下载
```bash
docker pull mysql:5.7
```

# 配置并且启动服务


## redis镜像配置
启动命令
```bash
docker run -d -p 6379:6379 --name redis redis
```
配置：null

## mysql镜像配置
```bash
# 记得路径设置成自己的文件绝对路径 password改为数据库密码
docker run -d -p 3306:3306 -v /your data path/:/var/lib/mysql/ -v /your config path/:/etc/mysql/mysql.conf.d/ -v /you log config/:/var/log/
-e MYSQL_ROOT_PASSWORD=4795 --name mysql mysql:5.7
```
配置：
文件名: my.cnf
```properties
[mysqld]
port	=	3306


character-set-server	=	utf8
#数据库字符集对应一些排序等规则，注意要和character-set-server对应
collation-server	=	utf8_general_ci
default-storage-engine	=	InnoDB
```
数据导入命令
```bash
# 首先登录mysql
# 创建数据库
create database schoolsystem character set utf8;
# 选择创建好的数据库
use xxx
# 导入sql文件
source /path/path/path/xxx.sql
```


## elasticsearch镜像配置

* 如果elasticsearch启动就死亡，出错执行（直接启动docker报错的话可以执行，一次性命令，重启后失效）：`sudo sysctl -w vm.max_map_count=262144`

获取一些基本配置文件
```bash
先在/home目录下建立tmp文件夹
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -v /home/tmp/:/root/ --name elasticsearch elasticsearch
docker exec -it elasticsearch /bin/bash
# 复制/usr/share/elasticsearch/下的data、config、logs三个文件夹内的文件分别到自己本地/root/下
# 复制完成后退出容器，/home/tmp下三个文件夹放置到自己指定的目录下
# 关闭容器并且删除
```
修改config/elasticsearch.yml配置文件，将`cluster.name`改成`school`

启动命令：
```bash
# -v指定的文件夹路径替换成上面的文件夹路径
docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -v /home/h4795/Documents/studytmp/es/config/:/usr/share/elasticsearch/config/  -v /home/h4795/Documents/studytmp/es/logs/:/usr/share/elasticsearch/logs/  -v /home/h4795/Documents/studytmp/es/data/:/usr/share/elasticsearch/data/ --name elasticsearch elasticsearch
```

安装ik分词插件，ik分词器版本必须和es一致，否则一定会安装失败
```bash
# 分词器开源地址：https://github.com/medcl/elasticsearch-analysis-ik
docker exec -it elasticsearch /bin/bash
cd bin
elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.4.2/elasticsearch-analysis-ik-6.4.2.zip
# 退出docker
exit
# 重启es
docker restart elasticsearch
```

*创建索引*

`从es6.0开始，一个index只能有一个type。7.0将完全去除type概念`

文章
```json
// put http://127.0.0.1:9200/article
{
  "settings": {
    "number_of_shards": "5",
    "number_of_replicas": "0",
    "index.analysis.analyzer.default.type": "ik_max_word"
  }
}
```

书籍
```json
// put http://127.0.0.1:9200/book
{
  "settings": {
    "number_of_shards": "5",
    "number_of_replicas": "0",
    "index.analysis.analyzer.default.type": "ik_max_word"
  }
}
```



*建立文档*
文章
```json
// post http://127.0.0.1:9200/article/_mapping/article
{
  "properties": {
    "id": {
      "type": "integer"
    },
    "title": {
      "type": "text",
      "analyzer":"ik_max_word"
    },
    "content": {
      "type": "text",
      "analyzer":"ik_max_word"
    },
    "createtime": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || epoch_millis"
    },
    "status": {
      "type": "integer"
    },
    "kind": {
      "type": "integer"
    },
    "cid": {
      "type": "integer"
    },
    "uid": {
      "type": "integer"
    },
    "username":{
      "type": "text"
    },
    "category":{
      "type": "text"
    }
  }
}
```


书籍
```json
// post http://127.0.0.1:9200/book/_mapping/book
{
  "properties":{
    "id":{
      "type":"integer"
    },
    "title":{
      "type": "text",
      "analyzer":"ik_max_word"
    },
    "author":{
      "type": "text",
      "analyzer":"ik_max_word"
    },
    "publisher":{
      "type": "text",
      "analyzer":"ik_max_word"
    },
    "pubdate":{
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || epoch_millis"
    },
    "isbn":{
      "type": "text",
      "analyzer":"ik_max_word"
    },
    "price":{
      "type": "text",
      "analyzer":"ik_max_word"
    },
    "pages":{
      "type":"integer"
    },
    "size":{
      "type": "integer"
    },
    "hasUse":{
      "type": "integer"
    }
  }
}
```



# 部署项目

1. 下载项目运行环境
```bash
docker pull registry.cn-shenzhen.aliyuncs.com/rocwong/java-dev:1.0

# 修改镜像的名称
docker tag [imageId] java-dev
```


2. 开启注册中心
```bash
# 启动镜像
docker run -it -p 7001:7001 --name eureka java-dev

# 下载代码
git clone https://github.com/BirdWong/schoolsystem.git


# 进入注册中心项目启动
cd schoolsystem/eureka_7001

mvn spring-boot:run


# 退出镜像
ctrl + p + q

```


3. 开启路由以及用户中心
```bash
# 启动镜像
docker run -it -p 4795:4795 --name gateway java-dev


# 下载代码
git clone https://github.com/BirdWong/schoolsystem.git


# 进入路由项目启动
cd schoolsystem/gateway_4795

mvn spring-boot:run


# 退出镜像
ctrl + p + q

```


4. 启动工作室管理项目
```bash
# 启动镜像
docker run -it -p 8001:8001 --name ccw java-dev


# 下载代码
git clone https://github.com/BirdWong/schoolsystem.git


# 进入路由项目启动
cd schoolsystem/ccwsystem_8001

mvn spring-boot:run


# 退出镜像
ctrl + p + q

```

**redis目前并没有设置密码，但是一定要设置32位密码， 可以通过sha256加密算法将短密码加密后设置成redis密码， 不设置密码会被入侵服务器**
**<font color="red">如果mysql、redis、es等服务的配置发生改变，请手动修改项目中的配置文件</font>**
