package io.github.tw0null.sharedInv

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SharedInv : JavaPlugin(), Listener, CommandExecutor {
    lateinit var inv: Inventory
    override fun onEnable() {
        getCommand("/sinv")?.setExecutor(this)
        server.pluginManager.registerEvents(this, this)
    }
    fun showSharedInventory(player: Player){
        if (!this::inv.isInitialized) {
            inv = Bukkit.createInventory(null, 54, Component.text("Shared Inventory"))
        }
        loadInv()
        player.openInventory(inv)
    }
    fun saveInv(){
        val configf = File(dataFolder, "inv.yml")
        val config = YamlConfiguration.loadConfiguration(configf)
        config.set("inv", inv.contents.toList())
        config.save(configf)
    }
    fun loadInv() {
        val configf = File(dataFolder, "inv.yml")
        if (!configf.exists()) return
        val config = YamlConfiguration.loadConfiguration(configf)

        val items = config.getList("inv") ?: return

        for ((index, item) in items.withIndex()) {
            if (item is ItemStack) {
                inv.setItem(index, item)
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            showSharedInventory(sender)
        } else {
            sender.sendMessage("The '/sinv' command can only be used by players.")
            return false
        }
        return true
    }
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val e_inv = e.inventory
        if (e_inv == inv) {
            saveInv()

        }
    }
}
