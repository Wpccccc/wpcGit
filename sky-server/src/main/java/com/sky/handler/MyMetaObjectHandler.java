package com.sky.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动填充公共字段
 * @author wpc
 * @date 2023/8/7 17:29
 */

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 排除自动填充的实体
     */
    public static List<String> excludeClasses = new ArrayList<>();

    static {
        excludeClasses.add("com.sky.entity.Category");

    }

    public boolean isExclude(MetaObject metaObject){
        if(excludeClasses != null && excludeClasses.size() > 0){
            Object originalObject = metaObject.getOriginalObject();
            for(String cls : excludeClasses){
                try {
                    Class<?> clazz = Class.forName(cls);
                    if(originalObject.getClass().isInstance(clazz.newInstance())){
                        return true;
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    @Override
    public void insertFill(MetaObject metaObject) {
        if (isExclude(metaObject)) {
            return;
        }
        log.info("开始自动填充...");
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createUser", BaseContext.getCurrentId(), metaObject);
        this.setFieldValByName("updateUser", BaseContext.getCurrentId(), metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (isExclude(metaObject)) {
            return;
        }
        log.info("开始自动填充...");
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateUser", BaseContext.getCurrentId(), metaObject);

    }
}
