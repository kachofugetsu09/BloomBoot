package hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.Bean;
import site.hnfy258.bean.factory.annotation.Component;

@Bean
public class TestController {
    @Autowired
    private TestService testService;

    public TestService getTestService() {
        return testService;
    }

    public String useTestService() {
        return testService.sayHello();
    }
}
