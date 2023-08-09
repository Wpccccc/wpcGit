package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author wpc
 * @date 2023/8/9 9:24
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 根据菜品名称查询菜品
     * @param name
     * @return
     */
    @Select("select * from dish where name = #{name}")
    Dish getByName(String name);
}
