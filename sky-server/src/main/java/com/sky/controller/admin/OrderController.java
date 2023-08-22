package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wpc
 * @date 2023/8/18 13:53
 */

@RestController("adminOrderController")
@Slf4j
@Api(tags = "订单管理")
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetails(@PathVariable String id) {
        log.info("查询订单详情:{}", id);
        return orderService.adminGetOrderDetails(id);
    }

    @ApiOperation(value = "接单")
    @PutMapping("/confirm")
    public Result<Object> confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单:{}", ordersConfirmDTO);
        return orderService.confirmOrder(ordersConfirmDTO);
    }

    @ApiOperation(value = "拒单")
    @PutMapping("/rejection")
    public Result<Object> rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单:{}", ordersRejectionDTO);
        return orderService.rejectionOrder(ordersRejectionDTO);
    }

    @ApiOperation(value = "取消订单")
    @PutMapping("/cancel")
    public Result<Object> cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单:{}", ordersCancelDTO);
        return orderService.adminCancelOrder(ordersCancelDTO);
    }

    @ApiOperation(value = "完成订单")
    @PutMapping("/complete/{id}")
    public Result<Object> completeOrder(@PathVariable String id) {
        log.info("完成订单:{}", id);
        return orderService.completeOrder(id);
    }

    @ApiOperation(value = "各个状态的订单数量统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statisticsCount() {
        log.info("各个状态的订单数量统计");
        return Result.success(orderService.statisticsCount());
    }

    @ApiOperation(value = "派送订单")
    @PutMapping("/delivery/{id}")
    public Result deliveryOrder(@PathVariable String id) {
        log.info("派送订单:{}", id);
        return orderService.deliveryOrder(id);
    }

    @ApiOperation(value = "订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> searchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索");
        return orderService.searchOrder(ordersPageQueryDTO);
    }

}
