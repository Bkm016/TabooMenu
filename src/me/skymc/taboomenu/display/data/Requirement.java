package me.skymc.taboomenu.display.data;

import me.skymc.taboomenu.display.Icon;
import me.skymc.taboomenu.handler.JavaScriptHandler;

import javax.script.CompiledScript;
import java.util.Objects;

/**
 * @Author sky
 * @Since 2018-06-02 23:12
 */
public class Requirement {

    private final Icon icon;
    private final int priority;
    private final String expression;
    private final boolean preCompile;
    private final CompiledScript compiledScript;

    public Requirement(Icon icon, int priority, String expression, boolean preCompile) {
        this.icon = icon;
        this.priority = priority;
        this.expression = expression;
        this.preCompile = preCompile;
        if (this.preCompile) {
            compiledScript = JavaScriptHandler.compile(expression);
        } else {
            compiledScript = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Requirement)) {
            return false;
        }
        Requirement that = (Requirement) o;
        return getPriority() == that.getPriority() &&
                isPreCompile() == that.isPreCompile() &&
                Objects.equals(getIcon(), that.getIcon()) &&
                Objects.equals(getExpression(), that.getExpression()) &&
                Objects.equals(getCompiledScript(), that.getCompiledScript());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIcon(), getPriority(), getExpression(), isPreCompile(), getCompiledScript());
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public int getPriority() {
        return priority;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getExpression() {
        return expression;
    }

    public boolean isPreCompile() {
        return preCompile;
    }

    public CompiledScript getCompiledScript() {
        return compiledScript;
    }
}
