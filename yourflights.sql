-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: localhost    Database: yourflights
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `airline`
--

DROP TABLE IF EXISTS `airline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `airline` (
  `id` bigint NOT NULL,
  `country` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhb8nqdq837qs1vwcpkbfjdlvf` (`user_id`),
  CONSTRAINT `FKhb8nqdq837qs1vwcpkbfjdlvf` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `airline`
--

LOCK TABLES `airline` WRITE;
/*!40000 ALTER TABLE `airline` DISABLE KEYS */;
INSERT INTO `airline` VALUES (2,'Poland','Polskie Linie Lotnicze LOT S.A. is the flag carrier of Poland. Based in Warsaw and established on 29 December 1928, it is one of the world\'s oldest airlines in operation.','LOT',NULL),(3,'Germany','Lufthansa is the largest German airline which, when combined with its subsidiaries, is the second largest airline in Europe in terms of passengers carried.','Lufthansa',NULL),(4,'Ireland','Ryanair is an Irish Low-cost airline founded in 1984. It has headquartered in Swords, Dublin, with its primary operational bases at Dublin and London Stansted airports.','Ryanair',NULL);
/*!40000 ALTER TABLE `airline` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `second_name` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj8dlm21j202cadsbfkoem0s58` (`user_id`),
  CONSTRAINT `FKj8dlm21j202cadsbfkoem0s58` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (246,'Wisła','Polska','user@user.pl','Adam','123456789','11-112','Małysz','Zakopiańska','user',245);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flight`
--

DROP TABLE IF EXISTS `flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flight` (
  `id` bigint NOT NULL,
  `arrival_date` varchar(255) DEFAULT NULL,
  `arrival_time` varchar(255) DEFAULT NULL,
  `departure_date` varchar(255) DEFAULT NULL,
  `departure_time` varchar(255) DEFAULT NULL,
  `place_of_departure` varchar(255) DEFAULT NULL,
  `place_of_destination` varchar(255) DEFAULT NULL,
  `airlineid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn9jlc217hutklfisrdoxu900u` (`airlineid`),
  CONSTRAINT `FKn9jlc217hutklfisrdoxu900u` FOREIGN KEY (`airlineid`) REFERENCES `airline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flight`
--

LOCK TABLES `flight` WRITE;
/*!40000 ALTER TABLE `flight` DISABLE KEYS */;
INSERT INTO `flight` VALUES (5,'22/01/2021','15:45','22/01/2021','12:30','Warsaw','Lisbon',2),(72,'24/01/2021','02:00','23/01/2021','18:00','London','New York',3),(77,'28/01/2021','13:35','28/01/2021','11:00','Miami','New Jersey',4),(81,'30/01/2021','18:20','30/01/2021','17:30','Cracow','Warsaw',2);
/*!40000 ALTER TABLE `flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (287);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` bigint NOT NULL,
  `reservation_date` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `customerid` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrdnwuxdns9a83jli5rs0mh5ql` (`customerid`),
  CONSTRAINT `FKrdnwuxdns9a83jli5rs0mh5ql` FOREIGN KEY (`customerid`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (193,'16/01/2021','admin',NULL,NULL),(195,'16/01/2021','admin',NULL,NULL),(274,'18/01/2021','user',NULL,'Created - informed');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_category`
--

DROP TABLE IF EXISTS `ticket_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_category` (
  `id` bigint NOT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  `category_price` decimal(19,2) NOT NULL,
  `total_number_of_seats` int NOT NULL,
  `flightid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtoblbhsgy3h2d33uekp0vbjv6` (`flightid`),
  CONSTRAINT `FKtoblbhsgy3h2d33uekp0vbjv6` FOREIGN KEY (`flightid`) REFERENCES `flight` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_category`
--

LOCK TABLES `ticket_category` WRITE;
/*!40000 ALTER TABLE `ticket_category` DISABLE KEYS */;
INSERT INTO `ticket_category` VALUES (54,'Economic Class',250.00,50,5),(55,'First Class',550.00,20,5),(56,'Business Class',300.00,30,5),(73,'Economic Class',200.00,60,72),(74,'Business Class',350.00,20,72),(75,'First Class',600.00,10,72),(78,'Business Class',300.00,15,77),(79,'Economic Class',150.00,70,77),(80,'First Class',700.00,10,77),(82,'Economic Class',50.00,60,81),(83,'Business Class',170.00,20,81),(84,'First Class',250.00,10,81);
/*!40000 ALTER TABLE `ticket_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_discount`
--

DROP TABLE IF EXISTS `ticket_discount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_discount` (
  `id` bigint NOT NULL,
  `discount` double NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_discount`
--

LOCK TABLES `ticket_discount` WRITE;
/*!40000 ALTER TABLE `ticket_discount` DISABLE KEYS */;
INSERT INTO `ticket_discount` VALUES (57,15,'Student Discount');
/*!40000 ALTER TABLE `ticket_discount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_order`
--

DROP TABLE IF EXISTS `ticket_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_order` (
  `id` bigint NOT NULL,
  `number_of_seats` int NOT NULL,
  `reservation_id` bigint DEFAULT NULL,
  `ticket_categoryid` bigint DEFAULT NULL,
  `ticket_discountid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjo3wm4olmccjoa1n17bbmsr39` (`reservation_id`),
  KEY `FK4mb5jbmixfh7hy5xph6p3ktgr` (`ticket_categoryid`),
  KEY `FKcay9bobq7ve5mui0at1o0wwng` (`ticket_discountid`),
  CONSTRAINT `FK4mb5jbmixfh7hy5xph6p3ktgr` FOREIGN KEY (`ticket_categoryid`) REFERENCES `ticket_category` (`id`),
  CONSTRAINT `FKcay9bobq7ve5mui0at1o0wwng` FOREIGN KEY (`ticket_discountid`) REFERENCES `ticket_discount` (`id`),
  CONSTRAINT `FKjo3wm4olmccjoa1n17bbmsr39` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_order`
--

LOCK TABLES `ticket_order` WRITE;
/*!40000 ALTER TABLE `ticket_order` DISABLE KEYS */;
INSERT INTO `ticket_order` VALUES (194,1,193,54,57),(196,1,195,73,57),(273,19,274,55,NULL),(282,1,274,55,NULL);
/*!40000 ALTER TABLE `ticket_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_role` int DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (245,'user@user.pl','$2a$10$3BxdVQdJ3WLRS/kZszdZMek8Oa8GmomfzZ.qSn3yUnbmCfT8RvcZ.',0,'user'),(2222,'admin@admin.pl','admin',2,'admin');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-01-18 22:18:29
