package chaye.com.ordermanage.enums;

public enum PaymentStatusEnum {
    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    REFUNDING(2, "退款中"),
    REFUNDED(3, "已退款");
    
    private final Integer code;
    private final String description;
    
    PaymentStatusEnum(Integer code, String description) {
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
        for (PaymentStatusEnum status : PaymentStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return "未知状态";
    }
}