    package site.hnfy258.bean.factory.support;

    import site.hnfy258.aop.framework.autoproxy.AspectJAutoProxyCreator;
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


        public ClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }


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
            registerAopProcessors();

            System.out.println("包扫描完成，共发现 " + beanFactory.getBeanDefinitionNames().length + " 个Bean定义");


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
                    throw new RuntimeException("无法加载类: " + element.getClassName(), e);
                }
            }
            return null;
        }


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

        private void registerAopProcessors() {
            // 实例化并注册AspectJAutoProxyCreator
            try {
                AspectJAutoProxyCreator creator = new AspectJAutoProxyCreator(beanFactory);
                // Set the bean factory before adding it as a post processor
                creator.setBeanFactory(beanFactory);

                // Initialize the advisor cache to process all aspect-related beans
                creator.initializeAdvisorsCache();

                beanFactory.addBeanPostProcessor(creator);
                System.out.println("已注册AOP处理器: AspectJAutoProxyCreator");
            } catch (Exception e) {
                System.err.println("注册AOP处理器失败");
                e.printStackTrace();
            }
        }
    }
