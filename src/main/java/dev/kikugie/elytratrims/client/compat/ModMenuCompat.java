package dev.kikugie.elytratrims.client.compat;
/*? if fabric {*/
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.kikugie.elytratrims.client.config.ConfigScreenProvider;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public @Nullable ConfigScreenFactory<? extends Screen> getModConfigScreenFactory() {
        var provider = ConfigScreenProvider.getScreen();
        return provider != null ? (ConfigScreenFactory<Screen>) provider::apply : null;
    }
}
/*?} */