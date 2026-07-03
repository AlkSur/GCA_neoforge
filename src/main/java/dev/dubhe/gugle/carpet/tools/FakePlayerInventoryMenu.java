package dev.dubhe.gugle.carpet.tools;

import dev.dubhe.gugle.carpet.mixin.AbstractContainerMenuAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class FakePlayerInventoryMenu extends ChestMenu {
    public FakePlayerInventoryMenu(int i, Inventory inventory, Container container) {
        super(MenuType.GENERIC_9x6, i, inventory, container, 6);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        return quickMove(this, slotIndex);
    }

    public static ItemStack quickMove(ChestMenu chestMenu, int slotIndex) {
        ItemStack remainingItem = ItemStack.EMPTY;
        Slot slot = chestMenu.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            remainingItem = slotStack.copy();
            if (slotIndex < 54) {
                // Move from container (fake player inv) to player inventory
                AbstractContainerMenuAccessor accessor = (AbstractContainerMenuAccessor) (chestMenu);
                if (!accessor.invokerMoveItemStackTo(slotStack, 54, chestMenu.slots.size(), true)) {
                    // Nothing could be moved, return the full original stack
                    return remainingItem;
                }
            } else if (slotStack.getItem() instanceof ArmorItem armorItem) {
                int ordinal = armorItem.getType().ordinal();
                // Try armor slot first, then general inventory
                boolean movedArmor = moveToArmor(chestMenu, slotStack, ordinal);
                boolean movedInv = moveToInventory(chestMenu, slotStack);
                if (!movedArmor && !movedInv) {
                    return remainingItem;
                }
            } else if (slotStack.is(Items.ELYTRA)) {
                boolean movedArmor = moveToArmor(chestMenu, slotStack, 1);
                boolean movedInv = moveToInventory(chestMenu, slotStack);
                if (!movedArmor && !movedInv) {
                    return remainingItem;
                }
            } else if (slotStack.has(DataComponents.FOOD)) {
                boolean movedOffhand = moveToOffHand(chestMenu, slotStack);
                boolean movedInv = moveToInventory(chestMenu, slotStack);
                if (!movedOffhand && !movedInv) {
                    return remainingItem;
                }
            } else {
                // General item: try inventory first
                boolean movedInv = moveToInventory(chestMenu, slotStack);
                if (!movedInv) {
                    // Inventory full, try armor/offhand area (slots 1-8)
                    AbstractContainerMenuAccessor accessor = (AbstractContainerMenuAccessor) (chestMenu);
                    if (!accessor.invokerMoveItemStackTo(slotStack, 1, 8, false)) {
                        return remainingItem;
                    }
                }
            }
            // Update the slot after modifications
            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return remainingItem;
    }

    // Move item to offhand slot (slot 7)
    private static boolean moveToOffHand(ChestMenu chestMenu, ItemStack slotStack) {
        AbstractContainerMenuAccessor accessor = (AbstractContainerMenuAccessor) (chestMenu);
        return accessor.invokerMoveItemStackTo(slotStack, 7, 8, false);
    }

    // Move item to the specific armor slot by ordinal (helmet=0, chestplate=1, leggings=2, boots=3)
    private static boolean moveToArmor(ChestMenu chestMenu, ItemStack slotStack, int ordinal) {
        AbstractContainerMenuAccessor accessor = (AbstractContainerMenuAccessor) (chestMenu);
        return accessor.invokerMoveItemStackTo(slotStack, ordinal + 1, ordinal + 2, false);
    }

    // Move item to inventory slots 18-53 (lower half of the 6-row container)
    private static boolean moveToInventory(ChestMenu chestMenu, ItemStack slotStack) {
        AbstractContainerMenuAccessor accessor = (AbstractContainerMenuAccessor) (chestMenu);
        return accessor.invokerMoveItemStackTo(slotStack, 18, 54, false);
    }
}