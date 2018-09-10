DROP TABLE IF EXISTS `customer`;
DROP TABLE IF EXISTS `new_customer`;

CREATE TABLE `customer` (
  `id` identity primary key,
  `firstName` varchar(255) default NULL,
  `lastName` varchar(255) default NULL,
  `birthdate` varchar(255)
) ;

CREATE TABLE `new_customer` (
   `id` identity primary key,
  `firstName` varchar(255) default NULL,
  `lastName` varchar(255) default NULL,
  `birthdate` varchar(255)
) ;


