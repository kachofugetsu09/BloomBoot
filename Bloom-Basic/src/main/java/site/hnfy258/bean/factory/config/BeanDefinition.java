package site.hnfy258.bean.factory.config;

import lombok.Getter;
import lombok.Setter;

/**
 * BeanDefinition 类用于定义 Bean 的元数据信息，包括 Bean 的类类型、作用域以及是否延迟初始化等。
 */
public class BeanDefinition {
        String SCOPE_SINGLETON = "singleton"; // 单例作用域常量

        String SCOPE_PROTOTYPE = "prototype"; // 原型作用域常量
        @Getter
        @Setter
        private Class beanClass; // Bean 的类类型
        @Getter
        @Setter
        private String scope; // Bean 的作用域
        private boolean isLazy = false; // 是否延迟初始化

        /**
         * 构造函数，用于创建一个 BeanDefinition 实例。
         * @param beanClass Bean 的类类型，用于指定该 Bean 的类。
         */
        public BeanDefinition(Class beanClass){
            this.beanClass = beanClass;
            this.scope = SCOPE_SINGLETON; // 默认作用域为单例
        }

        /**
         * 判断当前 Bean 是否为单例作用域。
         * @return 如果当前 Bean 的作用域为单例，则返回 true，否则返回 false。
         */
        public boolean isSingleton() {
            return SCOPE_SINGLETON.equals(scope);
        }

        /**
         * 判断当前 Bean 是否延迟初始化。
         * @return 如果当前 Bean 需要延迟初始化，则返回 true，否则返回 false。
         */
        public boolean isLazyInit() {
            return isLazy;
        }
}
