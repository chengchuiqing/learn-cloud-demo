package cn.itcast.order.service;

import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import com.qing.apis.UserClient;
import com.qing.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserClient userClient;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);

        /// 2. 利用Feign发起http请求，查询用户
        User user = userClient.queryById(order.getUserId());

        /// 3. 封装user到Order
        order.setUser(user);

        // 4.返回
        return order;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderMapper.findOrderListByUserId(userId);
    }
}
