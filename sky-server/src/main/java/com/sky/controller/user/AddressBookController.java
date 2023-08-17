package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wpc
 * @date 2023/8/17 16:26
 */
@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "用户地址管理")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @ApiOperation("查询当前登录用户的所有地址信息")
    @GetMapping("/list")
    public Result getAddressBookList() {
        log.info("查询当前登录用户的所有地址信息");
        return addressBookService.getAddressBookList();
    }

    @ApiOperation("查询默认地址")
    @GetMapping("/default")
    public Result getDefaultAddress() {
        log.info("查询默认地址");
        return addressBookService.getDefaultAddress();
    }

    @ApiOperation("新增地址")
    @PostMapping
    public Result addAddress(@RequestBody AddressBook addressBook) {
        log.info("新增地址:{}", addressBook);
        return addressBookService.addAddress(addressBook);
    }

    @ApiOperation("根据id查询地址")
    @GetMapping("/{id}")
    public Result getAddressById(@PathVariable Long id) {
        log.info("根据id查询地址:{}", id);
        return addressBookService.getAddressById(id);
    }

    @ApiOperation("根据id修改地址")
    @PutMapping
    public Result updateAddressById(@RequestBody AddressBook addressBook) {
        log.info("根据id修改地址:{}", addressBook);
        return addressBookService.updateAddressById(addressBook);
    }

    @ApiOperation("根据id删除地址")
    @DeleteMapping
    public Result deleteAddressById(Long id){
        log.info("根据id删除地址:{}", id);
        return addressBookService.deleteAddressById(id);
    }

    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("设置默认地址:{}", addressBook.getId());
        return addressBookService.setDefaultAddress(addressBook);
    }
}
