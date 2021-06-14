package ru.starrysparklez.noblockdrops;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.logging.Logger;

public class EventListener implements Listener {
    FileConfiguration config;
    Logger logger;
    boolean debug;

    public EventListener(Main plugin) {
        logger = plugin.logger;
        config = plugin.getConfig();
        debug = config.getConfigurationSection("plugin").getBoolean("debug_logs");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        Block block = event.getBlock();

        World world = block.getWorld();
        Location location = block.getLocation();

        PlayerInventory inv = player.getInventory();
        Collection<ItemStack> itemDrops = block.getDrops();
        ItemStack tool = inv.getItemInMainHand();

        String toolName = tool.getType().toString().toLowerCase();

        if (toolName.contains("axe") || toolName.contains("sword") || toolName.contains("spade")) {
            if (debug) logger.info(player.getDisplayName() + " breaks "
                    + block.getType().toString().toLowerCase() + " **using " + toolName + "**");
            if (tool.getDurability() > tool.getType().getMaxDurability()) {
                inv.clear(inv.getHeldItemSlot());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            } else {
                tool.setDurability((short) (tool.getDurability() + 1));
                logger.info(player.getDisplayName() + ": tool durability is now " + tool.getDurability());
            }
        } else {
            if (debug) logger.info(player.getDisplayName() + " breaks "
                    + block.getType().toString().toLowerCase());
        }

        if (itemDrops.isEmpty()) {
            world.getBlockAt(location).setType(Material.AIR);
        } else {
            block.getDrops().forEach(item -> {
                if (!inv.addItem(item).isEmpty()) {
                    player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(config.getConfigurationSection("messages").getString("error.inventory_is_full"))
                    );
                    world.dropItem(block.getLocation(), item);
                } else {
                    world.getBlockAt(location).setType(Material.AIR);
                }
            });
        }
    }
}
