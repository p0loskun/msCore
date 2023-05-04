package com.github.minersstudios.mscore.inventory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListedInventory extends CustomInventory implements Cloneable {
	private final @NotNull Multimap<Integer, InventoryButton> elements = ArrayListMultimap.create();
	private final @NotNull Map<Integer, ListedInventory> pages = new HashMap<>();
	private final int[] elementSlots;
	private int page;
	private int pagesSize;

	public ListedInventory(
			@NotNull String title,
			@Range(from = 1, to = 6) int verticalSize,
			@NotNull List<InventoryButton> elements,
			int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots,
			Object... args
	) {
		super(title, verticalSize, args);
		this.elementSlots = elementSlots;
		this.setElements(elements);
		this.page = 0;
		this.setButtons(this.getPageContents(this.page));
		this.updatePages();
	}

	@Contract("-> new")
	public @NotNull Multimap<Integer, InventoryButton> getElements() {
		return ArrayListMultimap.create(this.elements);
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

	public @Nullable ListedInventory createPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		if (page >= this.pagesSize) return null;
		ListedInventory listedInventory = this.clone();
		listedInventory.setButtons(this.getPageContents(page));
		listedInventory.setPageIndex(page);
		return listedInventory;
	}

	public @NotNull Map<Integer, ListedInventory> getPages() {
		return this.pages;
	}

	public void updatePages() {
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
			return (ListedInventory) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
