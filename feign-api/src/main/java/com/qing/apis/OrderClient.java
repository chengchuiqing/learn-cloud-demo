package com.qing.apis;

import com.qing.config.FeignClientConfiguration;
import com.qing.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "order-service", configuration = FeignClientConfiguration.class)
public interface OrderClient {

    @GetMapping("/order/orders/{userId}")
    List<Order> getOrdersByUserId(@PathVariable("userId") Long userId);
}
