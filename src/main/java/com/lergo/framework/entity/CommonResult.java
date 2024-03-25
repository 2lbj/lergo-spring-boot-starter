package com.lergo.framework.entity;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Schema(title = "统一返回数据体", description = "JSON包装格式")
public class CommonResult<T> implements Serializable {

    public static Integer CODE_SUCCESS = 200;
    public static Gson gson = new Gson();

    /**
     * 错误码
     */
    @Schema(description = "状态码")
    private Integer code;
    /**
     * 错误提示
     */
    @Schema(description = "状态描述")
    private String message;
    /**
     * 返回数据
     */
    @Schema(description = "数据体")
    private T data;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     * <p>
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T>    返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMessage());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.isTrue(!CODE_SUCCESS.equals(code), "code must not be success");
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = CODE_SUCCESS;
        result.data = data;
        result.message = "";
        return result;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public boolean successCode() {
        return Objects.equals(this.code, CODE_SUCCESS);
    }

    public CommonResult<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public CommonResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public CommonResult<T> setData(T data) {
        this.data = data;
        return this;
    }
}
