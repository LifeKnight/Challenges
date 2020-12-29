package com.lifeknight.challenges.mod;

import com.lifeknight.challenges.utilities.Miscellaneous;
import com.lifeknight.challenges.variables.SmartBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public abstract class Challenge {
    public static final List<Challenge> usableChallenges = new ArrayList<>();

    private final SmartBoolean smartBoolean;

    public static void beforeConfigurationLoad() {
        new Challenge("Inverted Mouse", "Your mouse is inverted.") {
            @Override
            public void apply() {
                Minecraft.getMinecraft().gameSettings.invertMouse = true;
                super.apply();
            }

            @Override
            public void unApply() {
                Minecraft.getMinecraft().gameSettings.invertMouse = false;
                super.unApply();
            }
        };

        new Challenge("Switch Mouse Buttons", "Left-click is right click, right-click is left-click.") {
            @Override
            public void apply() {
                Minecraft.getMinecraft().gameSettings.keyBindAttack.setKeyCode(-99);
                Minecraft.getMinecraft().gameSettings.keyBindUseItem.setKeyCode(-100);
                super.apply();
            }

            @Override
            public void unApply() {
                Minecraft.getMinecraft().gameSettings.keyBindAttack.setKeyCode(-100);
                Minecraft.getMinecraft().gameSettings.keyBindUseItem.setKeyCode(-99);
                super.unApply();
            }
        };

        new Challenge("Smooth Camera", "Smooth camera.") {
            @Override
            public void apply() {
                Minecraft.getMinecraft().gameSettings.smoothCamera = true;
                super.apply();
            }

            @Override
            public void unApply() {
                Minecraft.getMinecraft().gameSettings.smoothCamera = false;
                super.unApply();
            }
        };

        new Challenge("F1 Challenge", "No GUI.") {
            @Override
            public void apply() {
                Minecraft.getMinecraft().gameSettings.hideGUI = true;
                super.apply();
            }

            @Override
            public void unApply() {
                Minecraft.getMinecraft().gameSettings.hideGUI = false;
                super.unApply();
            }
        };

        new DisabledKeyBindingChallenge("No Forward Key", "You can't move forward.", Minecraft.getMinecraft().gameSettings.keyBindForward);
        new DisabledKeyBindingChallenge("No Backwards Key", "You can't move backwards.", Minecraft.getMinecraft().gameSettings.keyBindBack);
        new DisabledKeyBindingChallenge("No Sprinting", "You can't sprint.", Minecraft.getMinecraft().gameSettings.keyBindSprint);
        new DisabledKeyBindingChallenge("No Left-Click", "You can't left click.", Minecraft.getMinecraft().gameSettings.keyBindAttack);
        new DisabledKeyBindingChallenge("No Right-Click", "You can't right click.", Minecraft.getMinecraft().gameSettings.keyBindUseItem);
        new DisabledKeyBindingChallenge("No Jumping", "You can't right jump.", Minecraft.getMinecraft().gameSettings.keyBindJump);
        new DisabledKeyBindingChallenge("No Strafing", "You can't right strafe.", Minecraft.getMinecraft().gameSettings.keyBindLeft, Minecraft.getMinecraft().gameSettings.keyBindRight);
        new DisabledKeyBindingChallenge("No Sneaking", "You can't sneak.", Minecraft.getMinecraft().gameSettings.keyBindSneak);
    }

    public static void onConfigurationLoaded() {
        usableChallenges.removeIf(challenge -> !challenge.smartBoolean.getValue());
    }

    public Challenge(String name, String description) {
        usableChallenges.add(this);
        this.smartBoolean = new SmartBoolean(name, "Built-in Challenges", true, description) {
            @Override
            public void onSetValue() {
                if (this.getValue()) {
                    if (!usableChallenges.contains(Challenge.this)) {
                        usableChallenges.add(Challenge.this);
                    }
                } else {
                    usableChallenges.remove(Challenge.this);
                }
            }
        };
    }

    public static Challenge getRandomChallenge(Challenge currentChallenge) {
        return Miscellaneous.getRandomEntry(usableChallenges, currentChallenge);
    }

    public void apply() {
        KeyBinding.resetKeyBindingArrayAndHash();
    };

    public void unApply() {
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    public String getName() {
        return this.smartBoolean.getName();
    }

    public String getDescription() {
        return this.smartBoolean.getDescription();
    }
}
