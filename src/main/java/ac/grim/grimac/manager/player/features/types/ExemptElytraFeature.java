package ac.grim.grimac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.player.GrimPlayer;

public class ExemptElytraFeature extends GrimFeature {

    public ExemptElytraFeature() {
        super("ExemptElytra");
    }

    @Override
    public void setEnabled(GrimPlayer player, boolean enabled) {
        player.setExemptElytra(enabled);
    }

    @Override
    public boolean isEnabled(GrimPlayer player) {
        return player.isExemptElytra();
    }

    @Override
    public boolean isEnabledInConfig(GrimPlayer player, ConfigManager config) {
        return config.getBooleanElse("exempt-elytra", false);
    }

}
