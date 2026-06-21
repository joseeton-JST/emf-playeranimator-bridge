package com.joseetoon.emfplayeranimatorbridge.compat;

import net.minecraft.client.model.geom.ModelPart;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelPartTreeHelperTest {
    @Test
    void addsRootAndAllDescendants() {
        ModelPart grandChild = new ModelPart(List.of(), Map.of());
        ModelPart child = new ModelPart(List.of(), Map.of("grandChild", grandChild));
        ModelPart root = new ModelPart(List.of(), Map.of("child", child));

        Set<ModelPart> partsToPause = new HashSet<>();
        ModelPartTreeHelper.addPartTree(partsToPause, root);

        assertEquals(3, partsToPause.size());
        assertTrue(partsToPause.contains(root));
        assertTrue(partsToPause.contains(child));
        assertTrue(partsToPause.contains(grandChild));
    }

    @Test
    void ignoresNullRoot() {
        Set<ModelPart> partsToPause = new HashSet<>();

        ModelPartTreeHelper.addPartTree(partsToPause, null);

        assertTrue(partsToPause.isEmpty());
    }
}
