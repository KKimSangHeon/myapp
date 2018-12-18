-- MySQL dump 10.17  Distrib 10.3.11-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: myapp
-- ------------------------------------------------------
-- Server version	10.3.11-MariaDB-1:10.3.11+maria~bionic

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE IF NOT EXISTS myapp;

Use mysql;
GRANT ALL PRIVILEGES on myapp.* to 'user1'@'%' identified by 'passw0rd' with GRANT OPTION;

USE myapp;
--
-- Table structure for table `msg`
--

DROP TABLE IF EXISTS `msg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msg` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `msglog`
CREATE TABLE `msglog` (
  `ROW_ID` int(11) NOT NULL AUTO_INCREMENT,
  `M_PARTITION` int(11) NOT NULL,
  `M_OFFSET` bigint(20) NOT NULL,
  `GROUP_ID` int(11) DEFAULT NULL,
  `ACTIVITY_ID` int(11) DEFAULT NULL,
  `RETRY_COUNT` int(11) DEFAULT NULL,
  `DEMO_COMMAND` varchar(10) DEFAULT NULL,
  `STATUS` varchar(10) DEFAULT NULL,
  `APP` varchar(10) NOT NULL,
  `APP_INST` varchar(10) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `INSERT_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ROW_ID`)
);

--
-- Dumping data for table `msg`
--

LOCK TABLES `msg` WRITE;
/*!40000 ALTER TABLE `msg` DISABLE KEYS */;
/*!40000 ALTER TABLE `msg` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-12-16 12:24:56
