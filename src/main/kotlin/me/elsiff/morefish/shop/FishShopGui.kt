package me.elsiff.morefish.shop

import me.elsiff.morefish.fishing.Fish
import me.elsiff.morefish.gui.ChestInventoryGui
import me.elsiff.morefish.gui.state.ComponentClickState
import me.elsiff.morefish.gui.state.GuiCloseState
import me.elsiff.morefish.gui.state.GuiDragState
import me.elsiff.morefish.gui.state.GuiItemChangeState
import me.elsiff.morefish.item.FishItemStackConverter
import me.elsiff.morefish.item.edit
import me.elsiff.morefish.resource.template.TemplateBundle
import me.elsiff.morefish.util.OneTickScheduler
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * Created by elsiff on 2019-01-03.
 */
class FishShopGui(
        private val shop: FishShop,
        private val converter: FishItemStackConverter,
        private val oneTickScheduler: OneTickScheduler,
        private val templates: TemplateBundle,
        private val user: Player
) : ChestInventoryGui(user.server, 4, templates.shopGuiTitle.formattedEmpty()) {
    private val bottomBarSlots = slotsOf(minX..maxX, maxY)
    private val priceIconSlot = slotOf(centerX, maxY)
    private val fishSlots = slotsOf(minX..maxX, minY until maxY)

    init {
        val bottomBarIcon = ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        bottomBarIcon.edit<ItemMeta> { displayName = " " }
        for (slot in bottomBarSlots) {
            inventory.setItem(slot, bottomBarIcon)
        }

        updatePriceIcon(0.0)
        controllableSlots.addAll(fishSlots)
    }

    override fun handleItemChange(state: GuiItemChangeState) {
        oneTickScheduler.scheduleLater(this) { updatePriceIcon() }
    }

    override fun handleComponentClick(state: ComponentClickState) {
        if (state.slot == priceIconSlot) {
            val allFish = allFish()
            if (allFish.isEmpty()) {
                user.sendMessage(templates.shopNoFish.formattedEmpty())
            } else {
                val totalPrice = getTotalPrice()
                allFish.forEach {
                    val fish = it.first
                    val itemStack = it.second

                    repeat(itemStack.amount) {
                        shop.sell(user, fish)
                    }
                    itemStack.amount = 0
                }
                updatePriceIcon(0.0)
                user.sendMessage(templates.shopSold.formatted(mapOf("%price%" to totalPrice.toString())))
            }
        }
    }

    override fun handleDrag(state: GuiDragState) {
        inventory.setItem(priceIconSlot, ItemStack(Material.AIR))
        oneTickScheduler.scheduleLater(this) { updatePriceIcon() }
    }

    override fun handleClose(state: GuiCloseState) {
        oneTickScheduler.cancelAllOf(this)
        dropAllFish()
    }

    private fun dropAllFish() {
        allFish().forEach {
            val itemStack = it.second
            user.world.dropItem(user.location, itemStack.clone())
        }
    }

    private fun getTotalPrice(): Double {
        var sum = 0.0
        allFish().forEach {
            val fish = it.first
            val itemStack = it.second

            repeat(itemStack.amount) {
                sum += shop.priceOf(fish)
            }
        }
        return sum
    }

    private fun allFish(): List<Pair<Fish, ItemStack>> {
        return fishSlots
                .mapNotNull { slot -> inventory.getItem(slot) ?: null }
                .filter { itemStack -> converter.isFish(itemStack) }
                .map { itemStack -> Pair(converter.fish(itemStack), itemStack) }
    }

    private fun updatePriceIcon(price: Double = getTotalPrice()) {
        val emeraldIcon = ItemStack(Material.EMERALD)
        emeraldIcon.edit<ItemMeta> {
            displayName = templates.shopEmeraldIconName.formatted(mapOf(
                    "%price%" to price.toString()
            ))
        }
        inventory.setItem(priceIconSlot, emeraldIcon)
    }
}