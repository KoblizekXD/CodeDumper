package lol.koblizek.codedumper.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ClassExistsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;

        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You don't have permission to use this command.").color(NamedTextColor.RED));
            return true;
        }

        for (String arg : args) {
            try {
                Class.forName(arg);
                sender.sendMessage(Component.text("Class " + arg + " exists.").color(NamedTextColor.GREEN));
            } catch (ClassNotFoundException e) {
                sender.sendMessage(Component.text("Class " + arg + " does not exist.").color(NamedTextColor.RED));
            }
        }

        return true;
    }
}
