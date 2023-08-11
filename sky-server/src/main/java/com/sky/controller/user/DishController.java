package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wpc
 * @date 2023/8/11 16:23
 */

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;


    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result getDishListByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        return dishService.getDishWithFlavorListByCategoryId(categoryId);
    }
}
