package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wpc
 * @date 2023/8/17 16:28
 */

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    public Result getAddressBookList() {
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<AddressBook> addressBookList = addressBookMapper.selectList(queryWrapper);
        if (addressBookList == null || addressBookList.size() == 0) {
            return Result.error("当前用户没有地址信息");
        } else {
            return Result.success(addressBookList);
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    public Result getDefaultAddress() {
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_default", 1);
        AddressBook addressBook = addressBookMapper.selectOne(queryWrapper);
        if (addressBook == null) {
            return Result.error("当前用户没有默认地址");
        } else {
            return Result.success(addressBook);
        }
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    public Result addAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        //查询当前用户是否已有地址，如果没有则设置为默认地址
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", addressBook.getUserId());
        List<AddressBook> addressBookList = addressBookMapper.selectList(queryWrapper);
        if (addressBookList == null || addressBookList.size() == 0) {
            addressBook.setIsDefault(1);
        } else {
            addressBook.setIsDefault(0);
        }
        if (addressBookMapper.insert(addressBook) > 0) {
            return Result.success("新增地址成功");
        } else {
            return Result.error("新增地址失败");
        }
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    public Result getAddressById(Long id) {
        AddressBook addressBook = addressBookMapper.selectById(id);
        if (addressBook == null) {
            return Result.error("当前地址不存在");
        } else {
            return Result.success(addressBook);
        }
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    public Result updateAddressById(AddressBook addressBook) {
        if (addressBookMapper.updateById(addressBook) > 0) {
            return Result.success("修改地址成功");
        } else {
            return Result.error("修改地址失败");
        }
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    public Result deleteAddressById(Long id) {
        if (addressBookMapper.deleteById(id) > 0) {
            return Result.success("删除地址成功");
        } else {
            return Result.error("删除地址失败");
        }
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    public Result setDefaultAddress(AddressBook addressBook) {
        Long addressId = addressBook.getId();
        Long userId = BaseContext.getCurrentId();

        //将当前用户的所有地址设置为非默认地址
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<AddressBook> addressBookList = addressBookMapper.selectList(queryWrapper);
        for (AddressBook book : addressBookList) {
            book.setIsDefault(0);
            addressBookMapper.updateById(book);
        }

        //将当前地址设置为默认地址
        AddressBook addressBook1 = addressBookMapper.selectById(addressId);
        addressBook1.setIsDefault(1);
        if (addressBookMapper.updateById(addressBook1) > 0) {
            return Result.success("设置默认地址成功");
        } else {
            return Result.error("设置默认地址失败");
        }
    }
}
