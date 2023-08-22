package com.sky.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wpc
 * {@code @date} 2023/8/22 9:14
 */

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 0/1 * * * ?") //每分钟执行一次
    public void processTimeoutOrder() {
        log.info("定时任务：处理超时订单  {}", LocalDateTime.now());

        //1.查询超时且未付款订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Orders.PENDING_PAYMENT);
        queryWrapper.le("order_time", LocalDateTime.now().minusMinutes(15));
        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);

        //2.修改订单状态为已取消
        if (ordersList != null && ordersList.size() > 0) {
            log.info("超时订单数量：{}", ordersList.size());
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("超时未支付，系统自动取消");
                orders.setCancelTime(LocalDateTime.now());
                ordersMapper.updateById(orders);
            });
        } else {
            log.info("没有超时订单");
        }
    }

    /**
     * 处理一直在配送中的订单
     */
    @Scheduled(cron = "0 0 5 * * ?") //每天凌晨5点执行
    public void processDeliveryOrder() {
        log.info("定时任务：处理一直在配送中的订单  {}", LocalDateTime.now());

        //1.查询前一天且状态为配送中的订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", Orders.DELIVERY_IN_PROGRESS);
        queryWrapper.le("order_time", LocalDateTime.now().minusDays(1));
        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);

        //2.修改订单状态为已完成
        if (ordersList != null && ordersList.size() > 0) {
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                ordersMapper.updateById(orders);
            });
        }
    }
}
