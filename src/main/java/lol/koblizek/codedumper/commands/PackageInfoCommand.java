package lol.koblizek.codedumper.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PackageInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;

        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You don't have permission to use this command.").color(NamedTextColor.RED));
            return true;
        }

        for (String arg : args) {
            try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(arg.replaceAll("[.]", "/")) ;
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                for (String s : reader.lines().toList()) {
                    System.out.println(s);
                    sender.sendMessage(Component.text(s));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    public static void main(String[] args) {
        try (InputStream stream = PackageInfoCommand.class.getClassLoader().getResourceAsStream("lol/koblizek/codedumper/commands") ;
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            for (String s : reader.lines().toList()) {
                System.out.println(s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
