
DROP TABLE IF EXISTS `loinc`;
SET character_set_client = utf8mb4 ;
CREATE TABLE `loinc` (
                         `loinc_num` varchar(10) NOT NULL,
                         `long_common_name` varchar(255) DEFAULT NULL,
                         PRIMARY KEY (`loinc_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
