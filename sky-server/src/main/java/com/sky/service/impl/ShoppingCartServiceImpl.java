package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wpc
 * @date 2023/8/17 10:04
 */

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询购物车列表
     * @return
     */
    public List<ShoppingCart> getShoppingCart() {
        //动态获取当前登录用户的id
        Long userId = BaseContext.getCurrentId();
        log.info("当前登录用户的id为：{}", userId);
        //设置查询条件
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(queryWrapper);

        return shoppingCartList;

    }

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    public Result addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //判断是否已经存在该商品，如果存在则更新数量，如果不存在则添加，同一菜品不同口味也算不同商品
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", shoppingCart.getUserId());
        queryWrapper.eq( shoppingCart.getDishId() != null, "dish_id", shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId() != null, "setmeal_id", shoppingCart.getSetmealId());
        queryWrapper.eq(StringUtils.hasText(shoppingCart.getDishFlavor()), "dish_flavor", shoppingCart.getDishFlavor());
        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(queryWrapper);

        //如果不存在则添加
        if (shoppingCart1 == null){
            //判断本次添加的是菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();
            if (dishId != null){
                //本次添加的是菜品
                Dish dish = dishMapper.selectById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else if (setmealId != null){
                //本次添加的是套餐
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            } else {
                return Result.error("添加失败");
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
            return Result.success("添加成功");
        } else {
            //如果存在则更新数量
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartMapper.updateById(shoppingCart1);
            return Result.success("添加成功");
        }
    }



    /**
     * 删除购物车中的一个物品
     * @param shoppingCartDTO
     * @return
     */
    public Result subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //根据菜品id或者套餐id和用户id查询购物车中的商品
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        Long userId = BaseContext.getCurrentId();

        //设置查询条件
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq(dishId != null, "dish_id", dishId);
        queryWrapper.eq(setmealId != null, "setmeal_id", setmealId);
        queryWrapper.eq(StringUtils.hasText(shoppingCartDTO.getDishFlavor()), "dish_flavor", shoppingCartDTO.getDishFlavor());
        ShoppingCart shoppingCart = shoppingCartMapper.selectOne(queryWrapper);

        //如果购物车中的数量大于1，则减少数量
        if (shoppingCart.getNumber() > 1){
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateById(shoppingCart);
            return Result.success("删除成功");
        } else {
            //如果购物车中的数量等于1，则删除该商品
            shoppingCartMapper.deleteById(shoppingCart.getId());
            return Result.success("删除成功");
        }

    }

    /**
     * 清空购物车
     * @return
     */
    public Result cleanShoppingCart() {
        //动态获取当前登录用户的id
        Long userId = BaseContext.getCurrentId();

        //根据用户id删除购物车中的所有商品
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        if (shoppingCartMapper.delete(queryWrapper) > 0){
            return Result.success("清空成功");
        } else {
            return Result.error("清空失败");
        }
    }

}
