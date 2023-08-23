package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     * @param employeeLoginDTO 员工登录信息
     * @return 员工登录信息
     */

    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */

    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @ApiOperation(value = "新增员工")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}", employeeDTO);
        return employeeService.save(employeeDTO);
    }

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    @ApiOperation(value = "分页查询员工")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询员工：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation(value = "变更员工账号状态")
    @PostMapping("/status/{status}")
    public Result switchStatus(@PathVariable Integer status, Long id){
        log.info("变更员工账号状态：{}，{}", status, id);
        Result result = employeeService.switchStatus(status, id);
        return result;
    }

    @ApiOperation(value = "根据id查询员工")
    @GetMapping("/{id}")
    public Result getEmployeeById(@PathVariable Long id){
        log.info("根据id查询员工：{}", id);
        Result result = employeeService.getEmployeeById(id);
        return result;
    }

    @ApiOperation(value = "修改员工信息")
    @PutMapping
    public Result updateEmployeeInfo(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息：{}", employeeDTO);
        Result result = employeeService.updateEmployeeInfo(employeeDTO);
        return result;
    }

    @ApiOperation(value = "修改密码")
    @PutMapping("/editPassword")
    public Result<Object> editPassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改密码：{}", passwordEditDTO);
        passwordEditDTO.setEmpId(BaseContext.getCurrentId());
        return employeeService.editPassword(passwordEditDTO);
    }
}
