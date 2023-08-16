package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wpc
 * @date 2023/8/15 9:26
 */

@RestController
@RequestMapping("user/shopppingCart")
@Slf4j
@Api("购物车管理")
public class ShopCartController {

    public Result getShoppingCart(){
        return null;
    }
}
