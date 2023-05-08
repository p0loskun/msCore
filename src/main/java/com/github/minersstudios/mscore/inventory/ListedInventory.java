package com.github.minersstudios.mscore.inventory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.Container;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryCustom;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ListedInventory extends CustomInventory implements Cloneable {
	protected final @NotNull Multimap<Integer, InventoryButton> elements = ArrayListMultimap.create();
	protected final @NotNull Map<Integer, ListedInventory> pages = new HashMap<>();
	protected final @NotNull Map<Integer, StaticInventoryButton> staticButtons;
	protected final int[] elementSlots;
	protected int page;
	protected int pagesSize;

	public ListedInventory(
			@NotNull String title,
			@Range(from = 1, to = 6) int verticalSize,
			@NotNull List<InventoryButton> elements,
			int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots,
			Object... args
	) {
		super(title, verticalSize, args);
		this.elementSlots = elementSlots;
		this.page = 0;
		this.staticButtons = new HashMap<>(this.size);
		this.setElements(elements);
		this.updatePages();
		this.setButtons(this.getPageContents(this.page));
	}

	public boolean hasStaticButtons() {
		return !this.staticButtons.isEmpty();
	}

	public void setStaticButtons(@NotNull Map<Integer, StaticInventoryButton> buttons) {
		for (Map.Entry<Integer, StaticInventoryButton> entry : buttons.entrySet()) {
			this.setStaticButtonAt(entry.getKey(), entry.getValue());
		}
		this.updateStaticButtons();
	}

	public boolean setStaticButtonAt(
			@Range(from = 0, to = MAX_SIZE) int slot,
			@NotNull StaticInventoryButton button
	) {
		if (slot + 1 > this.size) return false;
		this.staticButtons.put(slot, button);
		return true;
	}

	public boolean setStaticButtonAndUpdate(
			@Range(from = 0, to = MAX_SIZE) int slot,
			@NotNull StaticInventoryButton button
	) {
		if (this.setStaticButtonAt(slot, button)) return false;
		this.updateStaticButtons();
		return true;
	}

	public boolean removeStaticButtonAt(@Range(from = 0, to = MAX_SIZE) int slot) {
		return slot + 1 <= this.size && this.staticButtons.remove(slot) != null;
	}

	@Override
	public @Nullable InventoryButton getClickedButton(int slot) {
		StaticInventoryButton staticInventoryButton = this.staticButtons.getOrDefault(slot, null);
		if (staticInventoryButton != null) {
			return staticInventoryButton.getButton(this);
		}
		return this.buttons.getOrDefault(slot, null);
	}

	public void updateStaticButtons() {
		if (!this.hasStaticButtons()) return;
		for (Map.Entry<Integer, StaticInventoryButton> entry : this.staticButtons.entrySet()) {
			for (ListedInventory listedInventory : this.pages.values()) {
				listedInventory.setItem(entry.getKey(), entry.getValue().getButton(this).getItem());
			}
		}
	}

	@Contract("-> new")
	public @NotNull Multimap<Integer, InventoryButton> getElements() {
		return ArrayListMultimap.create(this.elements);
	}

	public void setElements(@NotNull List<InventoryButton> elements) {
		this.elements.clear();
		this.setPagesSize((int) Math.ceil((double) elements.size() / this.elementSlots.length));
		for (int page = 0; page < this.pagesSize; page++) {
			for (int element = 0; element < this.elementSlots.length; element++) {
				int index = element + (page * this.elementSlots.length);
				if (index >= elements.size()) break;
				this.elements.put(page, elements.get(index));
			}
		}
	}

	public @NotNull Map<Integer, InventoryButton> getPageContents(int page) {
		Map<Integer, InventoryButton> content = new HashMap<>();
		int i = 0;
		for (InventoryButton inventoryButton : this.elements.get(page)) {
			content.put(this.elementSlots[i], inventoryButton);
			i++;
		}
		return content;
	}

	public @Nullable ListedInventory createPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		if (page >= this.pagesSize) return null;
		ListedInventory listedInventory = this.clone();
		listedInventory.setPageIndex(page);
		listedInventory.setButtons(this.getPageContents(page));
		return listedInventory;
	}

	public @NotNull Map<Integer, ListedInventory> getPages() {
		return this.pages;
	}

	public void updatePages() {
		this.pages.clear();
		for (int page = 0; page < this.pagesSize; page++) {
			this.pages.put(page, this.createPage(page));
		}
	}

	public @Nullable ListedInventory getPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		return this.pages.getOrDefault(page, null);
	}

	@Contract("-> new")
	public int[] getElementSlots() {
		return this.elementSlots.clone();
	}

	public int getPageIndex() {
		return this.page;
	}

	private void setPageIndex(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		this.page = page;
	}

	public int getNextPageIndex() {
		int next = this.page + 1;
		return next >= this.pagesSize ? -1 : next;
	}

	public int getPreviousPageIndex() {
		int previous = this.page - 1;
		return previous < 0 ? -1 : previous;
	}

	public int getPagesSize() {
		return this.pagesSize;
	}

	private void setPagesSize(@Range(from = 0, to = Integer.MAX_VALUE) int pagesSize) {
		this.pagesSize = pagesSize;
	}

	@Override
	public @NotNull ListedInventory clone() {
		try {
			ListedInventory clone = (ListedInventory) super.clone();
			Container newContainer = new CraftInventoryCustom(null, this.getSize(), this.title()).getInventory();
			Field inventoryField = CraftInventory.class.getDeclaredField("inventory");

			Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			Unsafe unsafe = (Unsafe) unsafeField.get(null);
			unsafe.putObject(clone, unsafe.objectFieldOffset(inventoryField), newContainer);

			clone.setContents(this.getContents());
			return clone;
		} catch (CloneNotSupportedException | IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
