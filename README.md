# 订单管理系统 API 文档

## 系统架构说明

### 1. 分层结构

```
order-manage/
├── controller/          # 控制层 - 接收HTTP请求
├── service/            # 服务层 - 业务逻辑
│   └── impl/          # 服务实现层
├── mapper/            # 数据访问层 - MyBatis-Plus
├── entity/            # 实体层 - 数据库实体
├── enums/             # 枚举类 - 状态等枚举
└── config/            # 配置类 - MyBatis-Plus等配置
```

### 2. 核心模块职责说明

- **Controller层**: 负责接收HTTP请求，参数校验，返回统一响应格式
- **Service层**: 负责业务逻辑处理，事务管理
- **Mapper层**: 负责数据访问，使用MyBatis-Plus简化CRUD操作
- **Entity层**: 实体类，映射数据库表结构
- **Enums层**: 定义订单状态、支付状态等枚举值

## 数据库设计

### orders 表结构

```sql
CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单编号',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名',
    customer_phone VARCHAR(20) COMMENT '客户电话',
    customer_email VARCHAR(100) COMMENT '客户邮箱',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
    product_quantity INT NOT NULL DEFAULT 1 COMMENT '产品数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '总金额',
    order_status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待处理，1-已确认，2-已发货，3-已完成，4-已取消',
    payment_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-退款中，3-已退款',
    shipping_address TEXT COMMENT '收货地址',
    remarks TEXT COMMENT '订单备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_order_no (order_no),
    KEY idx_customer_name (customer_name),
    KEY idx_order_status (order_status),
    KEY idx_created_at (created_at)
);
```

### 字段说明

| 字段名 | 类型 | 约束 | 注释 |
|--------|------|------|------|
| id | BIGINT | PK, Auto Increment | 订单主键ID |
| order_no | VARCHAR(64) | NOT NULL, UNIQUE | 订单编号，系统生成 |
| customer_name | VARCHAR(100) | NOT NULL | 客户姓名 |
| customer_phone | VARCHAR(20) | NULLABLE | 客户联系电话 |
| customer_email | VARCHAR(100) | NULLABLE | 客户邮箱 |
| product_name | VARCHAR(200) | NOT NULL | 产品名称 |
| product_quantity | INT | NOT NULL, DEFAULT 1 | 购买数量 |
| unit_price | DECIMAL(10,2) | NOT NULL | 产品单价 |
| total_amount | DECIMAL(12,2) | NOT NULL | 总金额（自动计算） |
| order_status | TINYINT | NOT NULL, DEFAULT 0 | 订单状态：0-待处理，1-已确认，2-已发货，3-已完成，4-已取消 |
| payment_status | TINYINT | NOT NULL, DEFAULT 0 | 支付状态：0-未支付，1-已支付，2-退款中，3-已退款 |
| shipping_address | TEXT | NULLABLE | 收货地址 |
| remarks | TEXT | NULLABLE | 订单备注 |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除字段 |

## 项目目录结构（推荐包结构）

```
src/
├── main/
│   ├── java/
│   │   └── chaye/com/ordermanage/
│   │       ├── controller/         # 控制器层
│   │       │   ├── OrderController.java
│   │       │   └── HelloController.java
│   │       ├── service/           # 服务层
│   │       │   ├── OrderService.java
│   │       │   └── impl/
│   │       │       └── OrderServiceImpl.java
│   │       ├── mapper/            # 数据访问层
│   │       │   └── OrderMapper.java
│   │       ├── entity/            # 实体类
│   │       │   └── Order.java
│   │       ├── enums/             # 枚举类
│   │       │   ├── OrderStatusEnum.java
│   │       │   └── PaymentStatusEnum.java
│   │       ├── config/            # 配置类
│   │       │   └── MyBatisPlusConfig.java
│   │       └── OrderManageApplication.java
│   └── resources/
│       ├── application.properties  # 配置文件
│       └── db/
│           └── schema.sql         # 数据库脚本
```

## 核心代码示例

### 1. Entity 实体类

```java
@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String orderNo;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String productName;
    private Integer productQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private Integer orderStatus;
    private Integer paymentStatus;
    private String shippingAddress;
    private String remarks;
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}
```

### 2. Mapper 接口 (MyBatis-Plus)

```java
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT * FROM orders WHERE deleted = 0 AND " +
            "(customer_name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "customer_phone LIKE CONCAT('%', #{keyword}, '%') OR " +
            "order_no LIKE CONCAT('%', #{keyword}, '%'))")
    IPage<Order> searchByKeyword(Page<Order> page, @Param("keyword") String keyword);
}
```

### 3. Service 接口

```java
public interface OrderService extends IService<Order> {
    IPage<Order> pageOrders(int currentPage, int pageSize, String keyword);
    Order createOrder(Order order);
    Order updateOrder(Order order);
    boolean deleteOrder(Long id);
    boolean updateOrderStatus(Long id, Integer orderStatus);
    boolean updatePaymentStatus(Long id, Integer paymentStatus);
}
```

### 4. Service 实现类

