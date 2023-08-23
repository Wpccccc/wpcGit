package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.StatisticsService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @author wpc
 * {@code @date} 2023/8/22 14:09
 */
@RestController
@Slf4j
@RequestMapping("/admin/report")
@Api(tags = "报表管理")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation(value = "营业额统计")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计开始时间：{}，结束时间：{} 之间的营业额", begin, end);
        TurnoverReportVO turnoverReportVO = statisticsService.turnoverStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }

    @ApiOperation(value = "订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计开始时间：{}，结束时间：{} 之间的订单数据", begin, end);
        OrderReportVO orderReportVO = statisticsService.orderStatistics(begin, end);
        return Result.success(orderReportVO);
    }

    @ApiOperation(value = "用户统计")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计开始时间：{}，结束时间：{} 之间的用户数据", begin, end);
        UserReportVO userReportVO = statisticsService.userStatistics(begin, end);
        return Result.success(userReportVO);
    }

    @ApiOperation(value = "销量统计")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> salesStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("统计开始时间：{}，结束时间：{} 之间的销量数据", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = statisticsService.salesStatistics(begin, end);
        return Result.success(salesTop10ReportVO);
    }

    @ApiOperation(value = "导出报表")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        log.info("导出报表");
        statisticsService.export(response);
    }
}
