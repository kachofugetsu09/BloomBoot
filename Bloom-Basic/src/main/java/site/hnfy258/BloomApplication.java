package site.hnfy258;

import site.hnfy258.bean.factory.context.BloomBootApplicationContext;
import site.hnfy258.common.exceptions.BeansException;

/**
 * BloomApplication是一个工具类，用于启动BloomBoot应用程序
 */
public class BloomApplication {

    /**
     * 静态方法，用于启动BloomBoot应用程序
     *
     * @param primarySource 主类，必须包含@BloomBootApplication注解
     * @param args 命令行参数
     * @return 应用上下文
     */
    public static BloomBootApplicationContext run(Class<?> primarySource, String... args) throws BeansException {
        return BloomBootApplicationContext.run(primarySource, args);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private BloomApplication() {
    }
}
