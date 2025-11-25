package chaye.com.ordermanage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新订单状态请求DTO
 */
@Data
public class UpdateOrderStatusRequest {
    
    /**
     * 订单状态
     */
    @NotNull(message = "订单状态不能为空")
    private Integer orderStatus;
    
}