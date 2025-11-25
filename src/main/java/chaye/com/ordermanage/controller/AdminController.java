package chaye.com.ordermanage.controller;

import chaye.com.ordermanage.common.Result;
import chaye.com.ordermanage.dto.AdminCreateRequest;
import chaye.com.ordermanage.dto.AdminUpdateRequest;
import chaye.com.ordermanage.entity.AdminUser;
import chaye.com.ordermanage.service.AdminUserService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员管理")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService adminUserService;

    /**
     * 新增管理员
     */
    @PostMapping
    @SaCheckLogin
    @Operation(summary = "新增管理员")
    public Result create(@Valid @RequestBody AdminCreateRequest request) {
        AdminUser adminUser = adminUserService.createAdmin(request);
        return Result.success(adminUser);
    }

    /**
     * 更新管理员
     */
    @PutMapping("/{id}")
    @SaCheckLogin
    @Operation(summary = "更新管理员")
    public Result update(@PathVariable Long id, @Valid @RequestBody AdminUpdateRequest request) {
        AdminUser adminUser = adminUserService.updateAdmin(id, request);
        return Result.success(adminUser);
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    @SaCheckLogin
    @Operation(summary = "删除管理员")
    public Result delete(@PathVariable Long id) {
        // 不能删除自己
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (currentUserId.equals(id)) {
            return Result.error("不能删除当前登录用户");
        }
        
        // 不能删除超级管理员
        AdminUser targetUser = adminUserService.getById(id);
        if (targetUser == null) {
            return Result.error("用户不存在");
        }
        if ("admin".equals(targetUser.getUsername())) {
            return Result.error("不能删除超级管理员账号");
        }
        
        boolean result = adminUserService.removeById(id);
        return result ? Result.success() : Result.error("删除失败");
    }

    /**
     * 分页查询管理员
     */
    @GetMapping
    @SaCheckLogin
    @Operation(summary = "分页查询管理员")
    public Result getAdminPage(@RequestParam(defaultValue = "1") Integer current,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam(required = false) String username) {
        Page<AdminUser> page = adminUserService.getAdminPage(current, size, username);
        return Result.success(page);
    }

    /**
     * 更新管理员状态
     */
    @PutMapping("/{id}/status")
    @SaCheckLogin
    @Operation(summary = "更新管理员状态")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        // 不能禁用自己
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (currentUserId.equals(id) && status == 0) {
            return Result.error("不能禁用当前登录用户");
        }
        
        // 不能禁用超级管理员
        AdminUser targetUser = adminUserService.getById(id);
        if (targetUser == null) {
            return Result.error("用户不存在");
        }
        if ("admin".equals(targetUser.getUsername()) && status == 0) {
            return Result.error("不能禁用超级管理员账号");
        }
        
        adminUserService.updateStatus(id, status);
        return Result.success();
    }
}