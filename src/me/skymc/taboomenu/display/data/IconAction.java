package me.skymc.taboomenu.display.data;

import javax.script.CompiledScript;
import java.util.Objects;

/**
 * @Author sky
 * @Since 2018-06-19 22:17
 */
public class IconAction {

    private String viewAction;
    private String clickAction;

    private CompiledScript viewActionScript;
    private CompiledScript clickActionScript;

    private boolean viewPrecompile;
    private boolean clickPrecompile;

    public String getViewAction() {
        return viewAction;
    }

    public void setViewAction(String viewAction) {
        this.viewAction = viewAction;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public CompiledScript getViewActionScript() {
        return viewActionScript;
    }

    public void setViewActionScript(CompiledScript viewActionScript) {
        this.viewActionScript = viewActionScript;
    }

    public CompiledScript getClickActionScript() {
        return clickActionScript;
    }

    public void setClickActionScript(CompiledScript clickActionScript) {
        this.clickActionScript = clickActionScript;
    }

    public boolean isViewPrecompile() {
        return viewPrecompile;
    }

    public void setViewPrecompile(boolean viewPrecompile) {
        this.viewPrecompile = viewPrecompile;
    }

    public boolean isClickPrecompile() {
        return clickPrecompile;
    }

    public void setClickPrecompile(boolean clickPrecompile) {
        this.clickPrecompile = clickPrecompile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IconAction)) {
            return false;
        }
        IconAction that = (IconAction) o;
        return isViewPrecompile() == that.isViewPrecompile() &&
                isClickPrecompile() == that.isClickPrecompile() &&
                Objects.equals(getViewAction(), that.getViewAction()) &&
                Objects.equals(getClickAction(), that.getClickAction()) &&
                Objects.equals(getViewActionScript(), that.getViewActionScript()) &&
                Objects.equals(getClickActionScript(), that.getClickActionScript());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getViewAction(), getClickAction(), getViewActionScript(), getClickActionScript(), isViewPrecompile(), isClickPrecompile());
    }
}
