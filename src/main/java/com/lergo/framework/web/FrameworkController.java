package com.lergo.framework.web;


import com.lergo.framework.annotation.Authentication;
import com.lergo.framework.annotation.LogTracker;
import com.lergo.framework.config.LergoConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("framework")
@Tag(name = "框架", description = "框架基础接口")
public class FrameworkController {

    @Resource
    LergoConfig lergoConfig;

    @GetMapping("ping")
    @LogTracker("...Ping")
    @Operation(summary = "服务连通性测试接口")
    public String ping() {
        return "PONG";
    }

//    @PostMapping("body")
//    @Operation(summary = "body")
//    public Demo body(
//            @RequestParam int x,
//            @RequestBody Demo request) {
//        return request;
//    }
//
//    @PostMapping(value = "form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "form")
//    public String form(
//            @RequestParam int x,
//            @RequestPart("s") String s,
//            @RequestPart("file") FilePart filePart) throws IOException {
//
//        Path tempFile = Files.createTempFile(s, filePart.filename());
//        filePart.transferTo(tempFile).subscribe();
//
////        DataBufferUtils.write(filePart.content(),
////                        AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE),
////                        0).doOnComplete(() -> {
////            System.out.println("finish");
////        }).subscribe();
//        return "=>" + tempFile;
//    }
//@Data
//static class Demo {
//
//    @Min(value = 1, message = "订单编号过小")
//    @Max(value = 100, message = "订单编号过大")
//    @NotNull(message = "订单编号不能为空")
//    private Integer orderId;
//
//    @NotBlank(message = "订单名称不能为空")
//    private String orderName;
//
//    @Size(min = 1, max = 10)
//    private List<String> goodsList;
//
//    @DecimalMin(value = "1", message = "订单金额必须大于等于1")
//    private BigDecimal amount;
//}

    @GetMapping("authentication")
    @Authentication
    @Operation(summary = "服务鉴权测试接口")
    public String authentication() {
        return "OK";
    }

    @GetMapping("config")
    public LergoConfig config() {
        return lergoConfig;
    }

}

