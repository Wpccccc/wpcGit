package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wpc
 * @date 2023/8/10 14:56
 */

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺管理")
public class ShopController {
    @Autowired
    private ShopService shopService;


    @ApiOperation("设置营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable String status) {
        log.info("设置营业状态");
        return shopService.setStatus(status);
    }

    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result getStatus() {
        log.info("获取营业状态");
        return shopService.getStatus();
    }
}
