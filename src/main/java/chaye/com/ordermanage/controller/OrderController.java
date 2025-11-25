package chaye.com.ordermanage.controller;

import chaye.com.ordermanage.common.Result;
import chaye.com.ordermanage.dto.UpdateOrderStatusRequest;
import chaye.com.ordermanage.entity.Order;
import chaye.com.ordermanage.service.OrderService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "订单管理")
@RequiredArgsConstructor
@SaCheckLogin
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "分页查询订单")
    public Result<Page<Order>> listOrders(
            @RequestParam(defaultValue = "1") @Min(1) int currentPage,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(required = false) String keyword) {

        IPage<Order> page; 
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = orderService.pageOrders(currentPage, pageSize, keyword);
        } else {
            page = orderService.pageOrders(currentPage, pageSize, null);
        }

        return Result.success((Page<Order>) page);
    }

    /**
     * 统一的订单查询接口，支持多条件组合查询
     * 支持参数：订单号、客户姓名、车牌号、订单状态、创建日期范围等
     */
    @GetMapping("/search")
    @Operation(summary = "多条件搜索订单")
    public Result<Page<Order>> searchOrders(
            @RequestParam(defaultValue = "1") @Min(1) int currentPage,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        IPage<Order> page = orderService.searchOrders(currentPage, pageSize, orderNo, customerName, licensePlate, orderStatus, startDate, endDate);
        return Result.success((Page<Order>) page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public Result<Order> getOrder(@PathVariable @NotNull Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return Result.success(order);
    }

    @PostMapping
    @Operation(summary = "创建订单")
    public Result<Order> createOrder(@Valid @RequestBody Order order) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        Order createdOrder = orderService.createOrder(order);
        return Result.success(createdOrder);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新订单")
    public Result<Order> updateOrder(@PathVariable @NotNull Long id, @Valid @RequestBody Order order) {
        order.setId(id);
        Order updatedOrder = orderService.updateOrder(order);
        return Result.success(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单")
    public Result<Void> deleteOrder(@PathVariable @NotNull Long id) {
        boolean success = orderService.deleteOrder(id);
        if (!success) {
            throw new RuntimeException("订单删除失败");
        }
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态")
    public Result<Void> updateOrderStatus(@PathVariable @NotNull Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        boolean success = orderService.updateOrderStatus(id, request.getOrderStatus());
        if (!success) {
            throw new RuntimeException("订单状态更新失败");
        }
        return Result.success();
    }

  }