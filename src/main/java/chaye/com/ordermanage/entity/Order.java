package chaye.com.ordermanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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