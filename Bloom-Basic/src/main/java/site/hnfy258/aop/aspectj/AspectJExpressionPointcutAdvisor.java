package site.hnfy258.aop.aspectj;

import lombok.Getter;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import site.hnfy258.aop.PointCut;
import site.hnfy258.aop.framework.PointcutAdvisor;

public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {
    @Setter
    private AspectJExpressionPointcut pointcut;
    @Getter
    @Setter
    private Advice advice;
    @Getter
    @Setter
    private String expression;

    @Override
    public PointCut getPointcut() {
        if(pointcut == null){
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

}
