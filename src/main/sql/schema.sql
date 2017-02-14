-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
USE seckill;
-- 创建秒杀库存表
CREATE TABLE seckill (
  `seckill_id`  BIGINT       NOT NULL AUTO_INCREMENT
  COMMENT '商品库存id',
  `name`        VARCHAR(120) NOT NULL
  COMMENT '商品名称',
  `number`      INT          NOT NULL
  COMMENT '商品数量',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '秒杀创建时间',
  `start_time`  TIMESTAMP    NOT NULL
  COMMENT '秒杀开始时间',
  `end_time`    TIMESTAMP    NOT NULL
  COMMENT '秒杀结束时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  DEFAULT CHARSET = utf8
  COMMENT '秒杀库存表';

-- 初始化数据
INSERT INTO seckill
(NAME, NUMBER, start_time, end_time)
VALUES
  ('1000秒杀iphone7', 100, '2017-2-7 00:00:00', '2017-2-8 00:00:00'),
  ('500秒杀ipad4', 200, '2017-2-7 00:00:00', '2017-2-8 00:00:00'),
  ('200秒杀小米6', 300, '2017-2-7 00:00:00', '2017-2-8 00:00:00'),
  ('100秒杀红米6', 400, '2017-2-7 00:00:00', '2017-2-8 00:00:00');

-- 秒杀成功明细表
CREATE TABLE success_killed (
  `seckill_id` BIGINT NOT NULL COMMENT '秒杀商品id',
  `user_phone` BIGINT NOT NULL  COMMENT '用户手机号',
  `state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识：-1：无效 0：成功 1：已付款 2：已发货',
  `create_time` TIMESTAMP NOT NULL COMMENT '表单创建时间',
  PRIMARY KEY (seckill_id, user_phone), /*联合主键*/
  KEY idx_create_time(create_time)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT '秒杀成功明细表';


