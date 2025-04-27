package io.github.tw0null.sharedInv

import io.github.zeettn.invfx.InvFX.frame
import io.github.zeettn.invfx.openFrame
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
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
        inv.contents.forEachIndexed { index, item ->
            if (item != null){
                val path = "inv.$index"
                config.set("$path.type", item.type.name)
                config.set("$path.amount", item.amount)
            }
        }
        config.save(configf)
    }
    fun loadInv(){
        val configf = File(dataFolder, "inv.yml")
        if (!configf.exists()) {return }
        val config = YamlConfiguration.loadConfiguration(configf)
        config.getConfigurationSection("inv") ?: return
        for (key in config.getKeys(false)) {
            val type = config.getString("$key.type")?.let { Material.valueOf(it) }
            type?: return
            val amount = config.getInt("$key.amount", 1)
            val item = ItemStack(type, amount)
            inv.setItem(key.toInt(), item)

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
