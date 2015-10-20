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

import com.cyberninjas.pdf.TextChunkExtractionStrategy;
import com.itextpdf.text.pdf.parser.Vector;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A strategy for locating where to place data on an invoice based on extracted text.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public class InvoiceTextExtractionStrategy extends TextChunkExtractionStrategy {

    private static final Log log = LogFactory.getLog(InvoiceTextExtractionStrategy.class);

    /**
     * A map containing the locations to write the cumulative cost amounts.
     */
    private Map<String, Vector> cumulativeCostLocationMap;

    /**
     * The location to write the cumulative cost subtotal.
     */
    private Vector cumulativeCostSubtotalLocation;

    /**
     * The location to write the total funded amount.
     */
    private Vector totalFundedAmountLocation;

    /**
     * Constructs an instance of {@code ItemIdExtractorStrategy}.
     */
    public InvoiceTextExtractionStrategy() {
    }

    /**
     * Parses the text with a PDF based on the given settings.
     *
     * @param settings settings used to parse the document.
     */
    public void parse(PdfInvoiceSettings settings) {
        cumulativeCostLocationMap = new HashMap();
        cumulativeCostSubtotalLocation = null;
        totalFundedAmountLocation = null;

        // locate the cumulative cost heading for aligning cumulative cost amounts
        TextChunk cumulativeCostHeadingTextChunk = matchText(settings.getCumulativeCostHeadingText());

        if (cumulativeCostHeadingTextChunk == null) {
            log.warn("Failed to locate the cumulative cost heading based on the text [" + settings.getCumulativeCostHeadingText() + "]");
        }

        // locate the ItemIds and their containing rows
        StringBuilder sb = new StringBuilder();

        TextChunk lastChunk = null;

        String lastItemId = null;

        for (TextChunk chunk : getTextChunks()) {
            if (lastChunk == null) {
                sb.append(chunk.getText());
            } else {
                if (chunk.sameLine(lastChunk)) {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (isChunkAtWordBoundary(chunk, lastChunk)
                            && !startsWithSpace(chunk.getText())
                            && !endsWithSpace(lastChunk.getText())) {
                        sb.append(' ');
                    }

                    sb.append(chunk.getText());
                } else {
                    if (sb.indexOf(settings.getItemIdSeparator()) > 0) {
                        String itemId = sb.substring(0, sb.indexOf(settings.getItemIdSeparator()));

                        cumulativeCostLocationMap.put(itemId, null);

                        lastItemId = itemId;
                    }

                    if (lastItemId != null
                            && sb.toString().matches(settings.getItemRowPattern())) {
                        cumulativeCostLocationMap.put(lastItemId, new Vector(cumulativeCostHeadingTextChunk.getEndLocation().get(Vector.I1), lastChunk.getEndLocation().get(Vector.I2), 0));

                        lastItemId = null;
                    }

                    sb = new StringBuilder();

                    sb.append(chunk.getText());
                }
            }

            lastChunk = chunk;
        }

        // check if all the ItemId rows have been located - if not, position based on the location of the ItemId text
        cumulativeCostLocationMap.keySet().stream().filter((itemId) -> (cumulativeCostLocationMap.get(itemId) == null)).forEach((itemId) -> {
            TextChunk itemIdTextChunk = this.matchText(itemId);
            
            if (itemIdTextChunk != null) {
                cumulativeCostLocationMap.replace(itemId, new Vector(cumulativeCostHeadingTextChunk.getEndLocation().get(Vector.I1), itemIdTextChunk.getEndLocation().get(Vector.I2), 0));
            } else {
                log.warn("Failed to locate row for itemId [" + itemId + "]");
            }
        });

        // locate where to write the cumulative cost subtotal
        TextChunk subTotalLabel = matchText(settings.getSubtotalLabelText());

        if (subTotalLabel != null) {
            cumulativeCostSubtotalLocation = new Vector(cumulativeCostHeadingTextChunk.getEndLocation().get(Vector.I1), subTotalLabel.getEndLocation().get(Vector.I2), 0);
        }

        // locate where to write the total funded amount
        TextChunk totalFundedAmountLabel = matchText(settings.getTotalFundedAmountLabelText());

        if (totalFundedAmountLabel != null) {
            totalFundedAmountLocation = new Vector(totalFundedAmountLabel.getEndLocation().get(Vector.I1) + settings.getTotalFundedAmountOffset(), totalFundedAmountLabel.getEndLocation().get(Vector.I2), 0);
        }
    }

    /**
     * Get a map containing the cumulative cost location for each ItemId on the page.
     *
     * @return a map containing the cumulative cost location for each ItemId on the page.
     */
    public Map<String, Vector> getCumulativeCostLocationMap() {
        return cumulativeCostLocationMap;
    }

    /**
     * Get the cumulative cost subtotal location on the page.
     *
     * @return the cumulative cost subtotal location on the page.
     */
    public Vector getCumulativeCostSubtotalLocation() {
        return cumulativeCostSubtotalLocation;
    }

    /**
     * Get the total funded amount location on the page.
     *
     * @return the total funded amount location on the page.
     */
    public Vector getTotalFundedAmountLocation() {
        return totalFundedAmountLocation;
    }

}
