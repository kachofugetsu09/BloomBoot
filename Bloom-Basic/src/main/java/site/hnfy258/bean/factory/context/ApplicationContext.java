package site.hnfy258.bean.factory.context;

import site.hnfy258.bean.factory.config.BeanFactory;

public interface ApplicationContext extends BeanFactory {

    String getApplicationName();

    // 刷新容器
    void refresh();

    // 关闭容器
    void close();


    void setApplicationName(String applicationName);
}
