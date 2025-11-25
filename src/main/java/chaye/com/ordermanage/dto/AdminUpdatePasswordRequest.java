package chaye.com.ordermanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新密码DTO
 */
@Data
public class AdminUpdatePasswordRequest {

    @NotBlank(message = "新密码不能为空")
    private String password;
}