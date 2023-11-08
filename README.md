# 构建版本

| LerGo | Spring-boot | 备注                |
|:------|:-----------:|:------------------|
| 1.0.0 |    2.6.7    | 初始化               |
| 1.0.1 |    2.6.7    | 修正部分问题            |
| 1.0.2 |    2.7.6    | 修正部分问题            |
| 1.0.9 |   2.7.17    | 升级OpenAPI/增加模块化启动 |

# 引入依赖

```yaml
 #微服务核心
 Spring Boot  (v2.7.17)
 - spring-boot-starter-webflux
 - spring-boot-autoconfigure-processor
 - spring-boot-starter-validation
 - spring-boot-configuration-processor
 - spring-boot-starter-actuator
 - spring-boot-starter-aop
 - spring-boot-starter-mail
 - spring-boot-starter-data-redis
 - spring-boot-starter-cache

  #语法糖
 lombok

  #接口文档
 OpenApi v1.7.0

  #外部调用
 OpenFeign v13.0

  #数据库连接池
 Druid v1.2.20

  #数据库开发辅助
 MybatisPlus v3.5.4
 PageHelper v1.4.7
 MapStruct v1.5.5.Final
 BeanSearcher v4.2.4

  #Google工具库
 Guava v32.1.3-jre

  #Hutool工具库
 Hutool v5.8.22

  #重试机制
 GuavaRetrying v2.0.0

  #规则引擎
 LiteFlow v2.11.3
```

# 开发规范

推荐依赖中间件 Redis PostgreSQL ~~MySQL/MariaDB~~

### 数据库设计约定

* 主键 字段统一使用 **id** 表示 类型为 [ *int8(64)* ] 保存内容`雪花ID`
* 创建时间 字段统一使用 **createtime** 表示 类型为 [ *timestamp(6)* ] 保存内容`时间辍`
* 更新时间 字段统一使用 **updatetime** 表示 类型为 [ *timestamp(6)* ] 保存内容`时间辍`
* 逻辑删除 字段统一使用 **deleted** 表示 类型为 [ *bool* ] 保存内容`T-F/1-0`
* JSON数据 字段统一使用 **xxx_json** 表示 类型为 [ *jsonb* ] 保存内容`json`

> 旧版本PG 可考虑用 json 类型代替

### 代码约定

* 数据库映射实体类不得使用基础类型

> 例如不得使用: int/long/boolean *阿里巴巴开发手册*

* 数据库映射如包含时间类型请尽量使用 **java.sql.Timestamp**

> MySQL/MariaDB 可考虑Long类型替换

PO代码样例:

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = false)
@ApiModel(value = "自动构建基准字段", description = "TgDemo")
public class TgDemo extends Model<TgDemo> {

    @ApiModelProperty("ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    private Long id;


    @ApiModelProperty("xxxxxx")
    private String name;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private Timestamp createtime;

    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = "FALSE", delval = "TRUE")
    @ApiModelProperty("是否已删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新时间")
    private Timestamp updatetime;

    @JsonRawValue
    private Object jsonProfile;
    
}
```
