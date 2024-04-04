package lol.koblizek.codedumper.commands;

import lol.koblizek.codedumper.CodeDumper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassSummarizerCommand implements CommandExecutor {

    // Color for methods: TextColor.fromHexString("#3a7cdd")

    public static Component convertField(Field field) {
        Component modifiers = Component.text(Modifier.toString(field.getModifiers()))
                .color(NamedTextColor.LIGHT_PURPLE);
        Component name = Component.text(field.getDeclaringClass().getSimpleName())
                .color(TextColor.fromHexString("#e4bd6d")).append(Component.text(".").color(NamedTextColor.WHITE))
                .append(Component.text(field.getName()).color(TextColor.fromHexString("#de6c74")));
        return modifiers.appendSpace().append(name);
    }

    public static Component appendComponents(Component... component) {
        Component result = Component.empty();
        for (Component c : component) {
            result = result.append(Component.text("  ")).append(c).appendNewline();
        }
        return result;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0)
            return false;
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("Only operators can use this command")
                    .color(NamedTextColor.RED));
            return false;
        }

        for (String arg : args) {
            try {
                Class<?> type = Class.forName(arg);
                Component[] fields = Arrays.stream(type.getDeclaredFields())
                        .map(ClassSummarizerCommand::convertField)
                        .toArray(Component[]::new);

                sender.sendMessage(Component.newline().append(Component.text("Class: ")
                        .color(NamedTextColor.GREEN)
                        .append(Component.text(type.getName())))
                        .color(NamedTextColor.WHITE).appendNewline()
                        .append(Component.text("Superclasses: ")
                                .color(NamedTextColor.YELLOW)
                                .append(Component.text(CodeDumper.getSuperclasses(type)
                                        .stream().map(Class::getName).collect(Collectors.joining(", "))))
                                .color(NamedTextColor.WHITE)).appendNewline()
                        .append(Component.text("Interfaces: ")
                                .color(NamedTextColor.YELLOW)
                                .append(Component.text(Arrays.stream(type.getInterfaces()).map(Class::getName)
                                        .collect(Collectors.joining(", "))))
                                .color(NamedTextColor.WHITE)).appendNewline().appendNewline()
                        .append(Component.text("Field summary: ").appendNewline()
                                .append(appendComponents(fields))));
            } catch (ClassNotFoundException e) {
                sender.sendMessage(MiniMessage.miniMessage()
                        .deserialize("<red>Class <bold>" + arg + "</bold> was not found in class loader: " + this.getClass().getClassLoader().getName() + "</red>"));
                return false;
            }
        }

        return true;
    }
}
