package chaye.com.ordermanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片上传响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "图片上传响应")
public class UploadImageResponse {

    /**
     * 上传状态
     */
    @Schema(description = "上传状态", example = "success")
    private String status;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息", example = "图片上传成功")
    private String message;

    /**
     * 图片访问URL
     */
    @Schema(description = "图片访问URL", example = "/uploads/images/1234567890.jpg")
    private String imageUrl;

    /**
     * 图片ID
     */
    @Schema(description = "图片ID", example = "1")
    private Long imageId;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "204800")
    private Long fileSize;

    /**
     * MIME类型
     */
    @Schema(description = "MIME类型", example = "image/jpeg")
    private String mimeType;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", example = "profile.jpg")
    private String originalName;

    /**
     * 快速创建成功响应的方法
     * @param message 成功消息
     * @param imageUrl 图片URL
     * @param imageId 图片ID
     * @param fileSize 文件大小
     * @param mimeType MIME类型
     * @param originalName 原始文件名
     * @return 成功响应对象
     */
    public static UploadImageResponse success(String message, String imageUrl, Long imageId, 
                                            Long fileSize, String mimeType, String originalName) {
        return new UploadImageResponse("success", message, imageUrl, imageId, fileSize, mimeType, originalName);
    }

    /**
     * 快速创建失败响应的方法
     * @param message 失败消息
     * @return 失败响应对象
     */
    public static UploadImageResponse error(String message) {
        return new UploadImageResponse("error", message, null, null, null, null, null);
    }
}