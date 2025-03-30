package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.BloomBootApplication;
import site.hnfy258.BloomApplication;
import site.hnfy258.common.exceptions.BeansException;

@BloomBootApplication
public class BloomBootDemo {
    public static void main(String[] args) throws BeansException {
        // 启动应用
        BloomApplication.run(BloomBootDemo.class, args);
        System.out.println("BloomBoot应用已启动!");
    }
}