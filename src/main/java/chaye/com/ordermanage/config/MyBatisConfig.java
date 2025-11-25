package chaye.com.ordermanage.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置
 * 包含自动填充配置和分页插件配置
 */
@Configuration
@MapperScan("chaye.com.ordermanage.mapper")
@Component
public class MyBatisConfig implements MetaObjectHandler {
    
    /**
     * 添加分页插件
     * 分页插件要放在最后
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        return interceptor;
    }
    
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间填充 - 订单实体字段
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        
        // 创建时间填充 - 管理员用户实体字段
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        
        // 更新时间填充 - 订单实体字段
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        
        // 更新时间填充 - 管理员用户实体字段
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 逻辑删除字段填充（如果实体有这个字段）
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间填充 - 订单实体字段
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        
        // 更新时间填充 - 管理员用户实体字段
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}