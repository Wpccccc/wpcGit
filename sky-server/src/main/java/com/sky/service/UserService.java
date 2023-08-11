package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.vo.UserLoginVO;

/**
 * @author wpc
 * @date 2023/8/11 14:03
 */
public interface UserService {

    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    UserLoginVO userLogin(UserLoginDTO userLoginDTO);
}
