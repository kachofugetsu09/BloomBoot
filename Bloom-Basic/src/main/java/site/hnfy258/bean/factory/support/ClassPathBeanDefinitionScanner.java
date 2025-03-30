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

public class ClassPathBeanDefinitionScanner implements ClassPathScanner {
    private final DefaultListableBeanFactory beanFactory;

    public ClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void scan(String... basePackages) {
        // 如果没有指定包，则扫描调用类所在的包
        if (basePackages == null || basePackages.length == 0) {
            String mainPackage = getMainClassPackage();
            if (mainPackage != null) {
                basePackages = new String[]{mainPackage};
            } else {
                throw new RuntimeException("无法确定主类所在包，请明确指定要扫描的包");
            }
        }

        for (String basePackage : basePackages) {
            scanPackage(basePackage);
        }
    }



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
            }
        }
        return null;
    }


    private void scanPackage(String basePackage) {
        String path = basePackage.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    // 处理文件系统中的类
                    scanDirectory(new File(resource.getFile()), basePackage);
                } else if ("jar".equals(protocol)) {
                    // 处理JAR包中的类
                    scanJarPackage(resource, basePackage);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("扫描包时出错: " + basePackage, e);
        }
    }

    private void scanDirectory(File directory, String packageName) {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                // 递归扫描子目录
                scanDirectory(file, packageName + "." + fileName);
            } else if (fileName.endsWith(".class")) {
                try {
                    // 获取类名并加载类
                    String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    processAnnotations(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scanJarPackage(URL url, String packageName) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        String packagePath = packageName.replace('.', '/');

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();

            // 检查是否属于指定的包
            if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                // 将路径转换为类名
                String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                try {
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    processAnnotations(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processAnnotations(Class<?> clazz) {
        // 处理所有支持的注解
        processBeanAnnotation(clazz, Component.class);
        processBeanAnnotation(clazz, Bean.class);
        processBeanAnnotation(clazz, Repository.class);
        processBeanAnnotation(clazz, Resource.class);
        processBeanAnnotation(clazz, Service.class);
    }

    private <T extends Annotation> void processBeanAnnotation(Class<?> clazz, Class<T> annotationType) {
        if (clazz.isAnnotationPresent(annotationType)) {
            BeanDefinition beanDefinition = new BeanDefinition(clazz);

            // 设置作用域
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