package chaye.com.ordermanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("chaye.com.ordermanage.mapper")
public class OrderManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderManageApplication.class, args);
    }

}
