package chaye.com.ordermanage.service;

import chaye.com.ordermanage.entity.Image;
import chaye.com.ordermanage.exception.FileUploadException;
import chaye.com.ordermanage.mapper.ImageMapper;
import chaye.com.ordermanage.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 图片服务实现类
 * 实现图片上传、管理等业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;

    @Value("${upload.path:uploads/images/}")
    private String uploadPath;

    @Value("${upload.max-size:5242880}")
    private long maxFileSize;

    @Override
    public Image uploadImage(MultipartFile file, Long userId) {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("上传文件不能为空");
        }

        // 验证用户ID
        if (userId == null) {
            throw new FileUploadException("用户ID不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileUploadException("文件名不能为空");
        }

        // 验证文件大小
        if (!FileUtil.isValidFileSize(file, maxFileSize)) {
            throw new FileUploadException("文件大小不能超过5MB");
        }

        // 验证文件类型
        if (!FileUtil.isValidImageType(file)) {
            throw new FileUploadException("只允许上传JPG、PNG、GIF、BMP、WEBP格式的图片文件");
        }

        try {
            // 生成唯一文件名
            String extension = FileUtil.getFileExtension(originalFilename);
            String uniqueFileName = FileUtil.generateUniqueFileName(originalFilename);
            
            // 保存文件到磁盘
            String savePath = FileUtil.saveFile(file, uploadPath, uniqueFileName);
            
            // 构建图片实体
            Image image = new Image();
            image.setUserId(userId);
            image.setFileName(uniqueFileName);
            image.setOriginalName(originalFilename);
            image.setFilePath(savePath);
            image.setSize(file.getSize());
            image.setMimeType(file.getContentType());
            image.setUploadTime(LocalDateTime.now());
            image.setDeleted(0);
            
            // 保存到数据库
            int result = imageMapper.insert(image);
            if (result != 1) {
                throw new FileUploadException("图片信息保存失败");
            }
            
            log.info("图片上传成功 - 用户ID: {}, 文件名: {}, 路径: {}", userId, originalFilename, savePath);
            return image;
            
        } catch (IOException e) {
            log.error("图片保存失败", e);
            throw new FileUploadException("图片保存失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("图片上传过程中发生错误", e);
            throw new FileUploadException("图片上传过程中发生错误: " + e.getMessage(), e);
        }
    }

    @Override
    public Image getImageById(Long id) {
        if (id == null) {
            throw new FileUploadException("图片ID不能为空");
        }
        
        Image image = imageMapper.selectById(id);
        if (image == null) {
            throw new FileUploadException("图片不存在或已被删除");
        }
        
        return image;
    }

    @Override
    public boolean deleteImage(Long id) {
        if (id == null) {
            throw new FileUploadException("图片ID不能为空");
        }
        
        Image image = imageMapper.selectById(id);
        if (image == null) {
            throw new FileUploadException("图片不存在");
        }
        
        int result = imageMapper.deleteById(id);
        if (result == 1) {
            log.info("图片删除成功 - 图片ID: {}", id);
            return true;
        } else {
            log.warn("图片删除失败 - 图片ID: {}", id);
            return false;
        }
    }
}