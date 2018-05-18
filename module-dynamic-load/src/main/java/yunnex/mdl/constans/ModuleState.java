package yunnex.mdl.constans;

/**
 * 模块的状态
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public enum ModuleState {
    // 服务可用，载入成功后为可用
    USEABLE(1, "可用"),
    // 服务不可用，可以调用start启动状态扭转成USABLE
    STOPED(2, "已暂停"),
    // 服务不可用，该状态不可逆
    DESTORY(3, "已销毁");

    /** 状态 */
    private int state;

    /** 状态名称 */
    private String name;

    private ModuleState(int state, String name) {
        this.state = state;
        this.name = name;
    }

    public static ModuleState getByState(int state) {
        for (ModuleState moduleState : ModuleState.values()) {
            if (moduleState.state == state) {
                return moduleState;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }

}
