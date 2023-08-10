package com.sky.service.impl;

import com.sky.result.Result;
import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @author wpc
 * @date 2023/8/10 15:00
 */

@Service
public class ShopServiceImpl implements ShopService {

    private static final String KEY = "shopStatus";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    public Result setStatus(String status) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(KEY,status);
        String shopStatus = (String) valueOperations.get(KEY);
        if (shopStatus.equals(status)){
            return Result.success("设置成功");
        }
        else {
            return Result.error("设置失败");
        }
    }

    /**
     * 获取营业状态
     * @return
     */
    public Result getStatus() {
        Integer status = Integer.parseInt(redisTemplate.opsForValue().get(KEY).toString());
        return Result.success(status);
    }
}
