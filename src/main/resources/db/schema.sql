-- 订单管理系统数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS order_manage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE order_manage;

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
    order_status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待处理，1-已确认，2-已发货，3-已完成，4-已取消',
    payment_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-退款中，3-已退款',
    remarks TEXT COMMENT '订单备注',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_order_no (order_no),
    KEY idx_customer_name (customer_name),
    KEY idx_order_status (order_status),
    KEY idx_created_at (created_at),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- 初始测试数据
INSERT INTO orders (order_no, customer_name, customer_phone, customer_email, license_plate, product_name, product_quantity, order_status, payment_status, remarks, deleted) VALUES
('ORD202411230001', '张三', '13800138000', 'zhangsan@example.com', '京A12345', 'iPhone 15 Pro', 1, 1, 1, '请尽快发货', 0),
('ORD202411230002', '李四', '13900139000', 'lisi@example.com', '沪B67890', 'MacBook Air', 2, 0, 0, '颜色要银色的', 0),
('ORD202411230003', '王五', '13700137000', 'wangwu@example.com', NULL, 'AirPods Pro', 1, 2, 1, NULL, 0);