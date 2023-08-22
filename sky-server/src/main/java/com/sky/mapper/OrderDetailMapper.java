package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wpc
 * @date 2023/8/18 15:11
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

    /**
     * 获取销售量
     * 根据name分组，统计销售量，倒序
     */
    @Select("select name,sum(number) as number from order_detail where order_id in " +
            "(select id from orders where order_time between #{begin} and #{end} and status = 5) " +
            "group by name order by number desc")
    List<GoodsSalesDTO> getSalesStatistics(LocalDateTime begin, LocalDateTime end);
}
