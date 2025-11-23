package chaye.com.ordermanage.service;

import chaye.com.ordermanage.entity.Order;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Order> {
    
    IPage<Order> pageOrders(int currentPage, int pageSize, String keyword);
    
    IPage<Order> searchOrders(int currentPage, int pageSize, String orderNo, String customerName, String licensePlate, Integer orderStatus, String startDate, String endDate);
    
    Order createOrder(Order order);
    
    Order updateOrder(Order order);
    
    boolean deleteOrder(Long id);
    
    boolean updateOrderStatus(Long id, Integer orderStatus);
    
    boolean updatePaymentStatus(Long id, Integer paymentStatus);
}