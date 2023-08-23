package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    Result save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工账号状态
     * @param status 账号状态
     * @param id 员工id
     * @return
     */
    Result switchStatus(Integer status, Long id);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Result getEmployeeById(Long id);

    /**
     * 修改员工信息
     * @param employeeDTO
     * @return
     */
    Result updateEmployeeInfo(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param passwordEditDTO 密码修改dto
     * @return 修改结果
     */
    Result<Object> editPassword(PasswordEditDTO passwordEditDTO);
}
