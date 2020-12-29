package com.lifeknight.challenges.mod;

import com.lifeknight.challenges.gui.Manipulable;
import com.lifeknight.challenges.gui.hud.EnhancedHudText;
import com.lifeknight.challenges.utilities.Chat;
import com.lifeknight.challenges.utilities.Miscellaneous;
import com.lifeknight.challenges.utilities.Text;
import com.lifeknight.challenges.utilities.User;
import com.lifeknight.challenges.variables.SmartBoolean;
import com.lifeknight.challenges.variables.SmartCycle;
import com.lifeknight.challenges.variables.SmartList;
import com.lifeknight.challenges.variables.SmartNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.util.EnumChatFormatting.GOLD;

@net.minecraftforge.fml.common.Mod(modid = Core.MOD_ID, name = Core.MOD_NAME, version = Core.MOD_VERSION, clientSideOnly = true)
public class Core {
    public static final String
            MOD_NAME = "Challenges",
            MOD_VERSION = "1.0",
            MOD_ID = "challenges";
    public static final EnumChatFormatting MOD_COLOR = GOLD;
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new SmartThreadFactory());
    public static boolean onHypixel = false;
    public static GuiScreen guiToOpen = null;
    public static final SmartBoolean runMod = new SmartBoolean("Mod", "Main", true, "The mod being enabled.") {
        @Override
        public void onSetValue() {
            if (!this.getValue()) {
                stopChallenge();
            }
        }
    };
    public static final SmartBoolean hudSnapping = new SmartBoolean("HUD Snapping", "HUD", true, "HUD components snapping to one another.");
    public static final SmartBoolean hudTextShadow = new SmartBoolean("HUD Text Shadow", "HUD", false, "Shadow behind text shown in the HUD.");
    public static final SmartBoolean hudTextBox = new SmartBoolean("HUD Text Box", "HUD", true, "Box surrounding text shown in the HUD.");
    public static final SmartNumber.SmartFloat hudTextBoxOpacity = new SmartNumber.SmartFloat("HUD Text Box Opacity", "HUD", 0.7F, 0F, 1F, "Opacity of the HUD Text Box.");
    public static final SmartNumber.SmartFloat chromaSpeed = new SmartNumber.SmartFloat("Chroma Speed", "HUD", 0.5F, 0.125F, 1F, "Speed of the chroma speed of text in the HUD.");
    public static final SmartNumber.SmartInteger timerTime = new SmartNumber.SmartInteger("Timer Time", "Timer", 15, 5, 120, "Interval between challenges.");
    public static final SmartCycle challengeTypes = new SmartCycle("Challenge Types", "Challenges", Arrays.asList("Built-in", "Custom", "All"), "Determines challenges; built-in, custom, both.");
    public static final SmartList.SmartStringList customChallenges = new SmartList.SmartStringList("Custom Challenges", "Challenges", "Challenges that you add!");
    public static com.lifeknight.challenges.utilities.Timer problemTimer;
    public static Configuration configuration;

    public static Challenge currentChallenge = null;
    public static String currentCustomChallenge = null;

    public static boolean isCustom = false;

    static {
        hudTextBoxOpacity.setiCustomDisplayString(objects -> {
                    float value = (float) objects[0];
                    return "HUD Text Box Opacity: " + (int) (value * 100) + "%";
                }
        );
        chromaSpeed.setiCustomDisplayString(objects -> {
                    float value = (float) objects[0];
                    return "Chroma Speed: " + (int) (value * 100) + "%";
                }
        );
    }

    /*
     */

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ModCommand());

        Miscellaneous.createEnhancedHudTextDefaultPropertyVariables();

        problemTimer = new com.lifeknight.challenges.utilities.Timer(timerTime.getValue());
        problemTimer.onEnd(Core::onTimerEnd);

        new EnhancedHudText("Challenge Timer", "Next Challenge") {
            @Override
            public String getTextToDisplay() {
                return problemTimer.isRunning() ? Text.formatTimeFromMilliseconds(problemTimer.getTotalMilliseconds(), 2, true) : "Paused";
            }

            @Override
            public boolean isVisible() {
                return true;
            }
        };

        new EnhancedHudText("Current Challenge", 0, 200, "Current Challenge") {
            @Override
            public String getTextToDisplay() {
                return getCurrentChallengeString();
            }

            @Override
            public boolean isVisible() {
                return true;
            }
        };

        Challenge.beforeConfigurationLoad();

        configuration = new Configuration();

        Challenge.onConfigurationLoaded();
    }

    public static String getCurrentChallengeString() {
        if (isCustom) {
            if (currentCustomChallenge == null) {
                return "(Custom) None";
            } else {
                return currentCustomChallenge;
            }
        } else {
            if (currentChallenge == null) {
                return "None";
            } else {
                return currentChallenge.getName();
            }
        }
    }

    public static String getCurrentChallengeDescription() {
        if (!(isCustom || currentChallenge == null)) {
            return currentChallenge.getDescription();
        }
        return null;
    }

    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Chat.sendQueuedChatMessages();
                onHypixel = !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
            }
        }, 1000);
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (!(runMod.getValue() && onHypixel)) return;
        String message = Text.removeFormattingCodes(event.message.getFormattedText());

        if (!message.contains(":")) {
            if (message.toLowerCase().startsWith("cages opened!") || message.toLowerCase().contains("protect your bed") || message.equals("You have respawned!")) {
                newChallenge();
            } else if (message.startsWith(User.getUsername()) && (message.endsWith(".") || message.endsWith("FINAL KILL!"))) {
                stopChallenge();
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent .Unload event) {
        stopChallenge();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            com.lifeknight.challenges.utilities.Timer.onRenderTick();
            if (guiToOpen != null) {
                Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
                guiToOpen = null;
            }
            if (runMod.getValue()) Manipulable.renderManipulables();
        }
    }

    public static void openGui(GuiScreen guiScreen) {
        guiToOpen = guiScreen;
    }

    public static void onTimerEnd() {
        newChallenge();
    }

    public static void stopChallenge() {
        if (isCustom) {
            currentCustomChallenge = null;
        } else {
            if (currentChallenge != null) {
                currentChallenge.unApply();
            }
            currentChallenge = null;
        }
        problemTimer.pause();
    }

    public static void newChallenge() {
        if (currentChallenge != null) {
            currentChallenge.unApply();
        }
        switch (challengeTypes.getValue()) {
            case 0:
                currentChallenge = Challenge.getRandomChallenge(currentChallenge);
                isCustom = false;
                if (currentChallenge != null) currentChallenge.apply();
                break;
            case 1:
                currentCustomChallenge = Miscellaneous.getRandomEntry(customChallenges.getValue(), currentCustomChallenge);
                isCustom = true;
                break;
            default:
                if (customChallenges.getValue().isEmpty() || (Miscellaneous.getRandomTrueOrFalse() && (Challenge.usableChallenges.size() > 1 || currentChallenge == null))) {
                    currentChallenge = Challenge.getRandomChallenge(currentChallenge);
                    isCustom = false;
                    currentChallenge.apply();
                } else {
                    currentCustomChallenge = Miscellaneous.getRandomEntry(customChallenges.getValue(), currentCustomChallenge);
                    isCustom = true;
                }
                break;
        }

        showTitle(GOLD + getCurrentChallengeString(), getCurrentChallengeDescription());


        problemTimer.reset();
        problemTimer.setTimeFromSeconds(timerTime.getValue());
        problemTimer.start();
    }

    public static void showTitle(String title, String subtitle) {
        Minecraft.getMinecraft().ingameGUI.displayTitle(title, null, 500, 1000, 500);
        if (subtitle != null) {
            Minecraft.getMinecraft().ingameGUI.displayTitle(null, subtitle, 500, 1000, 500);
        }
    }
}
