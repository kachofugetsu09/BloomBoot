package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.PostConstruct;
import site.hnfy258.bean.factory.annotation.Service;

@Service
public class OrderService {
    @Autowired
    private UserService userService;
    private int orderCount;
    
    @PostConstruct
    public void init() {
        this.orderCount = 10; // 初始化一些订单数据
        System.out.println("===============OrderService初始化=============" );
        System.out.println("OrderService初始化 " + orderCount + " orders");
    }
    
    public int getOrderCount() {
        return orderCount;
    }
}
