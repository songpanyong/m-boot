
USE `gh_bf_mimosa`;

DROP TABLE IF EXISTS `T_COMMON_AUDITEVENT`;

CREATE TABLE `T_COMMON_AUDITEVENT` (
  `oid` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `reqId` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventPath` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventStatus` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `extraMessage` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventOwner` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventSource` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `eventType` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventData` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `T_COMMON_CALENDAR`;

CREATE TABLE `T_COMMON_CALENDAR` (
  `oid` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sDate` date DEFAULT NULL,
  `eDate` date DEFAULT NULL,
  `operator` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `regenerator` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `T_COMMON_EVENTLOG`;

CREATE TABLE `T_COMMON_EVENTLOG` (
  `oid` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `reqId` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventPath` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventStatus` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `extraMessage` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventOwner` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventSource` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `eventType` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `eventData` longtext COLLATE utf8_unicode_ci,
  `eventName` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `T_COMMON_MAPPINGINFO`;

CREATE TABLE `T_COMMON_MAPPINGINFO` (
  `oid` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `lastUpdate` datetime DEFAULT NULL,
  PRIMARY KEY (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;