-- MySQL dump 10.13  Distrib 5.6.23, for osx10.10 (x86_64)
--
-- Host: 127.0.0.1    Database: cas
-- ------------------------------------------------------
-- Server version	5.5.46

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
-- Current Database: `cas`
--
create database cas;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `cas` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `cas`;

--
-- Table structure for table `RegisteredServiceImpl`
--

DROP TABLE IF EXISTS `RegisteredServiceImpl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RegisteredServiceImpl` (
  `expression_type` varchar(15) NOT NULL DEFAULT 'ant',
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allowedToProxy` tinyint(1) NOT NULL,
  `anonymousAccess` tinyint(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `evaluation_order` int(11) NOT NULL,
  `ignoreAttributes` tinyint(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `serviceId` varchar(255) DEFAULT NULL,
  `ssoEnabled` tinyint(1) NOT NULL,
  `theme` varchar(255) DEFAULT NULL,
  `username_attr` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RegisteredServiceImpl`
--

LOCK TABLES `RegisteredServiceImpl` WRITE;
/*!40000 ALTER TABLE `RegisteredServiceImpl` DISABLE KEYS */;
/*!40000 ALTER TABLE `RegisteredServiceImpl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SERVICETICKET`
--

DROP TABLE IF EXISTS `SERVICETICKET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SERVICETICKET` (
  `ID` varchar(255) NOT NULL,
  `NUMBER_OF_TIMES_USED` int(11) DEFAULT NULL,
  `CREATION_TIME` bigint(20) DEFAULT NULL,
  `EXPIRATION_POLICY` longblob NOT NULL,
  `LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `PREVIOUS_LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `FROM_NEW_LOGIN` tinyint(1) NOT NULL,
  `TICKET_ALREADY_GRANTED` tinyint(1) NOT NULL,
  `SERVICE` longblob NOT NULL,
  `ticketGrantingTicket_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK7645ADE132A2C0E5` (`ticketGrantingTicket_ID`),
  CONSTRAINT `FK7645ADE132A2C0E5` FOREIGN KEY (`ticketGrantingTicket_ID`) REFERENCES `TICKETGRANTINGTICKET` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SERVICETICKET`
--

LOCK TABLES `SERVICETICKET` WRITE;
/*!40000 ALTER TABLE `SERVICETICKET` DISABLE KEYS */;
/*!40000 ALTER TABLE `SERVICETICKET` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TICKETGRANTINGTICKET`
--

DROP TABLE IF EXISTS `TICKETGRANTINGTICKET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TICKETGRANTINGTICKET` (
  `ID` varchar(255) NOT NULL,
  `NUMBER_OF_TIMES_USED` int(11) DEFAULT NULL,
  `CREATION_TIME` bigint(20) DEFAULT NULL,
  `EXPIRATION_POLICY` longblob NOT NULL,
  `LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `PREVIOUS_LAST_TIME_USED` bigint(20) DEFAULT NULL,
  `AUTHENTICATION` longblob NOT NULL,
  `EXPIRED` tinyint(1) NOT NULL,
  `SERVICES_GRANTED_ACCESS_TO` longblob NOT NULL,
  `ticketGrantingTicket_ID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKB4C4CDDE32A2C0E5` (`ticketGrantingTicket_ID`),
  CONSTRAINT `FKB4C4CDDE32A2C0E5` FOREIGN KEY (`ticketGrantingTicket_ID`) REFERENCES `TICKETGRANTINGTICKET` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TICKETGRANTINGTICKET`
--

LOCK TABLES `TICKETGRANTINGTICKET` WRITE;
/*!40000 ALTER TABLE `TICKETGRANTINGTICKET` DISABLE KEYS */;
/*!40000 ALTER TABLE `TICKETGRANTINGTICKET` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `service_base_url` varchar(100) DEFAULT NULL,
  `service_id` bigint(20) DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `username` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `audit_log_date_index` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authority`
--

DROP TABLE IF EXISTS `authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `authority` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `authority` (`authority`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authority`
--

LOCK TABLES `authority` WRITE;
/*!40000 ALTER TABLE `authority` DISABLE KEYS */;
/*!40000 ALTER TABLE `authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community_account_details`
--

DROP TABLE IF EXISTS `community_account_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `community_account_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(30) NOT NULL,
  `infusionsoft_experience` int(11) DEFAULT NULL,
  `notification_email_address` varchar(255) DEFAULT NULL,
  `time_zone` varchar(30) NOT NULL,
  `twitter_handle` varchar(30) DEFAULT NULL,
  `user_account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_account_id` (`user_account_id`),
  UNIQUE KEY `user_account_id_2` (`user_account_id`),
  KEY `FKE15D39DA25EF4B00` (`user_account_id`),
  CONSTRAINT `FKE15D39DA25EF4B00` FOREIGN KEY (`user_account_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_account_details`
--

LOCK TABLES `community_account_details` WRITE;
/*!40000 ALTER TABLE `community_account_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `community_account_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `legacy_account`
--

DROP TABLE IF EXISTS `legacy_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `legacy_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(255) NOT NULL,
  `app_username` varchar(255) NOT NULL,
  `email3` varchar(255) NOT NULL,
  `email2` varchar(255) NOT NULL,
  `email1` varchar(255) NOT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_name` (`app_name`,`app_username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `legacy_account`
--

LOCK TABLES `legacy_account` WRITE;
/*!40000 ALTER TABLE `legacy_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `legacy_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locks`
--

DROP TABLE IF EXISTS `locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `locks` (
  `application_id` varchar(255) NOT NULL,
  `expiration_date` datetime DEFAULT NULL,
  `unique_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locks`
--

LOCK TABLES `locks` WRITE;
/*!40000 ALTER TABLE `locks` DISABLE KEYS */;
INSERT INTO `locks` VALUES ('cas-ticket-registry-cleaner',NULL,NULL);
/*!40000 ALTER TABLE `locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_attempt`
--

DROP TABLE IF EXISTS `login_attempt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login_attempt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `success` tinyint(1) DEFAULT NULL,
  `username` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `login_attempt_date_index` (`date`),
  KEY `login_attempt_username_index` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_attempt`
--

LOCK TABLES `login_attempt` WRITE;
/*!40000 ALTER TABLE `login_attempt` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_attempt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marketingOptions`
--

DROP TABLE IF EXISTS `marketingOptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marketingOptions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `desktopImageSrcUrl` varchar(2000) DEFAULT NULL,
  `enableAds` tinyint(1) NOT NULL,
  `href` varchar(2000) DEFAULT NULL,
  `mobileImageSrcUrl` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marketingOptions`
--

LOCK TABLES `marketingOptions` WRITE;
/*!40000 ALTER TABLE `marketingOptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `marketingOptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_client`
--

DROP TABLE IF EXISTS `oauth_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` varchar(100) NOT NULL,
  `client_secret` varchar(100) NOT NULL,
  `description` varchar(200) NOT NULL,
  `registeredService_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `client_id` (`client_id`),
  KEY `FK193E9A73AE2FD2BF` (`registeredService_id`),
  CONSTRAINT `FK193E9A73AE2FD2BF` FOREIGN KEY (`registeredService_id`) REFERENCES `RegisteredServiceImpl` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_client`
--

LOCK TABLES `oauth_client` WRITE;
/*!40000 ALTER TABLE `oauth_client` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_service_config`
--

DROP TABLE IF EXISTS `oauth_service_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_service_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `allow_anonymous` tinyint(1) NOT NULL,
  `description` varchar(200) NOT NULL,
  `name` varchar(100) NOT NULL,
  `service_key` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_service_config`
--

LOCK TABLES `oauth_service_config` WRITE;
/*!40000 ALTER TABLE `oauth_service_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_service_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending_user_account`
--

DROP TABLE IF EXISTS `pending_user_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pending_user_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(255) NOT NULL,
  `app_type` varchar(255) NOT NULL,
  `app_username` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(60) DEFAULT NULL,
  `last_name` varchar(60) DEFAULT NULL,
  `password_verification_required` tinyint(1) DEFAULT NULL,
  `registration_code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `registration_code` (`registration_code`),
  UNIQUE KEY `app_name` (`app_name`,`app_type`,`app_username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending_user_account`
--

LOCK TABLES `pending_user_account` WRITE;
/*!40000 ALTER TABLE `pending_user_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `pending_user_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rs_attributes`
--

DROP TABLE IF EXISTS `rs_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rs_attributes` (
  `RegisteredServiceImpl_id` bigint(20) NOT NULL,
  `a_name` varchar(255) NOT NULL,
  `a_id` int(11) NOT NULL,
  PRIMARY KEY (`RegisteredServiceImpl_id`,`a_id`),
  KEY `FK4322E153C595E1F` (`RegisteredServiceImpl_id`),
  CONSTRAINT `FK4322E153C595E1F` FOREIGN KEY (`RegisteredServiceImpl_id`) REFERENCES `RegisteredServiceImpl` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rs_attributes`
--

LOCK TABLES `rs_attributes` WRITE;
/*!40000 ALTER TABLE `rs_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `rs_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `version_rank` int(11) NOT NULL,
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(30) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  KEY `schema_version_vr_idx` (`version_rank`),
  KEY `schema_version_ir_idx` (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` VALUES (1,1,'0','<< Flyway Init >>','INIT','<< Flyway Init >>',NULL,'eric','2016-03-01 23:54:51',0,1);
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_question`
--

DROP TABLE IF EXISTS `security_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `security_question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` tinyint(1) NOT NULL,
  `iconPath` varchar(255) DEFAULT NULL,
  `question` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `question` (`question`),
  KEY `enabled_index` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `security_question`
--

LOCK TABLES `security_question` WRITE;
/*!40000 ALTER TABLE `security_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `security_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_question_response`
--

DROP TABLE IF EXISTS `security_question_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `security_question_response` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `response` varchar(255) NOT NULL,
  `security_question_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_question_id` (`security_question_id`,`user_id`),
  KEY `FKD8261D3BD7B30EBD` (`user_id`),
  KEY `FKD8261D3B52A5A93E` (`security_question_id`),
  CONSTRAINT `FKD8261D3B52A5A93E` FOREIGN KEY (`security_question_id`) REFERENCES `security_question` (`id`),
  CONSTRAINT `FKD8261D3BD7B30EBD` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `security_question_response`
--

LOCK TABLES `security_question_response` WRITE;
/*!40000 ALTER TABLE `security_question_response` DISABLE KEYS */;
/*!40000 ALTER TABLE `security_question_response` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` tinyint(1) NOT NULL,
  `first_name` varchar(60) NOT NULL,
  `last_name` varchar(60) NOT NULL,
  `password_recovery_code` varchar(32) DEFAULT NULL,
  `password_recovery_code_created_time` datetime DEFAULT NULL,
  `username` varchar(120) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_account`
--

DROP TABLE IF EXISTS `user_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(60) DEFAULT NULL,
  `app_name` varchar(255) NOT NULL,
  `app_type` varchar(255) NOT NULL,
  `app_username` varchar(255) NOT NULL,
  `disabled` tinyint(1) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_type` (`app_type`,`app_name`,`app_username`),
  UNIQUE KEY `user_id` (`user_id`,`app_name`,`app_type`),
  KEY `FK14C321B9D7B30EBD` (`user_id`),
  CONSTRAINT `FK14C321B9D7B30EBD` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_account`
--

LOCK TABLES `user_account` WRITE;
/*!40000 ALTER TABLE `user_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_authority`
--

DROP TABLE IF EXISTS `user_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_authority` (
  `user_id` bigint(20) NOT NULL,
  `authority_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`authority_id`),
  KEY `FKB55BEBCFD7B30EBD` (`user_id`),
  KEY `FKB55BEBCFEE89E7D7` (`authority_id`),
  CONSTRAINT `FKB55BEBCFEE89E7D7` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`id`),
  CONSTRAINT `FKB55BEBCFD7B30EBD` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_authority`
--

LOCK TABLES `user_authority` WRITE;
/*!40000 ALTER TABLE `user_authority` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_password`
--

DROP TABLE IF EXISTS `user_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_password` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` tinyint(1) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `password_encoded` varchar(255) DEFAULT NULL,
  `password_encoded_md5` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4B8D148FD7B30EBD` (`user_id`),
  CONSTRAINT `FK4B8D148FD7B30EBD` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_password`
--

LOCK TABLES `user_password` WRITE;
/*!40000 ALTER TABLE `user_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_password` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-01 16:59:26
