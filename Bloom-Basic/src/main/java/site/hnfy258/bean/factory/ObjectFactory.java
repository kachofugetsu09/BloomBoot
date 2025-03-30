package site.hnfy258.bean.factory;

import site.hnfy258.common.exceptions.BeansException;

public interface ObjectFactory<T> {

    T getObject() throws BeansException;

}