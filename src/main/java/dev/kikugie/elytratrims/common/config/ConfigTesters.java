package dev.kikugie.elytratrims.common.config;

import dev.kikugie.elytratrims.common.plugin.Tester;

public class ConfigTesters {
    public static class Trims implements Tester {
        @Override
        public boolean test(String mixinClassName) {
            return ServerConfigs.getConfig().addTrims;
        }
    }

    public static class Patterns implements Tester {
        @Override
        public boolean test(String mixinClassName) {
            return ServerConfigs.getConfig().addPatterns;
        }
    }

    public static class Glow implements Tester {
        @Override
        public boolean test(String mixinClassName) {
            return ServerConfigs.getConfig().addGlow;
        }
    }

    public static class CleanableElytra implements Tester {
        @Override
        public boolean test(String mixinClassName) {
            return ServerConfigs.getConfig().cleanableElytra;
        }
    }
}