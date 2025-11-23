package chaye.com.ordermanage.service.impl;

import chaye.com.ordermanage.entity.Order;
import chaye.com.ordermanage.mapper.OrderMapper;
import chaye.com.ordermanage.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
            wrapper.orderByDesc(Order::getCreatedAt);
            return page(page, wrapper);
        }
    }
    
    @Override
    public IPage<Order> searchByLicensePlate(int currentPage, int pageSize, String licensePlate) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Order::getLicensePlate, licensePlate.trim())
                   .orderByDesc(Order::getCreatedAt);
            return page(page, wrapper);
        } else {
            // 如果没有提供车牌号，返回空结果
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.isNull(Order::getLicensePlate)
                   .orderByDesc(Order::getCreatedAt);
            return page(page, wrapper);
        }
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
        order.setPaymentStatus(0); // 未支付
        
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
    
    @Override
    @Transactional
    public boolean updatePaymentStatus(Long id, Integer paymentStatus) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setPaymentStatus(paymentStatus);
        return updateById(order);
    }
}