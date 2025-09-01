package gungun974.uselessnumerical;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UselessNumericalMod implements ModInitializer {
    public static final String MOD_ID = "uselessnumerical";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("UselessNumerical initialized.");
    }
}
