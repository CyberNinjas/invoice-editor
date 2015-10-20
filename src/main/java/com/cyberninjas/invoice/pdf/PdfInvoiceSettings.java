/*
 * Copyright (C) 2015 Clifford Errickson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cyberninjas.invoice.pdf;

import com.cyberninjas.pdf.Alignment;

/**
 * Settings used to parse and write content to the PDF invoice.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public class PdfInvoiceSettings {

    /**
     * Cumulative cost table heading text.
     *
     * Default = "Cumulative Cost".
     */
    private String cumulativeCostHeadingText = "Cumulative Cost";

    /**
     * Subtotal label text.
     *
     * Default = "Subtotal".
     */
    private String subtotalLabelText = "Subtotal";

    /**
     * Total funded amount label text.
     *
     * Default = "Total Funded Amount".
     */
    private String totalFundedAmountLabelText = "Total Funded Amount";

    /**
     * The pattern used to identify an item row.
     *
     * The default layout identifies rows with the following three amounts:
     * <ol>
     * <li>A quantity (Hours)</li>
     * <li>A dollar amount (Rate)</li>
     * <li>A dollar amount (Cost this period)</li>
     * </ol>
     */
    private String itemRowPattern = ".*[0-9]+.[0-9]{2} \\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})? \\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?";

    /**
     * Used to identify ItemIds.
     *
     * Default = "|".
     */
    private String itemIdSeparator = "|";

    /**
     * The alignment for cumulative cost amounts.
     *
     * Default = RIGHT
     */
    private Alignment cumulativeCostAlignment = Alignment.RIGHT;

    /**
     * The alignment for cumulative cost subtotal.
     *
     * Default = RIGHT
     */
    private Alignment cumulativeCostSubtotalAlignment = Alignment.RIGHT;

    /**
     * The alignment for the total funded amount.
     *
     * Default = LEFT
     */
    private Alignment totalFundedAmountAlignment = Alignment.LEFT;

    /**
     * The horizontal offset from the end of the total funded amount label.
     *
     * Default = 5
     */
    private float totalFundedAmountOffset = 5f;

    /**
     * Constructs an instance of {@code PdfInvoiceSettings}.
     */
    public PdfInvoiceSettings() {

    }

    /**
     * Get the text used to identify the cumulative cost heading.
     *
     * @return the text used to identify the cumulative cost heading.
     */
    public String getCumulativeCostHeadingText() {
        return cumulativeCostHeadingText;
    }

    /**
     * Set the text used to identify the cumulative cost heading.
     *
     * @param cumulativeCostHeadingText the text used to identify the cumulative cost heading.
     */
    public void setCumulativeCostHeadingText(final String cumulativeCostHeadingText) {
        this.cumulativeCostHeadingText = cumulativeCostHeadingText;
    }

    /**
     * Get the text used to identify the cumulative cost subtotal label.
     *
     * @return the text used to identify the cumulative cost subtotal label.
     */
    public String getSubtotalLabelText() {
        return subtotalLabelText;
    }

    /**
     * Set the text used to identify the cumulative cost subtotal label.
     *
     * @param subtotalLabelText the text used to identify the cumulative cost subtotal label.
     */
    public void setSubtotalLabelText(final String subtotalLabelText) {
        this.subtotalLabelText = subtotalLabelText;
    }

    /**
     * Get the text to use to identify the total funded amount label.
     *
     * @return the text to use to identify the total funded amount label.
     */
    public String getTotalFundedAmountLabelText() {
        return totalFundedAmountLabelText;
    }

    /**
     * Set the text to use to identify the total funded amount label.
     *
     * @param totalFundedAmountLabelText the text to use to identify the total funded amount label.
     */
    public void setTotalFundedAmountLabelText(final String totalFundedAmountLabelText) {
        this.totalFundedAmountLabelText = totalFundedAmountLabelText;
    }

    /**
     * Get the horizontal offset from the total funded amount label.
     *
     * @return the horizontal offset from the total funded amount label.
     */
    public float getTotalFundedAmountOffset() {
        return totalFundedAmountOffset;
    }

    /**
     * Set the horizontal offset from the total funded amount label.
     *
     * @param totalFundedAmountOffset the horizontal offset from the total funded amount label.
     */
    public void setTotalFundedAmountOffset(float totalFundedAmountOffset) {
        this.totalFundedAmountOffset = totalFundedAmountOffset;
    }

    /**
     * Get the pattern used to identify ItemId rows.
     *
     * @return the pattern used to identify ItemId rows.
     */
    public String getItemRowPattern() {
        return itemRowPattern;
    }

    /**
     * Set the pattern used to identify ItemId rows.
     *
     * @param itemRowPattern the pattern used to identify ItemId rows.
     */
    public void setItemRowPattern(final String itemRowPattern) {
        this.itemRowPattern = itemRowPattern;
    }

    /**
     * Get the character used to identify ItemIds.
     *
     * @return the character used to identify ItemIds.
     */
    public String getItemIdSeparator() {
        return itemIdSeparator;
    }

    /**
     * Set the character used to identify ItemIds.
     *
     * @param itemIdSeparator the character used to identify ItemIds.
     */
    public void setItemIdSeparator(final String itemIdSeparator) {
        this.itemIdSeparator = itemIdSeparator;
    }

    /**
     * Get the alignment for cumulative cost amounts.
     *
     * @return the alignment for cumulative cost amounts.
     */
    public Alignment getCumulativeCostAlignment() {
        return cumulativeCostAlignment;
    }

    /**
     * Set the alignment for cumulative cost amounts.
     *
     * @param cumulativeCostAlignment the alignment for cumulative cost amounts.
     */
    public void setCumulativeCostAlignment(final Alignment cumulativeCostAlignment) {
        this.cumulativeCostAlignment = cumulativeCostAlignment;
    }

    /**
     * Get the alignment for cumulative cost subtotal amount.
     *
     * @return the alignment for cumulative cost subtotal amount.
     */
    public Alignment getCumulativeCostSubtotalAlignment() {
        return cumulativeCostSubtotalAlignment;
    }

    /**
     * Set the alignment for cumulative cost subtotal amount.
     *
     * @param cumulativeCostSubtotalAlignment the alignment for cumulative cost subtotal amount.
     */
    public void setCumulativeCostSubtotalAlignment(final Alignment cumulativeCostSubtotalAlignment) {
        this.cumulativeCostSubtotalAlignment = cumulativeCostSubtotalAlignment;
    }

    /**
     * Get the alignment for the total funded amount.
     *
     * @return the alignment for the total funded amount.
     */
    public Alignment getTotalFundedAmountAlignment() {
        return totalFundedAmountAlignment;
    }

    /**
     * Set the alignment for the total funded amount.
     *
     * @param totalFundedAmountAlignment the alignment for the total funded amount.
     */
    public void setTotalFundedAmountAlignment(final Alignment totalFundedAmountAlignment) {
        this.totalFundedAmountAlignment = totalFundedAmountAlignment;
    }

}
