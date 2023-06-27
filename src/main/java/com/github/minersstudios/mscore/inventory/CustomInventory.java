package com.github.minersstudios.mscore.inventory;

import com.github.minersstudios.mscore.inventory.actions.InventoryAction;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.minecraft.world.Container;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class CustomInventory extends CraftInventoryCustom implements Inventory, Cloneable {
    protected final int size;
    protected @NotNull Map<Integer, InventoryButton> buttons;
    protected @Nullable InventoryAction<InventoryOpenEvent> openAction;
    protected @Nullable InventoryAction<InventoryCloseEvent> closeAction;
    protected @Nullable InventoryAction<InventoryClickEvent> clickAction;
    protected @Nullable InventoryAction<InventoryClickEvent> bottomClickAction;

    /**
     * Last slot in the inventory (5th row, 9th column)
     * <br>
     * 0 is first slot
     */
    protected static final int LAST_SLOT = 53;

    /**
     * Custom inventory
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     */
    public CustomInventory(
            @NotNull String title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        super(null, verticalSize * 9, ChatUtils.createDefaultStyledText(title));
        this.size = verticalSize * 9;
        this.buttons = new HashMap<>(this.size);
    }

    /**
     * Integers - slot
     * <br>
     * InventoryButton - button placed in that slot
     *
     * @return Button map of this inventory
     */
    public @NotNull Map<Integer, InventoryButton> getButtons() {
        return this.buttons;
    }

    /**
     * Sets buttons in this inventory
     *
     * @param buttons Buttons to set
     * @throws IllegalArgumentException If any of the buttons is out of inventory size
     */
    public void setButtons(@NotNull Map<Integer, InventoryButton> buttons) throws IllegalArgumentException {
        for (Map.Entry<Integer, InventoryButton> entry : buttons.entrySet()) {
            this.setButtonAt(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @return True if this inventory has any buttons
     */
    public boolean hasButtons() {
        return !this.buttons.isEmpty();
    }

    /**
     * @param slot Slot to get button from
     * @return Button at specified slot or null if there is no button
     */
    public @Nullable InventoryButton getButtonAt(@Range(from = 0, to = LAST_SLOT) int slot) {
        return this.buttons.getOrDefault(slot, null);
    }

    /**
     * Sets button at specified slot
     *
     * @param slot   Slot to set button at
     * @param button Button to set
     * @throws IllegalArgumentException If slot is out of inventory size
     */
    public void setButtonAt(
            @Range(from = 0, to = LAST_SLOT) int slot,
            @NotNull InventoryButton button
    ) throws IllegalArgumentException {
        if (slot >= this.size) {
            throw new IllegalArgumentException();
        }

        this.buttons.put(slot, button);
        this.setItem(slot, button.item());
    }

    /**
     * @return Inventory action that is performed when this inventory is opened
     */
    public @Nullable InventoryAction<InventoryOpenEvent> getOpenAction() {
        return this.openAction;
    }

    /**
     * Sets inventory action that is performed when this inventory is opened
     *
     * @param openAction New open action
     */
    public void setOpenAction(@Nullable InventoryAction<InventoryOpenEvent> openAction) {
        this.openAction = openAction;
    }

    /**
     * Performs the opening action when the inventory is opened, if it is set
     *
     * @param event Event that triggered the action
     */
    public void doOpenAction(@NotNull InventoryOpenEvent event) {
        if (this.openAction != null) {
            this.openAction.doAction(event, this.clone());
        }
    }

    /**
     * @return Inventory action that is performed when this inventory is closed
     */
    public @Nullable InventoryAction<InventoryCloseEvent> getCloseAction() {
        return this.closeAction;
    }

    /**
     * Sets inventory action that is performed when this inventory is closed
     *
     * @param closeAction New close action
     */
    public void setCloseAction(@Nullable InventoryAction<InventoryCloseEvent> closeAction) {
        this.closeAction = closeAction;
    }

    /**
     * Performs the closing action when the inventory is closed, if it is set
     *
     * @param event Event that triggered the action
     */
    public void doCloseAction(@NotNull InventoryCloseEvent event) {
        if (this.closeAction != null) {
            this.closeAction.doAction(event, this.clone());
        }
    }

    /**
     * @return Inventory action that is performed when this inventory is clicked
     */
    public @Nullable InventoryAction<InventoryClickEvent> getClickAction() {
        return this.clickAction;
    }

    /**
     * Sets inventory action that is performed when this inventory is clicked
     *
     * @param clickAction New click action
     */
    public void setClickAction(@Nullable InventoryAction<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
    }

    /**
     * Performs the clicking action when the inventory is clicked, if it is set
     *
     * @param event Event that triggered the action
     */
    public void doClickAction(@NotNull InventoryClickEvent event) {
        if (this.clickAction != null) {
            this.clickAction.doAction(event, this.clone());
        }
    }

    /**
     * @return Inventory action that is performed when player is clicked bottom inventory
     */
    public @Nullable InventoryAction<InventoryClickEvent> getBottomInventoryClickAction() {
        return this.bottomClickAction;
    }

    /**
     * Sets inventory action that is performed when player is clicked bottom inventory
     *
     * @param bottomClickAction New bottom inventory click action
     */
    public void setBottomInventoryClickAction(@Nullable InventoryAction<InventoryClickEvent> bottomClickAction) {
        this.bottomClickAction = bottomClickAction;
    }

    /**
     * Performs the clicking action when player is clicked bottom inventory, if it is set
     *
     * @param event Event that triggered the action
     */
    public void doBottomClickAction(@NotNull InventoryClickEvent event) {
        if (this.bottomClickAction != null) {
            this.bottomClickAction.doAction(event, this.clone());
        }
    }

    /**
     * Creates a clone of this inventory with all the contents copied into it
     *
     * @return Clone of this inventory
     */
    @Override
    public @NotNull CustomInventory clone() {
        try {
            CustomInventory clone = (CustomInventory) super.clone();
            Container newContainer = new CraftInventoryCustom(null, this.getSize(), this.title()).getInventory();
            Field inventoryField = CraftInventory.class.getDeclaredField("inventory");

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            unsafe.putObject(clone, unsafe.objectFieldOffset(inventoryField), newContainer);

            clone.buttons = new HashMap<>(this.buttons);

            clone.setContents(this.getContents());
            return clone;
        } catch (CloneNotSupportedException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
