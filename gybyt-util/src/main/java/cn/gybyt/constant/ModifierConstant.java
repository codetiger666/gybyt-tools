package cn.gybyt.constant;

/**
 * 修饰符静态变量
 *
 * @program: utils
 * @classname: ModifierConstant
 * @author: codetiger
 * @create: 2023/1/8 17:44
 **/
public enum ModifierConstant {
    /**
     * 公共变量
     */
    PUBLIC("public", 1),
    /**
     * 私有变量
     */
    PRIVATE("private", 2),
    /**
     * 保护变量
     */
    PROTECT("protected", 4),
    /**
     * 静态变量
     */
    STATIC("static", 8),
    /**
     * 不可变变量
     */
    FINAL("final", 10);

    private final String modifier;
    private final Integer key;

    ModifierConstant(String modifier, Integer key) {
        this.modifier = modifier;
        this.key = key;
    }

    public String modifier() {
        return modifier;
    }

    public Integer key() {
        return key;
    }
}
