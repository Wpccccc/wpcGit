package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;

/**
 * @author wpc
 * @date 2023/8/9 15:57
 */
public interface SetmealService {

    /**
     * 分页查询套餐列表
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    Result saveWithDishes(SetmealDTO setmealDTO);
}
