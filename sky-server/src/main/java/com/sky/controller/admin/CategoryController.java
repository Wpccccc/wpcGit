package com.sky.controller.admin;

/**
 * @author wpc
 * @date 2023/8/7 10:48
 */


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理
 */
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @ApiOperation(value = "分页查询分类")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类:{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation(value = "新增分类")
    @PostMapping
    public Result saveCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类:{}",categoryDTO);
        return categoryService.saveCategory(categoryDTO);
    }

    @ApiOperation(value = "变更分类状态")
    @PostMapping("/status/{status}")
    public Result switchStatus(@PathVariable Integer status, Long id){
        log.info("变更分类状态：{}，{}", status, id);
        Result result = categoryService.switchStatus(status, id);
        return result;
    }

    @ApiOperation(value = "删除分类")
    @DeleteMapping
    public Result deleteCategory(Long id){
        log.info("删除分类：{}", id);
        Result result = categoryService.deleteCategory(id);
        return result;
    }

    @ApiOperation(value = "更新分类信息")
    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("更新分类：{}", categoryDTO);
        Result result = categoryService.updateCategory(categoryDTO);
        return result;
    }

    @ApiOperation(value = "根据类型查询分类")
    @GetMapping("/list")
    public Result listCategoryByType(Integer type){
        log.info("根据类型查询分类：{}", type);
        Result result = categoryService.listCategoryByType(type);
        return result;
    }
}
