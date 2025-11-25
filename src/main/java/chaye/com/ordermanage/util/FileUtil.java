package chaye.com.ordermanage.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 * 提供文件上传、验证等通用功能
 */
@Slf4j
@Component
public class FileUtil {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 生成唯一文件名
     * @param originalFilename 原始文件名
     * @return 生成的唯一文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 文件扩展名，不包含点（.）
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 验证文件是否为图片类型
     * @param file 待验证的文件
     * @return 是否为允许的图片类型
     */
    public static boolean isValidImageType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        
        String extension = getFileExtension(originalFilename);
        String mimeType = file.getContentType();
        
        // 验证文件扩展名
        if (!ALLOWED_IMAGE_TYPES.contains(extension)) {
            log.warn("文件扩展名不合法: {}", extension);
            return false;
        }
        
        // 验证MIME类型
        if (mimeType == null || !mimeType.startsWith("image/")) {
            log.warn("MIME类型不合法: {}", mimeType);
            return false;
        }
        
        return true;
    }

    /**
     * 验证文件大小是否在限制范围内
     * @param file 待验证的文件
     * @param maxSize 最大文件大小（字节）
     * @return 文件大小是否合法
     */
    public static boolean isValidFileSize(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }

    /**
     * 保存文件到指定路径
     * @param file 待保存的文件
     * @param targetPath 目标路径
     * @param fileName 文件名称
     * @return 保存后的完整文件路径
     * @throws IOException 保存过程中出现的异常
     */
    public static String saveFile(MultipartFile file, String targetPath, String fileName) throws IOException {
        Path directoryPath = Paths.get(targetPath);
        
        // 如果目录不存在，创建目录
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            log.info("创建目录: {}", directoryPath.toString());
        }
        
        // 构建完整文件路径
        Path targetFilePath = directoryPath.resolve(fileName);
        
        // 保存文件
        file.transferTo(targetFilePath);
        
        log.info("文件保存成功: {}", targetFilePath.toString());
        return targetFilePath.toString();
    }

    /**
     * 获取文件大小描述（友好的显示格式）
     * @param size 文件大小（字节）
     * @return 友好格式的文件大小描述
     */
    public static String getFileSizeDescription(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}