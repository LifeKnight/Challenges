package com.lifeknight.challenges.mod;

import com.lifeknight.challenges.gui.ManipulableGui;
import com.lifeknight.challenges.gui.SmartGui;
import com.lifeknight.challenges.gui.components.SmartButton;
import com.lifeknight.challenges.utilities.Chat;
import com.lifeknight.challenges.utilities.Text;
import com.lifeknight.challenges.variables.SmartVariable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.lifeknight.challenges.mod.Core.*;
import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;

public class ModCommand extends CommandBase {
    private final List<String> aliases = Collections.singletonList("mb");
    private final String[] mainCommands = {};

    public String getCommandName() {
        return MOD_ID;
    }

    public String getCommandUsage(ICommandSender iCommandSender) {
        return MOD_ID;
    }

    public List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] arguments, BlockPos blockPosition) {

        if (arguments.length > 0) {
            return Text.returnStartingEntries(this.mainCommands, arguments[0], true);
        }

        return Arrays.asList(this.mainCommands);
    }

    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return true;
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public boolean isUsernameIndex(String[] arguments, int argument1) {
        return false;
    }

    public int compareTo(ICommand iCommand) {
        return 0;
    }

    public void processCommand(ICommandSender iCommandSender, String[] arguments) throws CommandException {
        Core.openGui(new SmartGui(String.format("[%s] %s", MOD_VERSION, MOD_NAME), SmartVariable.getVariables(), Collections.singletonList(
                new SmartButton("Edit HUD") {
                    @Override
                    public void work() {
                        Core.openGui(new ManipulableGui());
                    }
                })));
    }

    public void addMainCommandMessage() {
        StringBuilder result = new StringBuilder(DARK_GREEN + "/" + MOD_ID);

        for (String command : this.mainCommands) {
            result.append(" ").append(command).append(",");
        }

        Chat.addChatMessage(result.substring(0, result.length() - 1));
    }
}
