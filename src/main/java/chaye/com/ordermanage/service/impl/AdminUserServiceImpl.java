package chaye.com.ordermanage.service.impl;

import chaye.com.ordermanage.dto.AdminCreateRequest;
import chaye.com.ordermanage.dto.AdminUpdateRequest;
import chaye.com.ordermanage.dto.AdminUpdatePasswordRequest;
import chaye.com.ordermanage.entity.AdminUser;
import chaye.com.ordermanage.mapper.AdminUserMapper;
import chaye.com.ordermanage.service.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 管理员用户服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public AdminUser getByUsername(String username) {
        LambdaQueryWrapper<AdminUser>wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getUsername, username)
               .eq(AdminUser::getDeleted, 0);
        return getOne(wrapper);
    }

    @Override
    @Transactional
    public AdminUser createAdmin(AdminCreateRequest request) {
        // 检查用户名是否已存在
        if (getByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(request.getUsername());
        adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
        adminUser.setNickname(request.getNickname());
        adminUser.setAvatar(request.getAvatar());
        adminUser.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        
        save(adminUser);
        return adminUser;
    }

    @Override
    @Transactional
    public AdminUser updateAdmin(Long id, AdminUpdateRequest request) {
        AdminUser adminUser = getById(id);
        if (adminUser == null || adminUser.getDeleted() == 1) {
            throw new RuntimeException("管理员不存在");
        }

        adminUser.setNickname(request.getNickname());
        adminUser.setAvatar(request.getAvatar());
        adminUser.setStatus(request.getStatus());
        
        updateById(adminUser);
        return adminUser;
    }

    @Override
    @Transactional
    public void updatePassword(Long id, AdminUpdatePasswordRequest request) {
        AdminUser adminUser = getById(id);
        if (adminUser == null || adminUser.getDeleted() == 1) {
            throw new RuntimeException("管理员不存在");
        }

        adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
        updateById(adminUser);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        AdminUser adminUser = getById(id);
        if (adminUser == null || adminUser.getDeleted() == 1) {
            throw new RuntimeException("管理员不存在");
        }

        adminUser.setStatus(status);
        updateById(adminUser);
    }

    @Override
    public Page<AdminUser> getAdminPage(Integer current, Integer size, String username) {
        Page<AdminUser> page = new Page<>(current, size);
        
        LambdaQueryWrapper<AdminUser>wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getDeleted, 0)
               .orderByDesc(AdminUser::getCreateTime);
        
        if (StringUtils.hasText(username)) {
            wrapper.like(AdminUser::getUsername, username);
        }
        
        // 不返回密码
        wrapper.select(AdminUser.class, info -> !info.getColumn().equals("password"));
        
        return page(page, wrapper);
    }
}