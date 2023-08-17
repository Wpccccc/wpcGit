package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;

import java.util.List;

/**
 * @author wpc
 * @date 2023/8/17 10:03
 */
public interface ShoppingCartService {

    /**
     * 查询购物车列表
     * @return
     */
    List<ShoppingCart> getShoppingCart();

    /**
     * 添加购物车
     * @return
     */
    Result addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
