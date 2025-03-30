/**
 * ClassPathScanner接口定义了扫描类路径的功能
 * 它主要用于扫描指定基础包下的类文件，并进行相应的处理
 */
package site.hnfy258.bean.factory.config;

/**
 * 定义了一个类路径扫描器接口
 * 该接口的实现类可以用来扫描指定基础包下的类文件
 */
public interface ClassPathScanner {
    /**
     * 扫描一个或多个基础包下的所有类文件
     *
     * @param basePackages 可变参数，表示一个或多个基础包的名称
     *                    每个基础包都应该以包的形式指定，例如"com.example.package"
     *                    如果未指定基础包，则默认扫描整个类路径
     */
    void scan(String... basePackages);
}
