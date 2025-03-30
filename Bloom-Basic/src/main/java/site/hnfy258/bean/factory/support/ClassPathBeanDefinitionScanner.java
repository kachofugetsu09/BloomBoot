package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.annotation.*;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.ClassPathScanner;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ClassPathBeanDefinitionScanner 类用于扫描指定包路径下的类，并根据注解生成Bean定义。
 */
public class ClassPathBeanDefinitionScanner implements ClassPathScanner {
    private final DefaultListableBeanFactory beanFactory;

    /**
     * 构造函数，初始化ClassPathBeanDefinitionScanner。
     *
     * @param beanFactory 用于注册Bean定义的工厂实例
     */
    public ClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 扫描指定的包路径，查找带有特定注解的类，并生成Bean定义。
     *
     * @param basePackages 要扫描的包路径数组，如果为空，则尝试获取主类所在的包
     */
    @Override
    public void scan(String... basePackages) {
        if (basePackages == null || basePackages.length == 0) {
            String mainPackage = getMainClassPackage();
            if (mainPackage != null) {
                basePackages = new String[]{mainPackage};
            } else {
                throw new RuntimeException("无法确定主类所在包，请明确指定要扫描的包");
            }
        }

        System.out.println("开始扫描包: " + String.join(", ", basePackages));
        for (String basePackage : basePackages) {
            scanPackage(basePackage);
        }

        System.out.println("包扫描完成，共发现 " + beanFactory.getBeanDefinitionNames().length + " 个Bean定义");
    }

    /**
     * 获取主类所在的包名。
     *
     * @return 主类所在的包名，如果找不到则返回null
     */
    private String getMainClassPackage() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals("main") && method.getParameterCount() == 1) {
                        return clazz.getPackage().getName();
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("无法加载类: " + element.getClassName(), e);
            }
        }
        return null;
    }

    /**
     * 扫描指定包路径下的所有类文件。
     *
     * @param basePackage 要扫描的包路径
     */
    private void scanPackage(String basePackage) {
        String path = basePackage.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            if (resources == null) {
                throw new RuntimeException("无法获取资源: " + path);
            }
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    scanDirectory(new File(resource.getFile()), basePackage);
                } else if ("jar".equals(protocol)) {
                    scanJarPackage(resource, basePackage);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("扫描包时出错: " + basePackage, e);
        }
    }

    /**
     * 扫描指定目录下的所有类文件，并处理带有特定注解的类。
     *
     * @param directory   要扫描的目录
     * @param packageName 当前扫描的包名
     */
    private void scanDirectory(File directory, String packageName) {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + fileName);
            } else if (fileName.endsWith(".class")) {
                try {
                    String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    processAnnotations(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("无法加载类: " + packageName + "." + fileName, e);
                }
            }
        }
    }

    /**
     * 扫描JAR包中的类文件，并处理带有特定注解的类。
     *
     * @param url         JAR包的URL
     * @param packageName 要扫描的包名
     * @throws IOException 如果读取JAR包时发生错误
     */
    private void scanJarPackage(URL url, String packageName) throws IOException {
        try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            String packagePath = packageName.replace('.', '/');

            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String entryName = jarEntry.getName();

                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                    try {
                        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                        processAnnotations(clazz);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("无法加载类: " + className, e);
                    }
                }
            }
        }
    }

    /**
     * 处理类上的注解，如果类带有特定注解，则生成Bean定义。
     *
     * @param clazz 要处理的类
     */
    private void processAnnotations(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Component || annotation instanceof Bean ||
                annotation instanceof Repository || annotation instanceof Resource ||
                annotation instanceof Service) {
                processBeanAnnotation(clazz, annotation.annotationType());
            }
        }
    }

    /**
     * 处理带有特定注解的类，生成Bean定义并注册到BeanFactory中。
     *
     * @param clazz          要处理的类
     * @param annotationType 注解类型
     * @param <T>            注解类型
     */
    private <T extends Annotation> void processBeanAnnotation(Class<?> clazz, Class<T> annotationType) {
        if (clazz.isAnnotationPresent(annotationType)) {
            BeanDefinition beanDefinition = new BeanDefinition(clazz);

            if (clazz.isAnnotationPresent(Scope.class)) {
                beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
            } else {
                beanDefinition.setScope("singleton");
            }

            T annotation = clazz.getAnnotation(annotationType);
            String beanName;

            try {
                Method valueMethod = annotationType.getDeclaredMethod("value");
                String value = (String) valueMethod.invoke(annotation);
                beanName = value.equals("") ? clazz.getSimpleName() : value;
            } catch (Exception e) {
                beanName = clazz.getSimpleName();
            }

            beanFactory.registerBeanDefinition(beanName, beanDefinition);
        }
    }
}
