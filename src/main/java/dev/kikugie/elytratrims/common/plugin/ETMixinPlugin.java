package dev.kikugie.elytratrims.common.plugin;

import dev.kikugie.elytratrims.common.ETReference;
import dev.kikugie.elytratrims.common.ETServer;
import dev.kikugie.elytratrims.common.plugin.RequirePlatform.Loader;
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
//        ETServer.mixinInit();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        AnnotationNode modRequirement = getAnnotation(mixinClassName, RequireMod.class);
        boolean modResult = modRequirement == null || ModStatus.isLoading(Annotations.getValue(modRequirement, "mod"));
        if (!modResult) return false;

        AnnotationNode testerRequirement = getAnnotation(mixinClassName, RequireTest.class);
        boolean testerResult = testerRequirement == null || runTester(Annotations.getValue(testerRequirement, "tester"), mixinClassName);
        if (!testerResult) return false;

        AnnotationNode platformRequirement = getAnnotation(mixinClassName, RequirePlatform.class);
        if (platformRequirement == null) return true;
        Loader loader = Annotations.getValue(platformRequirement, "loader");
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