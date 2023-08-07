package com.sky.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @author wpc
 * @date 2023/8/7 14:05
 */

@Component
public class CommonUtil {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 重置排序
     */
    public void resetSort(){
        //查询所有分类，按照sort从小到达存入map
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByAsc("sort");

        //sort值相同的，按照update_time排序
        queryWrapper.orderByDesc("update_time");
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        //重置sort
        int i = 1;
        for (Category category : categoryList) {
            category.setSort(i++);
        }
        //批量更新
        for (Category category : categoryList) {
            categoryMapper.updateById(category);
        }

    }

}
