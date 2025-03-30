package site.hnfy258.bean.factory.context;

import site.hnfy258.common.exceptions.BeansException;

public interface ConfigurableApplicationContext extends ApplicationContext {

    void refresh();

    void registerShutdownHook();

    void close();

}