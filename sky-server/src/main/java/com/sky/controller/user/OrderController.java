package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wpc
 * @date 2023/8/18 13:11
 */
@RestController("userOrderController")
@Slf4j
@Api(tags = "用户订单管理")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "用户提交订单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单:{}", ordersSubmitDTO);
        return orderService.submitOrder(ordersSubmitDTO);
    }

    @ApiOperation(value = "查询订单详情")
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable String id) {
        log.info("查询订单详情:{}", id);
        return orderService.userGetOrderDetail(id);
    }

    @ApiOperation(value = "查询历史订单")
    @GetMapping("/historyOrders")
    public Result<PageResult> getHistoryOrders(String page, String pageSize, String status) {
        log.info("查询历史订单:{},{},{}", page, pageSize, status);
        return orderService.getHistoryOrders(page, pageSize, status);
    }

    @ApiOperation(value = "取消订单")
    @PutMapping("/cancel/{id}")
    public Result<Object> cancelOrder(@PathVariable String id) {
        log.info("取消订单:{}", id);
        orderService.userCancelOrder(id);
        return Result.success("取消订单成功");
    }

    @ApiOperation(value = "再来一单")
    @PostMapping("/repetition/{id}")
    public Result<Object> repetitionOrder(@PathVariable String id) {
        log.info("再来一单:{}", id);
        orderService.repetitionOrder(id);
        return Result.success("再来一单成功");
    }

    @ApiOperation(value = "催单")
    @GetMapping("/reminder/{id}")
    public Result reminderOrder(@PathVariable String id) {
        log.info("催单:{}", id);
        return orderService.reminderOrder(id);
    }

    @ApiOperation(value = "支付订单")
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("支付订单:{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单:{}", orderPaymentVO);

        //模拟支付成功
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());

        return Result.success(orderPaymentVO);
    }

}
