package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wpc
 * {@code @date} 2023/8/23 9:20
 */
public interface WorkSpaceService {

    /**
     * 获取营业数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业数据
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 获取套餐概览
     * @return 套餐概览
     */
    SetmealOverViewVO getSetmealOverView();

    /**
     * 获取菜品概览
     * @return 菜品概览
     */

    DishOverViewVO getDishOverView();
    /**
     * 获取订单概览
     * @return 订单概览
     */
    OrderOverViewVO getOrderOverView();
}
