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
    protected final @NotNull Multimap<Integer, InventoryButton> elements;
    protected final int[] elementSlots;

    /**
     * Inventory with elements and pages
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     * @param elements     Elements of the inventory
     * @param elementSlots Slots of the elements in the inventory
     */
    public ElementListedInventory(
            @NotNull String title,
            @Range(from = 1, to = 6) int verticalSize,
            @NotNull List<InventoryButton> elements,
            int @Range(from = 0, to = Integer.MAX_VALUE) [] elementSlots
    ) {
        super(title, verticalSize);
        this.elementSlots = elementSlots;
        this.elements = ArrayListMultimap.create();

        this.setElements(elements);
        this.updatePages();
        this.setButtons(this.getPageContents(this.page));
    }

    /**
     * @return Elements of the inventory
     */
    @Contract(" -> new")
    public @NotNull Multimap<Integer, InventoryButton> getElements() {
        return ArrayListMultimap.create(this.elements);
    }

    /**
     * Set the elements of the inventory
     *
     * @param elements New elements of the inventory
     */
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

    /**
     * Gets copy of the element slots
     *
     * @return element slot array
     */
    @Contract(" -> new")
    public int[] getElementSlots() {
        return this.elementSlots.clone();
    }

    /**
     * @param page Page index
     * @return Elements of the page
     */
    public @NotNull Map<Integer, InventoryButton> getPageContents(int page) {
        Map<Integer, InventoryButton> content = new HashMap<>(this.elementSlots.length);
        int i = 0;

        for (InventoryButton inventoryButton : this.elements.get(page)) {
            content.put(this.elementSlots[i], inventoryButton);
            i++;
        }

        for (int slot : this.elementSlots) {
            content.putIfAbsent(slot, null);
        }
        return content;
    }

    /**
     * Creates an inventory page with the specified index and content
     *
     * @param page Page index
     * @return Page of the inventory
     */
    public @Nullable ListedInventory createPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        if (page >= this.pagesSize) return null;

        ListedInventory listedInventory = (ListedInventory) this.clone();
        listedInventory.setPageIndex(page);
        listedInventory.setButtons(this.getPageContents(page));
        return listedInventory;
    }

    /**
     * Updates the pages of the inventory
     * <br>
     * <b>Warning:</b> This method is expensive and should only be called when necessary
     */
    public void updatePages() {
        this.pages.clear();

        for (int page = 0; page < this.pagesSize; page++) {
            this.pages.put(page, this.createPage(page));
        }

        this.updateStaticButtons();
    }

    /**
     * Sets the pages size
     *
     * @param pagesSize New pages size
     */
    @Override
    protected void setPagesSize(@Range(from = 0, to = Integer.MAX_VALUE) int pagesSize) {
        this.pagesSize = pagesSize;
    }
}
