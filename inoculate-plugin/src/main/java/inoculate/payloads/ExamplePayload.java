package inoculate.payloads;

import inoculate.injector.Payload;
import org.bukkit.plugin.Plugin;

public class ExamplePayload implements Payload {
    @Override
    public void onLoad(Plugin plugin) {

    }

    @Override
    public void onEnable(Plugin plugin) {
        plugin.getLogger().info("Example payload injected");
    }
}

