[![Author](https://img.shields.io/badge/Author-hexLi-666699)](https://2lbj.github.io/) [![last-commit](https://img.shields.io/github/last-commit/2lbj/lergo-spring-boot-starter)](https://github.com/2lbj/lergo-spring-boot-starter) [![maven-central](https://img.shields.io/maven-central/v/io.github.2lbj/lergo-spring-boot-starter?link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fio.github.2lbj%2Flergo-spring-boot-starter)](https://central.sonatype.com/artifact/io.github.2lbj/lergo-spring-boot-starter) [![license](https://img.shields.io/badge/license-GPLv3.0-orange)](./LICENSE)

# LerGo是什么

**LerGo** 是一个基于 SpringBoot 的微服务`工程脚手架`, 旨在提供一套完整的微服务开发解决方案, 精简项目结构,
使开发者可以专注于业务开发而不是重复造轮子

![精简](https://img.shields.io/badge/%23-%E7%B2%BE%E7%AE%80-4f8a7c)
![高效](https://img.shields.io/badge/%23-%E9%AB%98%E6%95%88-4f8a7c)
![开箱即用](https://img.shields.io/badge/%23-%E5%BC%80%E7%AE%B1%E5%8D%B3%E7%94%A8-4f8a7c)

# 如何使用

**Lergo** 已经发布到 Maven 中央仓库, 可以直接引入依赖使用  
原则上你可以在任何基于SpringBoot项目中引入  
强烈推荐克隆 [LerGo-Example](https://github.com/2lbj/LerGo-Example) 作为基础工程, 亦可以参考其中代码最佳实践

```xml
<dependency>
    <groupId>io.github.2lbj</groupId>
    <artifactId>lergo-spring-boot-starter</artifactId>
    <version>1.0.9</version>
</dependency>
```

# 上游依赖

| LerGo | Spring-boot | 备注                |
|:------|:-----------:|:------------------|
| 1.0.0 |    2.6.7    | 初始化               |
| 1.0.1 |    2.6.7    | 修正部分问题            |
| 1.0.2 |    2.7.6    | 修正部分问题            |
| 1.0.9 |   2.7.17    | 升级OpenAPI/增加模块化启动 |

# 引入模块

```markdown
 > 微服务核心
 Spring Boot
 - spring-boot-starter-webflux
 - spring-boot-autoconfigure-processor
 - spring-boot-starter-validation
 - spring-boot-configuration-processor
 - spring-boot-starter-actuator
 - spring-boot-starter-aop
 - spring-boot-starter-mail
 - spring-boot-starter-data-redis
 - spring-boot-starter-cache

> 语法糖
 lombok

> 接口文档
 OpenApi

> 外部调用
 OpenFeign

> 数据库连接池
 Druid

> 数据库开发辅助
 MybatisPlus
 PageHelper
 MapStruct
 BeanSearcher

> Google工具库
 Guava

> Hutool工具库
 Hutool

> 重试机制
 GuavaRetrying

> 规则引擎
 LiteFlow 
```

# 配置文件说明

**LerGo** 提供了一些默认配置, 你可以在 `application-xxx.yml` 中覆盖它们

> 以下配置均为可选配置, 你可以根据自己的需求选择性配置  
> **甚至可以完全不写**

* 启动模块
   ```yaml
   lergo:
     boot-with-jdbc: false # 是否启用JDBC (默认禁用)
     boot-with-redis: false # 是否启用Redis (默认禁用)
   ```
* 文档配置
    ```yaml
    open-api:
      title:  基础开发脚手架 # 文档_项目标题 (默认spring.application.name)
      version: 1.0.0 # 文档_项目版本(默认application.version)
      termsOfService: ' https://your.service.com' # 文档_服务条款
      description: 基于SpringBoot的微服务开发脚手架 # 文档_项目描述
    ```

# TOTO

1. [ ] 日志 traceId 耗时
2. [ ] pageBean searchBean
3. [ ] 重试机制 feign
4. [x] 异常捕获及校验
5. [x] 前置解析模块 http状态码转义固定json格式