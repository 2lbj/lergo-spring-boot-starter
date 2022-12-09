# 构建版本
| LerGo | Spring-boot | 备注     |
|:------|:-----------:|:-------|
| 1.0.0 |    2.6.7    | 初始化    |
| 1.0.1 |    2.6.7    | 修正部分问题 |
| 1.0.2 |    2.7.6    | 修正部分问题 |

# 开发规范
基于 JAVA PostgreSQL ~~MySQL/MariaDB~~

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
