package com.ren.generator.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.ren.common.factory.NestedYamlPropertySourceFactory;

/**
 * 阿里云云存储专属配置类
 * @author ren
 * @date 2025/06/20 13:46
 */
@Component
// 1.该注解表示让SpringBoot自动扫描并加载该配置文件（有一个特点，只要该项目中任何一个地方加了该注释，其他配置类都能读取到该配置文件中的配置项）
// 但是注意，该配置类其实本身是为了properties格式设计的，不支持yaml，所以如果使用该注解导入yaml文件，虽然yaml被加载了，但是根据前缀匹配将会失效，例如@ConfigurationProperties(prefix =
// "cloud.storage.aliyun")
// 只能使用@Value("${aliyun-access-key-id}")这种无前缀方式读取，但是这样，如果项目中存在多个同名配置项，就会出问题，所以如果使用这个注解导入yaml，最好自定义一个工厂类，让他可以正确读取yaml
@PropertySource(value = {"classpath:/config/project/generator.yml"},
    factory = NestedYamlPropertySourceFactory.class)
public class GenProperties
{
	/** 作者 */
    private static String author;
	/** 默认生成包路径 */
    private static String packageName;
	/** 自动去除表前缀 */
	private static boolean autoRemovePre;
	/** 表前缀 */
	private static String tablePrefix;
	/** 是否允许生成文件覆盖到本地 */
	private static boolean allowOverwrite;

	public static String getAuthor() {
		return author;
	}

	@Value("${gen.author}") //由于静态的原因，所以需要这种方式注入
	public void setAuthor(String author) {
		GenProperties.author = author;
	}

	public static String getPackageName() {
		return packageName;
	}

	@Value("${gen.packageName}")
	public void setPackageName(String packageName) {
		GenProperties.packageName = packageName;
	}

	public static boolean getAutoRemovePre() {
		return autoRemovePre;
	}

	@Value("${gen.autoRemovePre}")
	public void setAutoRemovePre(boolean autoRemovePre) {
		GenProperties.autoRemovePre = autoRemovePre;
	}

	public static String getTablePrefix() {
		return tablePrefix;
	}

	@Value("${gen.tablePrefix}")
	public void setTablePrefix(String tablePrefix) {
		GenProperties.tablePrefix = tablePrefix;
	}

	public static boolean getAllowOverwrite() {
		return allowOverwrite;
	}

	@Value("${gen.allowOverwrite}")
	public void setAllowOverwrite(boolean allowOverwrite) {
		GenProperties.allowOverwrite = allowOverwrite;
	}
}