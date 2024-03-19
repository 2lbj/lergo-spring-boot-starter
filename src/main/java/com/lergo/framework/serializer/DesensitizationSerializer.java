package com.lergo.framework.serializer;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.lergo.framework.annotation.Desensitization;

import java.io.IOException;

/**
 * 数据脱敏序列化器
 */
public class DesensitizationSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private DesensitizedUtil.DesensitizedType type;
    private int prefixLen;
    private int suffixLen;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            if (type != null) {
                gen.writeString(DesensitizedUtil.desensitized(value, type));
            } else if (prefixLen > 0 || suffixLen > 0) {
                gen.writeString(StrUtil.hide(value, prefixLen, suffixLen));
            } else {
                gen.writeString(DesensitizedUtil.clear());
            }
        } else {
            gen.writeObject(DesensitizedUtil.clearToNull());
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property != null) {
            Desensitization desensitization = property.getAnnotation(Desensitization.class);
            if (desensitization != null) {
                this.type = desensitization.type();
                this.prefixLen = desensitization.prefixLen();
                this.suffixLen = desensitization.suffixLen();
            }
        }
        return this;
    }
}