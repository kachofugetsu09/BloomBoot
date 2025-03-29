package site.hnfy258.common.exceptions;

public class BeansException extends Exception{
    public BeansException(String msg, Exception e){
        super(msg);
    }

    public BeansException(String msg) {
        super(msg);
    }
}
