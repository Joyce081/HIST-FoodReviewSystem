package com.ren.ai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.manager.redis.RedisOperateManager;
import com.ren.common.utils.StringUtils;
import com.ren.framework.security.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 流式接口认证服务
 * 专门处理流式接口的认证逻辑，避免SpringSecurity冲突
 * 
 * @author ren
 */
@Service
@Slf4j
public class StreamAuthService {

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private RedisOperateManager redisOperateManager;

    /**
     * 流式接口认证结果
     */
    public static class AuthResult {
        private boolean success;
        private String errorMessage;
        private LoginUser loginUser;
        private boolean needRefresh;

        public AuthResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public AuthResult(boolean success, LoginUser loginUser, boolean needRefresh) {
            this.success = success;
            this.loginUser = loginUser;
            this.needRefresh = needRefresh;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public LoginUser getLoginUser() { return loginUser; }
        public boolean isNeedRefresh() { return needRefresh; }
    }

    /**
     * 执行流式接口认证（宽松模式）
     * 只验证用户身份有效性，不强制Token续期
     * 
     * @param request HTTP请求
     * @return 认证结果
     */
    public AuthResult authenticate(HttpServletRequest request) {
        try {
            // 1. 从请求头获取Token
            String accessToken = jwtUtils.getAccessToken(request);
            if (StringUtils.isBlank(accessToken)) {
                return new AuthResult(false, "未提供访问令牌");
            }
            
            // 2. 获取用户信息（不验证过期时间，允许稍微过期的Token）
            LoginUser loginUser;
            try {
                loginUser = jwtUtils.getLoginUserByToken(1, accessToken);
            } catch (Exception e) {
                // 如果是过期异常，尝试宽松解析
                log.debug("Token可能过期，尝试宽松解析: {}", e.getMessage());
                return new AuthResult(false, "访问令牌无效或已过期");
            }
            
            if (loginUser == null) {
                return new AuthResult(false, "用户信息获取失败");
            }
            
            // 3. 验证RefreshToken（防止强退用户访问）
            String refreshTokenKey = redisOperateManager.getRefreshTokenKey(loginUser.getUserId(), request);
            if (!redisOperateManager.hasKey(refreshTokenKey)) {
                return new AuthResult(false, "用户会话已失效，请重新登录");
            }
            
            // 4. 检查Token状态但不强制续期
            boolean needRefresh = false;
            try {
                int validationResult = jwtUtils.validateAccessToken(accessToken);
                needRefresh = (validationResult != 200);
            } catch (Exception e) {
                // Token过期但用户会话有效，允许继续使用
                log.info("用户 {} 的Token已过期但会话有效，允许流式访问", loginUser.getUsername());
                needRefresh = true;
            }
            
            log.debug("用户 {} 流式接口认证成功，Token状态: {}", 
                     loginUser.getUsername(), needRefresh ? "需要续期(用户访问其他页面时会自动续期)" : "正常");
            return new AuthResult(true, loginUser, needRefresh);
            
        } catch (Exception e) {
            log.error("流式接口认证失败", e);
            return new AuthResult(false, "认证失败: " + e.getMessage());
        }
    }

}