package inoculate.injector.jar;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Map;

public interface Injector {

    void injectClass(ClassNode classNode);
    void injectClass(Class<?> c) throws IOException;
    void injectMethod(String owner, MethodNode methodNode) throws ClassNotFoundException;
    void injectMethodInstructions(String owner, String name, String desc, InsnList instructions) throws ClassNotFoundException;
    Map<String, byte[]> save();

}
