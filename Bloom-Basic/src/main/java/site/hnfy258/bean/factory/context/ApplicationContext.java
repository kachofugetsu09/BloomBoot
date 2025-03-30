package site.hnfy258.bean.factory.context;

import site.hnfy258.bean.factory.config.BeanFactory;
import site.hnfy258.common.exceptions.BeansException;

public interface ApplicationContext extends BeanFactory {

    String getApplicationName();

    // 刷新容器
    void refresh() throws BeansException;

    // 关闭容器
    void close();


    void setApplicationName(String applicationName);
}
