package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wpc
 * @date 2023/8/9 9:16
 */

@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        return dishService.saveWithFlavor(dishDTO);
    }

    @ApiOperation("分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品：{}", dishPageQueryDTO);
        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }

    @ApiOperation("修改菜品售卖状态")
    @PostMapping("/status/{status}")
    public Result switchStatus(@PathVariable Integer status, Long id) {
        log.info("修改菜品售卖状态：{}", status, id);
        return dishService.switchStatus(status, id);
    }

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result getDishListByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        return dishService.getDishListByCategoryId(categoryId);
    }

    @ApiOperation("根据菜品id查询菜品")
    @GetMapping("/{id}")
    public Result getDishById(@PathVariable Long id) {
        log.info("根据菜品id查询菜品：{}", id);
        return Result.success(dishService.getDishById(id));
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        return dishService.updateDish(dishDTO);
    }

    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result deleteDish(String ids){
        log.info("批量删除菜品：{}", ids);
        return dishService.deleteDish(ids);
    }
}
