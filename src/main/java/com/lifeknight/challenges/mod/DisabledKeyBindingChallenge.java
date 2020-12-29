package com.lifeknight.challenges.mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.util.HashMap;
import java.util.Map;

class DisabledKeyBindingChallenge extends Challenge {
    private final Map<KeyBinding, Integer> originalKeyBindings = new HashMap<>();

    public DisabledKeyBindingChallenge(String name, String description, KeyBinding... keyBindings) {
        super(name, description);
        for (KeyBinding keyBinding : keyBindings) {
            this.originalKeyBindings.put(keyBinding, keyBinding.getKeyCode());
        }
    }

    @Override
    public void apply() {
        for (KeyBinding keyBinding : this.originalKeyBindings.keySet()) {
            if (keyBinding.isKeyDown()) {
                Minecraft.getMinecraft().setIngameNotInFocus();
                Minecraft.getMinecraft().setIngameFocus();
            }
            keyBinding.setKeyCode(0x00);
        }
        super.apply();
    }

    @Override
    public void unApply() {
        this.originalKeyBindings.forEach(KeyBinding::setKeyCode);
        super.unApply();
    }
}
