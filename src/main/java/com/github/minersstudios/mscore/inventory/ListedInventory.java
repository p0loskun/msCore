package com.github.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ListedInventory extends CustomInventory {
	protected final @NotNull Map<Integer, StaticInventoryButton> staticButtons = new HashMap<>();
	protected final @NotNull Map<Integer, ListedInventory> pages = new HashMap<>();
	protected int page;
	protected int pagesSize;

	public ListedInventory(
			@NotNull String title,
			@Range(from = 1, to = 6) int verticalSize
	) {
		super(title, verticalSize);
	}

	public boolean hasStaticButtons() {
		return !this.staticButtons.isEmpty();
	}

	public void setStaticButtons(@NotNull Map<Integer, StaticInventoryButton> buttons) throws IllegalArgumentException {
		for (Map.Entry<Integer, StaticInventoryButton> entry : buttons.entrySet()) {
			this.setStaticButtonAt(entry.getKey(), entry.getValue());
		}

		this.updateStaticButtons();
	}

	public void setStaticButtonAt(
			@Range(from = 0, to = MAX_SIZE) int slot,
			@NotNull StaticInventoryButton button
	) throws IllegalArgumentException {
		if (slot >= this.size) {
			throw new IllegalArgumentException();
		}

		this.staticButtons.put(slot, button);
	}

	public void setStaticButtonAndUpdate(
			@Range(from = 0, to = MAX_SIZE) int slot,
			@NotNull StaticInventoryButton button
	) throws IllegalArgumentException {
		this.setStaticButtonAt(slot, button);
		this.updateStaticButtons();
	}

	@Override
	public @Nullable InventoryButton getClickedButton(@Range(from = 0, to = Integer.MAX_VALUE) int slot) {
		StaticInventoryButton staticButton = this.staticButtons.getOrDefault(slot, null);
		if (staticButton != null) {
			return staticButton.getButton(this);
		}
		return this.buttons.getOrDefault(slot, null);
	}

	public void updateStaticButtons() {
		if (this.hasStaticButtons()) {
			for (Map.Entry<Integer, StaticInventoryButton> entry : this.staticButtons.entrySet()) {
				for (ListedInventory listedInventory : this.pages.values()) {
					listedInventory.setItem(entry.getKey(), entry.getValue().getButton(listedInventory).getItem());
				}
			}
		}
	}

	public void updateStaticButtons(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		ListedInventory listedInventory = this.pages.get(page);
		if (this.hasStaticButtons()) {
			for (Map.Entry<Integer, StaticInventoryButton> entry : this.staticButtons.entrySet()) {
				listedInventory.setItem(entry.getKey(), entry.getValue().getButton(listedInventory).getItem());
			}
		}
	}

	public @NotNull Map<Integer, ListedInventory> getPages() {
		return this.pages;
	}

	public @Nullable ListedInventory getPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		return this.pages.getOrDefault(page, null);
	}

	public @NotNull ListedInventory addPage() {
		int page = this.pagesSize;
		ListedInventory listedInventory = (ListedInventory) this.clone();

		listedInventory.setPageIndex(page);
		this.pages.put(page, listedInventory);
		this.updateStaticButtons(page);
		this.setPagesSize(this.pages.size());
		return listedInventory;
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
