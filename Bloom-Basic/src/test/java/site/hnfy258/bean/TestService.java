package hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.Bean;
import site.hnfy258.bean.factory.annotation.Service;

@Service
public class TestService {
    public String sayHello() {
        return "Hello from TestService";
    }
}

