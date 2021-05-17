package inoculate.injector.jar;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class JarReader {

    private final JarFile jar;

    public JarReader(File file) throws IOException {
        this.jar = new JarFile(file);
    }

    public JarReader(JarFile jar) {
        this.jar = jar;
    }

    public Map<String, byte[]> read() {
        final Map<String, byte[]> entries = new HashMap<>();

        jar.stream().forEach(entry -> {
            try {
                if (entry.isDirectory()) return;

                final InputStream stream = jar.getInputStream(entry);
                final byte[] bytes = IOUtils.toByteArray(stream);

                entries.put(entry.getName(), bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return entries;
    }

    public JarFile getJar() {
        return jar;
    }
}
