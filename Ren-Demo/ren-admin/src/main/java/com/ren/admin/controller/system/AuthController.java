package com.ren.admin.controller.system;

import java.util.concurrent.TimeUnit;

import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ren.common.controller.BaseController;
import com.ren.common.core.constant.AppConstants;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.dto.LoginDTO;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.manager.redis.RedisOperateManager;
import com.ren.common.properties.TokenProperties;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.ServletUtils;
import com.ren.common.utils.StringUtils;
import com.ren.common.utils.ip.AddressUtils;
import com.ren.common.utils.ip.IpUtils;
import com.ren.framework.factory.AsyncFactory;
import com.ren.framework.manager.AsyncManager;
import com.ren.framework.security.config.AuthenticationContextHolder;
import com.ren.framework.security.utils.JwtUtils;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "认证相关", description = "认证相关")
public class AuthController extends BaseController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private TokenProperties tokenProperties;
    @Autowired
    private RedisOperateManager redisOperateManager;

    @Value("${url.front}")
    private String frontUrl;

    /**
     * 自定义登录接口
     * @param loginDTO
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/04/24 10:28
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginDTO loginDTO, HttpServletRequest httpRequest) {
        try {
            //验证用户名密码
            //参数：SpringSecurity的认证管理器
            //如果认证成功,发挥返回Authentication，供用户使用
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            // 将用户名密码封装成一个springSecurity能够认识的对象，用于后面的认证流程
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            //获取用户身份信息
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            Long accessTokenExpireTime = DateUtils.current() + tokenProperties.getAccessTokenExpireTime() * 1000L;
            Long refreshTokenExpireTime = DateUtils.current() + tokenProperties.getRefreshTokenExpireTime() * 1000L;

            // 从请求头中获取User-Agent（浏览器与操作系统相关信息）
            String userAgentStr = ServletUtils.getRequest().getHeader("User-Agent");
            // 解析User-Agent
            UserAgent userAgent = UserAgentUtil.parse(userAgentStr);

            // 获取浏览器类型
            String browser = userAgent.getBrowser().getName();

            // 获取操作系统
            String os = userAgent.getOs().getName();

            loginUser.setLoginTime(DateUtils.currentSeconds());
            loginUser.setExpireTime(DateUtils.millisecondsToSeconds(accessTokenExpireTime));
            loginUser.setRefreshTokenExpireTime(DateUtils.millisecondsToSeconds(refreshTokenExpireTime));
            loginUser.setIpaddr(IpUtils.getIpAddr());
            loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(IpUtils.getIpAddr()));
            loginUser.setBrowser(browser);
            loginUser.setOs(os);

            // 生成双Token
            String accessToken = jwtUtils.createAccessToken(loginUser,accessTokenExpireTime);
            String refreshToken = jwtUtils.createRefreshToken(loginUser,refreshTokenExpireTime,httpRequest);

            //重新创建一个新的Authentication 对象（保留原始凭证和权限），存入SpringSecurity
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    loginUser,               // 新的 Principal
                    null, // 原始凭证（如密码，但是此框架没有使用SpringSecurity的自动验证，所以用不到凭证，存null即可）
                    authentication.getAuthorities() // 原始权限
            );

            // 将新创建的Authentication 对象存储到 SecurityContext,方便后面流程从SpringSecurity中直接获取用户信息
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // 异步登录操作日志并更新用户最后登录时间
            AsyncManager.me().execute(AsyncFactory.addLogininfor(loginUser.getUsername(), AppConstants.COMMON_INT_YES, "登录成功"));

            return success("登陆成功").put("accessToken",accessToken).put("refreshToken",refreshToken);
        } catch (BadCredentialsException e) {
            log.debug("登陆失败", e);
            // 密码错误
            AsyncManager.me().execute(AsyncFactory.addLogininfor(loginDTO.getUsername(), AppConstants.COMMON_INT_NO, "登录失败"));
            return error(401, e.getMessage());
        } catch (AuthenticationException e) {
            log.debug("登陆失败", e);
            // 其他认证异常（如用户被锁定）
            AsyncManager.me().execute(AsyncFactory.addLogininfor(loginDTO.getUsername(), AppConstants.COMMON_INT_NO, "登录失败"));
            return error(401, e.getMessage());
        }finally{
            AuthenticationContextHolder.clearContext();
        }
    }

    /**
     * 自动登录功能（没有什么其他操作，前台调用该接口实现自动登录，修改是否登录字段使用）
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/16 17:21
     */
    @PostMapping("/auto/login")
    public AjaxResult autoLogin() {
        return success();
    }

    /**
     * 自定义登出接口
     * @param loginUser
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/04/24 10:28
     */
    @PostMapping("/logout")
    public AjaxResult logout(@AuthenticationPrincipal LoginUser loginUser, HttpServletRequest request) {
        // 手动清理Security上下文
        SecurityContextHolder.clearContext();

        // 删除Redis中的RefreshToken
        jwtUtils.deleteRefreshToken(loginUser.getUserId(),request);

        // 将现在的这个AccessToken加入黑名单，防止退出后还能登录
        String accessToken = jwtUtils.getAccessToken(request);
        if(StringUtils.isNotBlank(accessToken)){
            redisOperateManager.setCacheObject(
                    redisOperateManager.getBlackTokenKey(accessToken),
                    "logged_out",
                    tokenProperties.getBlackListTime(),
                    TimeUnit.SECONDS);
        }

        return success("退出成功");
    }

    /**
     * 自定义刷新token接口
     * @param request
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/04/24 10:28
     */
    @PostMapping("/refreshToken")
    public AjaxResult refreshToken(HttpServletRequest request) {
        //从请求头中获取RefreshToken，只有RefreshToken有效才允许刷新Token，并验证refreshToken是否有效
        String refreshToken = jwtUtils.getRefreshToken(request);
        if (!jwtUtils.validateRefreshToken(refreshToken,request)) {
            log.debug("refreshToken失效，请重新登录");
            return error(401, "refreshToken失效，请重新登录");
        }

        try {
            //获取新的AccessToken
            String newAccessToken = jwtUtils.saveNewAuthenticationAndReturnAccessToken(2,refreshToken);

            // 返回双Token（其中accessToken为新生成的，refreshToken还是用原来的，保证每隔一段时间重新登陆一次，防止token永不失效）
            return success().put("accessToken",newAccessToken).put("refreshToken",refreshToken);
        }catch (Exception e){
            log.debug("refreshToken失效，请重新登录",e);
            return error(401, "refreshToken失效，请重新登录");
        }
    }

    /**
     * 获取AccessToken（该方法主要给外部资源通过VUE前端获取后端Token，之所以通过VUE中转，是为了保证VUE端Token的准确性）
     * 
     * @param request
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/11 16:23
     */
    @PostMapping("/getAccessToken")
    public AjaxResult getAccessToken(HttpServletRequest request) {
        // 从请求头中获取AccessToken
        String accessToken = jwtUtils.getAccessToken(request);
        if (StringUtils.isBlank(accessToken)) {
            return error("未提供 Token");
        }
        // 验证 accessToken 是否正常
        int validationResult = jwtUtils.validateAccessToken(accessToken);
        if (validationResult != 200) {
            return error("Token 无效");
        }

        if (jwtUtils.shouldRefreshToken(accessToken)) { // accessToken需要续期,重新生成token，并返回
            // 获取新的AccessToken
            String newAccessToken = jwtUtils.saveNewAuthenticationAndReturnAccessToken(1, accessToken);
            return success().put("accessToken", newAccessToken);
        } else { // accessToken无需续期,返回原token
            return success().put("accessToken", accessToken);
        }
    }

    /**
     * 获取Swagger使用的测试Token
     * 
     * @param loginDTO
     * @param httpRequest
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/18 17:27
     */
    @PostMapping("/getTestAccessToken")
    @Operation(summary = "获取测试Token", description = "获取测试Token")
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getTestAccessToken(
            @RequestBody
            LoginDTO loginDTO,
            HttpServletRequest httpRequest) {
        try {
            // 验证用户名密码
            // 参数：SpringSecurity的认证管理器
            // 如果认证成功,发挥返回Authentication，供用户使用
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(), loginDTO.getPassword());
            // 将用户名密码封装成一个springSecurity能够认识的对象，用于后面的认证流程
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 获取用户身份信息
            LoginUser loginUser = (LoginUser)authentication.getPrincipal();

            // 测试token失效时间
            Long testAccessTokenExpireTime =
                DateUtils.current() + tokenProperties.getTestAccessTokenExpireTime() * 1000L;

            // 生成测试Token
            String testAccessToken = jwtUtils.createAccessToken(loginUser, testAccessTokenExpireTime);

            return success("获取成功",testAccessToken);
        } catch (AuthenticationException e) {
            log.debug("获取失败", e);
            return error(401, e.getMessage());
        } finally {
            AuthenticationContextHolder.clearContext();
        }
    }

}