package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wpc
 * @date 2023/8/11 15:45
 */

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "查询套餐")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation(value = "根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result getDishListBySetmealId(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);
        return setmealService.getDishListBySetmealId(id);
    }

    @ApiOperation(value = "根据分类id查询套餐")
    @GetMapping("/list")
    public Result getSetmealByCategoryId(Long categoryId) {
        log.info("根据分类id查询套餐:{}", categoryId);
        return setmealService.getSetmealByCategoryId(categoryId);
    }
}
