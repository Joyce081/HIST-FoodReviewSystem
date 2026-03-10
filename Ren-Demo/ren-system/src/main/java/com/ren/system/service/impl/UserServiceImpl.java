package com.ren.system.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.common.core.constant.AppConstants;
import com.ren.common.core.constant.Constants;
import com.ren.common.core.domain.entity.User;
import com.ren.common.manager.SecurityManager;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.PageUtils;
import com.ren.common.utils.StringUtils;
import com.ren.system.core.domain.entity.DictData;
import com.ren.system.core.domain.entity.UserPost;
import com.ren.system.core.domain.entity.UserRole;
import com.ren.system.mapper.UserMapper;
import com.ren.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//ServiceImpl是Mybatis-Plus提供的一个针对IService的具体实现类
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private DictDataService dictDataService;
    @Autowired
    private UserPostService userPostService;


    /**
     * 添加用户详情
     * @param user
     * @author ren
     * @date 2025/04/16 16:24
     */
    @Override
    @Transactional
    public long addUser(User user,String createBy) {
        user.setIsDel(AppConstants.COMMON_INT_NO);
        //从字典表中查询出默认用户，进行设置
        DictData dictData = dictDataService.getDictDataByParam("sys-user-usertype",AppConstants.COMMON_INT_YES);
        user.setUserType(ObjUtil.isNotEmpty(dictData) ? (StringUtils.isNotBlank(dictData.getDictValue()) ? dictData.getDictValue() : "00") : "00");
        user.setCreateBy(createBy);
        user.setCreateTime(DateUtils.currentSeconds());
        // 使用设定的密码管理器对密码进行编码(设置默认密码)
        String password = configService.getConfigByConfigKey("sys.user.initPassword") != null ? (StringUtils.isNotBlank(configService.getConfigByConfigKey("sys.user.initPassword").getConfigValue()) ? configService.getConfigByConfigKey("sys.user.initPassword").getConfigValue() : "123456") : "123456";
        String encodedPassword = SecurityManager.encryptPassword(password);
        user.setPassword(encodedPassword);
        userMapper.insertUser(user);
        //添加角色
        if(user.getRoleIdArr() != null && user.getRoleIdArr().length > 0){
            List<UserRole> userRoleList = Arrays.stream(user.getRoleIdArr()).map(roleId -> new UserRole(user.getUserId(),roleId)).toList();
            userRoleService.addUserRoleBatch(userRoleList);
        }
        //添加岗位
        if(user.getPostIdArr() != null && user.getPostIdArr().length > 0){
            List<UserPost> userPostList = Arrays.stream(user.getPostIdArr()).map(postId -> new UserPost(user.getUserId(),postId)).toList();
            userPostService.addUserPostBatch(userPostList);
        }
        return user.getUserId();
    }

    /**
     * 编辑用户是否删除
     * @param userId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/05/04 13:59
     */
    @Override
    public void modifyUserIsDelById(long userId, int isDel, String updateBy) {
        userMapper.updateUserIsDelById(userId,isDel,updateBy,DateUtils.currentSeconds());
    }

    /**
     * 重置密码（后台使用）
     * @param userId
     * @param newPassword
     * @param updateBy
     * @author ren
     * @date 2025/05/04 13:38
     */
    @Override
    public int resetPassword(long userId, String newPassword, String updateBy) {
        if(StringUtils.isNotBlank(newPassword)){
            newPassword = SecurityManager.encryptPassword(newPassword);
        }
        return userMapper.resetPassword(userId,newPassword,updateBy,DateUtils.currentSeconds());
    }

    /**
     * 编辑用户
     * @param user
     * @param updateBy
     * @author ren
     * @date 2025/05/04 13:52
     */
    @Override
    @Transactional
    public void modifyUser(User user, String updateBy) {
        user.setUpdateBy(updateBy);
        user.setUpdateTime(DateUtils.currentSeconds());
        userMapper.updateUser(user);
        //先删除角色
        userRoleService.removeUserRoleByUserId(user.getUserId());
        //重新添加角色
        if(user.getRoleIdArr() != null && user.getRoleIdArr().length > 0){
            List<UserRole> userRoleList = Arrays.stream(user.getRoleIdArr()).map(roleId -> new UserRole(user.getUserId(),roleId)).toList();
            userRoleService.addUserRoleBatch(userRoleList);
        }
        //先删除岗位
        userPostService.removeUserPostByUserId(user.getUserId());
        //重新添加岗位
        if(user.getPostIdArr() != null && user.getPostIdArr().length > 0){
            List<UserPost> userPostList = Arrays.stream(user.getPostIdArr()).map(postId -> new UserPost(user.getUserId(),postId)).toList();
            userPostService.addUserPostBatch(userPostList);
        }
    }

    /**
     * 编辑用户（登录用）
     * @param userId
     * @param loginIp
     * @param loginDate
     * @param updateBy
     * @author ren
     * @date 2025/05/16 16:08
     */
    @Override
    public void modifyUserByLogin(long userId, String loginIp, long loginDate, String updateBy) {
        userMapper.updateUserByLogin(userId,loginIp,loginDate,updateBy,DateUtils.currentSeconds());
    }

    /**
     * 根据登陆账号获取username
     * @param username
     * @return com.ren.admin.entity.User
     * @author ren
     * @date 2025/05/04 17:27
     */
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectUserByParam(Map.of("username",username));
    }

    /**
     * 根据ID查询User
     * @param id
     * @return com.ren.entity.User
     * @author ren
     * @date 2025/04/17 15:44
     */
    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        return user;
    }

    /**
     * 获取用户列表
     * @return java.util.List<com.ren.admin.entity.User>
     * @author ren
     * @date 2025/04/26 15:52
     */
    @Override
    public IPage<User> listUserByPage(Long deptId, String searchLike, String userType, Integer sex) {
        if(StringUtils.isNotBlank(searchLike)){
            searchLike = StringUtils.format("%%{}%%",searchLike);
        }
        return userMapper.listUserByPage(PageUtils.createPage(User.class),deptId,searchLike,userType,sex);
    }

    /**
     * 获取用户列表
     * @return java.util.List<com.ren.common.domain.entity.User>
     * @author ren
     * @date 2025/05/22 17:07
     */
    @Override
    public List<User> listUserByParam(Long deptId, String searchLike, String userType, Integer sex) {
        if(StringUtils.isNotBlank(searchLike)){
            searchLike = StringUtils.format("%%{}%%",searchLike);
        }
        return userMapper.listUserByParam(deptId,searchLike,userType,sex);
    }

    /**
     * 获取用户列表
     * @param roleId
     * @return java.util.List<com.ren.common.core.entity.User>
     * @author ren
     * @date 2025/05/13 10:10
     */
    @Override
    public List<User> listUserByRoleId(long roleId) {
        List<User> userList = userMapper.listUserByRoleId(roleId);
        return userList;
    }

    /**
     * 获取用户列表
     * @param deptId
     * @return java.util.List<com.ren.common.core.entity.User>
     * @author ren
     * @date 2025/05/13 10:10
     */
    @Override
    public List<User> listUserByDeptId(long deptId) {
        List<User> userList = userMapper.listUserByDeptId(deptId);
        return userList;
    }

    /**
     * 校验手机号码是否唯一
     * @param user
     * @return boolean
     * @author ren
     * @date 2025/09/20 16:29
     */
    @Override
    public boolean checkPhoneUnique(User user)
    {
        Long userId = ObjUtil.isEmpty(user.getUserId()) ? -1L : user.getUserId();
        User info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (ObjUtil.isNotEmpty(info) && info.getUserId().longValue() != userId.longValue())
        {
            return AppConstants.COMMON_BOOLEAN_NO;
        }
        return AppConstants.COMMON_BOOLEAN_YES;
    }

    /**
     * 校验email是否唯一
     * @param user
     * @return boolean
     * @author ren
     * @date 2025/09/20 16:35
     */
    @Override
    public boolean checkEmailUnique(User user)
    {
        Long userId = ObjUtil.isEmpty(user.getUserId()) ? -1L : user.getUserId();
        User info = userMapper.checkEmailUnique(user.getEmail());
        if (ObjUtil.isNotEmpty(info) && info.getUserId().longValue() != userId.longValue())
        {
            return AppConstants.COMMON_BOOLEAN_NO;
        }
        return AppConstants.COMMON_BOOLEAN_YES;
    }

    /**
     * 修改用户个人详细信息
     * @param user
     * @return int
     * @author ren
     * @date 2025/09/20 16:35
     */
    @Override
    public int modifyUserProfile(User user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     * @param userId
     * @param avatar
     * @return int
     * @author ren
     * @date 2025/09/20 16:35
     */
    @Override
    public int modifyUserAvatar(long userId, String avatar) {
        return userMapper.updateUserAvatar(userId,avatar);
    }

}