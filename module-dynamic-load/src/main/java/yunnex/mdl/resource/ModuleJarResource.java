package yunnex.mdl.resource;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import yunnex.mdl.ModuleConfig;
import yunnex.mdl.ModuleResource;
import yunnex.mdl.bean.ModuleJarManifest;
import yunnex.mdl.utils.ModuleUtils;

/**
 * 模块jar包资源
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public class ModuleJarResource implements ModuleResource {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleJarResource.class);

    // jar包的路径
    private final String jarPath;

    public ModuleJarResource(String jarPath) {
        this.jarPath = jarPath;
    }

    @Override
    public ModuleConfig getResuorce() {
        JarFile jarFile = getJarFile(jarPath);
        if (jarFile != null) {
            return getConfigFromJarFile(jarFile);
        }
        return null;
    }

    /**
     * 从jar的MANIFEST.MF文件读取配置信息,具体如下:<br/>
     * <ul>
     * <li>Module-Name:模块名称，比如：game12</li>
     * <li>Module-Version:模块版本，比如：1.0.20170302</li>
     * <li>Module-Desc:模块简介，比如：互动营销活动之十二宫格</li>
     * <li>Module-Package:模块包的路径,用于模块类加载器优先加载，比如:yunnex.module.game</li>
     * <li>Module-Created:模块创建日期，比如:2017-02-01 14:12:59</li>
     * <li>Module-Digest:模块签名，用于校验jar是否为模块，签名方式:MD5(Name+Version+Package+Created)</li>
     * </ul>
     * 
     *
     * @param jarFile
     * @return
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    private ModuleConfig getConfigFromJarFile(JarFile jarFile) {
        try {
            ModuleJarManifest moduleJarManifest = ModuleJarManifest.parse(jarFile);
            if (moduleJarManifest != null) {
                ModuleConfig moduleConfig = new ModuleConfig();
                moduleConfig.setName(moduleJarManifest.getModuleName());
                moduleConfig.setVersion(moduleJarManifest.getModuleVersion());
                moduleConfig.setOverridePackages(Lists.newArrayList(StringUtils.split(moduleJarManifest.getModulePackage(), ",")));
                moduleConfig.setModuleUrl(Lists.newArrayList(new File(jarPath).toURI().toURL()));
                return moduleConfig;
            }
        } catch (Exception e) {
            LOG.warn("Get module resource error!JarPath=" + jarPath, e);
        }
        return null;
    }

    /**
     * 获取jar文件
     *
     * @return
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    private JarFile getJarFile(String jarPath) {
        if (StringUtils.isBlank(jarPath)) {
            LOG.info("Get module resource fail.The jar path is empt!");
            return null;
        }
        File file = new File(jarPath);
        if (!ModuleUtils.isJarFile(file)) {
            LOG.info("Get module resource from {} fail .Is not jar file!", jarPath);
            return null;
        }
        try {
            return new JarFile(file);
        } catch (IOException e) {
            LOG.error("Get module resource from " + jarPath + " error!", e);
        } catch (SecurityException e) {
            LOG.error("Get module resource from " + jarPath + " error!May be the jar is not validat!", e);
        }
        return null;
    }

}
