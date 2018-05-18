package yunnex.mdl.utils;

import java.io.File;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 模块工具
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public final class ModuleUtils {

    private ModuleUtils() {}

    /**
     * 判断newVersion是否比oldVersion的版本号要新
     *
     * @param oldVersion
     * @param newVersion
     * @return true:表示newVersion大于oldVersion<br/>
     *         false表示:newVersion小于oldVersion
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    public static boolean isNewVersion(String oldVersion, String newVersion) {
        return newVersion.compareTo(oldVersion) >= 1;
    }

    /**
     * 是否为jar文件
     *
     * @param file
     * @return
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    public static boolean isJarFile(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        return file.getName().toLowerCase().endsWith(".jar");
    }

    /**
     * 获取路径,支持:classpath:开头这种格式
     *
     * @param location
     * @return
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    public static String formatFilePath(String location) {
        if (location != null && location.trim().toLowerCase().startsWith("classpath")) {
            try {
                PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(
                                Thread.currentThread().getContextClassLoader());
                Resource resource = pathMatchingResourcePatternResolver.getResource(location);
                return resource.getFile().getAbsolutePath();
            } catch (Exception ex) {}
        }
        return location;
    }

}
