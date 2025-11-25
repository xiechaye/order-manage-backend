package chaye.com.ordermanage.mapper;

import chaye.com.ordermanage.entity.AdminUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员用户Mapper
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}