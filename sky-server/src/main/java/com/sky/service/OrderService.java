package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

/**
 * @author wpc
 * @date 2023/8/18 13:41
 */
public interface OrderService {

    /*
     * 用户端接口
     */

    /**
     * 用户提交订单
     * @param ordersSubmitDTO 订单提交信息
     * @return 封装到Result中的提交结果
     */
    Result<OrderSubmitVO> submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    Result<OrderVO> userGetOrderDetail(String id);

    /**
     * 查询历史订单
     * @param page 页码
     * @param pageSize 每页条数
     * @param status 订单状态
     * @return 历史订单
     */
    Result<PageResult> getHistoryOrders(String page, String pageSize, String status);

    /**
     * 取消订单
     * @param id 订单id
     */
    void userCancelOrder(String id);

    /**
     * 再来一单
     * @param id 订单id
     */
    void repetitionOrder(String id);

    /**
     * 催单
     * @param id 订单id
     * @return 封装到Result中的催单结果
     */
    Result<Object> reminderOrder(String id);

    /*
     * 管理员端接口
     */

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    Result<OrderVO> adminGetOrderDetails(String id);

    /**
     * 接单
     * @param ordersConfirmDTO 订单接单信息
     * @return 封装到Result中的接单结果
     */
    Result<Object> confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO 订单拒单信息
     * @return 封装到Result中的拒单结果
     */
    Result<Object> rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO 订单取消信息
     * @return 封装到Result中的取消结果
     */
    Result<Object> adminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    /**
     * 完成订单
     * @param id 订单id
     * @return 封装到Result中的完成结果
     */
    Result<Object> completeOrder(String id);

    /**
     * 各个状态的订单数量统计
     * @return 订单数量统计
     */
    OrderStatisticsVO statisticsCount();

    /**
     * 派送订单
     * @param id 订单id
     * @return 封装到Result中的派送结果
     */
    Result<Object> deliveryOrder(String id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO 订单搜索条件
     * @return 订单搜索结果
     */
    Result<PageResult> searchOrder(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 支付订单
     * @param ordersPaymentDTO 订单支付信息
     * @return 支付结果
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo 订单号
     */
    void paySuccess(String outTradeNo);
}
