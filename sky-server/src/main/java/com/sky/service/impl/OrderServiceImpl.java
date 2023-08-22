package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wpc
 * @date 2023/8/18 14:12
 */

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    /*
      用户端接口
     */

    /**
     * 用户提交订单
     * @param ordersSubmitDTO 订单提交信息
     * @return 封装到Result中的提交结果
     */
    @Transactional
    public Result<OrderSubmitVO> submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.处理各种业务异常
        //1.1 用户地址不存在
        AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //1.2 购物车商品不存在
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(new QueryWrapper<ShoppingCart>().eq("user_id", userId));
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //2.提交订单
        //2.1 向订单表插入1条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID); //设置付款状态为未付款
        orders.setStatus(Orders.PENDING_PAYMENT); //设置订单状态为待付款
        orders.setNumber(String.valueOf(System.currentTimeMillis())); //设置订单编号
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
        // 获取当前用户名,由于个人小程序无法获取微信用户的用户名，所以这里直接使用收货人姓名作为用户名
        orders.setUserName(addressBook.getConsignee());

        if (ordersMapper.insert(orders) <= 0){
            throw new OrderBusinessException("订单提交失败");
        }
        //2.2 向订单详情表插入n条数据
        shoppingCarts.forEach(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setId(null);
            if (orderDetailMapper.insert(orderDetail) <= 0){
                throw new OrderBusinessException("订单提交失败");
            }
        });
        //2.3 删除购物车中的商品
        shoppingCarts.forEach(shoppingCart -> {
            shoppingCartMapper.deleteById(shoppingCart.getId());
        });
        //3.封装VO对象返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return Result.success(orderSubmitVO);
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    public Result<OrderVO> userGetOrderDetail(String id) {
        //根据订单id查询订单
        Orders orders = ordersMapper.selectById(id);

        //根据订单id查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", id));

        //封装VO对象返回结果
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return Result.success(orderVO);
    }

    /**
     * 查询历史订单
     * @param page 页码
     * @param pageSize 每页条数
     * @param status 订单状态
     * @return 历史订单
     */
    public Result<PageResult> getHistoryOrders(String page, String pageSize, String status) {
        //根据用户id查询订单
        Long userId = BaseContext.getCurrentId();
        //设置分页参数
        IPage<Orders> iPage = new Page<>(Integer.parseInt(page), Integer.parseInt(pageSize));
        //根据订单状态查询订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq(StringUtils.hasText(status), "status", status);
        queryWrapper.orderByDesc("order_time");
        IPage<Orders> ordersIPage = ordersMapper.selectPage(iPage, queryWrapper);

        //封装VO对象返回结果
        List<Orders> ordersList = ordersIPage.getRecords();
        List<OrderVO> orderVOList = new ArrayList<>();
        ordersList.forEach(orders -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            //根据订单id查询订单详情
            List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", orders.getId()));
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        });

        PageResult pageResult = new PageResult(ordersIPage.getTotal(), orderVOList);
        if (pageResult.getTotal() == 0) {
            throw new OrderBusinessException("暂无订单");
        }
        return Result.success(pageResult);
    }

    /**
     * 取消订单
     * @param id 订单id
     */
    @Transactional
    public void userCancelOrder(String id) {
        //根据订单id查询订单
        Orders orders = ordersMapper.selectById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //判断订单状态是否为待付款或者待接单
        if (!orders.getStatus().equals(Orders.PENDING_PAYMENT) && !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //如果该订单已付款，需要退款
        if (orders.getPayStatus().equals(Orders.PAID)) {
            //调用微信退款接口
            //模拟退款成功
            orders.setPayStatus(Orders.REFUND);
        }
        //根据订单id更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消订单");
        orders.setCancelTime(LocalDateTime.now());
        ordersMapper.updateById(orders);
    }

    /**
     * 再来一单
     * @param id 订单id
     */
    public void repetitionOrder(String id) {
        //根据订单id查询订单
        Orders orderOG = ordersMapper.selectById(id);
        if (orderOG == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //将原订单中的商品添加到购物车
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", orderOG.getId()));
        Long userId = BaseContext.getCurrentId();
        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        });
    }

    /**
     * 催单
     * @param id 订单id
     * @return 封装到Result中的催单结果
     */
    public Result<Object> reminderOrder(String id) {
        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.selectById(id);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //通过websocket通知商家端
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2); //1表示来单提醒，2表示客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + ordersDB.getNumber());

        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
        return Result.success("催单成功");
    }



    /*
     * 管理员端接口
     */

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    public Result<OrderVO> adminGetOrderDetails(String id) {
        //根据订单id查询订单
        Orders orders = ordersMapper.selectById(id);
        //根据订单id查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", id));
        StringBuilder sb = new StringBuilder();
        orderDetailList.forEach(orderDetail -> {
            sb.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).append(";");
        });
        //封装VO对象返回结果
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDishes(sb.toString());
        orderVO.setOrderDetailList(orderDetailList);

        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO 订单接单信息
     * @return 封装到Result中的接单结果
     */
    public Result<Object> confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        //将订单状态改为已接单
        Orders orders = ordersMapper.selectById(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        ordersMapper.updateById(orders);

        return Result.success("接单成功");
    }

    /**
     * 拒单
     * @param ordersRejectionDTO 订单拒单信息
     * @return 封装到Result中的拒单结果
     */
    public Result<Object> rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = ordersMapper.selectById(ordersRejectionDTO.getId());
        //只有在订单状态为2时才能拒单
        if (!orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //如果该订单已付款，需要退款
        if (orders.getPayStatus().equals(Orders.PAID)) {
            //调用微信退款接口
            //模拟退款成功
            orders.setPayStatus(Orders.REFUND);
        }
        //更新订单状态、拒单原因、拒单时间
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelReason("商家拒单");
        orders.setCancelTime(LocalDateTime.now());

        if (ordersMapper.updateById(orders) > 0) {
            return Result.success("拒单成功");
        } else {
            return Result.error("拒单失败");
        }
    }

    /**
     * 取消订单
     * @param ordersCancelDTO 订单取消信息
     * @return 封装到Result中的取消结果
     */
    public Result<Object> adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = ordersMapper.selectById(ordersCancelDTO.getId());
        //如果该订单已付款，需要退款
        if (orders.getPayStatus().equals(Orders.PAID)) {
            //调用微信退款接口
            //模拟退款成功
            orders.setPayStatus(Orders.REFUND);
        }
        //更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        if (ordersMapper.updateById(orders) > 0) {
            return Result.success("取消成功");
        } else {
            return Result.error("取消失败");
        }
    }

    /**
     * 完成订单
     * @param id 订单id
     * @return 封装到Result中的完成结果
     */
    public Result<Object> completeOrder(String id) {
        Orders orders = ordersMapper.selectById(id);
        //只有在订单状态为4时才能完成
        if (!orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //更新订单状态、完成时间
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        if (ordersMapper.updateById(orders) > 0) {
            return Result.success("结单成功");
        } else {
            return Result.error("结单失败");
        }
    }

    /**
     * 各个状态的订单数量统计
     * @return 封装到Result中的订单数量统计结果
     */
    public OrderStatisticsVO statisticsCount() {
        //查询各个状态的订单数量
        Integer toBeConfirmed = ordersMapper.selectCount(new QueryWrapper<Orders>().eq("status", Orders.TO_BE_CONFIRMED));
        Integer confirmed = ordersMapper.selectCount(new QueryWrapper<Orders>().eq("status", Orders.CONFIRMED));
        Integer deliveryInProgress = ordersMapper.selectCount(new QueryWrapper<Orders>().eq("status", Orders.DELIVERY_IN_PROGRESS));

        //封装VO对象返回结果
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    /**
     * 派送订单
     * @param id 订单id
     * @return 封装到Result中的派送结果
     */
    public Result<Object> deliveryOrder(String id) {
        Orders orders = ordersMapper.selectById(id);
        //只有在订单状态为3时才能派送
        if (!orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //更新订单状态
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        if(ordersMapper.updateById(orders) > 0) {
            return Result.success("派送成功");
        } else {
            return Result.error("派送失败");
        }
    }

    /**
     * 订单搜索
     * @param ordersPageQueryDTO 订单搜索条件
     * @return 封装到Result中的订单搜索结果
     */
    public Result<PageResult> searchOrder(OrdersPageQueryDTO ordersPageQueryDTO) {
        //获取当前登录用户id
        Long userId = BaseContext.getCurrentId();
        //设置分页参数
        IPage<Orders> iPage = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //设置查询条件，订单号和手机号支持模糊查询
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.like(StringUtils.hasText(ordersPageQueryDTO.getNumber()), "number", ordersPageQueryDTO.getNumber());
        queryWrapper.like(StringUtils.hasText(ordersPageQueryDTO.getPhone()), "phone", ordersPageQueryDTO.getPhone());
        queryWrapper.eq(ordersPageQueryDTO.getStatus() != null, "status", ordersPageQueryDTO.getStatus());
        queryWrapper.ge(ordersPageQueryDTO.getBeginTime() != null, "order_time", ordersPageQueryDTO.getBeginTime());
        queryWrapper.le(ordersPageQueryDTO.getEndTime() != null, "order_time", ordersPageQueryDTO.getEndTime());
        queryWrapper.orderByDesc("order_time");

        IPage<Orders> ordersIPage = ordersMapper.selectPage(iPage, queryWrapper);

        if (ordersIPage.getTotal() == 0) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //封装VO对象返回结果
        List<Orders> ordersList = ordersIPage.getRecords();
        List<OrderVO> orderVOList = new ArrayList<>();
        ordersList.forEach(orders -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            //根据订单id查询订单详情，并拼接为字符串，格式为：菜品名*数量；菜品名*数量
            List<OrderDetail> orderDetailList = orderDetailMapper.selectList(new QueryWrapper<OrderDetail>().eq("order_id", orders.getId()));
            StringBuilder sb = new StringBuilder();
            orderDetailList.forEach(orderDetail -> {
                sb.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).append(";");
            });
            orderVO.setOrderDishes(sb.toString());
            orderVOList.add(orderVO);
        });

        return Result.success(new PageResult(ordersIPage.getTotal(), orderVOList));
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO 订单支付信息
     * @return 订单支付结果
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.selectById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );

        //模拟支付成功
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo 订单号
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.selectOne(new QueryWrapper<Orders>().eq("number", outTradeNo));

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        ordersDB.setStatus(Orders.TO_BE_CONFIRMED);
        ordersDB.setPayStatus(Orders.PAID);
        ordersDB.setCheckoutTime(LocalDateTime.now());

        ordersMapper.updateById(ordersDB);

        //通过websocket通知商家端
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1); //1表示来单提醒，2表示客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + outTradeNo);

        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }
}
