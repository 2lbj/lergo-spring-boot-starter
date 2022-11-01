package com.lergo.framework.utils;

import cn.hutool.core.bean.BeanUtil;

public class BeanTool {
    public static <T> T tryCastClass(Object o, Class<T> clazz) {
        return BeanUtil.mapToBean(
                BeanUtil.beanToMap(o),
                clazz,
                true,null);
    }
}
