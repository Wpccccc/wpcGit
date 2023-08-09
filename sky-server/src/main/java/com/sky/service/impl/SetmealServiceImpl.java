package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
}
