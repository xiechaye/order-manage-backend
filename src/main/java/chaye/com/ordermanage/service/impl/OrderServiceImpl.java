package chaye.com.ordermanage.service.impl;

import chaye.com.ordermanage.entity.Order;
import chaye.com.ordermanage.mapper.OrderMapper;
import chaye.com.ordermanage.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public IPage<Order> pageOrders(int currentPage, int pageSize, String keyword) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            return orderMapper.searchByKeyword(page, keyword.trim());
        } else {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeleted, 0)
                   .orderByDesc(Order::getCreatedAt);
            return page(page, wrapper);
        }
    }
    
    @Override
    public IPage<Order> searchOrders(int currentPage, int pageSize, String orderNo, String customerName, String licensePlate, Integer orderStatus, String startDate, String endDate) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        // 根据订单号模糊查询
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            wrapper.like(Order::getOrderNo, orderNo.trim());
        }
        
        // 根据客户姓名模糊查询
        if (customerName != null && !customerName.trim().isEmpty()) {
            wrapper.like(Order::getCustomerName, customerName.trim());
        }
        
        // 根据车牌号模糊查询
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            wrapper.like(Order::getLicensePlate, licensePlate.trim());
        }
        
        // 根据订单状态筛选
        if (orderStatus != null) {
            wrapper.eq(Order::getOrderStatus, orderStatus);
        }
        
        // 根据日期范围查询
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                LocalDateTime startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
                wrapper.ge(Order::getCreatedAt, startDateTime);
            } catch (Exception e) {
                // 忽略格式错误，继续执行
            }
        }
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDateTime endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
                wrapper.le(Order::getCreatedAt, endDateTime);
            } catch (Exception e) {
                // 忽略格式错误，继续执行
            }
        }
        
        // 添加逻辑删除过滤和排序
        wrapper.eq(Order::getDeleted, 0)
               .orderByDesc(Order::getCreatedAt);
        
        return page(page, wrapper);
    }
    
    @Override
    @Transactional
    public Order createOrder(Order order) {
        // 生成订单号
        String orderNo = "ORD" + LocalDateTime.now().toString().replaceAll("[^0-9]", "") + 
                        String.format("%04d", (int)(Math.random() * 10000));
        order.setOrderNo(orderNo);
        
        // 设置默认状态
        order.setOrderStatus(0); // 待处理
        
        save(order);
        return order;
    }
    
    @Override
    @Transactional
    public Order updateOrder(Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        Order existingOrder = getById(order.getId());
        if (existingOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        
        
        updateById(order);
        return getById(order.getId());
    }
    
    @Override
    @Transactional
    public boolean deleteOrder(Long id) {
        return removeById(id);
    }
    
    @Override
    @Transactional
    public boolean updateOrderStatus(Long id, Integer orderStatus) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setOrderStatus(orderStatus);
        return updateById(order);
    }
    
}