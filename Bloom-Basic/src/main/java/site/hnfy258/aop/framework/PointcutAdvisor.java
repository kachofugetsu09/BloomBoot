package site.hnfy258.aop.framework;


import site.hnfy258.aop.PointCut;

public interface PointcutAdvisor extends Advisor {
    PointCut getPointcut();
}
