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

@SuppressWarnings("unused")
public class ElementListedInventory extends ListedInventory {
	protected final @NotNull Multimap<Integer, InventoryButton> elements = ArrayListMultimap.create();
	protected final int[] elementSlots;

	public ElementListedInventory(
			@NotNull String title,
			@Range(from = 1, to = 6) int verticalSize,
			@NotNull List<InventoryButton> elements,
			int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots,
			Object... args
	) {
		super(title, verticalSize, args);
		this.elementSlots = elementSlots;
		this.setElements(elements);
		this.updatePages();
		this.setButtons(this.getPageContents(this.page));
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
		Map<Integer, InventoryButton> content = new HashMap<>(this.elementSlots.length);
		int i = 0;
		for (InventoryButton inventoryButton : this.elements.get(page)) {
			content.put(this.elementSlots[i], inventoryButton);
			i++;
		}
		for (int slot : this.elementSlots) {
			content.putIfAbsent(slot, new InventoryButton());
		}
		return content;
	}

	@Contract("-> new")
	public int[] getElementSlots() {
		return this.elementSlots.clone();
	}

	@Override
	public @Nullable ListedInventory createPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
		if (page >= this.pagesSize) return null;
		ListedInventory listedInventory = (ListedInventory) this.clone();
		listedInventory.setPageIndex(page);
		listedInventory.setButtons(this.getPageContents(page));
		return listedInventory;
	}

	public void updatePages() {
		this.pages.clear();
		for (int page = 0; page < this.pagesSize; page++) {
			this.pages.put(page, this.createPage(page));
		}
		this.updateStaticButtons();
	}
}
