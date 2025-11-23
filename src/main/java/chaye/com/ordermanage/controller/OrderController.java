package chaye.com.ordermanage.controller;

import chaye.com.ordermanage.entity.Order;
import chaye.com.ordermanage.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Validated
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") @Min(1) int currentPage,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(required = false) String keyword) {

        IPage<Order> page = orderService.pageOrders(currentPage, pageSize, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("currentPage", page.getCurrent());
        result.put("pageSize", page.getSize());

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

    @PatchMapping("/{id}/status")
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

    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable @NotNull Long id,
            @RequestParam @NotNull Integer paymentStatus) {
        try {
            boolean success = orderService.updatePaymentStatus(id, paymentStatus);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "支付状态更新成功");

                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "支付状态更新失败");

                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "支付状态更新失败: " + e.getMessage());

            return ResponseEntity.badRequest().body(result);
        }
    }
}