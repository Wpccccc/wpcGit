package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wpc
 * @date 2023/8/11 15:40
 */

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
@Api(tags = "查询分类")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "根据类型查询分类")
    @GetMapping("/list")
    public Result listCategoryByType(Integer type){
        log.info("根据类型查询分类：{}", type);
        Result result = categoryService.listCategoryByType(type);
        return result;
    }
}
