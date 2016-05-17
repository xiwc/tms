/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50614
Source Host           : localhost:3306
Source Database       : lhjz

Target Server Type    : MYSQL
Target Server Version : 50614
File Encoding         : 65001

Date: 2015-04-08 20:05:57
*/

SET FOREIGN_KEY_CHECKS=0;
SET NAMES 'utf8';

-- ----------------------------
-- Table structure for authorities
-- ----------------------------
--DROP TABLE IF EXISTS `authorities`;
--CREATE TABLE `authorities` (
--  `username` varchar(50) NOT NULL,
--  `authority` varchar(50) NOT NULL,
--  UNIQUE KEY `ix_auth_username` (`username`,`authority`),
--  CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of authorities
-- ----------------------------
INSERT INTO `authorities` (username, authority) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO `authorities` (username, authority) VALUES ('admin', 'ROLE_USER');
INSERT INTO `authorities` (username, authority) VALUES ('admin', 'ROLE_SUPER');
INSERT INTO `authorities` (username, authority) VALUES ('xiwc', 'ROLE_ADMIN');
INSERT INTO `authorities` (username, authority) VALUES ('xiwc', 'ROLE_USER');
INSERT INTO `authorities` (username, authority) VALUES ('lhjz', 'ROLE_USER');
INSERT INTO `authorities` (username, authority) VALUES ('test1', 'ROLE_USER');
INSERT INTO `authorities` (username, authority) VALUES ('test2', 'ROLE_USER');

-- ----------------------------
-- Table structure for groups
-- ----------------------------
--DROP TABLE IF EXISTS `groups`;
--CREATE TABLE `groups` (
--  `id` bigint(20) NOT NULL AUTO_INCREMENT,
--  `group_name` varchar(50) NOT NULL,
--  PRIMARY KEY (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of groups
-- ----------------------------

-- ----------------------------
-- Table structure for group_authorities
-- ----------------------------
--DROP TABLE IF EXISTS `group_authorities`;
--CREATE TABLE `group_authorities` (
--  `group_id` bigint(20) NOT NULL,
--  `authority` varchar(50) NOT NULL,
--  KEY `fk_group_authorities_group` (`group_id`),
--  CONSTRAINT `fk_group_authorities_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of group_authorities
-- ----------------------------

-- ----------------------------
-- Table structure for group_members
-- ----------------------------
--DROP TABLE IF EXISTS `group_members`;
--CREATE TABLE `group_members` (
--  `id` bigint(20) NOT NULL AUTO_INCREMENT,
--  `username` varchar(50) NOT NULL,
--  `group_id` bigint(20) NOT NULL,
--  PRIMARY KEY (`id`),
--  KEY `fk_group_members_group` (`group_id`),
--  CONSTRAINT `fk_group_members_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of group_members
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
--DROP TABLE IF EXISTS `users`;
--CREATE TABLE `users` (
--  `username` varchar(50) NOT NULL,
--  `password` varchar(255) NOT NULL,
--  `enabled` tinyint(1) NOT NULL,
--  PRIMARY KEY (`username`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` (username, password, enabled, status, create_date, version) VALUES ('admin', '$2a$10$of1L1pNENMuUeP2/pMfy1ePScKhrOzHIrHsuhL2u1ieoXClLP5wFG', '', 'Bultin', '2015-04-25 10:01:51', '0');
INSERT INTO `users` (username, password, enabled, status, create_date, version) VALUES ('xiwc', '$2a$10$qR3ar2k/g9gsLgPKAqqprOcN4tsfQAiSd7mdLNDIEC4ytwAUKKgzO', '', 'Normal', '2015-04-25 10:01:51', '0');
INSERT INTO `users` (username, password, enabled, status, create_date, version) VALUES ('lhjz', '$2a$10$cHoqi0vbJpkOe.ShF7A6qO0kf8lKOH/6tHr3oe7vA4UcKdtgGeJnq', '', 'Normal', '2015-04-25 10:01:51', '0');
INSERT INTO `users` (username, password, enabled, status, create_date, version) VALUES ('test1', '$2a$10$cHoqi0vbJpkOe.ShF7A6qO0kf8lKOH/6tHr3oe7vA4UcKdtgGeJnq', '', 'Normal', '2015-04-25 10:01:51', '0');
INSERT INTO `users` (username, password, enabled, status, create_date, version) VALUES ('test2', '$2a$10$cHoqi0vbJpkOe.ShF7A6qO0kf8lKOH/6tHr3oe7vA4UcKdtgGeJnq', '', 'Normal', '2015-04-25 10:01:51', '0');

-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
--DROP TABLE IF EXISTS `persistent_logins`;
--CREATE TABLE `persistent_logins` (
--  `series` varchar(64) NOT NULL,
--  `last_used` datetime NOT NULL,
--  `token` varchar(64) NOT NULL,
--  `username` varchar(64) NOT NULL,
--  PRIMARY KEY (`series`)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of persistent_logins

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('1', '2015-04-25 10:01:51', 'img08_b.jpg', 'upload/img/0/', 'Bultin', 'admin', '4dfa871e-6cc1-480c-a1a8-2a56dae30b51.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('2', '2015-04-25 10:01:51', 'img07_b.jpg', 'upload/img/0/', 'Bultin', 'admin', '91baaaf6-237f-47c1-80d1-6af3abd267a3.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('3', '2015-04-25 10:01:52', 'img04_b.jpg', 'upload/img/0/', 'Bultin', 'admin', '8734d1cc-0c69-4ab2-9611-3c86e7ca2c58.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('4', '2015-04-25 10:01:52', 'img01_b.jpg', 'upload/img/0/', 'Bultin', 'admin', 'b1e70aa4-2013-4150-9d71-47ebaca5963d.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('5', '2015-04-25 10:01:52', 'img02_b.jpg', 'upload/img/0/', 'Bultin', 'admin', '66170316-4b4c-45fe-bc55-9f25459a2072.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('6', '2015-04-25 10:01:52', 'img06_b.jpg', 'upload/img/0/', 'Bultin', 'admin', '94947a26-3c39-495e-8c65-62e7a7ade429.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('7', '2015-04-25 10:01:52', 'img05_b.jpg', 'upload/img/0/', 'Bultin', 'admin', 'ba77d88a-3a9f-4afc-bafe-27f05be65114.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('8', '2015-04-25 10:01:53', 'img03_b.jpg', 'upload/img/0/', 'Bultin', 'admin', '0c590fa7-430f-428a-aa38-fa29227caa43.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('10', '2015-05-31 14:27:58', '1.jpg', 'upload/img/0/', 'Bultin', 'admin', '09c2ec3a-3afc-43ff-af20-3828f0744446.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('11', '2015-05-31 14:27:58', '2.jpg', 'upload/img/0/', 'Bultin', 'admin', '749727e8-d955-4652-adf2-1ef217ee64b8.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('12', '2015-05-31 14:27:58', '3.jpg', 'upload/img/0/', 'Bultin', 'admin', 'bc2b78b3-e808-420e-a156-5ef54887d714.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('13', '2015-05-31 14:27:59', 'img-b1.jpg', 'upload/img/0/', 'Bultin', 'admin', '0a6bc830-91d4-4675-a413-4d93823f5ba0.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('14', '2015-05-31 14:27:59', 'img-b2.jpg', 'upload/img/0/', 'Bultin', 'admin', '1dee3a09-87a5-463d-819f-0e1e93bb5e7d.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('15', '2015-05-31 14:27:59', 'img-b3.jpg', 'upload/img/0/', 'Bultin', 'admin', 'e60b83c8-f450-4c03-8275-b415e02db4a1.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('17', '2015-06-09 19:47:19', 'map1.jpg', 'upload/img/0/', 'Normal', 'admin', 'a65dfd76-ace8-489d-9418-d240138bc60d.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('19', '2015-06-17 16:27:57', '‎2014‎‎9‎‎5‎17‎‎02‎‎48.jpg', 'upload/img/0/', 'Normal', 'lhjz', '28a2a8d9-f1ed-4a79-b78d-4bf4bb01a9b7.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('20', '2015-06-17 16:27:57', '‎2014‎‎10‎‎19‎10‎‎26‎‎23.jpg', 'upload/img/0/', 'Normal', 'lhjz', 'a99fc4a6-563f-4071-8a55-98500a10a564.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('21', '2015-06-17 16:27:58', '‎2014‎‎10‎‎19‎10‎‎26‎‎19.jpg', 'upload/img/0/', 'Normal', 'lhjz', '4d074bb7-5649-485a-b3bc-01304ff8ea37.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('22', '2015-06-17 16:27:58', '‎2014‎‎10‎‎19‎10‎‎26‎‎15.jpg', 'upload/img/0/', 'Normal', 'lhjz', 'de55fde4-1eb2-4e9f-95a3-6c860ff8192f.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('23', '2015-06-17 16:27:59', '‎2014‎‎10‎‎19‎10‎‎26‎‎12.jpg', 'upload/img/0/', 'Normal', 'lhjz', 'ad694594-c6ba-45d1-b367-2a6303c2a37b.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('24', '2015-06-17 16:27:59', '‎2014‎‎10‎‎19‎10‎‎25‎‎53.jpg', 'upload/img/0/', 'Normal', 'lhjz', '841ec752-20f5-45c3-a489-747e91c608e6.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('25', '2015-06-17 16:28:00', '‎2014‎‎10‎‎19‎10‎‎26‎‎26.jpg', 'upload/img/0/', 'Normal', 'lhjz', '324a37e1-aba8-4d9e-bf0d-78c6e47dd6dd.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('26', '2015-06-17 16:28:00', '‎2014‎‎10‎‎19‎10‎‎26‎‎29.jpg', 'upload/img/0/', 'Normal', 'lhjz', 'ca1a9999-1b62-4015-82a2-71a249f896e1.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('27', '2015-06-17 16:28:01', '‎2014‎‎10‎‎19‎10‎‎26‎‎34.jpg', 'upload/img/0/', 'Normal', 'lhjz', '88017fc2-6466-461e-b294-7e1bbcd4aa9c.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('28', '2015-06-17 16:47:01', '296BEEDD4C4C1B3A2794E7C609E1CE70.jpg', 'upload/img/0/', 'Normal', 'lhjz', '067b4fdd-73a7-49cc-983e-dfeefb88f943.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('29', '2015-06-17 16:47:01', '2F5FDD318C571EB0605BBB3B7795F765.jpg', 'upload/img/0/', 'Normal', 'lhjz', '85513508-daf2-4574-a2b8-1d854f69afc4.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('30', '2015-06-18 15:28:27', 'thicp邮箱问题.png', 'upload/img/0/', 'Normal', 'lhjz', '25b762c7-5e72-47e6-95d1-2ac013636c4c.png', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('31', '2015-06-20 17:28:55', '20100211100357_27.jpg', 'upload/img/0/', 'Normal', 'lhjz', '7e12e5da-39c0-43a3-8f1b-64733bb31ebd.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('32', '2015-06-20 17:29:13', '刘存峰治疗后.jpg', 'upload/img/0/', 'Normal', 'lhjz', '211843ca-19a3-488d-8914-4d7db59c7786.jpg', '0');
INSERT INTO `file` (`id`, `create_date`, `name`, `path`, `status`, `username`, `uuid_name`, `version`) VALUES ('33', '2015-06-20 17:29:15', '刘存峰治疗前.jpg', 'upload/img/0/', 'Normal', 'lhjz', '9ebc939b-78b8-41f1-8502-3339bc637692.jpg', '0');

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` (`id`, `create_date`, `_key`, `more`, `status`, `update_date`, `username`, `value`, `version`) VALUES ('1', '2015-06-09 19:39:48', 'Contact', null, 'Normal', '2015-06-09 22:26:22', 'lhjz', '{\"name\":\"北京立衡脊柱健康科技有限公司\",\"addr\":\"北京丰台马家堡东路公益东桥北路东海上海花园168号立衡脊柱\",\"phone\":\"13366675506\",\"mail\":\"lihengjizhu@163.com\",\"qq\":\"472256943\",\"bus\":\"125路\",\"map\":\"upload/img/0/a65dfd76-ace8-489d-9418-d240138bc60d.jpg\"}', '2');

-- ----------------------------
-- Records of settings
-- ----------------------------
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('4', '为了清除疝出的椎间隙内的椎间盘物质，德国SFESYS(椎间孔扩大术脊椎内窥镜系统)使用特殊的后外侧，经椎间孔入路。和传统的北侧入路相比，对于病人的创伤较小。使用北侧椎版切除术清除椎间孔内和经椎间孔脱出的椎间盘碎片，为了达到预计的手术部位对脊柱稳定', '2015-06-09 22:12:43', null, 'upload/img/0/91baaaf6-237f-47c1-80d1-6af3abd267a3.jpg', '0', '11', 'HotNews', '脊柱微创系统', 'Index', 'Normal', '脊柱微创系统', 'admin', '1');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('5', '生活中这些小方法对腰椎间盘突出的治疗起到一定的作用，能够解决自我治疗腰椎间盘突出症的目的。不过，很多时候饮食也是一个不可忽视的方面\n维生素E有扩张血管、促进血液循环、消除肌肉紧张的作用，同样能缓解腰椎间盘突出的疼痛的症状。日常生活中含维生素E多的食物有鳝鱼、大豆、花生米、芝麻、杏仁等。', '2015-06-09 22:12:43', null, 'upload/img/0/8734d1cc-0c69-4ab2-9611-3c86e7ca2c58.jpg', '0', '10', 'HotNews', '腰椎间盘突出患者食物应注意', 'Index', 'Normal', '腰椎间盘突出患者食物应注意', 'admin', '1');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('6', '脊髓灰质炎是脊髓灰质炎病毒引起的急性传染病，好发于小儿，又称“小儿麻痹症”。病情轻重差异很大，临床表现不同程度肌肉弛缓性麻痹。严重者可于急性期因呼吸、吞咽麻痹而死亡，多数患者可治愈，少数留有后遗症。发病年龄以4个月至5岁小儿发病率最高。', '2015-06-09 22:12:43', null, 'upload/img/0/b1e70aa4-2013-4150-9d71-47ebaca5963d.jpg', '0', '5', 'HotNews', '脊髓灰质炎中医诊疗技术', 'Index', 'Normal', '脊髓灰质炎中医诊疗技术', 'admin', '1');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('7', '近年来，以治疗椎间盘突出为代表的脊柱手术开展得越来越多，治疗脊膜瘤、神经鞘瘤也纷纷在人体这条“大梁”上开刀，但刀锋所到之处，不仅切除了病根，也难免损伤了神经，为患者在术后留下疼痛的隐患。不少患者以为“做手术哪有不疼的”，术后长年受到疼痛的煎熬只好认命，其实，目前对术后神经痛已有了有效的治疗方法。\n　　文/记者伍君仪 通讯员崔艳玲、邝莹', '2015-06-09 22:12:43', null, 'upload/img/0/4dfa871e-6cc1-480c-a1a8-2a56dae30b51.jpg', '0', '6', 'HotNews', '脊柱术后神经痛 慎行“一刀切”', 'Index', 'Normal', '脊柱术后神经痛 慎行“一刀切”', 'admin', '1');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('8', null, '2015-06-09 22:14:36', null, null, '0', '4', 'MoreNews', '1', 'Index', 'Normal', '关爱健康—认识脊柱病症位置', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('9', null, '2015-06-09 22:14:36', null, null, '0', '5', 'MoreNews', '1', 'Index', 'Normal', '脊髓灰质炎中医诊疗技术', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('10', null, '2015-06-09 22:14:36', null, null, '0', '3', 'MoreNews', '1', 'Index', 'Normal', '脊柱对人体的作用是怎样的?', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('11', null, '2015-06-09 22:14:36', null, null, '0', '1', 'MoreNews', '1', 'Index', 'Normal', '颈椎病所引发的病症介绍', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('12', null, '2015-06-09 22:14:36', null, null, '0', '2', 'MoreNews', '1', 'Index', 'Normal', '用瑜伽认识你的脊柱', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('13', null, '2015-06-09 22:14:44', null, null, '0', '7', 'MoreNews', '2', 'Index', 'Normal', '详解强直性脊柱炎病因', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('14', null, '2015-06-09 22:14:44', null, null, '0', '8', 'MoreNews', '2', 'Index', 'Normal', '早期强直性脊柱炎的症状', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('15', null, '2015-06-09 22:14:44', null, null, '0', '5', 'MoreNews', '2', 'Index', 'Normal', '脊髓灰质炎中医诊疗技术', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('16', null, '2015-06-09 22:14:44', null, null, '0', '6', 'MoreNews', '2', 'Index', 'Normal', '脊柱术后神经痛 慎行“一刀切”', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('17', null, '2015-06-09 22:14:44', null, null, '0', '10', 'MoreNews', '2', 'Index', 'Normal', '腰椎间盘突出患者食物应注意', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('18', null, '2015-06-09 22:14:44', null, null, '0', '9', 'MoreNews', '2', 'Index', 'Normal', 'NO与脊髓水平伤害性信息调控', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('19', null, '2015-06-09 22:15:00', null, null, '0', '8', 'MoreNews', '1', 'Index', 'Normal', '早期强直性脊柱炎的症状', 'admin', '0');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('21', '', '2015-06-17 16:47:35', null, 'upload/img/0/067b4fdd-73a7-49cc-983e-dfeefb88f943.jpg', '0', '', 'BigImg', '动态详细链接', 'Index', 'Normal', '', 'lhjz', '1');
INSERT INTO `settings` (`id`, `content`, `create_date`, `detail`, `img_url`, `_index`, `link`, `module`, `more`, `page`, `status`, `title`, `username`, `version`) VALUES ('22', null, '2015-06-17 16:47:35', null, 'upload/img/0/85513508-daf2-4574-a2b8-1d854f69afc4.jpg', '0', null, 'BigImg', null, 'Index', 'Normal', null, 'lhjz', '0');


-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('1', '2016-05-17 15:55:49', 'admin', '团队协作', 'STEP', 'Normal', '0');
INSERT INTO `project` VALUES ('2', '2016-05-17 15:56:46', 'admin', '新致云主站', 'WORK', 'Normal', '0');
INSERT INTO `project` VALUES ('3', '2016-05-17 15:57:54', 'admin', '管理控制台', 'CONSOLE', 'Normal', '0');


-- ----------------------------
-- Records of language
-- ----------------------------
INSERT INTO `language` VALUES ('1', '2016-05-17 15:59:25', 'admin', '中文', 'zh', 'Normal', '0');
INSERT INTO `language` VALUES ('2', '2016-05-17 15:59:52', 'admin', '英文', 'en', 'Normal', '0');
INSERT INTO `language` VALUES ('3', '2016-05-17 16:00:30', 'admin', '日语', 'jp', 'Normal', '0');


-- ----------------------------
-- Records of language_project
-- ----------------------------
INSERT INTO `language_project` VALUES ('1', '1');
INSERT INTO `language_project` VALUES ('2', '1');
INSERT INTO `language_project` VALUES ('3', '1');
INSERT INTO `language_project` VALUES ('1', '2');
INSERT INTO `language_project` VALUES ('2', '2');
INSERT INTO `language_project` VALUES ('3', '2');
INSERT INTO `language_project` VALUES ('1', '3');
INSERT INTO `language_project` VALUES ('2', '3');
INSERT INTO `language_project` VALUES ('3', '3');
