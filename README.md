![Author](https://img.shields.io/badge/Author-hexLi-666699) ![last-commit](https://img.shields.io/github/last-commit/2lbj/lergo-spring-boot-starter) ![maven-central](https://img.shields.io/maven-central/v/io.github.2lbj/lergo-spring-boot-starter?link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fio.github.2lbj%2Flergo-spring-boot-starter&link=https%3A%2F%2Fmvnrepository.com%2Fartifact%2Fio.github.2lbj%2Flergo-spring-boot-starter)

# LerGo是什么

**LerGo** 是一个基于 SpringBoot 的微服务工程脚手架, 旨在提供一套完整的微服务开发解决方案, 精简项目结构,
使开发者可以专注于业务开发而不是重复造轮子

![简单](https://img.shields.io/badge/%23-%E7%AE%80%E5%8D%95-4f8a7c)
![高效](https://img.shields.io/badge/%23-%E9%AB%98%E6%95%88-4f8a7c)
![开箱即用](https://img.shields.io/badge/%23-%E5%BC%80%E7%AE%B1%E5%8D%B3%E7%94%A8-4f8a7c)

# 如何使用

**Lergo** 已经发布到 Maven 中央仓库, 可以直接引入依赖使用  
原则上你可以在任何基于SpringBoot项目中引入, 但是推荐使用 [LerGo-Example](https://github.com/2lbj/LerGo-Example) 作为基础工程
亦可以参考其中代码最佳实践

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

# 开发规范

推荐依赖中间件 Redis PostgreSQL ~~MySQL/MariaDB~~

### 数据库设计约定

* 主键 **id** <kbd>int8(64)</kbd> 内容:`雪花ID`
* 创建时间 **create_time** <kbd>timestamp(6)</kbd> 内容:`时间戳`
* 更新时间 **update_time** <kbd>timestamp(6)</kbd> 内容:`时间戳`
* 逻辑删除 **deleted** <kbd>bool</kbd> 内容:`T-F/1-0`
* JSON数据 **xxx_json** <kbd>jsonb</kbd> 内容:`json`

> 旧版本PG 可考虑用 json 类型代替

### 代码约定

* 数据库映射实体类不得使用基础类型

> 例如不得使用: int/long/boolean *阿里巴巴开发手册*

* 数据库映射如包含时间类型请尽量使用 **java.sql.Timestamp**

> MySQL/MariaDB 可考虑Long类型替换
