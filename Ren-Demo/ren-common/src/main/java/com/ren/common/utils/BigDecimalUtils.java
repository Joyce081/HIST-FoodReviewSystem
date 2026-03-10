package com.ren.common.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * BigDecimal 工具类（避免精度丢失、空指针、未指定舍入模式等问题）
 */
public final class BigDecimalUtils {

    // 禁止实例化
    private BigDecimalUtils() {
        throw new AssertionError("不允许实例化工具类");
    }

    //-------------------------------- 初始化方法 --------------------------------

    /**
     * 安全创建BigDecimal（优先使用String构造，避免double精度问题）
     * @param value 数值（支持String、Number类型）
     * @return 非空BigDecimal（若输入为null返回BigDecimal.ZERO）
     */
    public static BigDecimal createSafe(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof String) {
            String str = ((String) value).trim();
            return str.isEmpty() ? BigDecimal.ZERO : new BigDecimal(str);
        }
        if (value instanceof Number) {
            // 避免Double/Float的精度问题，转String后构造
            return new BigDecimal(value.toString());
        }
        throw new IllegalArgumentException("不支持的类型: " + value.getClass());
    }

    //-------------------------------- 运算方法（默认舍入模式：四舍五入）（直接使用BigDecimal运算） --------------------------------

    /**
     * 加法（处理null值）
     * @param v1 加数（若为null视为0）
     * @param v2 被加数（若为null视为0）
     * @return 非空结果
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2,Integer scale) {
        v1 = nullToZero(v1);
        v2 = nullToZero(v2);
        if(scale != null && scale > 0){
            return scale(v1.add(v2),scale);
        }else{
            return v1.add(v2);
        }
    }

    /**
     * 减法（处理null值）
     * @param v1 减数（若为null视为0）
     * @param v2 被减数（若为null视为0）
     * @return 非空结果
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2,Integer scale) {
        v1 = nullToZero(v1);
        v2 = nullToZero(v2);
        if(scale != null && scale > 0){
            return scale(v1.subtract(v2),scale);
        }else{
            return v1.subtract(v2);
        }
    }

    /**
     * 乘法（自动处理小数点后多余精度，默认四舍五入）
     * @param v1 乘数（若为null视为0）
     * @param v2 被乘数（若为null视为0）
     * @param scale 保留小数位数
     * @return 非空结果
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2,Integer scale) {
        v1 = nullToZero(v1);
        v2 = nullToZero(v2);
        if(scale != null && scale > 0){
            return v1.multiply(v2).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return v1.multiply(v2);
        }
    }

    /**
     * 除法（强制指定舍入模式，避免ArithmeticException）
     * @param v1 除数（若为null视为0）
     * @param v2 被除数（若为null会抛出异常）
     * @param scale 保留小数位数
     * @return 非空结果
     * @throws ArithmeticException 被除数为0或null
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale) {
        v1 = nullToZero(v1);
        if (v2 == null || isZero(v2)) {
            throw new ArithmeticException("被除数不能为0");
        }
        return v1.divide(v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 平方运算
     * @param value
     * @param scale
     * @return java.math.BigDecimal
     * @author ren
     * @date 2025/7/22
     */
    public static BigDecimal square(BigDecimal value, Integer scale) {
        if (value == null || isZero(value)) {
            throw new ArithmeticException("值不能为0");
        }
        if(scale != null && scale > 0){
            return value.multiply(value).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return value.multiply(value);
        }
    }

    /**
     * 开根号
     * @param value
     * @param scale
     * @return java.math.BigDecimal
     * @author ren
     * @date 2025/07/22 14:33
     */
    public static BigDecimal rootNumber(BigDecimal value, Integer scale) {
        if (value == null || isZero(value)) {
            throw new ArithmeticException("值不能为0");
        }
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP); // 精度默认设置为10位，四舍五入
        if(scale != null && scale > 0){
            return value.sqrt(mc).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return value.sqrt(mc);
        }
    }

    /**
     * 计算分子占分母的百分比（保留指定小数位数）
     * @param numerator   分子（部分值）
     * @param denominator 分母（总值）
     * @param scale       保留的小数位数（例如：2表示保留两位小数）
     * @return 百分比数值（未带百分号，例如 25.55 表示 25.55%）
     * @throws ArithmeticException 分母为0或负数时抛出异常
     */
    public static BigDecimal calculatePercentage(BigDecimal numerator, BigDecimal denominator, int scale) {
        // 1. 验证分母合法性
        if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("分母必须为正数且不为零");
        }

        // 2. 计算百分比公式: (numerator / denominator) * 100
        // 关键点：除法时指定中间精度（scale+2避免舍入误差），乘法用常量优化[4,6](@ref)
        BigDecimal percentage = numerator
                .divide(denominator, scale + 2, RoundingMode.HALF_UP) // 多保留2位中间精度
                .multiply(BigDecimal.valueOf(100));                  // 乘以100

        // 3. 四舍五入到目标小数位数
        return percentage.setScale(scale, RoundingMode.HALF_UP);
    }

    //-------------------------------- 运算方法（默认舍入模式：四舍五入）（传入double转为BigDecimal运算，最后返回BigDecimal） --------------------------------

    /**
     * 加法（处理null值）
     * @param v1 加数（若为null视为0）
     * @param v2 被加数（若为null视为0）
     * @return 非空结果
     */
    public static BigDecimal add(double v1, double v2,Integer scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        v2Decimal = nullToZero(v2Decimal);
        if(scale != null && scale > 0){
            return scale(v1Decimal.add(v2Decimal),scale);
        }else{
            return v1Decimal.add(v2Decimal);
        }
    }

    /**
     * 减法（处理null值）
     * @param v1 减数（若为null视为0）
     * @param v2 被减数（若为null视为0）
     * @return 非空结果
     */
    public static BigDecimal subtract(double v1, double v2,Integer scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        v2Decimal = nullToZero(v2Decimal);
        if(scale != null && scale > 0){
            return scale(v1Decimal.subtract(v2Decimal),scale);
        }else{
            return v1Decimal.subtract(v2Decimal);
        }
    }

    /**
     * 乘法（自动处理小数点后多余精度，默认四舍五入）
     * @param v1 乘数（若为null视为0）
     * @param v2 被乘数（若为null视为0）
     * @param scale 保留小数位数
     * @return 非空结果
     */
    public static BigDecimal multiply(double v1, double v2,Integer scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        v2Decimal = nullToZero(v2Decimal);
        if(scale != null && scale > 0){
            return v1Decimal.multiply(v2Decimal).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return v1Decimal.multiply(v2Decimal);
        }
    }

    /**
     * 除法（强制指定舍入模式，避免ArithmeticException）
     * @param v1 除数（若为null视为0）
     * @param v2 被除数（若为null会抛出异常）
     * @param scale 保留小数位数
     * @return 非空结果
     * @throws ArithmeticException 被除数为0或null
     */
    public static BigDecimal divide(double v1, double v2, int scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        if (v2Decimal == null || isZero(v2Decimal)) {
            throw new ArithmeticException("被除数不能为0");
        }
        return v1Decimal.divide(v2Decimal, scale, RoundingMode.HALF_UP);
    }

    /**
     * 平方运算
     * @param value
     * @param scale
     * @return java.math.BigDecimal
     * @author ren
     * @date 2025/7/22
     */
    public static BigDecimal square(double value, Integer scale) {
        BigDecimal valueDecimal = createSafe(value);
        if (valueDecimal == null || isZero(valueDecimal)) {
            throw new ArithmeticException("值不能为0");
        }
        if(scale != null && scale > 0){
            return valueDecimal.multiply(valueDecimal).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return valueDecimal.multiply(valueDecimal);
        }
    }

    /**
     * 开根号
     * @param value
     * @param scale
     * @return java.math.BigDecimal
     * @author ren
     * @date 2025/07/22 14:33
     */
    public static BigDecimal rootNumber(double value, Integer scale) {
        BigDecimal valueDecimal = createSafe(value);
        if (valueDecimal == null || isZero(valueDecimal)) {
            throw new ArithmeticException("值不能为0");
        }
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP); // 精度默认设置为10位，四舍五入
        if(scale != null && scale > 0){
            return valueDecimal.sqrt(mc).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return valueDecimal.sqrt(mc);
        }
    }

    /**
     * 计算分子占分母的百分比（保留指定小数位数）
     * @param numerator   分子（部分值）
     * @param denominator 分母（总值）
     * @param scale       保留的小数位数（例如：2表示保留两位小数）
     * @return 百分比数值（未带百分号，例如 25.55 表示 25.55%）
     * @throws ArithmeticException 分母为0或负数时抛出异常
     */
    public static BigDecimal calculatePercentage(double numerator, double denominator, int scale) {
        BigDecimal numeratorDecimal = createSafe(numerator);
        BigDecimal denominatorDecimal = createSafe(denominator);
        // 1. 验证分母合法性
        if (denominatorDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("分母必须为正数且不为零");
        }

        // 2. 计算百分比公式: (numerator / denominator) * 100
        // 关键点：除法时指定中间精度（scale+2避免舍入误差），乘法用常量优化[4,6](@ref)
        BigDecimal percentage = numeratorDecimal
                .divide(denominatorDecimal, scale + 2, RoundingMode.HALF_UP) // 多保留2位中间精度
                .multiply(BigDecimal.valueOf(100));                  // 乘以100

        // 3. 四舍五入到目标小数位数
        return percentage.setScale(scale, RoundingMode.HALF_UP);
    }

    //-------------------------------- 运算方法（默认舍入模式：四舍五入）（传入String转为BigDecimal运算，最后返回BigDecimal） --------------------------------

    /**
     * 加法（处理null值）
     * @param v1 加数（若为null视为0）
     * @param v2 被加数（若为null视为0）
     * @return 非空结果
     */
    public static BigDecimal add(String v1, String v2,Integer scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        v2Decimal = nullToZero(v2Decimal);
        if(scale != null && scale > 0){
            return scale(v1Decimal.add(v2Decimal),scale);
        }else{
            return v1Decimal.add(v2Decimal);
        }
    }

    /**
     * 减法（处理null值）
     * @param v1 减数（若为null视为0）
     * @param v2 被减数（若为null视为0）
     * @return 非空结果
     */
    public static BigDecimal subtract(String v1, String v2,Integer scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        v2Decimal = nullToZero(v2Decimal);
        if(scale != null && scale > 0){
            return scale(v1Decimal.subtract(v2Decimal),scale);
        }else{
            return v1Decimal.subtract(v2Decimal);
        }
    }

    /**
     * 乘法（自动处理小数点后多余精度，默认四舍五入）
     * @param v1 乘数（若为null视为0）
     * @param v2 被乘数（若为null视为0）
     * @param scale 保留小数位数
     * @return 非空结果
     */
    public static BigDecimal multiply(String v1, String v2, Integer scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        v2Decimal = nullToZero(v2Decimal);
        if(scale != null && scale > 0){
            return v1Decimal.multiply(v2Decimal).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return v1Decimal.multiply(v2Decimal);
        }
    }

    /**
     * 除法（强制指定舍入模式，避免ArithmeticException）
     * @param v1 除数（若为null视为0）
     * @param v2 被除数（若为null会抛出异常）
     * @param scale 保留小数位数
     * @return 非空结果
     * @throws ArithmeticException 被除数为0或null
     */
    public static BigDecimal divide(String v1, String v2, int scale) {
        BigDecimal v1Decimal = createSafe(v1);
        BigDecimal v2Decimal = createSafe(v2);
        v1Decimal = nullToZero(v1Decimal);
        if (v2Decimal == null || isZero(v2Decimal)) {
            throw new ArithmeticException("被除数不能为0");
        }
        return v1Decimal.divide(v2Decimal, scale, RoundingMode.HALF_UP);
    }

    /**
     * 平方运算
     * @param value
     * @param scale
     * @return java.math.BigDecimal
     * @author ren
     * @date 2025/7/22
     */
    public static BigDecimal square(String value, Integer scale) {
        BigDecimal valueDecimal = createSafe(value);
        if (valueDecimal == null || isZero(valueDecimal)) {
            throw new ArithmeticException("值不能为0");
        }
        if(scale != null && scale > 0){
            return valueDecimal.multiply(valueDecimal).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return valueDecimal.multiply(valueDecimal);
        }
    }

    /**
     * 开根号
     * @param value
     * @param scale
     * @return java.math.BigDecimal
     * @author ren
     * @date 2025/07/22 14:33
     */
    public static BigDecimal rootNumber(String value, Integer scale) {
        BigDecimal valueDecimal = createSafe(value);
        if (valueDecimal == null || isZero(valueDecimal)) {
            throw new ArithmeticException("值不能为0");
        }
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP); // 精度默认设置为10位，四舍五入
        if(scale != null && scale > 0){
            return valueDecimal.sqrt(mc).setScale(scale, RoundingMode.HALF_UP);
        }else{
            return valueDecimal.sqrt(mc);
        }
    }

    /**
     * 计算分子占分母的百分比（保留指定小数位数）
     * @param numerator   分子（部分值）
     * @param denominator 分母（总值）
     * @param scale       保留的小数位数（例如：2表示保留两位小数）
     * @return 百分比数值（未带百分号，例如 25.55 表示 25.55%）
     * @throws ArithmeticException 分母为0或负数时抛出异常
     */
    public static BigDecimal calculatePercentage(String numerator, String denominator, int scale) {
        BigDecimal numeratorDecimal = createSafe(numerator);
        BigDecimal denominatorDecimal = createSafe(denominator);
        // 1. 验证分母合法性
        if (denominatorDecimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("分母必须为正数且不为零");
        }

        // 2. 计算百分比公式: (numerator / denominator) * 100
        // 关键点：除法时指定中间精度（scale+2避免舍入误差），乘法用常量优化[4,6](@ref)
        BigDecimal percentage = numeratorDecimal
                .divide(denominatorDecimal, scale + 2, RoundingMode.HALF_UP) // 多保留2位中间精度
                .multiply(BigDecimal.valueOf(100));                  // 乘以100

        // 3. 四舍五入到目标小数位数
        return percentage.setScale(scale, RoundingMode.HALF_UP);
    }

    //-------------------------------- 比较方法（避免equals的标度问题） --------------------------------

    /**
     * 判断是否相等（值相等，标度不同视为不等）
     * @param v1 值1
     * @param v2 值2
     * @return 是否相等
     */
    public static boolean equals(BigDecimal v1, BigDecimal v2) {
        if (v1 == null && v2 == null) return true;
        if (v1 == null || v2 == null) return false;
        return v1.compareTo(v2) == 0 && v1.scale() == v2.scale();
    }

    /**
     * 判断数值是否相等（忽略标度差异）
     * @param v1 值1
     * @param v2 值2
     * @return 值是否相等
     */
    public static boolean equalsValue(BigDecimal v1, BigDecimal v2) {
        if (v1 == null && v2 == null) return true;
        if (v1 == null || v2 == null) return false;
        return v1.compareTo(v2) == 0;
    }

    /**
     * 判断是否大于
     * @param v1 值1（若为null视为0）
     * @param v2 值2（若为null视为0）
     * @return v1 > v2 ?
     */
    public static boolean greaterThan(BigDecimal v1, BigDecimal v2) {
        return nullToZero(v1).compareTo(nullToZero(v2)) > 0;
    }

    /**
     * 判断是否小于
     * @param v1 值1（若为null视为0）
     * @param v2 值2（若为null视为0）
     * @return v1 < v2 ?
     */
    public static boolean lessThan(BigDecimal v1, BigDecimal v2) {
        return nullToZero(v1).compareTo(nullToZero(v2)) < 0;
    }

    //-------------------------------- 工具方法 --------------------------------

    /**
     * 空转零
     */
    public static BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 判断是否为0（处理null）
     */
    public static boolean isZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 设置精度（默认四舍五入）
     * @param value 原始值（若为null返回0）
     * @param scale 小数位数
     */
    public static BigDecimal scale(BigDecimal value, int scale) {
        return nullToZero(value).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 设置精度（默认四舍五入）
     * @param value 原始值（若为null返回0）
     * @param scale 小数位数
     */
    public static BigDecimal scale(double value, int scale) {
        return nullToZero(createSafe(value)).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 设置精度（默认四舍五入）
     * @param value 原始值（若为null返回0）
     * @param scale 小数位数
     */
    public static BigDecimal scale(String value, int scale) {
        return nullToZero(createSafe(value)).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化输出（如保留2位小数）
     * @param value 数值（若为null返回"0.00"）
     * @param scale 小数位数
     */
    public static BigDecimal format(BigDecimal value, int scale) {
        return scale(nullToZero(value), scale);
    }

    /**
     * 格式化输出（如保留2位小数）
     * @param value 数值（若为null返回"0.00"）
     * @param scale 小数位数
     */
    public static double format(double value, int scale) {
        return scale(nullToZero(createSafe(value)), scale).doubleValue();
    }

    /**
     * 格式化输出（如保留2位小数）
     * @param value 数值（若为null返回"0.00"）
     * @param scale 小数位数
     */
    public static String format(String value, int scale) {
        return scale(nullToZero(createSafe(value)), scale).toString();
    }

    /**
     * BigDecimal转String
     * @param value 数值
     */
    public static String toString(BigDecimal value) {
        return value.toString();
    }

    /**
     * BigDecimal转double
     * @param value 数值
     */
    public static double toDouble(BigDecimal value) {
        return value.doubleValue();
    }

}