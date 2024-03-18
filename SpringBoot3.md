# spring3.x + JDK17 升级替换

> https://zhuanlan.zhihu.com/p/675114644

javax.persistence.*   -> jakarta.persistence.*  
javax.validation.*    -> jakarta.validation.*  
javax.servlet.*       -> jakarta.servlet.*  
javax.annotation.*    -> jakarta.annotation.*  
javax.transaction.*   -> jakarta.transaction.*

- javax.annotation.Resource -> jakarta.annotation.Resource
- javax.validation.ConstraintViolationException -> jakarta.validation.ConstraintViolationException
- javax.validation.Path -> jakarta.validation.Path
- org.jetbrains.annotations.NotNull -> jakarta.validation.constraints.NotNull
- org.springdoc.core.GroupedOpenApi -> org.springdoc.core.models.GroupedOpenApi
    - springdoc.api-docs.enabled=false ==> springdoc.swagger-ui.enabled=false

# Swagger 2.x -> OpenAPI 3.x

- @Api -> @Tag
- @ApiIgnore -> @Parameter(hidden = true) or @Operation(hidden = true) or @Hidden
- @ApiImplicitParam -> @Parameter
- @ApiImplicitParams -> @Parameters
- @ApiModel -> @Schema
- @ApiModelProperty(hidden = true) -> @Schema(accessMode = READ_ONLY)
- @ApiModelProperty -> @Schema
- @ApiOperation(value = "foo", notes = "bar") -> @Operation(summary = "foo", description = "bar")
- @ApiParam -> @Parameter
- @ApiResponse(code = 404, message = "foo") -> @ApiResponse(responseCode = "404", description = "foo")