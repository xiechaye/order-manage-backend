package chaye.com.ordermanage.controller;

import chaye.com.ordermanage.common.Result;
import chaye.com.ordermanage.dto.UploadImageResponse;
import chaye.com.ordermanage.entity.Image;
import chaye.com.ordermanage.exception.FileUploadException;
import chaye.com.ordermanage.service.ImageService;
import chaye.com.ordermanage.util.FileUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件上传控制器
 * 提供外部接口供前端上传图片
 * 支持HTTP FormData方式上传，易于前端集成
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "文件上传", description = "图片上传相关接口")
@Validated
public class UploadController {

    private final ImageService imageService;

    @Value("${upload.path:uploads/images/}")
    private String uploadPath;

    /**
     * 上传图片接口
     *
     * @param image 上传的图片文件
     * @return 上传结果，包含图片访问路径
     */
    @PostMapping("/image")
    @SaCheckLogin
    @Operation(summary = "上传图片", description = "用户上传图片，需要登录权限")
    public Result<UploadImageResponse> uploadImage(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("image") MultipartFile image) {
        
        if (image.isEmpty()) {
            throw new FileUploadException("上传文件不能为空");
        }
        
        try {
            // 获取当前登录用户ID
            Long loginId = getLoginUserId();
            if (loginId == null) {
                throw new FileUploadException("用户信息获取失败，请重新登录");
            }
            
            // 调用服务层保存图片
            Image savedImage = imageService.uploadImage(image, loginId);
            
            // 构建返回信息
            String imageUrl = "/uploads/images/" + savedImage.getFileName();
            
            UploadImageResponse response = UploadImageResponse.success(
                    "图片上传成功",
                    imageUrl,
                    savedImage.getId(),
                    savedImage.getSize(),
                    savedImage.getMimeType(),
                    savedImage.getOriginalName()
            );
            
            return Result.success(response);
            
        } catch (FileUploadException e) {
            log.warn("图片上传失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("图片上传异常", e);
            return Result.error("图片上传失败，请稍后重试");
        }
    }

    /**
     * 查看图片接口
     * 支持通过文件名直接访问图片，例如：/api/upload/image/abc123.jpg
     * 
     * @param fileName 图片文件名
     * @return 图片资源
     */
    @GetMapping("/image/{fileName}")
    @Operation(summary = "获取图片", description = "通过文件名获取图片资源")
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "图片文件名", required = true)
            @PathVariable String fileName) {
        
        try {
            // 安全验证文件名，防止路径遍历攻击
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\\\")) {
                log.warn("非法的文件名: {}", fileName);
                return ResponseEntity.badRequest().build();
            }
            
            String fullPath = uploadPath + fileName;
            File file = new File(fullPath);
            
            if (!file.exists() || !file.isFile()) {
                log.warn("图片不存在: {}", fullPath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            
            // 根据文件扩展名设置对应的Content-Type
            String contentType = getContentType(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("获取图片异常: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除图片接口
     * 用户只能删除自己上传的图片
     * 
     * @param imageId 图片ID
     * @return 删除结果
     */
    @DeleteMapping("/image/{imageId}")
    @SaCheckLogin
    @Operation(summary = "删除图片", description = "删除指定的图片，需要登录权限")
    public Result<Void> deleteImage(
            @Parameter(description = "图片ID", required = true)
            @PathVariable Long imageId) {
        
        try {
            Long loginId = getLoginUserId();
            if (loginId == null) {
                throw new FileUploadException("用户信息获取失败，请重新登录");
            }
            
            // 获取图片信息
            Image image = imageService.getImageById(imageId);
            
            // 验证用户权限（用户只能删除自己的图片）
            if (!image.getUserId().equals(loginId)) {
                return Result.error(403, "只能删除自己上传的图片");
            }
            
            // 删除图片
            boolean success = imageService.deleteImage(imageId);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("图片删除失败");
            }
            
        } catch (FileUploadException e) {
            log.warn("删除图片失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除图片异常", e);
            return Result.error("删除图片失败，请稍后重试");
        }
    }

    /**
     * 获取当前登录用户ID
     * 从Sa-Token中获取
     */
    private Long getLoginUserId() {
        try {
            Object loginId = StpUtil.getLoginId();
            if (loginId == null) {
                return null;
            }
            return Long.valueOf(loginId.toString());
        } catch (Exception e) {
            log.error("获取登录用户信息失败", e);
            return null;
        }
    }

    /**
     * 根据文件名获取对应的Content-Type
     */
    private String getContentType(String fileName) {
        String extension = FileUtil.getFileExtension(fileName).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
}