package com.sky.service;

import com.sky.result.Result;

/**
 * @author wpc
 * @date 2023/8/10 14:58
 */
public interface ShopService {

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    Result setStatus(String status);

    /**
     * 获取营业状态
     * @return
     */
    Result getStatus();
}
