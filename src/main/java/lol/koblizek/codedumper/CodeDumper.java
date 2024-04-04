package lol.koblizek.codedumper;

import lol.koblizek.codedumper.commands.ClassSummarizerCommand;
import lol.koblizek.codedumper.commands.DecompileCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CodeDumper extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("getclass").setExecutor(new ClassSummarizerCommand());
        getCommand("decompile").setExecutor(new DecompileCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static List<Class<?>> getSuperclasses(Class<?> type) {
        List<Class<?>> superclasses = new ArrayList<>();

        Class<?> superclass = type.getSuperclass();
        while (superclass != null) {
            superclasses.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return superclasses;
    }
}
