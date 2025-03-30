package site.hnfy258.bean.factory.context;

import site.hnfy258.bean.factory.config.BeanFactory;
import site.hnfy258.common.exceptions.BeansException;

/**
 * ApplicationContext 接口扩展了 BeanFactory，提供了应用上下文的功能。
 * 该接口定义了获取和设置应用名称、刷新和关闭容器的方法。
 */
public interface ApplicationContext extends BeanFactory {

    /**
     * 获取当前应用的名称。
     *
     * @return 返回当前应用的名称。
     */
    String getApplicationName();

    /**
     * 刷新应用上下文，重新加载所有配置和Bean定义。
     * 该方法通常用于在运行时动态更新应用上下文。
     *
     * @throws BeansException 如果刷新过程中发生错误，抛出此异常。
     */
    void refresh() throws BeansException;

    /**
     * 关闭应用上下文，释放所有资源。
     * 调用此方法后，应用上下文将不再可用。
     */
    void close();

    /**
     * 设置当前应用的名称。
     *
     * @param applicationName 要设置的应用名称。
     */
    void setApplicationName(String applicationName);
}
