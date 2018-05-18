package yunnex.mdl.bean;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yunnex.mdl.constans.ModuleJarManifestElementKeys;

/**
 * 模块jar包Manifest内容定义
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public final class ModuleJarManifest {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleJarManifest.class);

    /** 签名秘钥 */
    private static final String DIGEST_SECRET_KEY = "49ca1ad02bc423c70921e060eac343c5";

    // 模块名称
    private String moduleName;

    // 模块版本
    private String moduleVersion;

    // 模块简介
    private String moduleDesc;

    // 模块包路径
    private String modulePackage;

    // 模块创建名称
    private String moduleCreated;

    // 模块签名
    private String moduleDigest;

    public ModuleJarManifest() {
        super();
    }

    public ModuleJarManifest(String moduleName, String moduleVersion, String moduleDesc, String modulePackage, String moduleCreated) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.moduleDesc = moduleDesc;
        this.modulePackage = modulePackage;
        this.moduleCreated = moduleCreated;
        this.moduleDigest = DIGEST_SECRET_KEY;
    }

    public ModuleJarManifest(String moduleName, String moduleVersion, String moduleDesc, String modulePackage, String moduleCreated,
                    String moduleDigest) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.moduleDesc = moduleDesc;
        this.modulePackage = modulePackage;
        this.moduleCreated = moduleCreated;
        this.moduleDigest = moduleDigest;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public String getModuleDesc() {
        return moduleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc = moduleDesc;
    }

    public String getModulePackage() {
        return modulePackage;
    }

    public void setModulePackage(String modulePackage) {
        this.modulePackage = modulePackage;
    }

    public String getModuleCreated() {
        return moduleCreated;
    }

    public void setModuleCreated(String moduleCreated) {
        this.moduleCreated = moduleCreated;
    }

    public String getModuleDigest() {
        return moduleDigest;
    }

    public void setModuleDigest(String moduleDigest) {
        this.moduleDigest = moduleDigest;
    }

    /**
     * 获取Manifest内容
     *
     * @return
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    public String getManifest() {
        StringBuilder sb = new StringBuilder();
        sb.append(ModuleJarManifestElementKeys.MODULE_NAME).append(":").append(moduleName).append("\r\n");
        sb.append(ModuleJarManifestElementKeys.MODULE_VERSION).append(":").append(moduleVersion).append("\r\n");
        sb.append(ModuleJarManifestElementKeys.MODULE_DESC).append(":").append(moduleDesc).append("\r\n");
        sb.append(ModuleJarManifestElementKeys.MODULE_PACKAGE).append(":").append(modulePackage).append("\r\n");
        sb.append(ModuleJarManifestElementKeys.MODULE_CREATED).append(":").append(moduleCreated).append("\r\n");
        sb.append(ModuleJarManifestElementKeys.MODULE_DIGEST).append(":").append(moduleDigest).append("\r\n");
        return sb.toString();
    }

    /**
     * 从jar文件中解析
     *
     * @param jarFile
     * @return 解析失败返回NULL
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    public static ModuleJarManifest parse(JarFile jarFile) {
        if (jarFile == null) {
            return null;
        }
        try {
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            if (attributes != null && !attributes.isEmpty()) {
                ModuleJarManifest moduleJarManifest = new ModuleJarManifest();
                moduleJarManifest.setModuleName(attributes.getValue(ModuleJarManifestElementKeys.MODULE_NAME));
                moduleJarManifest.setModuleVersion(attributes.getValue(ModuleJarManifestElementKeys.MODULE_VERSION));
                moduleJarManifest.setModuleDesc(attributes.getValue(ModuleJarManifestElementKeys.MODULE_DESC));
                moduleJarManifest.setModulePackage(attributes.getValue(ModuleJarManifestElementKeys.MODULE_PACKAGE));
                moduleJarManifest.setModuleDigest(attributes.getValue(ModuleJarManifestElementKeys.MODULE_DIGEST));
                LOG.debug("Parse jar file to moduleJarManifest!\r\n{}", moduleJarManifest.getManifest());
                return moduleJarManifest;
            }
        } catch (IOException e) {
            LOG.warn("Parse jar file to ModuleJarManifest error!JarFile=" + jarFile.getName(), e);
        }
        return null;
    }

    /**
     * 校验签名:拿Manifest中的digest与md5(moduleName + moduleVersion + modulePackage + moduleCreated +
     * DIGEST_SECRET_KEY)做对比
     *
     * @return
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    public boolean checkDigest() {
        return DIGEST_SECRET_KEY.equalsIgnoreCase(moduleDigest);
    }

    /**
     * 生成签名
     *
     * @return
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    // public String generatorDigest() {
    // return StringUtils.newStringUtf8(DigestUtils.md5(moduleName + moduleVersion + modulePackage +
    // moduleCreated + DIGEST_SECRET_KEY));
    // }

}
