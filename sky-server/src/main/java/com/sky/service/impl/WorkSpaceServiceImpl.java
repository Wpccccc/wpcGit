package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.entity.User;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.*;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wpc
 * {@code @date} 2023/8/23 9:30
 */
@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 获取营业数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业数据
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        //1.获取营业额
        Double turnover = ordersMapper.getTurnover(begin, end, Orders.COMPLETED);
        if (turnover == null) {
            turnover = 0.0;
        }

        //2.获取有效订单数
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("order_time", begin, end);
        queryWrapper.eq("status", Orders.COMPLETED);
        Integer validOrderCount = ordersMapper.selectCount(queryWrapper);
        if (validOrderCount == null) {
            validOrderCount = 0;
        }

        //3.获取订单完成率
        double orderCompletionRate = 0.0;
        Integer totalOrderCount = ordersMapper.selectCount(new QueryWrapper<Orders>().between("order_time", begin, end));
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount / totalOrderCount.doubleValue();
            //保留4位小数
            orderCompletionRate = (double) Math.round(orderCompletionRate * 10000) / 10000;
        }

        //4.获取平均客单价
        double unitPrice = 0.0;
        if (validOrderCount != 0) {
            unitPrice = turnover / validOrderCount;
            //保留两位小数
            unitPrice = (double) Math.round(unitPrice * 100) / 100;
        }

        //5.获取新增用户数
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.between("create_time", begin, end);
        Integer newUsers = userMapper.selectCount(userQueryWrapper);
        if (newUsers == null) {
            newUsers = 0;
        }
        //6.封装数据
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 获取套餐概览
     * @return 套餐概览
     */
    public SetmealOverViewVO getSetmealOverView() {
        //1.查询所有在售套餐数量
        Integer sole = setmealMapper.selectCount(new QueryWrapper<Setmeal>().eq("status", StatusConstant.ENABLE));

        //2.查询所有停售套餐数量
        Integer disable = setmealMapper.selectCount(new QueryWrapper<Setmeal>().eq("status", StatusConstant.DISABLE));

        //3.返回封装数据
        return SetmealOverViewVO.builder()
                .sold(sole)
                .discontinued(disable)
                .build();
    }

    /**
     * 获取菜品概览
     * @return 菜品概览
     */
    public DishOverViewVO getDishOverView() {
        //1.查询所有在售菜品数量
        Integer sole = dishMapper.selectCount(new QueryWrapper<Dish>().eq("status", StatusConstant.ENABLE));

        //2.查询所有停售菜品数量
        Integer disable = dishMapper.selectCount(new QueryWrapper<Dish>().eq("status", StatusConstant.DISABLE));

        //3.返回封装数据
        return DishOverViewVO.builder()
                .sold(sole)
                .discontinued(disable)
                .build();
    }

    /**
     * 获取订单概览
     * @return 订单概览
     */
    public OrderOverViewVO getOrderOverView() {
        //1.查询所有待接单订单数量
        Integer waitingOrders = ordersMapper.selectCount(new QueryWrapper<Orders>().eq("status", Orders.TO_BE_CONFIRMED));

        //2.查询所有待派送订单数量
        Integer deliveredOrders = ordersMapper.selectCount(new QueryWrapper<Orders>().eq("status", Orders.CONFIRMED));

        //3.查询所有已完成订单数量
        Integer completedOrders = ordersMapper.selectCount(new QueryWrapper<Orders>().eq("status", Orders.COMPLETED));

        //4.查询所有已取消订单数量
        Integer cancelledOrders = ordersMapper.selectCount(new QueryWrapper<Orders>().eq("status", Orders.CANCELLED));

        //5.查询所有订单数量
        Integer allOrders = ordersMapper.selectCount(new QueryWrapper<>());

        //6.返回封装数据
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }
}
