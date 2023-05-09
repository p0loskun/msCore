package com.github.minersstudios.mscore.inventory;

import com.github.minersstudios.mscore.inventory.actions.InventoryAction;
import com.github.minersstudios.mscore.utils.ChatUtils;
import net.minecraft.world.Container;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryCustom;
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
	protected Object[] args;

	protected static final int MAX_SIZE = 54;

	public CustomInventory(
			@NotNull String title,
			@Range(from = 1, to = 6) int verticalSize,
			Object... args
	) {
		super(null, verticalSize * 9, ChatUtils.createDefaultStyledText(title));
		this.size = verticalSize * 9;
		this.buttons = new HashMap<>(this.size);
		this.args = args;
	}

	public @NotNull Map<Integer, InventoryButton> getButtons() {
		return this.buttons;
	}

	public boolean hasButtons() {
		return !this.buttons.isEmpty();
	}

	public void setButtons(@NotNull Map<Integer, InventoryButton> buttons) {
		for (Map.Entry<Integer, InventoryButton> entry : buttons.entrySet()) {
			this.setButtonAt(entry.getKey(), entry.getValue());
		}
	}

	public boolean setButtonAt(
			@Range(from = 0, to = MAX_SIZE) int slot,
			@NotNull InventoryButton button
	) {
		if (slot + 1 > this.size) return false;
		this.buttons.put(slot, button);
		this.setItem(slot, button.getItem());
		return true;
	}

	public @Nullable InventoryButton getClickedButton(int slot) {
		return this.buttons.getOrDefault(slot, null);
	}

	public @Nullable InventoryAction<InventoryOpenEvent> getOpenAction() {
		return this.openAction;
	}

	public void setOpenAction(@Nullable InventoryAction<InventoryOpenEvent> openAction) {
		this.openAction = openAction;
	}

	public void doOpenAction(@NotNull InventoryOpenEvent event) {
		if (this.openAction != null) {
			this.openAction.doAction(event, this.clone());
		}
	}

	public @Nullable InventoryAction<InventoryCloseEvent> getCloseAction() {
		return this.closeAction;
	}

	public void setCloseAction(@Nullable InventoryAction<InventoryCloseEvent> closeAction) {
		this.closeAction = closeAction;
	}

	public void doCloseAction(@NotNull InventoryCloseEvent event) {
		if (this.closeAction != null) {
			this.closeAction.doAction(event, this.clone());
		}
	}

	public @Nullable InventoryAction<InventoryClickEvent> getClickAction() {
		return this.clickAction;
	}

	public void setClickAction(@Nullable InventoryAction<InventoryClickEvent> clickAction) {
		this.clickAction = clickAction;
	}

	public void doClickAction(@NotNull InventoryClickEvent event) {
		if (this.clickAction != null) {
			this.clickAction.doAction(event, this.clone());
		}
	}

	public @Nullable InventoryAction<InventoryClickEvent> getBottomInventoryClickAction() {
		return this.bottomClickAction;
	}

	public void setBottomInventoryClickAction(@Nullable InventoryAction<InventoryClickEvent> bottomInventoryClickAction) {
		this.bottomClickAction = bottomInventoryClickAction;
	}

	public void doBottomClickAction(@NotNull InventoryClickEvent event) {
		if (this.bottomClickAction != null) {
			this.bottomClickAction.doAction(event, this.clone());
		}
	}

	public Object[] getArgs() {
		return this.args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

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
