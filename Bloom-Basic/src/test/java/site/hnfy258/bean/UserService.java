package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.PostConstruct;
import site.hnfy258.bean.factory.annotation.Service;

@Service
public class UserService {
    @Autowired
    private OrderService orderService;
    
    private String username;
    
    @PostConstruct
    public void init() {
        this.username = "defaultUser";
        System.out.println("===============UserService初始化=============" );
        System.out.println("UserService 初始化 with username: " + username);
    }
    
    public String getUserInfo() {
        return "User: " + username + ", Orders: " + orderService.getOrderCount();
    }
}
