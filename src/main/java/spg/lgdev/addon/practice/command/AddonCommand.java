package spg.lgdev.addon.practice.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import spg.lgdev.addon.practice.iSpigotPracticeAddon;
import spg.lgdev.addon.practice.util.CC;
import spg.lgdev.iSpigot;

import java.util.function.Consumer;

public class AddonCommand implements CommandExecutor {

    private String PREFIX = CC.translate("&a[iSpigot Practice Addon]");
    private String PREFIX_ERROR = CC.translate("&c[iSpigot Practice Addon]");

    private Consumer<CommandSender> HELP_MESSAGE = (sender -> {
        sender.sendMessage(CC.translate(PREFIX + "&f Command Usages:"));
        sender.sendMessage(CC.translate("  &7/practice-addon reload &a- Reloads the addon configuration"));
        sender.sendMessage(CC.translate("  &7/practice-addon status &a- Shows the current addon status, including what practice plugin it had hooked and what knockback profiles are loaded"));
        sender.sendMessage(CC.translate("  &7/practice-addon link [kit] [knockback] &a- Links a practice kit's knockback profile with an iSpigot's knockback profile"));
        sender.sendMessage(CC.translate("  &7/practice-addon unlink [kit] &a- Unlinks a kit from its linked knockback profile"));
    });

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
            HELP_MESSAGE.accept(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            long start = System.currentTimeMillis();
            iSpigotPracticeAddon
                    .getInstance()
                    .load();
            long end = System.currentTimeMillis();
            sender.sendMessage(CC.translate(PREFIX + "&f Reloaded addon in " + (end - start) + "ms."));

            return true;
        } else if (args[0].equalsIgnoreCase("status")) {
            sender.sendMessage(CC.translate(PREFIX + "&f Current Status:"));
            sender.sendMessage(CC.translate("&f  Provider: &a" + iSpigotPracticeAddon.getInstance().getCurrentProvider().getName()));
            sender.sendMessage(CC.translate("&f  Default Knockback: &a" + iSpigotPracticeAddon.getDefault()));
            sender.sendMessage(CC.translate("&f  Loaded Knockbacks (Linked with specific kits): "));
            iSpigotPracticeAddon
                    .KNOCKBACK_PROFILES
                    .forEach((kit, profile) -> {
                        sender.sendMessage(CC.translate("&7    -  &fPractice Kit &a" + kit + "&f has been linked with &a" + profile));
                    });
            return true;
        } else if (args[0].equalsIgnoreCase("link")) {
            if (args.length < 3) {
                sender.sendMessage(CC.translate(PREFIX_ERROR + " Usage: /practice-addon link [kitName] [profileName]"));
                return true;
            }

            String kitName = args[1];
            String profileName = args[2];

            if (iSpigot.INSTANCE.getKnockbackHandler().getKnockbackProfile(profileName) == null) {
                sender.sendMessage(CC.translate(PREFIX_ERROR + " The specified knockback profile does not exist in iSpigot."));
                return true;
            }

            if (iSpigotPracticeAddon.getInstance().getProfileFromKit(kitName) != null) {
                iSpigotPracticeAddon.KNOCKBACK_PROFILES.replace(kitName, profileName);
            } else {
                iSpigotPracticeAddon.KNOCKBACK_PROFILES.put(kitName, profileName);
            }

            sender.sendMessage(CC.translate(PREFIX + "&f You have linked the kit &a" + kitName + "&f with a knockback profile named &a" + profileName + "&f."));
            iSpigotPracticeAddon.getInstance()
                    .save();
            return true;
        } else if (args[0].equalsIgnoreCase("unlink")) {
            if (args.length < 2) {
                sender.sendMessage(CC.translate(PREFIX_ERROR + " Usage: /practice-addon unlink [kitName]"));
                return true;
            }

            String kitName = args[1];

            if (iSpigotPracticeAddon.getInstance().getProfileFromKit(kitName) == null) {
                sender.sendMessage(CC.translate(PREFIX_ERROR + " The kit " + kitName + " does not have a knockback profile linked yet."));
                return true;
            }

            iSpigotPracticeAddon.KNOCKBACK_PROFILES.remove(kitName);
            sender.sendMessage(CC.translate(PREFIX + "&f You have unlinked the kit &a" + kitName + "&f's knockback profile"));
            iSpigotPracticeAddon.getInstance()
                    .save();
            return true;
        } else {
            HELP_MESSAGE.accept(sender);
            return true;
        }
    }

}
