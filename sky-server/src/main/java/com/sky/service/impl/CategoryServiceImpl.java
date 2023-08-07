package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.utils.CommonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @author wpc
 * @date 2023/8/7 10:51
 */

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        IPage page = new Page(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //根据分类名称模糊查询
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(categoryPageQueryDTO.getType()!=null,"type",categoryPageQueryDTO.getType());
        queryWrapper.like(StringUtils.hasText(categoryPageQueryDTO.getName()), "name", categoryPageQueryDTO.getName());
        queryWrapper.orderByAsc("sort");
        IPage result = categoryMapper.selectPage(page, queryWrapper);
        return new PageResult(result.getTotal(), result.getRecords());
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    public Result saveCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(0);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        try{
            categoryMapper.insert(category);
            commonUtil.resetSort();
            return Result.success();
        }
        catch (Exception e){
            return Result.error("新增失败");
        }
    }
}