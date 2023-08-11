package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wpc
 * @date 2023/8/9 15:57
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        IPage page = new Page(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        QueryWrapper queryWrapper = new QueryWrapper();
        //根据名称模糊查询
        queryWrapper.like(StringUtils.hasText(setmealPageQueryDTO.getName()), "name", setmealPageQueryDTO.getName());
        //过滤是否启售status
        queryWrapper.eq(setmealPageQueryDTO.getStatus() != null, "status", setmealPageQueryDTO.getStatus());
        //过滤分类categoryId
        queryWrapper.eq(setmealPageQueryDTO.getCategoryId() != null, "category_id", setmealPageQueryDTO.getCategoryId());
        //根据更新时间倒序
        queryWrapper.orderByDesc("update_time");

        IPage<Setmeal> setmealIPage = setmealMapper.selectPage(page, queryWrapper);

        List<Setmeal> setmealList = setmealIPage.getRecords();
        List<SetmealVO> setmealVOList = new ArrayList<>();
        //将Setmeal转换为SetmealVO,并补全分类名称以及口味
        setmealList.forEach(setmeal -> {
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(setmeal, setmealVO);
            setmealVO.setCategoryName(categoryMapper.selectById(setmeal.getCategoryId()).getName());
            setmealVOList.add(setmealVO);
        });
        return new PageResult(setmealIPage.getTotal(), setmealVOList);

    }

    /**
     * 新增套餐及对应菜品
     * @param setmealDTO
     * @return
     */
    public Result saveWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        if (setmealMapper.insert(setmeal) > 0) {
            Long setmealId = setmeal.getId();
            List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
            if (setmealDishes != null && setmealDishes.size() > 0) {
                for (SetmealDish setmealDish : setmealDishes) {
                    setmealDish.setSetmealId(setmealId);
                    if (setmealDishMapper.insert(setmealDish) <= 0) {
                        return Result.error("新增套餐菜品失败");
                    }
                }
            }
            return Result.success("新增套餐成功");
        } else {
            return Result.error("新增套餐失败");
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public Result getSetmealById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        //补全分类名称
        setmealVO.setCategoryName(categoryMapper.selectById(setmeal.getCategoryId()).getName());
        //补全套餐中的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(new QueryWrapper<SetmealDish>().eq("setmeal_id", id));
        setmealVO.setSetmealDishes(setmealDishes);

        if (setmealDishes != null) {
            return Result.success(setmealVO);
        } else {
            return Result.error("查询套餐失败");
        }
    }

    /**
     * 修改套餐售卖状态
     * @param status
     * @param id
     * @return
     */
    public Result switchStatus(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        if (setmealMapper.updateById(setmeal) > 0) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    public Result updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        if (setmealMapper.updateById(setmeal) > 0) {
            //先删除原有的套餐菜品
            setmealDishMapper.delete(new QueryWrapper<SetmealDish>().eq("setmeal_id", setmeal.getId()));
            //再添加新的套餐菜品
            List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
            if (setmealDishes != null && setmealDishes.size() > 0) {
                for (SetmealDish setmealDish : setmealDishes) {
                    setmealDish.setSetmealId(setmeal.getId());
                    if (setmealDishMapper.insert(setmealDish) <= 0) {
                        return Result.error("修改套餐菜品失败");
                    }
                }
            }
            return Result.success("修改套餐成功");
        } else {
            return Result.error("修改套餐失败");
        }
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    public Result deleteSetmeal(String ids) {
        if (setmealMapper.deleteBatchIds(Arrays.asList(ids.split(","))) <= 0) {
            return Result.error("批量删除套餐失败");
        } else {
            if (setmealDishMapper.delete(new UpdateWrapper<SetmealDish>().in("setmeal_id", Arrays.asList(ids.split(",")))) <= 0) {
                return Result.error("批量删除套餐菜品失败");
            } else {
                return Result.success("批量删除套餐成功");
            }
        }
    }

    /**
     * 根据套餐id查询所含菜品
     * @param id
     * @return
     */
    public Result getDishListBySetmealId(Long id) {
        List<DishItemVO> dishItemVOList = new ArrayList<>();
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(new QueryWrapper<SetmealDish>().eq("setmeal_id", id));
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                DishItemVO dishItemVO = new DishItemVO();
                Dish dish = dishMapper.selectById(setmealDish.getDishId());
                BeanUtils.copyProperties(dish, dishItemVO);
                dishItemVO.setCopies(setmealDish.getCopies());
                dishItemVOList.add(dishItemVO);
            }
            return Result.success(dishItemVOList);
        } else {
            return Result.error("查询套餐所含菜品失败");
        }
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    public Result getSetmealByCategoryId(Long categoryId) {
        List<Setmeal> setmeals = setmealMapper.selectList(new QueryWrapper<Setmeal>()
                .eq("category_id", categoryId)
                .eq("status", StatusConstant.ENABLE));
        if (setmeals != null && setmeals.size() > 0) {
            return Result.success(setmeals);
        } else {
            return Result.error("查询套餐失败");
        }
    }


}
