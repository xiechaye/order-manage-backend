package chaye.com.ordermanage.service;

import chaye.com.ordermanage.entity.Image;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片服务接口
 * 定义图片相关的业务逻辑
 */
public interface ImageService {

    /**
     * 上传图片
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 上传后的图片实体
     */
    Image uploadImage(MultipartFile file, Long userId);

    /**
     * 根据ID获取图片信息
     * @param id 图片ID
     * @return 图片实体
     */
    Image getImageById(Long id);

    /**
     * 删除图片（逻辑删除）
     * @param id 图片ID
     * @return 是否删除成功
     */
    boolean deleteImage(Long id);
}