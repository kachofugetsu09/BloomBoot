package site.hnfy258.aop.framework.autoproxy;

import org.aopalliance.intercept.MethodInterceptor;
import site.hnfy258.aop.AdvisedSupport;
import site.hnfy258.aop.TargetSource;
import site.hnfy258.aop.annotation.Aspect;
import site.hnfy258.aop.aspectj.AspectJExpressionPointcutAdvisor;
import site.hnfy258.aop.framework.AspectJAdvisorFactory;
import site.hnfy258.aop.framework.ProxyFactory;
import site.hnfy258.aop.framework.adapter.CompositeMethodInterceptor;
import site.hnfy258.bean.factory.config.BeanFactory;
import site.hnfy258.bean.factory.config.BeanPostProcessor;
import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自动代理创建器，负责为符合条件的Bean创建代理
 */
public class AspectJAutoProxyCreator implements BeanPostProcessor {
    
    private DefaultListableBeanFactory beanFactory;
    private AspectJAdvisorFactory advisorFactory = new AspectJAdvisorFactory();
    private final Map<String, List<AspectJExpressionPointcutAdvisor>> advisorsCache = new ConcurrentHashMap<>();
    private final List<String> aspectNames = new ArrayList<>();
    
    public AspectJAutoProxyCreator(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        System.out.println("已注册AOP处理器: AspectJAutoProxyCreator");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 如果是切面类，记录下来
        if (bean.getClass().isAnnotationPresent(Aspect.class)) {
            System.out.println("发现切面类: " + beanName + ", 类型: " + bean.getClass().getName());
            aspectNames.add(beanName);

            // 立即初始化通知器缓存
            if (advisorsCache.isEmpty()) {
                initializeAdvisorsCache();
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 如果是切面类，不进行代理
        if (bean.getClass().isAnnotationPresent(Aspect.class)) {
            return bean;
        }

        // 确保所有切面都已初始化并缓存
        if (advisorsCache.isEmpty() && !aspectNames.isEmpty()) {
            System.out.println("初始化切面缓存，发现切面数量: " + aspectNames.size());
            initializeAdvisorsCache();
        }

        // 获取所有适用于该bean的通知器
        List<AspectJExpressionPointcutAdvisor> advisors = getAdvisorsForBean(bean, beanName);

        if (advisors.isEmpty()) {
            return bean;
        }

        System.out.println("为Bean创建代理: " + beanName + ", 匹配的通知器数量: " + advisors.size());

        // 创建代理
        try {
            // 创建一个AdvisedSupport，包含所有匹配的通知器
            AdvisedSupport advisedSupport = new AdvisedSupport();
            TargetSource targetSource = new TargetSource(bean);
            advisedSupport.setTargetSource(targetSource);
            advisedSupport.setProxyTargetClass(true);

            // 创建一个复合拦截器，包含所有匹配的通知器
            List<MethodInterceptor> interceptors = new ArrayList<>();
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                interceptors.add((MethodInterceptor) advisor.getAdvice());
                System.out.println("添加拦截器: " + advisor.getAdvice().getClass().getSimpleName() +
                        " 表达式: " + advisor.getExpression());
            }

            if (!interceptors.isEmpty()) {
                // 如果只有一个拦截器，直接使用它
                if (interceptors.size() == 1) {
                    advisedSupport.setMethodInterceptor(interceptors.get(0));
                    advisedSupport.setMethodMatcher(advisors.get(0).getPointcut().getMethodMatcher());
                } else {
                    // 否则创建一个复合拦截器
                    advisedSupport.setMethodInterceptor(new CompositeMethodInterceptor(interceptors));
                    advisedSupport.setMethodMatcher((method, targetClass) -> {
                        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                            if (advisor.getPointcut().getMethodMatcher().matches(method, targetClass)) {
                                return true;
                            }
                        }
                        return false;
                    });
                }

                // 创建代理
                Object proxy = new ProxyFactory(advisedSupport).getProxy();
                System.out.println("成功为Bean创建代理: " + beanName);
                return proxy;
            }
        } catch (Exception e) {
            System.err.println("为Bean创建代理失败: " + beanName + ", 错误: " + e.getMessage());
            e.printStackTrace();
            throw new BeansException("Error creating proxy for bean [" + beanName + "]", e);
        }

        return bean;
    }

    /**
     * 初始化通知器缓存
     */
    public void initializeAdvisorsCache() {
        for (String aspectName : aspectNames) {
            try {
                Object aspectInstance = beanFactory.getBean(aspectName);
                System.out.println("处理切面: " + aspectName + ", 类型: " + aspectInstance.getClass().getName());
                List<AspectJExpressionPointcutAdvisor> advisors = advisorFactory.getAdvisors(aspectInstance);
                System.out.println("从切面 " + aspectName + " 提取的通知器数量: " + advisors.size());
                for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                    System.out.println("  - 通知器: " + advisor.getAdvice().getClass().getSimpleName() +
                            ", 表达式: " + advisor.getExpression());
                }
                advisorsCache.put(aspectName, advisors);
            } catch (Exception e) {
                System.err.println("处理切面失败: " + aspectName + ", 错误: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to get aspect: " + aspectName, e);
            }
        }
    }

    /**
     * 获取所有适用于该bean的通知器
     */
    private List<AspectJExpressionPointcutAdvisor> getAdvisorsForBean(Object bean, String beanName) {
        List<AspectJExpressionPointcutAdvisor> result = new ArrayList<>();
        Class<?> targetClass = bean.getClass();

        // 处理CGLIB代理类
        if (targetClass.getName().contains("$$EnhancerByCGLIB$$")) {
            targetClass = targetClass.getSuperclass();
        }

        System.out.println("检查bean: " + beanName + " 原始类: " + targetClass.getName());

        for (List<AspectJExpressionPointcutAdvisor> advisors : advisorsCache.values()) {
            for (AspectJExpressionPointcutAdvisor advisor : advisors) {
                if (advisor.getPointcut().getClassFilter().matches(targetClass)) {
                    System.out.println("Bean " + beanName + " 匹配切点表达式: " + advisor.getExpression());
                    result.add(advisor);
                } else {
                    System.out.println("Bean " + beanName + " 不匹配切点表达式: " + advisor.getExpression());
                }
            }
        }

        if (result.isEmpty()) {
            System.out.println("Bean " + beanName + " 没有匹配任何切点表达式");
        } else {
            System.out.println("Bean " + beanName + " 匹配了 " + result.size() + " 个切点表达式");
        }

        return result;
    }
}
