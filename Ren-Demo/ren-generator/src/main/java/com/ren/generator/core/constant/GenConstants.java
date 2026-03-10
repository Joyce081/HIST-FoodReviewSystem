package com.ren.generator.core.constant;

public class GenConstants {

    /**代码生成-生成代码方式-zip压缩包*/
    public static final String GEN_GENTYPE_ZIP = "0";
    /**代码生成-生成代码方式-自定义路径*/
    public static final String GEN_GENTYPE_CUSTOMIZE = "1";

    /**代码生成-Java源文件路径*/
    public static final String PROJECT_PATH = "main/java";
    /**代码生成-Mybatis映射文件路径*/
    public static final String MYBATIS_PATH = "main/resources/mapper";
    /**代码生成-默认上级菜单ID*/
    public static final String DEFAULT_PARENT_MENU_ID = "3";

    /** 代码生成-表类型-单表（增删改查） */
    public static final String TPL_CRUD = "crud";
    /** 代码生成-表类型-树表（增删改查） */
    public static final String TPL_TREE = "tree";
    /** 代码生成-表类型-主子表（增删改查） */
    public static final String TPL_SUB = "sub";

    /** 代码生成-树编码字段名 */
    public static final String TREE_CODE = "treeCode";
    /** 代码生成-树父编码字段名 */
    public static final String TREE_PARENT_CODE = "treeParentCode";
    /** 代码生成-树名称字段名 */
    public static final String TREE_NAME = "treeName";
    /** 代码生成-上级菜单ID字段名 */
    public static final String PARENT_MENU_ID = "parentMenuId";
    /** 代码生成-上级菜单名称字段名 */
    public static final String PARENT_MENU_NAME = "parentMenuName";

    /** 数据库字段类型-字符串类型 */
    public static final String[] COLUMNTYPE_STR = { "char", "varchar", "nvarchar", "varchar2" };
    /** 数据库字段类型-文本类型 */
    public static final String[] COLUMNTYPE_TEXT = { "tinytext", "text", "mediumtext", "longtext" };
    /** 数据库字段类型-时间类型 */
    public static final String[] COLUMNTYPE_TIME = { "datetime", "time", "date", "timestamp" };
    /** 数据库字段类型-整型（由于各种有无符号以及各种取值范围问题，所以"bit", "tinyint", "smallint", "mediumint"均当作int对待） */
    public static final String[] COLUMNTYPE_INT = { "bit", "tinyint", "smallint", "mediumint", "int", "integer" };
    /** 数据库字段类型-长整型 */
    public static final String[] COLUMNTYPE_BIGINT = { "bigint"};
    /** 数据库字段类型-单精度浮点类型 */
    public static final String[] COLUMNTYPE_FLOAT = { "float"};
    /** 数据库字段类型-双精度浮点类型 */
    public static final String[] COLUMNTYPE_DOUBLE = { "double"};
    /** 数据库字段类型-安全浮点类型 */
    public static final String[] COLUMNTYPE_DECIMAL = { "decimal"};
    /** 数据库字段类型-数字类型 */
    public static final String[] COLUMNTYPE_NUMBER = { "number"};


    /** Java实体类类型-字符串类型 */
    public static final String TYPE_STRING = "String";
    /** Java实体类类型-整型 */
    public static final String TYPE_INTEGER = "Integer";
    /** Java实体类类型-长整型 */
    public static final String TYPE_LONG = "Long";
    /** Java实体类类型-单精度浮点型 */
    public static final String TYPE_FLOAT = "Float";
    /** Java实体类类型-双精度浮点型 */
    public static final String TYPE_DOUBLE = "Double";
    /** Java实体类类型-高精度计算类型 */
    public static final String TYPE_BIGDECIMAL = "BigDecimal";
    /** Java实体类类型-时间类型 */
    public static final String TYPE_DATE = "Date";

    /** VUE页面字段-不需要添加字段 */
    public static final String[] COLUMNNAME_NOT_ADD = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time" };
    /** VUE页面字段-不需要编辑字段 */
    public static final String[] COLUMNNAME_NOT_EDIT = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time" };
    /** VUE页面字段-不需要显示的列表字段 */
    public static final String[] COLUMNNAME_NOT_LIST = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time" };
    /** VUE页面字段-不需要查询字段 */
    public static final String[] COLUMNNAME_NOT_QUERY = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time", "remark" };

    /** Entity基类字段 */
    public static final String[] BASE_ENTITY = { "createBy", "createTime", "updateBy", "updateTime", "remark" };

    /** VUE页面组件类型-文本框 */
    public static final String HTML_INPUT = "input";
    /** VUE页面组件类型-文本域 */
    public static final String HTML_TEXTAREA = "textarea";
    /** VUE页面组件类型-下拉框 */
    public static final String HTML_SELECT = "select";
    /** VUE页面组件类型-单选框 */
    public static final String HTML_RADIO = "radio";
    /** VUE页面组件类型-复选框 */
    public static final String HTML_CHECKBOX = "checkbox";
    /** VUE页面组件类型-日期控件 */
    public static final String HTML_DATETIME = "datetime";
    /** VUE页面组件类型-图片上传控件 */
    public static final String HTML_IMAGE_UPLOAD = "imageUpload";
    /** VUE页面组件类型-文件上传控件 */
    public static final String HTML_FILE_UPLOAD = "fileUpload";
    /** VUE页面组件类型-富文本控件 */
    public static final String HTML_EDITOR = "editor";

    /** Sql查询方式-模糊查询 */
    public static final String QUERY_LIKE = "LIKE";
    /** Sql查询方式-相等查询 */
    public static final String QUERY_EQ = "EQ";

    /** 是否需要-需要 */
    public static final String REQUIRE = "1";
    /** 是否需要-不需要 */
    public static final String NOT_REQUIRE = "0";

}