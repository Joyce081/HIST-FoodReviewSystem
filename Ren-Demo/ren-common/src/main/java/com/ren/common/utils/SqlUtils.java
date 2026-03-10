package com.ren.common.utils;

import java.util.List;

import cn.hutool.core.exceptions.UtilException;

/**
 * SQL操作安全工具类
 *
 * 主要功能：提供SQL注入防护、排序字段安全校验等安全相关操作，有效预防常见SQL注入攻击
 *
 * 核心功能包含：
 *  1. SQL关键字黑名单检测：阻止包含危险SQL关键字的输入
 *  2. ORDER BY子句安全校验：确保排序字段格式合法且安全
 *  3. 输入参数规范化：限制特殊字符和最大长度
 *
 * @author ren
 */
public class SqlUtils
{
    /**
     * SQL危险关键字黑名单正则表达式
     *
     * 包含：常见SQL操作、注入攻击技术、特殊字符及危险函数
     * 设计特点：
     *  1. 结尾带空格（如"and "）避免误判普通单词
     *  2. 包含Unicode特殊字符\u000B（垂直制表符）防止绕过
     *  3. 覆盖常用SQL注入攻击技术（如报错注入、时间盲注）
     */
    public static String SQL_REGEX = "\u000B|and |extractvalue|updatexml|sleep|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |union |like |+|/*|user()";

    /**
     * ORDER BY子句字符白名单正则表达式
     *
     * 仅允许以下字符：字母、数字、下划线、空格、逗号、小数点
     * 限制排序字段只能包含表名/列名等安全字符，防止SQL注入
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * ORDER BY子句最大长度限制（500字符）
     *
     * 防止超长SQL攻击（如通过超长字段名或注释发起攻击）
     */
    private static final int ORDER_BY_MAX_LENGTH = 500;

    /**
     * ORDER BY子句安全验证与规范化方法
     *
     * 功能描述：
     *  1. 验证传入值是否符合安全规范（SQL_PATTERN）
     *  2. 检查值长度是否超过限制（ORDER_BY_MAX_LENGTH）
     *  3. 对不符合要求的输入抛出安全异常
     *
     * 适用场景：动态排序参数处理（如接口中的orderBy参数）
     *
     * @param value 待验证的排序字段字符串（如"create_time desc"）
     * @return 原样返回通过验证的值
     * @throws UtilException 当值非法或超长时抛出安全异常
     */
    public static String escapeOrderBySql(String value)
    {
        // 非空校验：只有非空值才进行安全验证
        if (StringUtils.isNotBlank(value)) {
            // 验证格式：使用白名单正则校验
            if (!isValidOrderBySql(value)) {
                throw new UtilException("参数不符合规范，不能进行查询");
            }
            // 长度校验：防止超长攻击
            if (StringUtils.length(value) > ORDER_BY_MAX_LENGTH) {
                throw new UtilException("参数已超过最大限制，不能进行查询");
            }
        }
        // 返回原始值（本方法不进行转义，仅做验证）
        return value;
    }

    /**
     * ORDER BY子句格式验证方法（内部使用）
     *
     * 使用白名单正则（SQL_PATTERN）校验输入值格式
     *
     * @param value 待验证的字符串
     * @return true-格式正确安全，false-存在潜在风险
     */
    public static boolean isValidOrderBySql(String value)
    {
        // 正则匹配：值必须完全符合白名单字符规范
        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL关键字黑名单检测方法
     *
     * 功能描述：
     *  1. 检测输入值是否包含SQL_REGEX中的危险关键字
     *  2. 使用不区分大小写检测（indexOfIgnoreCase）防止大小写绕过
     *  3. 对匹配到的危险输入立即抛出安全异常
     *
     * 适用场景：所有SQL拼接前的用户输入验证
     *
     * @param value 待检测的用户输入（如搜索关键词、表名等）
     * @throws UtilException 当检测到危险关键字时抛出安全异常
     */
    public static void filterKeyword(String value)
    {
        // 空值跳过：空字符串不做处理
        if (StringUtils.isBlank(value)) {
            return;
        }

        // 解析黑名单：将正则字符串拆分为独立的关键字数组
        List<String> sqlKeywords = StringUtils.split(SQL_REGEX, "\\|");

        // 遍历黑名单：对每个关键字进行检查
        for (String sqlKeyword : sqlKeywords) {
            // 不区分大小写检测：防止大小写变体绕过
            if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1) {
                // 发现危险关键字时立即抛出异常
                throw new UtilException("参数存在SQL注入风险");
            }
        }
    }
}