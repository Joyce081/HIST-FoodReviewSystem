package com.ren.admin.controller.monitor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import com.ren.common.controller.BaseController;
import com.ren.common.core.constant.RedisCacheConstants;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.manager.SecurityManager;
import com.ren.common.manager.redis.RedisOperateManager;
import com.ren.common.utils.StringUtils;
import com.ren.framework.security.utils.JwtUtils;
import com.ren.monitor.core.domain.vo.SysUserOnlineVO;
import com.ren.monitor.service.SysUserOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitor/user/online")
@Slf4j
@Tag(name = "监控-用户", description = "用户相关监控")
public class UserOnlineController extends BaseController {

    @Autowired
    private RedisOperateManager redisOperateManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SysUserOnlineService sysUserOnlineService;

    /**
     * 查询在线用户
     * @param ipaddr
     * @param userName
     * @return java.util.List<com.ren.monitor.domain.vo.SysUserOnlineVO>
     * @author ren
     * @date 2025/06/05 11:42
     */
    @GetMapping("/list")
    @Operation(summary = "查询在线用户", description = "查询在线用户")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getOnlineUsers(
            @RequestParam(required = false) @Schema(description = "IP地址") String ipaddr,
            @RequestParam(required = false) @Schema(description = "用户名") String userName) {
        List<SysUserOnlineVO> userOnlineList = new ArrayList<>();
        String pattern = RedisCacheConstants.REFRESH_TOKEN_KEY + ":" + "*"; // 匹配所有refresh_token:开头的键
        // 使用SCAN命令安全遍历（避免KEYS阻塞）
        try(Cursor<String> cursor = redisOperateManager.scan(pattern)){

            while (cursor.hasNext()) {
                String key = cursor.next();
                // 获取键对应的值（JWT Token）
                String refreshToken = redisOperateManager.getCacheObject(key);
                LoginUser user = jwtUtils.getLoginUserByToken(2,refreshToken);

                if(ObjUtil.isNotNull(user) && ObjUtil.isNotNull(user.getUser())){
                    // 简化的条件处理
                    SysUserOnlineVO onlineUser = determineOnlineUser(ipaddr, userName, user, key);
                    if (onlineUser != null) {
                        userOnlineList.add(onlineUser);
                    }
                }
            }
        }
        return success(userOnlineList);
    }

    /**
     * 条件判断查询登录用户
     * @param ipaddr
     * @param userName
     * @param user
     * @param key
     * @return com.ren.monitor.domain.vo.SysUserOnlineVO
     * @author ren
     * @date 2025/06/05 16:10
     */
    @Operation(summary = "条件判断查询登录用户", description = "条件判断查询登录用户")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    private SysUserOnlineVO determineOnlineUser(
            @RequestParam(required = false) @Schema(description = "IP地址") String ipaddr,
            @RequestParam(required = false) @Schema(description = "用户名") String userName,
            @ModelAttribute LoginUser user,
            @RequestParam(required = false) @Schema(description = "刷新token") String key){
        if (StringUtils.isAllNotBlank(ipaddr, userName)) {
            return sysUserOnlineService.selectOnlineByInfo(ipaddr, userName, user, key);
        } else if (StringUtils.isNotBlank(ipaddr)) {
            return sysUserOnlineService.selectOnlineByIpaddr(ipaddr, user, key);
        } else if (StringUtils.isNotBlank(userName)) {
            return sysUserOnlineService.selectOnlineByUserName(userName, user, key);
        } else {
            return sysUserOnlineService.selectOnline(user, key);
        }
    }

    /**
     * 强退用户
     * @param tokenId
     * @return com.ren.common.domain.model.dto.AjaxResult
     * @author ren
     * @date 2025/06/05 16:40
     */
    @GetMapping("/compulsoryWithdrawal")
    @Operation(summary = "强退用户", description = "强退用户")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult compulsoryWithdrawal(@RequestParam @Schema(description = "token主键") String tokenId, HttpServletRequest request)
    {
        String refreshToken = redisOperateManager.getCacheObject(tokenId);
        if(StringUtils.isNotBlank(refreshToken)){
            String refreshTokenKey = redisOperateManager.getRefreshTokenKey(SecurityManager.getUserId(), request);
            if(refreshTokenKey.equals(tokenId)){
                return error("不可强退当前帐号，请正常退出");
            }
        }else{
            return error("用户不存在");
        }
        redisOperateManager.deleteObject(tokenId);
        return success();
    }

}