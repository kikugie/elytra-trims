package dev.kikugie.elytratrims.client.access;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ElytraRotationAccessor {
    Vector3f elytra_trims$UP = new Vector3f(0, 1, 0);
    Quaternionf elytra_trims$getVector();
    boolean elytra_trims$isElytra();
    void elytra_trims$setElytra(boolean value);

    default Quaternionf elytra_trims$rotateElytra(Quaternionf source) {
        if (elytra_trims$isElytra()) {
            Quaternionf vec = elytra_trims$getVector();
            source.rotateAxis((float) Math.PI, elytra_trims$UP, vec);
            return vec;
        } else return source;
    }
}
