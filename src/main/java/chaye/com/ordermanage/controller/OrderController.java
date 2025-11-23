package chaye.com.ordermanage.controller;

import chaye.com.ordermanage.entity.Order;
import chaye.com.ordermanage.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") @Min(1) int currentPage,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(required = false) String keyword) {

        IPage<Order> page; 
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 原有keyword搜索逻辑保持不变，用于基础搜索
            page = orderService.pageOrders(currentPage, pageSize, keyword);
        } else {
            // 无关键字时返回所有订单
            page = orderService.pageOrders(currentPage, pageSize, null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("currentPage", page.getCurrent());
        result.put("pageSize", page.getSize());

        return ResponseEntity.ok(result);
    }

    /**
     * 统一的订单查询接口，支持多条件组合查询
     * 支持参数：订单号、客户姓名、车牌号、订单状态、创建日期范围等
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchOrders(
            @RequestParam(defaultValue = "1") @Min(1) int currentPage,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 如果所有条件都为空，则返回所有订单
        boolean hasSearchCriteria = (orderNo != null && !orderNo.trim().isEmpty()) ||
                (customerName != null && !customerName.trim().isEmpty()) ||
                (licensePlate != null && !licensePlate.trim().isEmpty()) ||
                (orderStatus != null) ||
                (startDate != null && !startDate.trim().isEmpty()) ||
                (endDate != null && !endDate.trim().isEmpty());

        IPage<Order> page;
        if (hasSearchCriteria) {
            page = orderService.searchOrders(currentPage, pageSize, orderNo, customerName, licensePlate, orderStatus, startDate, endDate);
        } else {
            // 没有搜索条件时，返回所有订单
            page = orderService.pageOrders(currentPage, pageSize, null);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("currentPage", page.getCurrent());
        result.put("pageSize", page.getSize());
        result.put("hasSearchCriteria", hasSearchCriteria);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable @NotNull Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单不存在");
            return ResponseEntity.badRequest().body(result);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", order);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", createdOrder);
            result.put("message", "订单创建成功");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单创建失败: " + e.getMessage());

            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(
            @PathVariable @NotNull Long id, @Valid @RequestBody Order order) {
        order.setId(id);
        try {
            Order updatedOrder = orderService.updateOrder(order);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", updatedOrder);
            result.put("message", "订单更新成功");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单更新失败: " + e.getMessage());

            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable @NotNull Long id) {
        try {
            boolean success = orderService.deleteOrder(id);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "订单删除成功");

                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "订单删除失败");

                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单删除失败: " + e.getMessage());

            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable @NotNull Long id,
            @RequestParam @NotNull Integer orderStatus) {
        try {
            boolean success = orderService.updateOrderStatus(id, orderStatus);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "订单状态更新成功");

                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "订单状态更新失败");

                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "订单状态更新失败: " + e.getMessage());

            return ResponseEntity.badRequest().body(result);
        }
    }

  }