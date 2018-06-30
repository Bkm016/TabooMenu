package me.skymc.taboomenu.handler;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import me.skymc.taboomenu.TabooMenu;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Objects;

/**
 * @Author sky
 * @Since 2018-06-02 22:48
 */
public class ScriptHandler {

    private static ScriptEngine scriptEngine;
    private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static HashMap<String, CompiledScript> variables = new HashMap<>();

    public static void inst() {
        try {
            NashornScriptEngineFactory factory = (NashornScriptEngineFactory) scriptEngineManager.getEngineFactories().stream().filter(factories -> "Oracle Nashorn".equalsIgnoreCase(factories.getEngineName())).findFirst().orElse(null);
            scriptEngine = Objects.requireNonNull(factory).getScriptEngine("-doe", "--global-per-engine");
        } catch (Exception ignored) {
            scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        }
    }

    public static void loadVariables() {
        for (String name : TabooMenu.getInst().getConfig().getConfigurationSection("Variables").getKeys(false)) {
            CompiledScript compiledScript = compile(TabooMenu.getInst().getConfig().getString("Variables." + name));
            if (compiledScript != null) {
                variables.put(name, compiledScript);
            }
        }
    }

    public static CompiledScript compile(String script) {
        try {
            Compilable compilable = (Compilable) scriptEngine;
            return compilable.compile(script);
        } catch (Exception e) {
            TabooMenu.getTLogger().error("JavaScript &c" + script + "&4 Compile Failed: &c" + e.toString());
            return null;
        }
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public static ScriptEngineManager getScriptEngineManager() {
        return scriptEngineManager;
    }

    public static HashMap<String, CompiledScript> getVariables() {
        return variables;
    }
}
