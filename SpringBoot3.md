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