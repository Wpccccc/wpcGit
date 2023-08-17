package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wpc
 * @date 2023/8/15 9:26
 */

@RestController
@RequestMapping("user/shoppingCart")
@Slf4j
@Api("购物车管理")
public class ShopCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public Result getShoppingCart(){
        log.info("获取购物车列表");
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCart();
        return Result.success(shoppingCartList);
    }

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shopingCartDTO){
        log.info("添加购物车：{}", shopingCartDTO);
        return shoppingCartService.addShoppingCart(shopingCartDTO);
    }

    @ApiOperation("删除购物车中的一个物品")
    @PostMapping("/sub")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shopingCartDTO){
        log.info("删除购物车中的一个物品：{}", shopingCartDTO);
        return shoppingCartService.subShoppingCart(shopingCartDTO);
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result cleanShoppingCart(){
        log.info("清空购物车");
        return shoppingCartService.cleanShoppingCart();
    }
}
