package inoculate.injector.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class JarWriter {

    private JarOutputStream jos;

    public JarWriter(File file) throws IOException {
        this.jos = new JarOutputStream(new FileOutputStream(file));
    }

    public void write(Map<String, byte[]> entries) throws IOException {
        for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
            write(entry.getKey(), entry.getValue());
        }
    }

    public void write(String name, byte[] bytes) throws IOException {
        jos.putNextEntry(new ZipEntry(name));
        jos.write(bytes);
    }

    public void close() throws IOException {
        jos.close();
        jos = null;
    }

    public JarOutputStream getJarOutputStream() {
        return jos;
    }
}
