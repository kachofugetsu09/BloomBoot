package site.hnfy258.aop;


public interface PointCut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
