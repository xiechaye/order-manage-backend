package chaye.com.ordermanage.mapper;

import chaye.com.ordermanage.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    @Select("SELECT * FROM orders WHERE deleted = 0 AND " +
            "(customer_name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "customer_phone LIKE CONCAT('%', #{keyword}, '%') OR " +
            "order_no LIKE CONCAT('%', #{keyword}, '%')) ")
    IPage<Order> searchByKeyword(Page<Order> page, @Param("keyword") String keyword);
}