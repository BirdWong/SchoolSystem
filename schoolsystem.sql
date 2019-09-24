-- MySQL dump 10.13  Distrib 5.7.26, for Linux (x86_64)
--
-- Host: localhost    Database: schoolsystem
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `announcement` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `create_time` date NOT NULL,
  `modify_time` date NOT NULL,
  `html_content` mediumtext NOT NULL,
  `content` mediumtext NOT NULL,
  `views` int(10) unsigned DEFAULT '0',
  `cid` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fk_category_id` (`cid`),
  CONSTRAINT `fk_announcement_category` FOREIGN KEY (`cid`) REFERENCES `announcement_category` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcement_category`
--

DROP TABLE IF EXISTS `announcement_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `announcement_category` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role` enum('ALL','ROLE_ING','ROLE_ADMIN','ROLE_ADMINISTRATOR') NOT NULL,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `announcement_category_name_uindex` (`name`),
  KEY `announcement_category_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article` (
  `aid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `create_time` date NOT NULL,
  `modify_time` date NOT NULL,
  `status` int(11) NOT NULL,
  `html_content` mediumtext NOT NULL,
  `content` mediumtext NOT NULL,
  `views` int(11) NOT NULL,
  `kind` int(11) NOT NULL,
  `uid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`aid`),
  KEY `article_user_uid_fk` (`uid`),
  KEY `article_status` (`status`),
  KEY `article_id` (`aid`),
  CONSTRAINT `article_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_collection`
--

DROP TABLE IF EXISTS `article_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article_collection` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `aid` int(10) unsigned NOT NULL,
  `uid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `article_collection_user_uid_fk` (`uid`),
  KEY `article_collection_article_infomation_id_fk` (`aid`),
  CONSTRAINT `article_collection_article_infomation_id_fk` FOREIGN KEY (`aid`) REFERENCES `article_infomation` (`id`),
  CONSTRAINT `article_collection_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_infomation`
--

DROP TABLE IF EXISTS `article_infomation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article_infomation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `aid` int(10) unsigned NOT NULL,
  `cid` int(10) unsigned NOT NULL,
  `uid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `article_infomation_article_aid_fk` (`aid`),
  KEY `article_infomation_category_cid_fk` (`cid`),
  KEY `article_infomation_user_uid_fk` (`uid`),
  CONSTRAINT `article_infomation_article_aid_fk` FOREIGN KEY (`aid`) REFERENCES `article` (`aid`),
  CONSTRAINT `article_infomation_category_cid_fk` FOREIGN KEY (`cid`) REFERENCES `category` (`cid`),
  CONSTRAINT `article_infomation_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_labels`
--

DROP TABLE IF EXISTS `article_labels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article_labels` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `aid` int(10) unsigned NOT NULL,
  `lid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `article_labels_article_aid_fk` (`aid`),
  KEY `article_labels_label_lid_fk` (`lid`),
  CONSTRAINT `article_labels_article_aid_fk` FOREIGN KEY (`aid`) REFERENCES `article` (`aid`),
  CONSTRAINT `article_labels_label_lid_fk` FOREIGN KEY (`lid`) REFERENCES `label` (`lid`)
) ENGINE=InnoDB AUTO_INCREMENT=16264 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `authentication`
--

DROP TABLE IF EXISTS `authentication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authentication` (
  `aid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` int(10) unsigned DEFAULT NULL,
  `role` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`aid`),
  KEY `authentication_user_uid_fk` (`uid`),
  CONSTRAINT `authentication_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `book` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `publisher` varchar(255) NOT NULL,
  `pubdate` date DEFAULT NULL,
  `isbn` varchar(255) DEFAULT NULL,
  `price` varchar(255) NOT NULL,
  `pages` int(11) NOT NULL,
  `has_size` int(10) unsigned NOT NULL,
  `use_size` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `borrow`
--

DROP TABLE IF EXISTS `borrow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `borrow` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` int(10) unsigned NOT NULL,
  `bid` int(10) unsigned NOT NULL,
  `begin_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(4) NOT NULL,
  `days` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `borrow_book_id_fk` (`bid`),
  KEY `borrow_user_uid_fk` (`uid`),
  CONSTRAINT `borrow_book_id_fk` FOREIGN KEY (`bid`) REFERENCES `book` (`id`),
  CONSTRAINT `borrow_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `cid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int(10) unsigned DEFAULT '0',
  `cname` varchar(255) NOT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `collaborators`
--

DROP TABLE IF EXISTS `collaborators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collaborators` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pid` int(10) unsigned NOT NULL,
  `uid` int(10) unsigned NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pid` (`pid`,`uid`),
  UNIQUE KEY `collaborators_name_uindex` (`name`),
  KEY `collaborators_user_uid_fk` (`uid`),
  CONSTRAINT `collaborators_project_id_fk` FOREIGN KEY (`pid`) REFERENCES `project` (`id`),
  CONSTRAINT `collaborators_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commit`
--

DROP TABLE IF EXISTS `commit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sha` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message` text NOT NULL,
  `url` varchar(255) NOT NULL,
  `cid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `commit_sha_uindex` (`sha`),
  KEY `commit_collaborators_id_fk` (`cid`),
  CONSTRAINT `commit_collaborators_id_fk` FOREIGN KEY (`cid`) REFERENCES `collaborators` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `label`
--

DROP TABLE IF EXISTS `label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `label` (
  `lid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `lname` varchar(10) DEFAULT NULL,
  `uid` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`lid`),
  KEY `label_user_uid_fk` (`uid`),
  CONSTRAINT `label_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` longtext NOT NULL,
  `html_description` longtext NOT NULL,
  `status` int(11) NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `days` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `uid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account_number` varchar(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `picture_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `user_account_number_uindex` (`account_number`),
  UNIQUE KEY `user_email_uindex` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_focus`
--

DROP TABLE IF EXISTS `user_focus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_focus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `from_uid` int(10) unsigned NOT NULL,
  `to_uid` int(10) unsigned NOT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-24 13:12:56
