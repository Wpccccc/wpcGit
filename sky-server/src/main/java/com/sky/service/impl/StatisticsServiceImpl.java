package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.exception.BaseException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.StatisticsService;
import com.sky.service.WorkSpaceService;
import com.sky.utils.CommonUtil;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wpc
 * {@code @date} 2023/8/22 14:17
 */

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 营业额统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计结果
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = commonUtil.getDateList(begin, end);
        List<String> totalAmountList = new ArrayList<>();

        //查询每天的营业额
        for (LocalDate date : dateList) {
            //设置统计的时间区间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //统计每天的营业额，其值为订单总金额
            Double totalAmount = ordersMapper.getTurnover(beginTime, endTime,Orders.COMPLETED);
            if (totalAmount == null) {
                totalAmount = 0.0;
            }
            totalAmountList.add(totalAmount.toString());
        }
        //将日期与营业额转换为字符串
        String dateStr = StringUtils.join(dateList, ",");
        String totalAmountStr = String.join(",", totalAmountList);

        //封装结果
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(dateStr);
        turnoverReportVO.setTurnoverList(totalAmountStr);

        return turnoverReportVO;
    }

    /**
     * 订单统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计结果
     */
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = commonUtil.getDateList(begin, end);  //日期列表
        List<Integer> orderList = new ArrayList<>();                    //每日总订单数
        List<Integer> completedOrderList = new ArrayList<>();           //每日完成订单数
        int totalOrder = 0;                                         //总订单数
        int totalCompletedOrder = 0;                                //总完成订单数
        double orderCompletionRate = 0.0;                               //订单完成率

        //查询每天的订单数和完成订单数
        for (LocalDate date : dateList) {
            //设置统计的时间区间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            QueryWrapper<Orders> orderQueryWrapper = new QueryWrapper<>();
            QueryWrapper<Orders> completedOrderQueryWrapper = new QueryWrapper<>();

            //统计每天的订单数
            orderQueryWrapper.between("order_time", beginTime, endTime);
            Integer order = ordersMapper.selectCount(orderQueryWrapper);
            if (order == null) {
                order = 0;
            }
            orderList.add(order);
            totalOrder += order;

            //统计每天的完成订单数
            completedOrderQueryWrapper.between("order_time", beginTime, endTime);
            completedOrderQueryWrapper.eq("status", Orders.COMPLETED);
            Integer completedOrder = ordersMapper.selectCount(completedOrderQueryWrapper);
            if (completedOrder == null) {
                completedOrder = 0;
            }
            completedOrderList.add(completedOrder);
            totalCompletedOrder += completedOrder;
        }
        //计算订单完成率
        if (totalOrder != 0) {
            orderCompletionRate = (double) totalCompletedOrder / (double) totalOrder;
        }
        //将日期与订单数转换为字符串
        String dateStr = StringUtils.join(dateList, ",");
        String orderStr = StringUtils.join(orderList, ",");
        String completedOrderStr = StringUtils.join(completedOrderList, ",");

        //封装结果
        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(dateStr);
        orderReportVO.setOrderCountList(orderStr);
        orderReportVO.setValidOrderCountList(completedOrderStr);
        orderReportVO.setTotalOrderCount(totalOrder);
        orderReportVO.setValidOrderCount(totalCompletedOrder);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);

        return orderReportVO;
    }

    /**
     * 用户统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计结果
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = commonUtil.getDateList(begin, end);
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        //查询每天的总用户数和新增用户数
        for (LocalDate date : dateList) {
            //设置统计的时间区间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            QueryWrapper<User> totalUserQueryWrapper = new QueryWrapper<>();
            QueryWrapper<User> newUserQueryWrapper = new QueryWrapper<>();

            //统计每天的总用户数
            totalUserQueryWrapper.le("create_time", endTime);
            Integer totalUser = userMapper.selectCount(totalUserQueryWrapper);
            if (totalUser == null) {
                throw new BaseException("查询用户总数失败");
            }
            totalUserList.add(totalUser);

            //统计每天的新增用户数
            newUserQueryWrapper.between("create_time", beginTime, endTime);
            Integer newUser = userMapper.selectCount(newUserQueryWrapper);
            if (newUser == null) {
                newUser = 0;
            }
            newUserList.add(newUser);
        }
        //将日期与用户数转换为字符串
        String dateStr = StringUtils.join(dateList, ",");
        String totalUserStr = StringUtils.join(totalUserList, ",");
        String newUserStr = StringUtils.join(newUserList, ",");

        //封装结果
        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(dateStr);
        userReportVO.setTotalUserList(totalUserStr);
        userReportVO.setNewUserList(newUserStr);

        return userReportVO;
    }

    /**
     * 销售统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销售统计结果
     */
    public SalesTop10ReportVO salesStatistics(LocalDate begin, LocalDate end) {
        //设置统计的时间区间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //查询销量
        List<GoodsSalesDTO> goodsSalesDTOList = orderDetailMapper.getSalesStatistics(beginTime, endTime);

        //取销量前10的商品，并将其名称和销量转换成字符串
        List<String> nameList = new ArrayList<>();
        List<Integer> salesList = new ArrayList<>();
        for (int i = 0; i < 10 && i < goodsSalesDTOList.size(); i++) {
            nameList.add(goodsSalesDTOList.get(i).getName());
            salesList.add(goodsSalesDTOList.get(i).getNumber());
        }

        //将商品名称和销量转换为字符串
        String nameStr = StringUtils.join(nameList, ",");
        String salesStr = StringUtils.join(salesList, ",");
        //封装结果
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(nameStr);
        salesTop10ReportVO.setNumberList(salesStr);

        return salesTop10ReportVO;
    }

    /**
     * 导出报表
     * @param response 响应
     */
    public void export(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        //1.查询数据
        BusinessDataVO businessDataVO = workSpaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //2.通过POI将数据写入Excel
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            if (resourceAsStream != null) {
                XSSFWorkbook excel = new XSSFWorkbook(resourceAsStream);
                XSSFSheet sheet = excel.getSheetAt(0);

                //设置时间
                sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "至" + end);
                //填充概览营业数据
                XSSFRow row = sheet.getRow(3);
                row.getCell(2).setCellValue(businessDataVO.getTurnover()); //营业额
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate()); //订单完成率
                row.getCell(6).setCellValue(businessDataVO.getNewUsers()); //新增用户数
                row = sheet.getRow(4);
                row.getCell(2).setCellValue(businessDataVO.getValidOrderCount()); //有效订单数
                row.getCell(4).setCellValue(businessDataVO.getUnitPrice()); //平均客单价

                //填充明细数据
                for (int i = 0; i < 30; i++) {
                    LocalDate date = begin.plusDays(i);
                    BusinessDataVO detailData = workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                    row = sheet.getRow(7 + i);
                    row.getCell(1).setCellValue(date.toString()); //日期
                    row.getCell(2).setCellValue(detailData.getTurnover()); //营业额
                    row.getCell(3).setCellValue(detailData.getValidOrderCount()); //有效订单数
                    row.getCell(4).setCellValue(detailData.getOrderCompletionRate()); //订单完成率
                    row.getCell(5).setCellValue(detailData.getUnitPrice()); //平均客单价
                    row.getCell(6).setCellValue(detailData.getNewUsers()); //新增用户数
                }

                //3.通过输出流将Excel文件输出到浏览器
                ServletOutputStream outputStream = response.getOutputStream();
                excel.write(outputStream);

                //4.关闭资源
                outputStream.close();
                excel.close();
            }

        } catch (IOException e) {
            throw new BaseException("导出报表失败");
        }


    }
}
