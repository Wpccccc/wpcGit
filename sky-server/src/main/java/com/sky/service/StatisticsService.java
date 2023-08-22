package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

/**
 * @author wpc
 * {@code @date} 2023/8/22 14:16
 */
public interface StatisticsService {

    /**
     * 营业额统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计结果
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计结果
     */
    OrderReportVO orderStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计结果
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 销售统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销售统计结果
     */
    SalesTop10ReportVO salesStatistics(LocalDate begin, LocalDate end);
}
