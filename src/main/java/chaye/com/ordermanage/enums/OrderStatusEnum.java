package chaye.com.ordermanage.enums;

public enum OrderStatusEnum {
    PENDING_PICKUP(0, "待取货"),
    COMPLETED(1, "已完成"),
    CANCELLED(2, "已取消");
    
    private final Integer code;
    private final String description;
    
    OrderStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static String getDescriptionByCode(Integer code) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return "未知状态";
    }
}