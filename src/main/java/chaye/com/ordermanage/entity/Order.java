package chaye.com.ordermanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单实体类
 * 用于表示系统中的订单信息，对应数据库中的orders表
 */
@Data
@TableName("orders")
public class Order {
    
    /**
     * 订单ID
     * 使用MyBatis-Plus的分布式ID生成策略
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 订单编号
     * 自定义订单号，用于业务识别
     */
    private String orderNo;
    
    /**
     * 客户姓名
     * 下单客户的名称
     */
    private String customerName;
    
    /**
     * 客户电话
     * 客户的联系方式
     */
    private String customerPhone;
    
    /**
     * 客户邮箱（可选）
     * 客户的电子邮箱地址，可为空
     */
    private String customerEmail;
    
    /**
     * 车牌号（可选）
     * 客户车辆的牌照号码，可重复，可为空
     */
    private String licensePlate;
    
    /**
     * 产品名称
     * 订单中产品的名称
     */
    private String productName;
    
    /**
     * 产品数量
     * 订单中产品的数量
     */
    private Integer productQuantity;
    
    /**
     * 订单状态（可选）
     * 订单的处理状态（如：待处理、已发货、已完成等）
     * 如果为空，默认为待处理状态
     */
    private Integer orderStatus;
    
    /**
     * 支付状态（可选）
     * 订单的支付状态（如：未支付、已支付、已退款等）
     * 如果为空，默认为未支付状态
     */
    private Integer paymentStatus;
    
    /**
     * 备注（可选）
     * 订单的额外说明或备注信息，可为空
     */
    private String remarks;
    
    /**
     * 创建时间
     * 订单创建时间，自动填充
     * 使用JSON格式化输出
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 订单最后更新时间，自动填充
     * 使用JSON格式化输出
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * 逻辑删除标记
     * 0-未删除，1-已删除
     * 使用MyBatis-Plus的逻辑删除功能
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted = 0;
}