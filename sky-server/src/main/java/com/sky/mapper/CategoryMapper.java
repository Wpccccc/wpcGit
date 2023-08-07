package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wpc
 * @date 2023/8/7 10:50
 */

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
