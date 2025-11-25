package chaye.com.ordermanage.mapper;

import chaye.com.ordermanage.entity.Image;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图片数据访问层接口
 * 继承BaseMapper，提供基础的CRUD操作
 */
@Mapper
public interface ImageMapper extends BaseMapper<Image> {
    
}