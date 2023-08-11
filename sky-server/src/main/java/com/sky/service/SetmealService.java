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

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    Result getSetmealById(Long id);

    /**
     * 修改套餐售卖状态
     * @param status
     * @param id
     * @return
     */
    Result switchStatus(Integer status, Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    Result updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    Result deleteSetmeal(String ids);

    /**
     * 根据套餐id查询所含菜品
     * @param id
     * @return
     */
    Result getDishListBySetmealId(Long id);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    Result getSetmealByCategoryId(Long categoryId);
}
