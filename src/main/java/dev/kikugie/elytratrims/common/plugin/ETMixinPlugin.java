package dev.kikugie.elytratrims.common.plugin;

import dev.kikugie.elytratrims.common.ETReference;
import dev.kikugie.elytratrims.common.config.ServerConfigs;
import dev.kikugie.elytratrims.common.plugin.RequirePlatform.Loader;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class ETMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        ServerConfigs.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (shouldApply(mixinClassName)) return true;
        String shortName = StringUtils.substringAfter(mixinClassName, "mixin.");
        if (!shortName.startsWith("compat.")) // Reduce unneeded spam
            ETReference.LOGGER.info("Disabled mixin %s".formatted(shortName));
        return false;
    }

    private boolean shouldApply(String mixin) {
        AnnotationNode mixinConfigurable = getAnnotation(mixin, MixinConfigurable.class);
        boolean configResult = mixinConfigurable == null || !ServerConfigs.getMixinConfig().contains(mixin);
        if (!configResult) return false;

        AnnotationNode modRequirement = getAnnotation(mixin, RequireMod.class);
        boolean modResult = modRequirement == null || ModStatus.isLoading(Annotations.getValue(modRequirement));
        if (!modResult) return false;

        AnnotationNode testerRequirement = getAnnotation(mixin, RequireTest.class);
        boolean testerResult = testerRequirement == null || runTester(Annotations.getValue(testerRequirement), mixin);
        if (!testerResult) return false;

        AnnotationNode platformRequirement = getAnnotation(mixin, RequirePlatform.class);
        if (platformRequirement == null) return true;
        Loader loader = Annotations.getValue(platformRequirement);
        return (loader == Loader.FABRIC && ModStatus.isFabric) || (loader == Loader.FORGE && !ModStatus.isFabric);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
    private boolean runTester(Type type, String mixinClassName) {
        try {
            Class<?> clazz = Class.forName(type.getClassName());
            if (clazz.isInterface()) {
                ETReference.LOGGER.error("Tester class {} should be implemented", clazz.getName());
                return false;
            }
            Tester tester = (Tester) clazz.getConstructor().newInstance();
            return tester.test(mixinClassName);
        } catch (Exception e) {
            ETReference.LOGGER.error("Failed to instantiate tester from class {}: {}", type.getClassName(), e);
            return false;
        }
    }

    @Nullable
    private AnnotationNode getAnnotation(String className, Class<? extends Annotation> annotation) {
        try {
            ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
            return Annotations.getVisible(classNode, annotation);
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }
}