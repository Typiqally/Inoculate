package inoculate.injector.jar;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JarInjector implements Injector {

    private Map<String, byte[]> entries;
    private List<ClassNode> nodes = new ArrayList<>();

    public JarInjector(Map<String, byte[]> entries) {
        this.entries = entries;
    }

    public void injectClass(ClassNode classNode) {
        nodes.add(classNode);
    }

    public void injectClass(Class<?> c) throws IOException {
        final List<ClassNode> nodes = ClassNodeVisitor.fromClass(c);

        nodes.forEach(this::injectClass);
    }

    @Override
    public void injectMethod(String owner, MethodNode methodNode) throws ClassNotFoundException {
        final ClassNode classNode = getClassNodeFromName(owner);
        classNode.methods.add(methodNode);

        injectClass(classNode);
    }

    @Override
    public void injectMethodInstructions(String owner, String name, String desc, InsnList instructions) throws ClassNotFoundException {
        final ClassNode classNode = getClassNodeFromName(owner);
        final MethodNode methodNode = classNode.methods.stream()
                .filter(method -> method.name.equals(name) && method.desc.equals(desc))
                .findFirst()
                .orElseThrow(ClassNotFoundException::new);

        methodNode.instructions.insert(instructions);

        injectClass(classNode);
    }

    private ClassNode getClassNodeFromName(String name) throws ClassNotFoundException {
        if (!entries.containsKey(name)) {
            throw new ClassNotFoundException();
        }

        final byte[] bytes = entries.get(name);
        return ClassNodeVisitor.acceptNode(bytes);
    }

    @Override
    public Map<String, byte[]> save() {
        nodes.forEach(node -> {
            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(new CheckClassAdapter(writer, true));

            final byte[] bytes = writer.toByteArray();

            entries.put(node.name.concat(".class"), bytes);
        });

        return entries;
    }

    public Map<String, byte[]> getEntries() {
        return entries;
    }
}
