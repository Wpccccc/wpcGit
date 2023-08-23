package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author wpc
 * {@code @date} 2023/8/23 9:12
 */
@Slf4j
@RestController
@Api(tags = "工作台管理")
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    @ApiOperation(value = "获取今日数据")
    @GetMapping("/businessData")
    public Result<BusinessDataVO> getTodayData() {
        log.info("获取今日数据");
        LocalDateTime begin = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return Result.success(workSpaceService.getBusinessData(begin, end));
    }

    @ApiOperation(value = "获取套餐概览")
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> getSetmealOverView() {
        log.info("获取套餐概览");
        return Result.success(workSpaceService.getSetmealOverView());
    }

    @ApiOperation(value = "获取菜品概览")
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> getDishOverView() {
        log.info("获取菜品概览");
        return Result.success(workSpaceService.getDishOverView());
    }

    @ApiOperation(value = "获取订单概览")
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> getOrderOverView() {
        log.info("获取订单概览");
        return Result.success(workSpaceService.getOrderOverView());
    }
}
