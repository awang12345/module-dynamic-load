package yunnex.mdl.constans;

/**
 * 模块jar包Manifest文件中key
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public interface ModuleJarManifestElementKeys {
    
    /** 模块名称，比如：game12 */
    String MODULE_NAME = "Module-Name";
    
    /** 模块版本，比如：1.0.20170302 */
    String MODULE_VERSION = "Module-Version";
    
    /** 模块简介，比如：互动营销活动之十二宫格 */
    String MODULE_DESC = "Module-Desc";
    
    /** 模块包的路径,用于模块类加载器优先加载，比如:yunnex.module.game */
    String MODULE_PACKAGE = "Module-Package";
    
    /** 模块创建日期，比如:2017-02-01 14:12:59 */
    String MODULE_CREATED = "Module-Created";
    
    /** 模块签名，用于校验jar是否为模块，签名方式:MD5(Name+Version+Package+Created+SecretKey) */
    String MODULE_DIGEST = "Module-Digest";

}
