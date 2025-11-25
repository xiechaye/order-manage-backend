-- 订单管理系统数据库脚本
-- 包含订单管理、用户管理和图片上传功能

-- 创建数据库
CREATE DATABASE IF NOT EXISTS order_manage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE order_manage;

-- =====================================================
-- 订单管理部分
-- =====================================================

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单编号',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名',
    customer_phone VARCHAR(20) COMMENT '客户电话',
    customer_email VARCHAR(100) COMMENT '客户邮箱',
    license_plate VARCHAR(20) COMMENT '车牌号（可选）',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
    product_quantity INT NOT NULL DEFAULT 1 COMMENT '产品数量',
    order_status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待取货，1-已完成，2-已取消',
    remarks TEXT COMMENT '订单备注',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_no (order_no),
    KEY idx_customer_name (customer_name),
    KEY idx_order_status (order_status),
    KEY idx_created_at (created_at),
    KEY idx_deleted (deleted),
    KEY idx_license_plate (license_plate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- =====================================================
-- 用户管理部分
-- =====================================================

-- 管理员用户表（业务管理）
CREATE TABLE IF NOT EXISTS `admin_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(32) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname` varchar(32) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1启用 0禁用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：1删除 0未删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- 普通用户表（图片上传关联用户）
CREATE TABLE IF NOT EXISTS `t_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1:启用，0:禁用）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- =====================================================
-- 图片上传功能
-- =====================================================

-- 图片信息表
CREATE TABLE IF NOT EXISTS `t_image` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID，关联用户表',
    `file_name` varchar(255) NOT NULL COMMENT '文件存储名称',
    `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
    `file_path` varchar(500) NOT NULL COMMENT '文件存储路径',
    `size` bigint NOT NULL COMMENT '文件大小（字节）',
    `mime_type` varchar(100) DEFAULT NULL COMMENT 'MIME类型',
    `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除字段（0:未删除，1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_upload_time` (`upload_time`),
    KEY `idx_user_id_mime_type` (`user_id`, `mime_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片信息表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 订单初始测试数据
INSERT INTO orders (order_no, customer_name, customer_phone, customer_email, license_plate, product_name, product_quantity, order_status, remarks, deleted) VALUES
('ORD202411230001', '张三', '13800138000', 'zhangsan@example.com', '京A12345', 'iPhone 15 Pro', 1, 1, '请尽快发货', 0),
('ORD202411230002', '李四', '13900139000', 'lisi@example.com', '沪B67890', 'MacBook Air', 2, 0, '颜色要银色的', 0),
('ORD202411230003', '王五', '13700137000', 'wangwu@example.com', NULL, 'AirPods Pro', 1, 2, NULL, 0);

-- 默认管理员账号：admin/123456
-- 密码使用BCrypt加密：$2a$10$pC.9/1w1qnaRa11jVl026.jQcAfp5wYs5cYhePEGS5xKTFS/3NGMq
INSERT INTO `admin_user` (`username`, `password`, `nickname`, `avatar`, `status`) VALUES 
('admin', '$2a$10$pC.9/1w1qnaRa11jVl026.jQcAfp5wYs5cYhePEGS5xKTFS/3NGMq', '超级管理员', NULL, 1),
('test', '$2a$10$pC.9/1w1qnaRa11jVl026.jQcAfp5wYs5cYhePEGS5xKTFS/3NGMq', '测试用户', NULL, 1);

-- 普通用户测试数据（密码为123456的BCrypt加密结果）
-- $2a$10$WIpkJ3PWKPqFppoUQW9v4uXue9XBUIjLJcAqzcjvVnpW3kNq4CS2q
INSERT INTO `t_user` (`username`, `password`, `email`) VALUES
('user1', '$2a$10$WIpkJ3PWKPqFppoUQW9v4uXue9XBUIjLJcAqzcjvVnpW3kNq4CS2q', 'user1@example.com'),
('user2', '$2a$10$WIpkJ3PWKPqFppoUQW9v4uXue9XBUIjLJcAqzcjvVnpW3kNq4CS2q', 'user2@example.com'),
('image_user', '$2a$10$WIpkJ3PWKPqFppoUQW9v4uXue9XBUIjLJcAqzcjvVnpW3kNq4CS2q', 'image_test@example.com');