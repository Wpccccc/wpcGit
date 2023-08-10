package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wpc
 * @date 2023/8/10 15:19
 */

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "店铺管理")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result getStatus() {
        log.info("获取营业状态");
        return shopService.getStatus();
    }
}
