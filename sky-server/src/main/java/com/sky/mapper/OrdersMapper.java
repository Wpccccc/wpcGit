package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * @author wpc
 * @date 2023/8/18 15:10
 */

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 获取营业额
     * @param begin 开始时间
     * @param end 结束时间
     * @param status 订单状态
     * @return 营业额
     */
    @Select("select sum(amount) from orders where order_time between #{begin} and #{end} and status = #{status}")
    Double getTurnover(LocalDateTime begin, LocalDateTime end, Integer status);
}
