package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.PostConstruct;
import site.hnfy258.bean.factory.annotation.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        System.out.println("UserService 初始化完成!");
    }

    public void findUser(String id) {
        System.out.println("UserService 查找用户: " + id);
        userRepository.findById(id);
    }
}