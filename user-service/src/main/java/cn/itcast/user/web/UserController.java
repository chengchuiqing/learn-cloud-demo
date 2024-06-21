package cn.itcast.user.web;

import cn.itcast.user.config.PatternProperties;
import cn.itcast.user.handler.GlobalFallBackHandler;
import cn.itcast.user.pojo.User;
import cn.itcast.user.service.UserService;
import cn.itcast.user.vo.OrderResultVO;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.qing.apis.OrderClient;
import com.qing.pojo.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
@RefreshScope
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 路径： /user/110
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {
        System.out.println("我被调用了");
        return userService.queryById(id);
    }

    // 注入 nacos 中的配置属性
    @Value("${xyz.name:}")
    private String name;

    @Autowired
    private PatternProperties patternProperties;

    @GetMapping("/now")
    public String now() {
        System.out.println(name);
        String dateformat = patternProperties.getDateformat();
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateformat, Locale.CHINA));
    }

    /**
     * Sentinel 关联资源
     */
    @GetMapping(value = "/hello1")
    public String hello1() {
        return "Hello Sentinel1";
    }

    @GetMapping(value = "/hello2")
    public String hello2() {
        return "Hello Sentinel2";
    }

    /**
     * Sentinel 链路
     */
    @GetMapping(value = "/hello3")
    public String hello3() {
        userService.doSomething();
        return "Hello Sentinel3";
    }

    @GetMapping(value = "/hello4")
    public String hello4() {
        userService.doSomething();
        return "Hello Sentinel4";
    }

    // 熔断降级–根据平均响应时间降级
    @GetMapping(value = "/hello5/{flag}")
    public String hello5(@PathVariable("flag") int flag) throws InterruptedException {
        if (flag == 1) {
            TimeUnit.SECONDS.sleep(3);
        }
        return "Hello Sentinel5";
    }

    @GetMapping(value = "/hello6/{flag}")
    public String hello6(@PathVariable("flag") int flag) throws InterruptedException {
        if (flag == 1) {
            int i = 1 / 0;
        }
        return "Hello Sentinel6";
    }


    @Autowired
    private OrderClient orderClient;

    @SentinelResource(value = "findUserOrders", blockHandler = "hotHandler")
    @GetMapping("/findUserOrders")
    public OrderResultVO findUserOrders(@RequestParam(value = "userId", required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }

        OrderResultVO orderResultVO = new OrderResultVO();
        User user = userService.queryById(userId);

        // 远程调用
        List<Order> orderList = orderClient.getOrdersByUserId(userId);

        orderResultVO.setUserName(user.getUsername());
        orderResultVO.setOrderList(orderList);
        return orderResultVO;
    }

    // 该方法返回值类型、形参数量、参数类型、形参名称需要与原方法一致
    public OrderResultVO hotHandler(Long userId, BlockException e) {
        e.printStackTrace();
        OrderResultVO orderResultVO = new OrderResultVO();
        orderResultVO.setUserName("访问被限流了");
        return orderResultVO;
    }

    @GetMapping("hello7")
    @SentinelResource(value = "hello7", fallback = "fallbackHandler", fallbackClass = GlobalFallBackHandler.class)
    public String hello7(@RequestParam("flag") int flag) {
        int i = 1 / 0;
        return "Hello Sentinel7";
    }

    // 定义兜底方法，该方法的返回值类型、形参数量、形参类型均与原方法一致
    public String fallbackHandler(int flag) {
        return "业务出现异常";
    }
}
