package dev.kikugie.elytratrims.client.resource;

import dev.kikugie.elytratrims.client.access.ElytraSourceAccessor;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class ETResourceListener {
    private static TrimEntries trims = new TrimEntries();

    public static void reset() {
        trims = new TrimEntries();
    }

    public static void addTrims(@NotNull List<Identifier> patterns, Identifier palette, Map<String, Identifier> materials) {
        List<Identifier> elytraPatterns = new ArrayList<>(patterns.size());
        for (Identifier texture : patterns) {
            String path = texture.getPath();
            if (path.contains("armor") && !path.contains("leggings"))
                elytraPatterns.add(texture.withPath(it -> it.replaceFirst("armor", "elytra")));
        }
        if (!elytraPatterns.isEmpty())
            trims.add(elytraPatterns, palette, materials);
    }

    @SuppressWarnings("DataFlowIssue")
    public static @NotNull List<AtlasSource> getTrims() {
        List<AtlasSource> sources = new ArrayList<>();
        for (Identifier palette : trims) {
            var textures = trims.getPatterns(palette);
            var materials = trims.getMaterials(palette);
            var source = new PalettedPermutationsAtlasSource(textures, palette, materials);
            ((ElytraSourceAccessor) source).elytra_trims$ignoreListener();
            sources.add(source);
        }
        return sources;
    }


    private static class TrimEntries implements Iterable<Identifier> {
        private final Map<Identifier, Set<Identifier>> paletteToPatterns = new HashMap<>();
        private final Map<Identifier, Set<Material>> paletteToMaterials = new HashMap<>();

        public void add(List<Identifier> patterns, Identifier palette, @NotNull Map<String, Identifier> materials) {
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

        public @Unmodifiable List<Identifier> getPatterns(Identifier palette) {
            return List.copyOf(this.paletteToPatterns.get(palette));
        }

        @SuppressWarnings("unchecked")
        public @Unmodifiable Map<String, Identifier> getMaterials(Identifier palette) {
            return Map.ofEntries(this.paletteToMaterials.get(palette).stream().map(Material::toEntry).toArray(Map.Entry[]::new));
        }
    }

    private record Material(String id, Identifier path) {
        public Map.Entry<String, Identifier> toEntry() {
            return Map.entry(this.id, this.path);
        }
    }
}