package me.skymc.taboomenu.setting;

/**
 * @Author sky
 * @Since 2018-06-05 19:49
 */
public enum IconSettings {

    /**
     * 物品序号
     */
    ID("id|material"),

    /**
     * 物品数量
     */
    AMOUNT("amount"),

    /**
     * 物品名称
     */
    NAME("(display-)?name"),

    /**
     * 物品描述
     */
    LORE("lore(s)?"),

    /**
     * 物品颜色
     */
    COLOR("(armor-)?color"),

    /**
     * 附魔特效
     */
    SHINY("shiny|glow|enchant(ed|ment)?"),

    /**
     * 头颅皮肤
     */
    SKULL_OWNER("skull(-owner)?"),

    /**
     * 旗帜图案
     */
    BANNER_PATTERN("banner(-pattern)?"),

    /**
     * 隐藏属性
     */
    HIDE_ATTRIBUTE("hide-(attribute|nbt)"),

    /**
     * 触发命令
     */
    COMMAND("command(s)?"),

    /**
     * 消耗金币
     */
    PRICE("price|money|balance"),

    /**
     * 消耗点券
     */
    POINTS("point(s)?"),

    /**
     * 消耗等级
     */
    LEVELS("level(s)?"),

    /**
     * 消耗物品
     */
    REQUIRED_ITEM("(require(d)?|requirement)-item(s)?"),

    /**
     * 填充界面
     */
    FULL("(slot-)?full"),

    /**
     * 坐标拷贝
     */
    SLOT_COPY("(slot-)?copy"),

    /**
     * 点击权限
     */
    PERMISSION("permission"),

    /**
     * 点击权限:提示
     */
    PERMISSION_MESSAGE("permission-message"),

    /**
     * 显示权限
     */
    PERMISSION_VIEW("permission-view|view-permission"),

    /**
     * 条件扩展
     */
    REQUIREMENT("require(d)?|requirement"),

    /**
     * 条件表达式:表达式
     */
    REQUIREMENT_EXPRESSION("expression"),

    /**
     * 条件表达式:是否预编译
     */
    REQUIREMENT_PRECOMPILE("precompile"),

    /**
     * 条件表达式:优先级
     */
    REQUIREMENT_PRIORITY("priority"),

    /**
     * 条件表达式:物品
     */
    REQUIREMENT_ITEM("item"),

    /**
     * 特殊动作
     */
    ACTION("action"),

    /**
     * 特殊动作:查看
     */
    ACTION_VIEW("view"),

    /**
     * 特殊动作:是否预编译查看表达式
     */
    ACTION_VIEW_PRECOMPILE("(view-)?precompile"),

    /**
     * 特殊动作:点击
     */
    ACTION_CLICK("click"),

    /**
     * 特殊动作:是否预编译点击表达式
     */
    ACTION_CLICK_PRECOMPILE("(click-)?precompile"),

    /**
     * 生成蛋类型
     */
    EGG_TYPE("(egg|mob|entity)(-type)?"),

    /**
     * 药水类型
     */
    POTION_TYPE("potion(-type)?"),

    /**
     * 头颅皮肤
     * 依赖插件:TabooLib
     */
    SKULL_TEXTURE("skull-texture"),

    /**
     * 冷却时间
     * 依赖插件:TabooLib
     */
    COOLDOWN("cooldown"),

    /**
     * 冷却时间:物品
     * 依赖插件:TabooLib
     */
    COOLDOWN_ITEM("cooldown-item"),

    /**
     * 冷却时间:提示
     * 依赖插件:TabooLib
     */
    COOLDOWN_MESSAGE("cooldown-message"),

    /**
     * 用于旧数据导入
     */
    DEPRECATED_DATA_VALUE("data(-value)?"),
    DEPRECATED_POSITION_X("(position-|location-)?x"),
    DEPRECATED_POSITION_Y("(position-|location-)?y");

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
