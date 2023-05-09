package com.github.minersstudios.mscore.inventory;

import com.github.minersstudios.mscore.inventory.actions.ButtonClickAction;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class InventoryButton {
	private @Nullable Player whoClicked;
	private @Nullable ItemStack item;
	private @Nullable ButtonClickAction clickAction;

	public InventoryButton() {
		this(null, null, null);
	}

	public InventoryButton(
			@Nullable ItemStack item
	) {
		this(null, item, null);
	}

	public InventoryButton(
			@Nullable ItemStack item,
			@Nullable ButtonClickAction clickAction
	) {
		this(null, item, clickAction);
	}

	public InventoryButton(
			@Nullable Player whoClicked,
			@Nullable ItemStack item
	) {
		this(whoClicked, item, null);
	}

	public InventoryButton(
			@Nullable Player whoClicked,
			@Nullable ItemStack item,
			@Nullable ButtonClickAction clickAction
	) {
		this.whoClicked = whoClicked;
		this.item = item;
		this.clickAction = clickAction;
	}

	public static void playClickSound(@NotNull Player player) {
		player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.5f, 1.0f);
	}

	public @Nullable Player getWhoClicked() {
		return this.whoClicked;
	}

	public void setWhoClicked(@Nullable Player whoClicked) {
		this.whoClicked = whoClicked;
	}

	public @Nullable ItemStack getItem() {
		return this.item;
	}

	public void setItem(@Nullable ItemStack item) {
		this.item = item;
	}

	public @Nullable ButtonClickAction getClickAction() {
		return this.clickAction;
	}

	public void setClickAction(@Nullable ButtonClickAction clickAction) {
		this.clickAction = clickAction;
	}

	public void doAction(@NotNull Player player, @NotNull CustomInventory customInventory) {
		if (this.clickAction != null) {
			this.clickAction.doAction(player, customInventory, this);
		}
	}
}
