package inoculate.injector;

import org.bukkit.plugin.Plugin;

public interface Payload {

    void onLoad(Plugin plugin);
    void onEnable(Plugin plugin);

}
