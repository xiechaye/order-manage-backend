package chaye.com.ordermanage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Web配置类
 * 配置静态资源访问，使上传的图片可以直接通过URL访问
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path:uploads/images/}")
    private String uploadPath;

    /**
     * 配置静态资源处理器
     * 将上传目录映射为可访问的静态资源
     * 
     * 上传的图片可以通过以下URL访问：
     * /uploads/images/{filename}
     * 
     * 例如：
     * http://localhost:8080/api/uploads/images/abc123.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        ensureUploadDirectoryExists();
        
        // 配置资源处理器 - 链式调用CacheControl，避免重复设置
        CacheControl cacheControl = CacheControl.maxAge(7, TimeUnit.DAYS)
                .cachePublic();
        
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + uploadPath)
                .setCacheControl(cacheControl);
    }

    /**
     * 确保上传目录存在
     * 如果不存在则创建目录
     */
    private void ensureUploadDirectoryExists() {
        try {
            // 标准化路径，处理不同操作系统
            uploadPath = uploadPath.replaceAll("\\\\", "/");
            if (!uploadPath.endsWith("/")) {
                uploadPath += "/";
            }
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                if (uploadDir.mkdirs()) {
                    System.out.println("创建上传目录: " + uploadPath);
                } else {
                    throw new RuntimeException("创建上传目录失败，可能没有文件系统权限");
                }
            } else if (!uploadDir.isDirectory()) {
                throw new RuntimeException("上传路径 " + uploadPath + " 不是有效的目录，可能是文件");
            }
            
            // 验证目录是否可写
            if (!uploadDir.canWrite()) {
                throw new RuntimeException("上传目录 " + uploadPath + " 没有写入权限");
            }
            
        } catch (SecurityException e) {
            throw new RuntimeException("没有足够的权限访问上传目录 " + uploadPath, e);
        } catch (Exception e) {
            throw new RuntimeException("创建上传目录失败: " + e.getMessage(), e);
        }
    }
}