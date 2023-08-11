package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wpc
 * @date 2023/8/9 15:59
 */

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐管理")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation(value = "分页查询套餐列表")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询套餐列表:{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation(value = "新增套餐")
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐:{}",setmealDTO);
        return setmealService.saveWithDishes(setmealDTO);
    }

    @ApiOperation(value = "根据id查询套餐")
    @GetMapping("/{id}")
    public Result getSetmealById(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);
        return setmealService.getSetmealById(id);
    }

    @ApiOperation(value = "修改套餐售卖状态")
    @PostMapping("/status/{status}")
    public Result switchStatus(@PathVariable Integer status, Long id){
        log.info("修改套餐售卖状态:{},{}",status,id);
        return setmealService.switchStatus(status,id);
    }

    @ApiOperation(value = "修改套餐")
    @PutMapping
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐:{}",setmealDTO);
        return setmealService.updateSetmeal(setmealDTO);
    }


    @ApiOperation(value = "删除套餐")
    @DeleteMapping
    public Result deleteSetmeal(String ids){
        log.info("删除套餐:{}",ids);
        return setmealService.deleteSetmeal(ids);
    }
}
