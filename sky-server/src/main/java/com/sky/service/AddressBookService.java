package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.result.Result;

import java.util.List;

/**
 * @author wpc
 * @date 2023/8/17 16:28
 */
public interface AddressBookService {

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    Result getAddressBookList();

    /**
     * 查询默认地址
     * @return
     */
    Result getDefaultAddress();

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    Result addAddress(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    Result getAddressById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    Result updateAddressById(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    Result deleteAddressById(Long id);

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    Result setDefaultAddress(AddressBook addressBook);
}
