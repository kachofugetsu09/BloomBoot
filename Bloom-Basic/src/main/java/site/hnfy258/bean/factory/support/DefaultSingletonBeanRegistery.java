package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.DisposableBean;
import site.hnfy258.bean.factory.ObjectFactory;
import site.hnfy258.bean.factory.config.SingletonBeanRegistry;
import site.hnfy258.common.exceptions.BeansException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的单例Bean注册实现类，用于管理和注册单例Bean实例。
 * 该类实现了SingletonBeanRegistry接口，提供了单例Bean的添加和获取功能。
 */
public class DefaultSingletonBeanRegistery implements SingletonBeanRegistry {
    // 使用ConcurrentHashMap来存储单例Bean实例，确保线程安全
    //1级缓存，普通对象
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    //2级缓存，提前暴露对象，没有完全实例化的对象
    private Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    //3级缓存，代理对象
    private Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<>();



    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        if (!this.singletonObjects.containsKey(beanName)) {
            this.singletonFactories.put(beanName, singletonFactory);
            this.earlySingletonObjects.remove(beanName);
        }
    }


    /**
     * 根据Bean名称获取已注册的单例Bean实例。
     * 实现三级缓存机制：
     * 1. singletonObjects：一级缓存，存放完全初始化好的对象
     * 2. earlySingletonObjects：二级缓存，存放提前暴露的对象（未完全初始化）
     * 3. singletonFactories：三级缓存，存放对象工厂
     */
    @Override
    public Object getSingleton(String beanName) throws BeansException {
        // 先从一级缓存中获取
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null) {
            // 如果一级缓存中没有，则从二级缓存中获取
            singletonObject = earlySingletonObjects.get(beanName);
            if (singletonObject == null) {
                // 如果二级缓存中也没有，则从三级缓存中获取对象工厂
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    // 通过对象工厂获取对象
                    singletonObject = singletonFactory.getObject();
                    // 将对象放入二级缓存
                    earlySingletonObjects.put(beanName, singletonObject);
                    // 从三级缓存中移除工厂
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    // 添加注册可销毁Bean的方法
    protected void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    // 添加销毁单例Bean的方法
    public void destroySingletons() throws BeansException {
        // 按照注册的逆序销毁Bean
        String[] disposableBeanNames = disposableBeans.keySet().toArray(new String[0]);
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            String beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
        // 清除所有缓存的单例Bean
        singletonObjects.clear();
        earlySingletonObjects.clear();
        singletonFactories.clear();
    }
}
