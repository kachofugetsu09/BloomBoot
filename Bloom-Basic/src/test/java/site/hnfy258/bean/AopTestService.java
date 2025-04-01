package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.PostConstruct;
import site.hnfy258.bean.factory.annotation.Service;

@Service
public class AopTestService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @PostConstruct
    public void init() {
        System.out.println("\n=============== AOP 测试开始 =============");
        
        // 测试 UserService 方法
        System.out.println("\n测试 UserService.getUserInfo 方法:");
        String userInfo = userService.getUserInfo();
        System.out.println("获取到的用户信息: " + userInfo);
        
        System.out.println("\n测试 UserService.printUserInfo 方法:");
        userService.printUserInfo();
        
        // 测试 OrderService 方法
        System.out.println("\n测试 OrderService.getOrderCount 方法:");
        int count = orderService.getOrderCount();
        System.out.println("当前订单数量: " + count);
        
        System.out.println("\n测试 OrderService.createOrder 方法:");
        orderService.createOrder("测试商品");
        System.out.println("创建订单后的数量: " + orderService.getOrderCount());
        
        System.out.println("\n=============== AOP 测试结束 =============");
    }
}