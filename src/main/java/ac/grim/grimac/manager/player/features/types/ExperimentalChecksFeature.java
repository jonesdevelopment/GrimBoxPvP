package ac.grim.grimac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.player.GrimPlayer;

public class ExperimentalChecksFeature extends GrimFeature {

    public ExperimentalChecksFeature() {
        super("ExperimentalChecks");
    }

    @Override
    public void setEnabled(GrimPlayer player, boolean enabled) {
        player.setExperimentalChecks(enabled);
    }

    @Override
    public boolean isEnabled(GrimPlayer player) {
        return player.isExperimentalChecks();
    }

    @Override
    public boolean isEnabledInConfig(GrimPlayer player, ConfigManager config) {
        return config.getBooleanElse("experimental-checks", false);
    }

}
