package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wpc
 * @date 2023/8/18 15:10
 */

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
