package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wpc
 * @date 2023/8/11 14:05
 */

@Service
public class UserServiceImpl implements UserService {

    //微信接口地址
    private static final String API_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    public UserLoginVO userLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获取当前用户的openid
        String openid = getApiResult(userLoginDTO.getCode()).getString("openid");

        //判断openid是否为空，为空则登录失败，抛出异常
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //根据openid查询用户信息，如果用户不存在，则注册新用户
        User user = userMapper.selectByOpenid(openid);
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            if (userMapper.insert(user) <= 0){
                //注册失败
                throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
            }
        }

        //生成Jwt令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        //返回用户登录信息
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();


        return userLoginVO;
    }

    /**
     * 调用微信接口服务，获取请求结果
     * @param code
     * @return
     */
    private JSONObject getApiResult(String code) {
        Map<String,String> params = new HashMap<>();
        params.put("appid",weChatProperties.getAppid());
        params.put("secret",weChatProperties.getSecret());
        params.put("js_code",code);
        params.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(API_URL, params);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject;
    }
}
