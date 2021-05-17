package inoculate.injector.jar;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JarFileMapConverter {

    public static List<ClassNode> fromMap(Map<String, byte[]> entries) {
        final List<ClassNode> nodes = new ArrayList<>();

        entries.forEach((name, bytes) -> {
            if (!name.endsWith(".class"))
                return;

            final ClassNode node = ClassNodeVisitor.acceptNode(bytes);
            nodes.add(node);
        });

        return nodes;
    }

    public static Map<String, byte[]> toMap(List<ClassNode> nodes, int flags) {
        final Map<String, byte[]> entries = new HashMap<>();

        for (ClassNode node : nodes) {
            final ClassWriter writer = new ClassWriter(flags);
            node.accept(new CheckClassAdapter(writer, true));

            final String name = node.name.concat(".class");
            final byte[] bytes = writer.toByteArray();

            entries.put(name, bytes);
        }

        return entries;
    }

}
