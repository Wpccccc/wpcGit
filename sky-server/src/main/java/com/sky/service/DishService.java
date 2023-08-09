package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;

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

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 修改菜品售卖状态
     * @param status 菜品状态
     * @param id 菜品id
     * @return
     */
    Result switchStatus(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    Result getDishListByCategoryId(Long categoryId);

    /**
     * 根据菜品id查询菜品
     * @param id
     * @return
     */
    DishVO getDishById(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    Result updateDish(DishDTO dishDTO);


    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    Result bulkDeleteDish(String ids);
}
