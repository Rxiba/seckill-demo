/*
 Navicat Premium Data Transfer

 Source Server         : Demo
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : localhost:3306
 Source Schema         : seckill

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 15/07/2022 15:49:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `goods_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `goods_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `goods_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品价格',
  `goods_stock` int NULL DEFAULT 0 COMMENT '商品库存,-1表示没有限制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_goods
-- ----------------------------
INSERT INTO `t_goods` VALUES (1, 'IPhone13', 'IPhone13 128GB', '/img/iphone13.png', '阉割版A15', 5999.00, 100);
INSERT INTO `t_goods` VALUES (2, 'IPhone13 Pro', 'IPhone13 Pro 128GB', '/img/iphone13pro.png', '性价比之王', 7999.00, 50);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `goods_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `delivery_addr_id` bigint NULL DEFAULT NULL COMMENT '收获地址ID',
  `goods_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冗余过来的商品名称',
  `goods_count` int NULL DEFAULT 0 COMMENT '商品数量',
  `goods_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品价格',
  `order_channel` tinyint NULL DEFAULT 0 COMMENT '1pc,2android,3ios',
  `status` tinyint NULL DEFAULT 0 COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` datetime NULL DEFAULT NULL COMMENT '订单创建时间',
  `pay_date` datetime NULL DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49705 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (49703, 15802218687, 1, 0, 'IPhone13', 1, 5499.00, 1, 0, '2022-07-14 19:50:37', NULL);
INSERT INTO `t_order` VALUES (49704, 15802218687, 2, 0, 'IPhone13 Pro', 1, 8499.00, 1, 0, '2022-07-15 15:17:36', NULL);

-- ----------------------------
-- Table structure for t_seckill_goods
-- ----------------------------
DROP TABLE IF EXISTS `t_seckill_goods`;
CREATE TABLE `t_seckill_goods`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀商品ID',
  `goods_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `seckill_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
  `stock_count` int NULL DEFAULT NULL COMMENT '秒杀库存数量',
  `start_date` datetime NULL DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime NULL DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_seckill_goods
-- ----------------------------
INSERT INTO `t_seckill_goods` VALUES (1, 1, 5499.00, 9, '2022-07-14 15:35:00', '2022-07-14 21:00:00');
INSERT INTO `t_seckill_goods` VALUES (2, 2, 8499.00, 4, '2022-07-14 16:41:00', '2022-07-18 23:00:00');

-- ----------------------------
-- Table structure for t_seckill_order
-- ----------------------------
DROP TABLE IF EXISTS `t_seckill_order`;
CREATE TABLE `t_seckill_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID',
  `goods_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `seckill_uid_gid`(`user_id`, `goods_id`) USING BTREE COMMENT '用户id+商品id的唯一索引，解决同一个用户秒杀多个商品'
) ENGINE = InnoDB AUTO_INCREMENT = 49696 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_seckill_order
-- ----------------------------
INSERT INTO `t_seckill_order` VALUES (49694, 15802218687, 49703, 1);
INSERT INTO `t_seckill_order` VALUES (49695, 15802218687, 49704, 2);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL COMMENT '用户ID，手机号码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt)+salt)',
  `slat` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '盐值',
  `head` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `register_date` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime NULL DEFAULT NULL COMMENT '最后一次登录时间',
  `login_count` int(11) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '登录次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (13000000000, 'user0', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000001, 'user1', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000002, 'user2', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000003, 'user3', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000004, 'user4', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000005, 'user5', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000006, 'user6', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000007, 'user7', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000008, 'user8', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000009, 'user9', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000010, 'user10', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000011, 'user11', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000012, 'user12', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000013, 'user13', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000014, 'user14', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000015, 'user15', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000016, 'user16', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000017, 'user17', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000018, 'user18', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (13000000019, 'user19', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, '2022-07-11 15:47:56', NULL, 00000000001);
INSERT INTO `t_user` VALUES (15802218687, 'Yoke', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, NULL, NULL, NULL);
INSERT INTO `t_user` VALUES (18622998017, 'xb', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c', NULL, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
