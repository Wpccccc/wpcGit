package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wpc
 * @date 2023/8/9 9:21
 */

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Transactional
    public Result saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        if ( dishMapper.insert(dish) > 0) {
            Long dishId = dish.getId();
            List<DishFlavor> flavors = dishDTO.getFlavors();
            if (flavors != null && flavors.size() > 0) {
                for (DishFlavor flavor : flavors) {
                    flavor.setDishId(dishId);
                    if ( dishFlavorMapper.insert(flavor) <= 0) {
                        return Result.error("新增口味失败");
                    }
                }
            }
            return Result.success("新增菜品成功");
        } else {
            return Result.error("新增菜品失败");
        }
    }
}
