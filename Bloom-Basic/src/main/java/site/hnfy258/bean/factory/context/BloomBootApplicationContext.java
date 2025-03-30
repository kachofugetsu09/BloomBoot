package site.hnfy258.bean.factory.context;

import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;

public class BloomBootApplicationContext implements ApplicationContext{
        private DefaultListableBeanFactory beanFactory;


        public BloomBootApplicationContext() {
            this.beanFactory = new DefaultListableBeanFactory();
            init();
        }


        private void init() {
        }


}
