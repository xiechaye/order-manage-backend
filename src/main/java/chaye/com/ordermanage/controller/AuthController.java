package chaye.com.ordermanage.controller;

import chaye.com.ordermanage.common.Result;
import chaye.com.ordermanage.dto.AdminCreateRequest;
import chaye.com.ordermanage.dto.LoginRequest;
import chaye.com.ordermanage.dto.LoginResponse;
import chaye.com.ordermanage.entity.AdminUser;
import chaye.com.ordermanage.service.AdminUserService;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理")
@RequiredArgsConstructor
public class AuthController {

    private final AdminUserService adminUserService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 初始化管理员 - 用于开发和测试环境
     * 在生产环境中应该删除或限制此接口
     */
    @PostMapping("/init-admin")
    @Operation(summary = "初始化管理员(仅用于测试)")
    public Result initAdmin(@RequestParam(defaultValue = "123456") String password) {
        try {
            // 检查是否已有管理员用户
            if (adminUserService.getByUsername("admin") != null) {
                return Result.error("管理员用户已存在");
            }

            // 创建默认管理员
            AdminCreateRequest request = new AdminCreateRequest();
            request.setUsername("admin");
            request.setPassword(password);
            request.setNickname("超级管理员");
            request.setStatus(1);
            
            AdminUser adminUser = adminUserService.createAdmin(request);
            return Result.success(adminUser);
        } catch (Exception e) {
            return Result.error("初始化管理员失败：" + e.getMessage());
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result login(@Valid @RequestBody LoginRequest request) {
        // 根据用户名查找用户
        AdminUser adminUser = adminUserService.getByUsername(request.getUsername());
        if (adminUser == null) {
            return Result.error("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 验证状态
        if (adminUser.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        // 登录
        StpUtil.login(adminUser.getId());
        String token = StpUtil.getTokenValue();

        // 组装登录响应信息
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setTokenPrefix("Bearer ");
        loginResponse.setUserId(adminUser.getId());
        loginResponse.setUsername(adminUser.getUsername());
        loginResponse.setNickname(adminUser.getNickname());
        loginResponse.setAvatar(adminUser.getAvatar());

        return Result.success(loginResponse);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "当前用户信息")
    public Result info() {
        // 获取当前登录用户ID
        Object loginId = StpUtil.getLoginId();
        if (loginId == null) {
            return Result.error("未登录");
        }

        // 查询用户信息
        AdminUser adminUser = adminUserService.getById(Long.parseLong(loginId.toString()));
        if (adminUser == null || adminUser.getDeleted() == 1) {
            return Result.error("用户不存在");
        }

        return Result.success(adminUser);
    }
}