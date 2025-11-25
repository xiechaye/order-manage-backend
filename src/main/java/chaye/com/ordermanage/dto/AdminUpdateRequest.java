package chaye.com.ordermanage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新管理员DTO
 */
@Data
public class AdminUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String avatar;

    @NotNull(message = "状态不能为空")
    private Integer status;
}