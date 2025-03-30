package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Component;
import site.hnfy258.bean.factory.annotation.Scope;

@Component("customComponent")
@Scope("prototype")
public class ScopedComponent {
    // 具有自定义名称和作用域的组件
}