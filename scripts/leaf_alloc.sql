-- mysql
DROP TABLE IF EXISTS `leaf_alloc`;
CREATE TABLE `leaf_alloc` (
  `biz_tag` varchar(128)  NOT NULL DEFAULT '',
  `prefix`  varchar(4) not null default '',
  `max_id` bigint(20) NOT NULL DEFAULT '1',
  `step` int(11) NOT NULL,
  `description` varchar(256)  DEFAULT NULL,
  `max_number`  bigint(20) not null default 999999,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`biz_tag`)
);

-- pgsql
-- pgsql没有on update，因此要手动更新。
CREATE TABLE leaf_alloc
(
    biz_tag varchar(128) NOT NULL DEFAULT '' primary key,
    prefix varchar(4) not null default '',
    max_id bigint NOT NULL DEFAULT 1,
    step int NOT NULL,
    description varchar(256) DEFAULT NULL,
    max_number bigint not null default 999999,
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
