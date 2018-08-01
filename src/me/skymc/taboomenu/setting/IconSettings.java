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
     * 附魔特效
     */
    SHINY("SHINY"),

    /**
     * 头颅皮肤
     */
    SKULL_OWNER("SKULL-OWNER"),

    /**
     * 旗帜图案
     */
    BANNER_PATTERN("BANNER-PATTERN"),

    /**
     * 隐藏属性
     */
    HIDE_ATTRIBUTE("HIDE-ATTRIBUTE"),

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
     * 消耗物品
     */
    REQUIRED_ITEM("REQUIRED-ITEM"),

    /**
     * 填充界面
     */
    FULL("FULL"),

    /**
     * 坐标拷贝
     */
    SLOT_COPY("SLOT-COPY"),

    /**
     * 点击权限
     */
    PERMISSION("PERMISSION"),

    /**
     * 点击权限:提示
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
     * 条件表达式:表达式
     */
    REQUIREMENT_EXPRESSION("EXPRESSION"),

    /**
     * 条件表达式:是否预编译
     */
    REQUIREMENT_PRECOMPILE("PRECOMPILE"),

    /**
     * 条件表达式:优先级
     */
    REQUIREMENT_PRIORITY("PRIORITY"),

    /**
     * 条件表达式:物品
     */
    REQUIREMENT_ITEM("ITEM"),

    /**
     * 特殊动作
     */
    ACTION("ACTION"),

    /**
     * 特殊动作:查看
     */
    ACTION_VIEW("VIEW"),

    /**
     * 特殊动作:是否预编译查看表达式
     */
    ACTION_VIEW_PRECOMPILE("VIEW-PRECOMPILE"),

    /**
     * 特殊动作:点击
     */
    ACTION_CLICK("CLICK"),

    /**
     * 特殊动作:是否预编译点击表达式
     */
    ACTION_CLICK_PRECOMPILE("CLICK-PRECOMPILE"),

    /**
     * 生成蛋类型
     */
    EGG_TYPE("EGG-TYPE"),

    /**
     * 药水类型
     */
    POTION_TYPE("POTION-TYPE"),

    /**
     * 头颅皮肤
     * 依赖插件:TabooLib
     */
    SKULL_TEXTURE("SKULL-TEXTURE"),

    /**
     * 冷却时间
     * 依赖插件:TabooLib
     */
    COOLDOWN("COOLDOWN"),

    /**
     * 冷却时间:物品
     * 依赖插件:TabooLib
     */
    COOLDOWN_ITEM("COOLDOWN-ITEM"),

    /**
     * 冷却时间:提示
     * 依赖插件:TabooLib
     */
    COOLDOWN_MESSAGE("COOLDOWN-MESSAGE"),

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
