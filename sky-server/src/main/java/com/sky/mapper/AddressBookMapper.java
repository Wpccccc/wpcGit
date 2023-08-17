package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wpc
 * @date 2023/8/17 16:27
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
