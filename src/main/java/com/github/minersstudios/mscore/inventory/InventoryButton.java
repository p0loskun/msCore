package com.github.minersstudios.mscore.inventory;

import com.github.minersstudios.mscore.inventory.actions.ButtonClickAction;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class InventoryButton {
    private @Nullable Player whoClicked;
    private @Nullable ItemStack item;
    private @Nullable ButtonClickAction clickAction;

    /**
     * Empty button
     */
    public InventoryButton() {
        this(null, null, null);
    }

    /**
     * Button with item
     *
     * @param item Item to be displayed on button
     */
    public InventoryButton(
            @Nullable ItemStack item
    ) {
        this(null, item, null);
    }

    /**
     * Button with item and click action
     *
     * @param item        Item to be displayed on button
     * @param clickAction Action to be performed when button is clicked
     */
    public InventoryButton(
            @Nullable ItemStack item,
            @Nullable ButtonClickAction clickAction
    ) {
        this(null, item, clickAction);
    }

    /**
     * Button with player and item
     *
     * @param whoClicked Player who clicked the button
     * @param item       Item to be displayed on button
     */
    public InventoryButton(
            @Nullable Player whoClicked,
            @Nullable ItemStack item
    ) {
        this(whoClicked, item, null);
    }

    /**
     * Button with player, item and click action
     *
     * @param whoClicked  Player who clicked the button
     * @param item        Item to be displayed on button
     * @param clickAction Action to be performed when button is clicked
     */
    public InventoryButton(
            @Nullable Player whoClicked,
            @Nullable ItemStack item,
            @Nullable ButtonClickAction clickAction
    ) {
        this.whoClicked = whoClicked;
        this.item = item;
        this.clickAction = clickAction;
    }

    /**
     * Plays click sound to player
     *
     * @param player Player to whom the sound will be played
     */
    public static void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.5f, 1.0f);
    }

    /**
     * @return Player who clicked the button
     */
    public @Nullable Player getWhoClicked() {
        return this.whoClicked;
    }

    /**
     * Sets player who clicked the button
     *
     * @param whoClicked New player
     */
    public void setWhoClicked(@Nullable Player whoClicked) {
        this.whoClicked = whoClicked;
    }

    /**
     * @return Item to be displayed on button
     */
    public @Nullable ItemStack getItem() {
        return this.item;
    }

    /**
     * Sets item to be displayed on button
     *
     * @param item New item
     */
    public void setItem(@Nullable ItemStack item) {
        this.item = item;
    }

    /**
     * @return Click action to be performed when button is clicked
     */
    public @Nullable ButtonClickAction getClickAction() {
        return this.clickAction;
    }

    /**
     * Sets click action to be performed when button is clicked
     *
     * @param clickAction New click action
     */
    public void setClickAction(@Nullable ButtonClickAction clickAction) {
        this.clickAction = clickAction;
    }

    /**
     * Performs click action when button is clicked
     *
     * @param event           Event that triggered the action
     * @param customInventory Custom inventory that is involved in this event
     */
    public void doClickAction(
            @NotNull InventoryClickEvent event,
            @NotNull CustomInventory customInventory
    ) {
        if (this.clickAction != null) {
            this.clickAction.doAction(event, customInventory.clone(), this);
        }
    }
}
