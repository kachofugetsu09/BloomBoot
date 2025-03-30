package site.hnfy258.bean.factory.context;

import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

public class BloomBootApplicationContext implements ApplicationContext{
        private DefaultListableBeanFactory beanFactory;


        public BloomBootApplicationContext() {
            this.beanFactory = new DefaultListableBeanFactory();
            refresh();
            registerBeanPostProcessors();
            finishBeanFactoryInitialization();
        }

    private void finishBeanFactoryInitialization() {
    }

    private void registerBeanPostProcessors() {
    }


    @Override
        public String getApplicationName() {
            return "";
        }
    
        @Override
        public void refresh() {
    
        }
    
        @Override
        public void close() {
    
        }
    
        @Override
        public void setApplicationName(String applicationName) {
    
        }
    
        @Override
        public Object getBean(String name) throws BeansException {
            return null;
        }
    
        @Override
        public Object getBean(Class<?> beanClass) throws BeansException {
            return null;
        }
}
