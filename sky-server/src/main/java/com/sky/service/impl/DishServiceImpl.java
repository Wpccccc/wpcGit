package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.utils.CommonUtil;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author wpc
 * @date 2023/8/9 9:21
 */

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 新增菜品及对应口味
     * @param dishDTO
     * @return
     */
    @Transactional
    public Result saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        if ( dishMapper.insert(dish) > 0) {
            Long dishId = dish.getId();
            List<DishFlavor> flavors = dishDTO.getFlavors();
            if (flavors != null && flavors.size() > 0) {
                for (DishFlavor flavor : flavors) {
                    flavor.setDishId(dishId);
                    if ( dishFlavorMapper.insert(flavor) <= 0) {
                        return Result.error("新增口味失败");
                    }
                }
            }
            return Result.success("新增菜品成功");
        } else {
            return Result.error("新增菜品失败");
        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        IPage page = new Page(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        QueryWrapper queryWrapper = new QueryWrapper();
        //过滤是否启售
        queryWrapper.eq(dishPageQueryDTO.getStatus()!=null,"status",dishPageQueryDTO.getStatus());
        //过滤分类
        queryWrapper.eq(dishPageQueryDTO.getCategoryId()!=null,"category_id",dishPageQueryDTO.getCategoryId());
        //根据菜品名称模糊查询
        queryWrapper.like(StringUtils.hasText(dishPageQueryDTO.getName()), "name", dishPageQueryDTO.getName());
        //根据更新时间降序
        queryWrapper.orderByDesc("update_time");

        IPage<Dish> dishIPage = dishMapper.selectPage(page, queryWrapper);
        List<Dish> dishList = dishIPage.getRecords();
        List<DishVO> dishVOList = new ArrayList<>();

        //将Dish转换为DishVO,并补全分类名称以及口味
        dishList.forEach(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setCategoryName(categoryMapper.selectById(dish.getCategoryId()).getName());
            dishVO.setFlavors(dishFlavorMapper.selectList(new QueryWrapper<DishFlavor>().eq("dish_id", dish.getId())));
            dishVOList.add(dishVO);
        });
        return new PageResult(dishIPage.getTotal(), dishVOList);
    }

    /**
     * 修改菜品售卖状态
     * @param status 菜品状态
     * @param id 菜品id
     * @return
     */
    public Result switchStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        if (dishMapper.updateById(dish) > 0) {
            // 清空该菜品所在分类对应的缓存
            Set categoryIdByDishId = commonUtil.getCategoryIdByDishId(id+"");
            redisTemplate.delete(categoryIdByDishId);

            return Result.success();
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 根据分类id查询菜品列表
     * @param categoryId 菜品id
     * @return
     */
    public Result getDishListByCategoryId(Long categoryId) {
        List<Dish> dishList = dishMapper.selectList(new QueryWrapper<Dish>().eq("category_id", categoryId));
        return Result.success(dishList);
    }

    /**
     * 根据id查询菜品
     * @param id 菜品id
     * @return
     */
    public DishVO getDishById(Long id) {
        Dish dish = dishMapper.selectById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setCategoryName(categoryMapper.selectById(dish.getCategoryId()).getName());
        dishVO.setFlavors(dishFlavorMapper.selectList(new QueryWrapper<DishFlavor>().eq("dish_id", dish.getId())));
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    public Result updateDish(DishDTO dishDTO) {
        String originalCategoryId = dishMapper.selectById(dishDTO.getId()).getCategoryId().toString();
        boolean isChangeCategory = !originalCategoryId.equals(dishDTO.getCategoryId().toString());

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        if (dishMapper.updateById(dish) > 0) {
            //删除原有口味
            dishFlavorMapper.delete(new UpdateWrapper<DishFlavor>().eq("dish_id", dish.getId()));
            //新增新的口味
            List<DishFlavor> flavors = dishDTO.getFlavors();
            if (flavors != null && flavors.size() > 0) {
                for (DishFlavor flavor : flavors) {
                    flavor.setDishId(dish.getId());
                    if ( dishFlavorMapper.insert(flavor) <= 0) {
                        return Result.error("新增口味失败");
                    }
                }
            }
            //清空该key对应的缓存
            String newKey = "dish_" + dish.getCategoryId();
            if (isChangeCategory) {
                System.out.println("修改了分类");
                String oldKey = "dish_" + originalCategoryId;
                redisTemplate.delete(newKey);
                redisTemplate.delete(oldKey);
            } else {
                System.out.println("没有修改分类");
                redisTemplate.delete(newKey);
            }

            return Result.success("修改菜品成功");
        } else {
            return Result.error("修改菜品失败");
        }
    }


    /**
     * 批量删除菜品
     * @param ids 菜品id
     * @return
     */
    public Result deleteDish(String ids) {
        Set categoryIdByDishId = commonUtil.getCategoryIdByDishId(ids);
        if (dishMapper.deleteBatchIds(Arrays.asList(ids.split(","))) <= 0) {
            return Result.error("批量删除菜品失败");
        } else {
            if (dishFlavorMapper.delete(new UpdateWrapper<DishFlavor>().in("dish_id", Arrays.asList(ids.split(",")))) <= 0) {
                return Result.error("批量删除菜品口味失败");
            } else {
                //清空对应的缓存
                redisTemplate.delete(categoryIdByDishId);

                return Result.success("批量删除菜品成功");
            }
        }
    }

    /**
     * 用户端
     * 根据分类id查询菜品及对应口味
     * @param categoryId
     * @return
     */
    public Result getDishWithFlavorListByCategoryId(Long categoryId) {

        //构建Redis中缓存数据的key，格式为：dish_1
        String key = "dish_" + categoryId;

        //查询Redis中是否存在该key
        List<DishVO> redisDishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);

        //如果存在，直接返回
        if (redisDishVOList != null && redisDishVOList.size() > 0) {
            return Result.success(redisDishVOList);
        }

        //如果不存在，查询数据库
        List<Dish> dishList = dishMapper.selectList(new QueryWrapper<Dish>()
                .eq("category_id", categoryId)
                .eq("status", StatusConstant.ENABLE));
        List<DishVO> dishVOList = new ArrayList<>();
        dishList.forEach(dish -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(dishFlavorMapper.selectList(new QueryWrapper<DishFlavor>().eq("dish_id", dish.getId())));
            dishVO.setCategoryName(categoryMapper.selectById(dish.getCategoryId()).getName());
            dishVOList.add(dishVO);
        });
        if (dishVOList.size() > 0) {
            //将查询到的数据存入Redis中
            redisTemplate.opsForValue().set(key, dishVOList);
            return Result.success(dishVOList);
        } else {
            return Result.error("该分类下没有菜品");
        }
    }
}
