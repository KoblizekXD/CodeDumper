package lol.koblizek.codedumper.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IContextSource;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class DecompileCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0)
            return false;
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You must be an operator to use this command.")
                    .color(NamedTextColor.RED));
            return true;
        }
        String target = args[0].replace(".", "/") + ".class";

        Fernflower fernflower = new Fernflower(new ComponentSaver(sender), IFernflowerPreferences.getDefaults(), new ComponentLogger(sender));
        fernflower.addSource(new ByteSource(bytesOf(target)));
        fernflower.decompileContext();
        return true;
    }

    public byte[] bytesOf(String resource) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                return null;
            }
            return stream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ByteSource implements IContextSource {

        private final byte[] bytes;

        public ByteSource(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public String getName() {
            return "Unknown class";
        }

        @Override
        public Entries getEntries() {
            return Entries.EMPTY;
        }

        @Override
        public InputStream getInputStream(String resource) throws IOException {
            return new ByteArrayInputStream(bytes);
        }

        @Override
        public IOutputSink createOutputSink(IResultSaver saver) {
            return new IOutputSink() {
                @Override
                public void begin() {

                }

                @Override
                public void acceptClass(String qualifiedName, String fileName, String content, int[] mapping) {
                    saver.saveClassFile("", qualifiedName, fileName, content, mapping);
                }

                @Override
                public void acceptDirectory(String directory) {

                }

                @Override
                public void acceptOther(String path) {

                }

                @Override
                public void close() throws IOException {

                }
            };
        }
    }


    public static class ComponentLogger extends IFernflowerLogger {

        private final CommandSender commandSender;

        public ComponentLogger(CommandSender sender) {
            this.commandSender = sender;
        }

        @Override
        public void writeMessage(String message, Severity severity) {
            commandSender.sendMessage(Component.text(message));
        }

        @Override
        public void writeMessage(String message, Severity severity, Throwable t) {
            commandSender.sendMessage(Component.text(message));
        }
    }

    public static class ComponentSaver implements IResultSaver {

        private final CommandSender sender;

        public ComponentSaver(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public void saveFolder(String path) {

        }

        @Override
        public void copyFile(String source, String path, String entryName) {

        }

        @Override
        public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
            Component component = Component.text("Decompiled source for: " + qualifiedName)
                    .color(NamedTextColor.GOLD)
                    .appendNewline().appendNewline()
                    .color(NamedTextColor.WHITE)
                    .append(Component.text(content));
            sender.sendMessage(component);
        }

        @Override
        public void createArchive(String path, String archiveName, Manifest manifest) {

        }

        @Override
        public void saveDirEntry(String path, String archiveName, String entryName) {

        }

        @Override
        public void copyEntry(String source, String path, String archiveName, String entry) {

        }

        @Override
        public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
            Component component = Component.text("Decompiled source for: " + qualifiedName)
                    .color(NamedTextColor.GOLD)
                    .appendNewline().appendNewline()
                    .color(NamedTextColor.WHITE)
                    .append(Component.text(content));
            sender.sendMessage(component);
        }

        @Override
        public void closeArchive(String path, String archiveName) {

        }
    }
}
