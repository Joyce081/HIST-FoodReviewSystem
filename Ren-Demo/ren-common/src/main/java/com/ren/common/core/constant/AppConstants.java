package com.ren.common.core.constant;

/**
 * 通用常量（该类用于存储一些，基本不会变动的常量）
 *
 * @author ren
 * @time: 2025/05/23
 */
public class AppConstants {

    /*===================================================通用开始=======================================================*/
    /**通用-是*/
    public static final int COMMON_INT_YES = 1;
    /**通用-否*/
    public static final int COMMON_INT_NO = 0;

    /**通用-是*/
    public static final boolean COMMON_BOOLEAN_YES = true;
    /**通用-否*/
    public static final boolean COMMON_BOOLEAN_NO = false;

    /*===================================================通用结束=======================================================*/


    /*===================================================用户开始=======================================================*/
    /**用户-性别-男*/
    public static final int USER_SEX_MAN = 0;
    /**用户-性别-女*/
    public static final int USER_SEX_WOMAN = 1;
    /**用户-性别-未知*/
    public static final int USER_SEX_UNKNOWN = 2;
    /*===================================================用户结束=======================================================*/


    /*===================================================菜单开始=======================================================*/
    /**菜单-菜单类型-目录*/
    public static final String MENU_TYPE_DIR = "M";
    /**菜单-菜单类型-菜单*/
    public static final String MENU_TYPE_MENU = "C";
    /**菜单-菜单类型-按钮*/
    public static final String MENU_TYPE_BUTTON = "F";
    /*===================================================菜单结束=======================================================*/


    /*===================================================角色开始=======================================================*/
    /**角色-超级管理员角色名称*/
    public static final String ROLE_SUPER_KEY = "admin";

    /**角色-可查看数据范围-全部数据权限*/
    public static final int DATA_SCOPE_ALL = 1;
    /**角色-可查看数据范围-自定数据权限*/
    public static final int DATA_SCOPE_CUSTOMIZE = 2;
    /**角色-可查看数据范围-本部门数据权限*/
    public static final int DATA_SCOPE_THIS_DEPARTMENT = 3;
    /**角色-可查看数据范围-本部门及以下数据权限*/
    public static final int DATA_SCOPE_THIS_DEPARTMENT_AND_BELOW = 4;
    /**角色-可查看数据范围-仅本人数据权限*/
    public static final int DATA_SCOPE_MYSELF = 5;
    /*===================================================角色结束=======================================================*/


    /*===================================================公告开始=======================================================*/
    /**通知公告-公告类型-通知*/
    public static final int NOTICE_TYPE_NOTICE = 1;
    /**通知公告-公告类型-公告*/
    public static final int NOTICE_TYPE_BULLETIN = 2;
    /*===================================================公告结束=======================================================*/


    /*===================================================操作日志开始====================================================*/
    /**操作日志-业务类型-其它*/
    public static final int BUSINESS_TYPE_OTHER = 0;
    /**操作日志-业务类型-新增*/
    public static final int BUSINESS_TYPE_ADD = 1;
    /**操作日志-业务类型-修改*/
    public static final int BUSINESS_TYPE_MODIFY = 2;
    /**操作日志-业务类型-删除*/
    public static final int BUSINESS_TYPE_REMOVE = 3;

    /**操作日志-操作类别-其它*/
    public static final int OPERATOR_TYPE_OTHER = 0;
    /**操作日志-操作类别-后台用户*/
    public static final int OPERATOR_TYPE_BACK = 1;
    /**操作日志-操作类别-手机端用户*/
    public static final int OPERATOR_TYPE_MOBILE = 2;
    /*==================================================操作日志结束=====================================================*/


    /*==================================================任务日志开始=====================================================*/
    /**任务日志-任务状态-正常*/
    public static final int TIMED_TASK_LOG_STATUS_NORMAL = 0;
    /**任务日志-任务状态-失败*/
    public static final int TIMED_TASK_LOG_STATUS_FAIL = 1;
    /*==================================================任务日志结束=====================================================*/


    /*==================================================AI对话开始======================================================*/
    /**AI对话-大模型-DeepSeek*/
    public static final int AICHATCLIENTCONVERSATION_MODEL_DEEPSEEK = 0;

    /**AI对话-消息角色-系统*/
    public static final String AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_SYSTEM = "system";
    /**AI对话-消息角色-用户*/
    public static final String AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_USER = "user";
    /**AI对话-消息角色-AI*/
    public static final String AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_ASSISTANT = "assistant";
    /**AI对话-消息角色-工具*/
    public static final String AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_TOOL = "tool";
    /*==================================================AI对话结束======================================================*/

}