package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Repository;

@Repository
public class UserRepository {
    
    public void findById(String id) {
        System.out.println("UserRepository 正在查询数据库，用户ID: " + id);
    }
}