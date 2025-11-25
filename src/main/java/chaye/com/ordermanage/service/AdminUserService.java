package chaye.com.ordermanage.service;

import chaye.com.ordermanage.dto.AdminCreateRequest;
import chaye.com.ordermanage.dto.AdminUpdateRequest;
import chaye.com.ordermanage.dto.AdminUpdatePasswordRequest;
import chaye.com.ordermanage.entity.AdminUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService extends IService<AdminUser> {

    /**
     * 根据用户名查找用户
     */
    AdminUser getByUsername(String username);

    /**
     * 创建管理员
     */
    AdminUser createAdmin(AdminCreateRequest request);

    /**
     * 更新管理员
     */
    AdminUser updateAdmin(Long id, AdminUpdateRequest request);

    /**
     * 更新管理员密码
     */
    void updatePassword(Long id, AdminUpdatePasswordRequest request);

    /**
     * 更新管理员状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 分页查询管理员
     */
    Page<AdminUser> getAdminPage(Integer current, Integer size, String username);
}