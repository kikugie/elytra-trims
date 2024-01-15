package dev.kikugie.elytratrims.client.compat;
/*? if fabric {*/
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.kikugie.elytratrims.client.config.ConfigScreenProvider;
import org.jetbrains.annotations.Nullable;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public @Nullable ConfigScreenFactory<?> getModConfigScreenFactory() {
        var provider = ConfigScreenProvider.getScreen();
        return provider != null ? provider::apply : null;
    }
}
/*?} */