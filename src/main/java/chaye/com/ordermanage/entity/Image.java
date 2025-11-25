package chaye.com.ordermanage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图片实体类
 * 用于存储上传图片的元数据信息
 */
@Data
@TableName("t_image")
public class Image {

    /**
     * 主键ID，自动生成
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联用户表
     */
    private Long userId;

    /**
     * 文件存储名称（生成唯一文件名）
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    private Integer deleted;
}