package inoculate.injector.jar;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassNodeVisitor {

    public static ClassNode acceptNode(byte[] bytes) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);

        reader.accept(node, 0);

        return node;
    }

    public static List<ClassNode> fromClass(Class<?> c) throws IOException {
        final List<ClassNode> nodes = new ArrayList<>();

        final String name = c.getName().replace('.', '/').concat(".class");
        final InputStream stream = c.getClassLoader().getResourceAsStream(name);
        final byte[] bytes = IOUtils.toByteArray(Objects.requireNonNull(stream));

        stream.close();

        final ClassNode node = acceptNode(bytes);
        nodes.add(node);

        node.innerClasses.forEach(innerClassNode -> {
            if (innerClassNode.name.startsWith("java")) {
                return;
            }

            try {
                final InputStream innerStream = c.getClassLoader().getResourceAsStream(innerClassNode.name + ".class");
                final byte[] innerBytes = IOUtils.toByteArray(Objects.requireNonNull(innerStream));

                innerStream.close();

                nodes.add(acceptNode(innerBytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return nodes;
    }

}
