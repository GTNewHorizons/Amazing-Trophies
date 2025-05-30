package glowredman.amazingtrophies;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import glowredman.amazingtrophies.api.AmazingTrophiesAPI;

public class CommandTrophy extends CommandBase {

    @Override
    public String getCommandName() {
        return "trophy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.trophy.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        if ("list".equals(args[0])) {
            Set<String> trophies = AmazingTrophiesAPI.getTrophyIDs();
            if (trophies.isEmpty()) {
                throw new CommandException("commands.trophy.list.empty");
            }
            sender.addChatMessage(new ChatComponentTranslation("commands.trophy.list", String.join(", ", trophies)));
            return;
        }

        if ("award".equals(args[0]) && args.length > 1) {
            String trophyID = args[1];
            EntityPlayer player;
            boolean withDesc = false;

            if (!AmazingTrophiesAPI.getTrophyIDs()
                .contains(trophyID)) {
                throw new CommandException("commands.trophy.award.wrongTrophyID", trophyID);
            }

            if (args.length == 2) {
                player = getCommandSenderAsPlayer(sender);
            } else {
                player = getPlayer(sender, args[2]);
                if (args.length > 3) {
                    withDesc = Boolean.parseBoolean(args[3]);
                }
            }

            if (withDesc) {
                AmazingTrophiesAPI.awardTrophy(trophyID, player);
            } else {
                ItemStack trophy = AmazingTrophiesAPI.getTrophyWithNBT(trophyID, null);
                if (!player.inventory.addItemStackToInventory(trophy)) {
                    // Drop the trophy if the player's inventory is full
                    World world = player.worldObj;
                    world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, trophy));
                }
            }

            // notifyOperators
            func_152373_a(sender, this, "commands.trophy.award.success", trophyID, player.getCommandSenderName());
            return;
        }

        throw new WrongUsageException(this.getCommandUsage(sender));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "award", "list");
        }
        if ("award".equals(args[0])) {
            if (args.length == 2) {
                return getListOfStringsFromIterableMatchingLastWord(args, AmazingTrophiesAPI.getTrophyIDs());
            }
            if (args.length == 3) {
                return getListOfStringsMatchingLastWord(
                    args,
                    MinecraftServer.getServer()
                        .getAllUsernames());
            }
            if (args.length == 4) {
                return getListOfStringsMatchingLastWord(args, "true", "false");
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length >= 3 && "award".equals(args[0]) && index == 2;
    }
}
