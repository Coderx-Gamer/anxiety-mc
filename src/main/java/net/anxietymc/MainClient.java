package net.anxietymc;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class MainClient implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    // game init
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initialization of AnxietyMC complete.");
    }
}