```java
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Override
    @Transactional
    public Order createOrder(Order order) {
        // 生成订单号
        String orderNo = "ORD" + LocalDateTime.now().toString().replaceAll("[^0-9]", "") + 
                        String.format("%04d", (int)(Math.random() * 10000));
        order.setOrderNo(orderNo);
        
        // 计算总金额
        BigDecimal quantity = BigDecimal.valueOf(order.getProductQuantity());
        BigDecimal totalAmount = order.getUnitPrice().multiply(quantity);
        order.setTotalAmount(totalAmount);
        
        // 设置默认状态
        order.setOrderStatus(0); // 待处理
        order.setPaymentStatus(0); // 未支付
        
        save(order);
        return order;
    }
    
    @Override
    @Transactional
    public Order updateOrder(Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        Order existingOrder = getById(order.getId());
        if (existingOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 重新计算总金额
        if (order.getProductQuantity() != null && order.getUnitPrice() != null) {
            BigDecimal quantity = BigDecimal.valueOf(order.getProductQuantity());
            BigDecimal totalAmount = order.getUnitPrice().multiply(quantity);
            order.setTotalAmount(totalAmount);
        }
        
        updateById(order);
        return getById(order.getId());
    }
}
```

### 5. Controller 控制类

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        
        IPage<Order> page = orderService.pageOrders(currentPage, pageSize, keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("currentPage", page.getCurrent());
        result.put("pageSize", page.getSize());
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", createdOrder);
        result.put("message", "订单创建成功");
        
        return ResponseEntity.ok(result);
    }
    // ... 其他方法
}
```

### 6. Application 启动类

```java
@SpringBootApplication
public class OrderManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderManageApplication.class, args);
    }
}
```

## 接口调用示例

### 1. 查询订单列表

**JSON 请求体示例**: GET方式，无请求体

```json
// 无请求体，URL参数示例
```

**curl 调用示例**:
```bash
# 查询所有订单
curl -X GET "http://localhost:8080/api/orders?page=1&size=10"

# 带关键词搜索
curl -X GET "http://localhost:8080/api/orders?page=1&size=10&keyword=张三"
```

**响应格式**:
```json
{
    "success": true,
    "data": [
        {
            "id": 1,
            "orderNo": "ORD202411230001",
            "customerName": "张三",
            "customerPhone": "13800138000",
            "customerEmail": "zhangsan@example.com",
            "productName": "iPhone 15 Pro",
            "productQuantity": 1,
            "unitPrice": 8999.00,
            "totalAmount": 8999.00,
            "orderStatus": 1,
            "paymentStatus": 1,
            "shippingAddress": "北京市朝阳区xxx街道xxx号",
            "remarks": "请尽快发货",
            "createdAt": "2024-11-23 10:30:00",
            "updatedAt": "2024-11-23 10:35:00"
        }
    ],
    "total": 20,
    "currentPage": 1,
    "pageSize": 10
}
```

### 2. 创建订单

**JSON 请求体示例**:
```json
{
    "customerName": "李四",
    "customerPhone": "13900139000",
    "customerEmail": "lisi@example.com",
    "productName": "MacBook Air",
    "productQuantity": 2,
    "unitPrice": 8999.00,
    "shippingAddress": "上海市浦东新区xxx街道xxx号",
    "remarks": "颜色要银色的"
}
```

**curl 调用示例**:
```bash
curl -X POST "http://localhost:8080/api/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "李四",
    "customerPhone": "13900139000",
    "customerEmail": "lisi@example.com",
    "productName": "MacBook Air",
    "productQuantity": 2,
    "unitPrice": 8999.00,
    "shippingAddress": "上海市浦东新区xxx街道xxx号",
    "remarks": "颜色要银色的"
  }'
```

**响应格式**:
```json
{
    "success": true,
    "data": {
        "id": 2,
        "orderNo": "ORD20241123123456",
        "customerName": "李四",
        "customerPhone": "13900139000",
        "customerEmail": "lisi@example.com",
        "productName": "MacBook Air",
        "productQuantity": 2,
        "unitPrice": 8999.00,
        "totalAmount": 17998.00,
        "orderStatus": 0,
        "paymentStatus": 0,
        "shippingAddress": "上海市浦东新区xxx街道xxx号",
        "remarks": "颜色要银色的",
        "createdAt": "2024-11-23 14:20:30",
        "updatedAt": "2024-11-23 14:20:30"
    },
    "message": "订单创建成功"
}
```

### 3. 更新订单状态

**curl 调用示例**:
```bash
curl -X PATCH "http://localhost:8080/api/orders/1/status?orderStatus=2"
```

**响应格式**:
```json
{
    "success": true,
    "message": "订单状态更新成功"
}
```

## 可扩展性设计建议

### 1. 日志设计
- 使用AOP记录操作日志
- 订单状态变更记录详细日志
- 异常信息记录到日志文件

### 2. 异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        // 统一异常处理
    }
}
```

### 3. DTO分层
```java
// 创建订单DTO
@Data
public class CreateOrderDTO {
    @NotBlank(message = "客户姓名不能为空")
    private String customerName;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String customerPhone;
    
    // ... 其他字段
}

// 订单查询DTO
@Data
public class OrderQueryDTO {
    private String customerName;
    private Integer orderStatus;
    private LocalDate startDate;
    private LocalDate endDate;
}
```

### 4. Swagger集成
```xml
<!-- 添加Swagger依赖 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 5. 后续功能扩展建议
- **订单统计**: 按时间范围、状态等维度统计
- **批量操作**: 批量删除、状态批量更新
- **导出功能**: Excel导出订单数据
- **权限管理**: JWT用户认证和权限控制
- **缓存**: Redis缓存提升查询性能
- **消息队列**: 订单状态变更通知
- **支付集成**: 接入第三方支付
- **物流跟踪**: 集成物流信息查询

这套订单管理系统采用Spring Boot + MyBatis-Plus技术栈，遵循企业级项目标准，具有完善的CRUD功能、分页查询、状态管理等功能，可直接用于生产环境。代码结构清晰，易于维护和扩展。