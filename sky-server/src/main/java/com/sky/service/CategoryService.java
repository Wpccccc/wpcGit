package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;

/**
 * @author wpc
 * @date 2023/8/7 10:51
 */
public interface CategoryService {
    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    Result saveCategory(CategoryDTO categoryDTO);

    /**
     * 修改分类状态
     * @param status 分类状态
     * @param id 分类id
     * @return
     */
    Result switchStatus(Integer status, Long id);

    /**
     * 删除分类
     * @param id
     * @return
     */
    Result deleteCategory(Long id);

    /**
     * 更新分类信息
     * @param categoryDTO
     * @return
     */
    Result updateCategory(CategoryDTO categoryDTO);
}
