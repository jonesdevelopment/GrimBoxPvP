package ac.grim.grimac.manager.player.features.types;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.player.GrimPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class GrimFeature {

    private final String name;

    public abstract void setEnabled(GrimPlayer player, boolean enabled);

    public abstract boolean isEnabled(GrimPlayer player);

    public abstract boolean isEnabledInConfig(GrimPlayer player, ConfigManager config);

}
