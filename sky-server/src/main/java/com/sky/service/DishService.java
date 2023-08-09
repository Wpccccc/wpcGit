package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.result.Result;

/**
 * @author wpc
 * @date 2023/8/9 9:20
 */
public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    Result saveWithFlavor(DishDTO dishDTO);
}
