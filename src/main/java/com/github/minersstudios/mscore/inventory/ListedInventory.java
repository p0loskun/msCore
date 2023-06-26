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

    /**
     * Listed inventory with pages
     *
     * @param title        Title of the inventory
     * @param verticalSize Vertical size of the inventory
     */
    public ListedInventory(
            @NotNull String title,
            @Range(from = 1, to = 6) int verticalSize
    ) {
        super(title, verticalSize);
    }

    /**
     * @return True if the inventory has any static buttons
     */
    public boolean hasStaticButtons() {
        return !this.staticButtons.isEmpty();
    }

    /**
     * Sets static buttons in this inventory
     *
     * @param buttons Static buttons to set
     * @throws IllegalArgumentException If any of the static buttons is out of inventory size
     * @see #setStaticButtonAt(int, StaticInventoryButton)
     */
    public void setStaticButtons(@NotNull Map<Integer, StaticInventoryButton> buttons) throws IllegalArgumentException {
        for (Map.Entry<Integer, StaticInventoryButton> entry : buttons.entrySet()) {
            this.setStaticButtonAt(entry.getKey(), entry.getValue());
        }

        this.updateStaticButtons();
    }

    /**
     * Sets static button at specified slot
     * <br>
     * Static buttons are buttons that do not change when the page changes
     *
     * @param slot   Slot to set static button at
     * @param button Static button to set
     * @throws IllegalArgumentException If slot is out of inventory size
     */
    public void setStaticButtonAt(
            @Range(from = 0, to = LAST_SLOT) int slot,
            @NotNull StaticInventoryButton button
    ) throws IllegalArgumentException {
        if (slot >= this.size) {
            throw new IllegalArgumentException();
        }

        this.staticButtons.put(slot, button);
    }

    /**
     * @param slot Slot to get button from
     * @return {@link StaticInventoryButton} / {@link InventoryButton} at specified slot or null if there is no button
     */
    @Override
    public @Nullable InventoryButton getButtonAt(@Range(from = 0, to = Integer.MAX_VALUE) int slot) {
        StaticInventoryButton staticButton = this.staticButtons.getOrDefault(slot, null);
        if (staticButton != null) {
            return staticButton.getButton(this);
        }
        return this.buttons.getOrDefault(slot, null);
    }

    /**
     * Updates static buttons in all pages
     */
    public void updateStaticButtons() {
        if (this.hasStaticButtons()) {
            for (Map.Entry<Integer, StaticInventoryButton> entry : this.staticButtons.entrySet()) {
                for (ListedInventory listedInventory : this.pages.values()) {
                    listedInventory.setItem(entry.getKey(), entry.getValue().getButton(listedInventory).getItem());
                }
            }
        }
    }

    /**
     * Updates static buttons in specified page
     *
     * @param page Page to update static buttons in
     */
    public void updateStaticButtons(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        ListedInventory listedInventory = this.pages.get(page);
        if (this.hasStaticButtons()) {
            for (Map.Entry<Integer, StaticInventoryButton> entry : this.staticButtons.entrySet()) {
                listedInventory.setItem(entry.getKey(), entry.getValue().getButton(listedInventory).getItem());
            }
        }
    }

    /**
     * Integer - page index
     * <br>
     * ListedInventory - page
     *
     * @return Map of pages
     */
    public @NotNull Map<Integer, ListedInventory> getPages() {
        return this.pages;
    }

    /**
     * @param page page index
     * @return Page at specified index or null if there is no page
     */
    public @Nullable ListedInventory getPage(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        return this.pages.getOrDefault(page, null);
    }

    /**
     * Adds new page to the {@link #pages} with next index and static buttons
     *
     * @return New page
     */
    public @NotNull ListedInventory addPage() {
        int page = this.pagesSize;
        ListedInventory listedInventory = (ListedInventory) this.clone();

        listedInventory.setPageIndex(page);
        this.pages.put(page, listedInventory);
        this.updateStaticButtons(page);
        this.setPagesSize(this.pages.size());
        return listedInventory;
    }

    /**
     * @return Current page index
     */
    public int getPageIndex() {
        return this.page;
    }

    /**
     * Sets current page index
     *
     * @param page Page index to set
     */
    protected void setPageIndex(@Range(from = 0, to = Integer.MAX_VALUE) int page) {
        this.page = page;
    }

    /**
     * @return Next page index or -1 if there is no next page
     */
    public int getNextPageIndex() {
        int next = this.page + 1;
        return next >= this.pagesSize ? -1 : next;
    }

    /**
     * @return Previous page index or -1 if there is no previous page
     */
    public int getPreviousPageIndex() {
        int previous = this.page - 1;
        return previous < 0 ? -1 : previous;
    }

    /**
     * @return Pages size
     */
    public int getPagesSize() {
        return this.pagesSize;
    }

    /**
     * Sets pages size
     * <br>
     * This method is used to update pages size in all pages
     *
     * @param pagesSize Pages size to set
     */
    protected void setPagesSize(@Range(from = 0, to = Integer.MAX_VALUE) int pagesSize) {
        for (ListedInventory listedInventory : this.pages.values()) {
            listedInventory.pagesSize = pagesSize;
        }
    }
}
