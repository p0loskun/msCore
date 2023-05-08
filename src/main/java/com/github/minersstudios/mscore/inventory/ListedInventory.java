package com.github.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ListedInventory extends CustomInventory {
	protected final @NotNull Map<Integer, ListedInventory> pages = new HashMap<>();
	protected final @NotNull Map<Integer, StaticInventoryButton> staticButtons;
	protected int page;
	protected int pagesSize;

	public ListedInventory(
			@NotNull String title,
			@Range(from = 1, to = 6) int verticalSize,
			Object... args
	) {
		super(title, verticalSize, args);
		this.page = 0;
		this.staticButtons = new HashMap<>(this.size);
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
		if (!this.setStaticButtonAt(slot, button)) return false;
		this.updateStaticButtons();
		return true;
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
				listedInventory.setItem(entry.getKey(), entry.getValue().getButton(listedInventory).getItem());
			}
		}
	}

	public void updateStaticButtons(int page) {
		ListedInventory listedInventory = this.pages.get(page);
		if (!this.hasStaticButtons()) return;
		for (Map.Entry<Integer, StaticInventoryButton> entry : this.staticButtons.entrySet()) {
			listedInventory.setItem(entry.getKey(), entry.getValue().getButton(listedInventory).getItem());
		}
	}

	public @Nullable ListedInventory createPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		ListedInventory listedInventory = (ListedInventory) this.clone();
		listedInventory.setPageIndex(page);
		this.pages.put(page, listedInventory);
		this.updateStaticButtons(page);
		this.setPagesSize(this.pages.size());
		return listedInventory;
	}

	public @NotNull Map<Integer, ListedInventory> getPages() {
		return this.pages;
	}

	public @Nullable ListedInventory getPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		return this.pages.getOrDefault(page, null);
	}

	public int getPageIndex() {
		return this.page;
	}

	protected void setPageIndex(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
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

	protected void setPagesSize(@Range(from = 0, to = Integer.MAX_VALUE) int pagesSize) {
		for (ListedInventory listedInventory : this.pages.values()) {
			listedInventory.pagesSize = pagesSize;
		}
	}
}
