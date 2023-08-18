package dev.kikugie.elytratrims.resource;

import com.google.common.base.Preconditions;
import dev.kikugie.elytratrims.access.ElytraSourceAccessor;
import dev.kikugie.elytratrims.util.LogWrapper;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ETResourceListener {
    private static final LogWrapper LOGGER = LogWrapper.of("ET Resource Listener");
    private static ETResourceListener instance;
    private final TrimEntries trims = new TrimEntries();


    public static void init() {
        instance = new ETResourceListener();
    }

    public static void close() {
        checkActive();
        instance = null;
    }

    public static void addTrims(List<Identifier> patterns, Identifier palette, Map<String, Identifier> materials) {
        checkActive();
        instance.trims.add(patterns, palette, materials);
    }

    public static List<AtlasSource> getTrims() {
        checkActive();
        List<AtlasSource> sources = new ArrayList<>();
        for (Identifier palette : instance.trims) {
            PalettedPermutationsAtlasSource atlasSource = new PalettedPermutationsAtlasSource(
                    instance.trims.getPatterns(palette),
                    palette,
                    instance.trims.getMaterials(palette));
            ((ElytraSourceAccessor) atlasSource).elytra_trims$ignoreListener();
            sources.add(atlasSource);
        }
        return sources;
    }

    private static void checkActive() {
        Preconditions.checkState(instance != null, "Resource listener not initialized");
    }

    private static class TrimEntries implements Iterable<Identifier> {
        private final Map<Identifier, Set<Identifier>> paletteToPatterns = new HashMap<>();
        private final Map<Identifier, Set<Material>> paletteToMaterials = new HashMap<>();

        public void add(List<Identifier> patterns, Identifier palette, Map<String, Identifier> materials) {
            Set<Identifier> existingPatterns = this.paletteToPatterns.computeIfAbsent(palette, id -> new HashSet<>());
            existingPatterns.addAll(patterns);

            Set<Material> existingMaterials = this.paletteToMaterials.computeIfAbsent(palette, id -> new HashSet<>());
            materials.forEach((id, material) -> existingMaterials.add(new Material(id, material)));
        }

        @NotNull
        @Override
        public Iterator<Identifier> iterator() {
            return this.paletteToPatterns.keySet().iterator();
        }

        public List<Identifier> getPatterns(Identifier palette) {
            return List.copyOf(this.paletteToPatterns.get(palette));
        }

        @SuppressWarnings("unchecked")
        public Map<String, Identifier> getMaterials(Identifier palette) {
            return Map.ofEntries(this.paletteToMaterials.get(palette).stream().map(Material::toEntry).toArray(Map.Entry[]::new));
        }
    }

    private record Material(String id, Identifier path) {
        public Map.Entry<String, Identifier> toEntry() {
            return Map.entry(this.id, this.path);
        }
    }
}
