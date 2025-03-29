package site.hnfy258.bean.factory.config;

import lombok.Getter;
import lombok.Setter;

public class BeanDefinition {
        String SCOPE_SINGLETON = "singleton";

        String SCOPE_PROTOTYPE = "prototype";
        @Getter
        @Setter
        private Class beanClass;
        @Getter
        @Setter
        private String scope;


                public BeanDefinition(Class beanClass){
                    this.beanClass = beanClass;
                    this.scope = SCOPE_SINGLETON;
                }
}
