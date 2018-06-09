package me.skymc.taboomenu.setting;

/**
 * @Author sky
 * @Since 2018-06-05 19:49
 */
public enum IconSettings {

    /**
     * 物品序号
     */
    ID("ID"),

    /**
     * 物品数量
     */
    AMOUNT("AMOUNT"),

    /**
     * 物品名称
     */
    NAME("NAME"),

    /**
     * 物品描述
     */
    LORE("LORE"),

    /**
     * 物品颜色
     */
    COLOR("COLOR"),

    /**
     * 头颅皮肤
     */
    SKULL_OWNER("SKULL-OWNER"),

    /**
     * 触发命令
     */
    COMMAND("COMMAND"),

    /**
     * 消耗金币
     */
    PRICE("PRICE"),

    /**
     * 消耗点券
     */
    POINTS("POINTS"),

    /**
     * 消耗等级
     */
    LEVELS("LEVELS"),

    /**
     * 点击权限
     */
    PERMISSION("PERMISSION"),

    /**
     * 点击权限提示
     */
    PERMISSION_MESSAGE("PERMISSION-MESSAGE"),

    /**
     * 显示权限
     */
    PERMISSION_VIEW("PERMISSION-VIEW"),

    /**
     * 条件扩展
     */
    REQUIREMENT("REQUIREMENT"),

    /**
     * 条件表达式
     */
    REQUIREMENT_EXPRESSION("EXPRESSION"),

    /**
     * 条件表达式是否预编译
     */
    REQUIREMENT_PRECOMPILE("PRECOMPILE"),

    /**
     * 条件优先级
     */
    REQUIREMENT_PRIORITY("PRIORITY"),

    /**
     * 条件展示物品
     */
    REQUIREMENT_ITEM("ITEM"),

    /**
     * 附魔特效
     */
    SHINY("SHINY"),

    /**
     * 坐标拷贝
     */
    SLOT_COPY("SLOT-COPY"),

    /**
     * 隐藏属性
     */
    HIDE_ATTRIBUTE("HIDE-ATTRIBUTE"),

    /**
     * 用于旧数据导入
     */
    DEPRECATED_DATA_VALUE("DATA-VALUE"),
    DEPRECATED_POSITION_X("POSITION-X"),
    DEPRECATED_POSITION_Y("POSITION-Y"),
    DEPRECATED_ENCHANTMENT("ENCHANTMENT"),
    DEPRECATED_PERMISSION_VIEW("VIEW-PERMISSION");

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    private final String text;

    IconSettings(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
