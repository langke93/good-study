package org.langke.spring.boot.rest.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;


/**
 * Jackson工具类
 *
 * @author langke
 * @date 2019-12-24
 */
public class JacksonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象转化为Json格式字符串
     *
     * @param obj
     * @return
     * @throws java.io.IOException
     * @throws com.fasterxml.jackson.databind.JsonMappingException
     * @throws com.fasterxml.jackson.core.JsonGenerationException
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将Json格式字符串转化为对象
     *
     * @param <T>
     * @param json
     * @param requiredType
     * @return
     */
    public static <T> T toObject(String json, Class<T> requiredType) {
        try {
            if(json==null|| json.length()==0) {
                return null;
            }
            return (T) mapper.readValue(json, requiredType);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage() + ",json:" + json);
        }
    }

    /**
     * 将Json格式字符串转化为对象
     *
     * @param <T>
     * @param json
     * @param type
     * @return
     */
    public static <T> T toObject(String json, TypeReference<T> type) {
        try {
            if(json==null|| json.length()==0) {
                return null;
            }
            return  mapper.readValue(json, type);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

}
