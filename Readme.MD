# BloomBoot

## 项目目标

通过学习 Spring 核心功能，实现一个轻量级的框架，以加深对 Spring 原理的理解。

## 功能规划

### Bean 基础功能
- [x] Bean 的注册机制
- [x] Bean 工厂实现

- [x] `@Component` 注解支持
- [x] `@Bean` 注解支持
- [x] `@Repository` 注解支持
- [x] `@Scope` 注解支持
- [x] `@Resource` 注解支持
- [x] `@Service` 注解支持
- [x] `@Autowired` 注解支持
- [x] `BloomBoot` 注解支持
- [ ] 消费组/监听组注解
- [x] 循环依赖解决方案
- [x] Bean 的动态实例化
- [x] Bean 的初始化和销毁方法

### 特色功能
- [ ] 常用 Bean 判断与优先加载 (Bean 使用频率分析)
- [ ] Bean 懒加载优化
- [ ] 细粒度的 Bean 加载控制注解

### AOP 相关功能
- [ ] 基于 JDK 动态代理实现 AOP
- [ ] 基于 CGLib 实现 AOP
- [ ] 将 AOP 扩展到 Bean 生命周期中
