package com.sky.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wpc
 * @date 2023/8/7 14:05
 */

@Component
public class CommonUtil {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

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

    /**
     * 根据菜品id获取分类id
     * @param ids 菜品id串
     * @return 分类id集合
     */
    public Set getCategoryIdByDishId(String ids){
        //根据菜品id查询分类id
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("category_id");
        queryWrapper.in("id", Arrays.asList(ids.split(",")));
        List<HashMap<String, Object>> maps = dishMapper.selectMaps(queryWrapper);
        //获取分类id,并添加前缀"dish_"后保存到Set返回
        Set categoryIds = maps.stream().map(map -> "dish_" + map.get("category_id")).collect(Collectors.toSet());

        return categoryIds;

    }

    /**
     * 根据套餐id获取分类id
     * @param ids 套餐id串
     * @return 分类id集合
     */
    public Set getCategoryIdBySetmealId(String ids){
        //根据套餐id查询分类id
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("category_id");
        queryWrapper.in("id", Arrays.asList(ids.split(",")));
        List<HashMap<String, Object>> maps = setmealMapper.selectMaps(queryWrapper);

        Set categoryIds = maps.stream().map(map -> map.get("category_id")).collect(Collectors.toSet());

        return categoryIds;

    }

    /**
     * 根据起始时间和结束时间获取日期列表,以yyyy-MM-dd格式返回
     * @param begin 起始时间
     * @param end 结束时间
     * @return 日期列表
     */
    public List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (begin.isBefore(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
