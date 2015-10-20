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

import com.cyberninjas.invoice.InvoiceEditor;
import com.cyberninjas.pdf.Alignment;
import com.cyberninjas.pdf.PageVector;
import com.cyberninjas.pdf.PdfEditor;
import com.cyberninjas.pdf.PdfException;
import com.cyberninjas.pdf.TextChunkExtractionStrategy;
import com.cyberninjas.pdf.TextChunkExtractionStrategy.TextChunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.Vector;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class for editing PDF invoices.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public class PdfInvoiceEditor extends PdfEditor implements InvoiceEditor {

    private static final Log log = LogFactory.getLog(PdfInvoiceEditor.class);

    /**
     * For formatting currency amounts.
     */
    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    /**
     * A map containing the page and locations to write the cumulative cost amounts.
     */
    private final Map<String, PageVector> cumulativeCostLocationMap = new HashMap();

    /**
     * The page and location to write the cumulative cost subtotal.
     */
    private PageVector cumulativeCostSubtotalLocation;

    /**
     * The page and location to write the total funded amount.
     */
    private PageVector totalFundedAmountLocation;

    /**
     * The default font used.
     */
    private BaseFont defaultFont;

    /**
     * The default font size.
     */
    private int defaultSize;

    /**
     * Settings used for parsing and writing content.
     */
    private final PdfInvoiceSettings settings;

    /**
     * Constructs an instance of {@code PdfInvoiceEditor}.
     *
     * @param src the source PDF file location.
     * @throws IOException on I/O error reading source file.
     */
    public PdfInvoiceEditor(String src)
            throws IOException {
        this(src, new PdfInvoiceSettings());
    }

    /**
     * Constructs an instance of {@code PdfInvoiceEditor}.
     *
     * @param src the source PDF file location.
     * @param settings for overriding some used for parsing and writing content.
     * @throws IOException on I/O error reading source file.
     */
    public PdfInvoiceEditor(String src, PdfInvoiceSettings settings)
            throws IOException {
        super(src);

        this.settings = settings;
    }

    /**
     * Set the default font and size.
     *
     * @param font path to the .ttf of the default font.
     * @param size default font size
     * @throws IOException on I/O error reading font file.
     */
    public void setFontAndSize(final String font, final int size)
            throws IOException {
        try {
            defaultFont = BaseFont.createFont(font, BaseFont.WINANSI, BaseFont.EMBEDDED);
        } catch (DocumentException ex) {
            throw new PdfException("Failed to configure font", ex);
        }

        defaultFont.setSubset(true);

        defaultSize = size;
    }

    /**
     * Parses PDF to determine location of text.
     *
     * @throws IOException on I/O error parsing PDF.
     */
    public void parseContent()
            throws IOException {
        PdfReader reader = getReader();

        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        InvoiceTextExtractionStrategy strategy;

        for (int pageNum = 1; pageNum <= getReader().getNumberOfPages(); pageNum++) {
            strategy = parser.processContent(pageNum, new InvoiceTextExtractionStrategy());

            strategy.parse(settings);

            for (String itemId : strategy.getCumulativeCostLocationMap().keySet()) {
                cumulativeCostLocationMap.put(itemId, new PageVector(pageNum, strategy.getCumulativeCostLocationMap().get(itemId)));
            }

            //set the location to write the cumulative cost subtotal
            if (cumulativeCostSubtotalLocation == null
                    && strategy.getCumulativeCostSubtotalLocation() != null) {
                cumulativeCostSubtotalLocation = new PageVector(pageNum, strategy.getCumulativeCostSubtotalLocation());
            }

            // set the location to write the total funded amount
            if (totalFundedAmountLocation == null
                    && strategy.getTotalFundedAmountLocation() != null) {
                totalFundedAmountLocation = new PageVector(pageNum, strategy.getTotalFundedAmountLocation());
            }
        }
    }

    @Override
    public List<String> listItemIds() {
        return new ArrayList(cumulativeCostLocationMap.keySet());
    }

    /**
     * Writes the cumulative cost amount to the given ItemId row.
     *
     * @param itemId the ItemId.
     * @param amount the amount to write.
     */
    @Override
    public void writeCumulativeCost(String itemId, double amount) {
        PageVector cumulativeCostLocation = cumulativeCostLocationMap.get(itemId);

        if (cumulativeCostLocation != null) {
            writeText(cumulativeCostLocation.getPageNum(), numberFormat.format(amount), settings.getCumulativeCostAlignment(), cumulativeCostLocation.get(Vector.I1), cumulativeCostLocation.get(Vector.I2));
        } else {
            log.warn("Failed to write the cumulative cost for item id [" + itemId + "]. Location not identified.");
        }
    }

    /**
     * Writes the cumulative cost subtotal amount to the identified location.
     *
     * @param amount the amount to write.
     */
    @Override
    public void writeCumulativeCostSubtotal(final double amount) {
        if (cumulativeCostSubtotalLocation != null) {
            writeText(cumulativeCostSubtotalLocation.getPageNum(), numberFormat.format(amount), settings.getCumulativeCostSubtotalAlignment(), cumulativeCostSubtotalLocation.get(Vector.I1), cumulativeCostSubtotalLocation.get(Vector.I2));
        } else {
            log.warn("Failed to write the cumulative cost subtotal. Location not identified.");
        }
    }

    /**
     * Writes the total funded amount to the identified location.
     *
     * @param amount the amount to write.
     */
    @Override
    public void writeTotalFundedAmount(final double amount) {
        if (totalFundedAmountLocation != null) {
            writeText(totalFundedAmountLocation.getPageNum(), numberFormat.format(amount), settings.getTotalFundedAmountAlignment(), totalFundedAmountLocation.get(Vector.I1), totalFundedAmountLocation.get(Vector.I2));
        } else {
            log.warn("Failed to write the total funded amount. Location not identified.");
        }
    }

    /**
     * Write text to a PDF in given format and location.
     *
     * @param pageNum the page to write to.
     * @param text the text to write.
     * @param align the alignment.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public final void writeText(final int pageNum, final String text, final Alignment align, final float x, final float y) {
        writeText(pageNum, text, defaultFont, defaultSize, align, x, y, 0);
    }

    /**
     * Write text relative to the the first match of the reference text.
     *
     * <p>
     * Note: Due to the way PDF stores text, found blocks of text may contain additional text beyond the reference text.
     * This can cause blocks to be larger than expected requiring a larger or smaller offset to be set to align
     * properly.</p>
     *
     * @param text the text to write.
     * @param referenceText the reference text to write relative to.
     * @param offset the offset to write relative to the reference text.
     * @param align the alignment.
     * @throws IOException on I/O error.
     */
    public final void writeTextAtOffset(String text, String referenceText, float offset, final Alignment align)
            throws IOException {
        this.writeTextAtOffset(text, referenceText, offset, align, false);
    }

    /**
     * Write text relative to the matching reference text.
     *
     * <p>
     * Note: Due to the way PDF stores text, found blocks of text may contain additional text beyond the reference text.
     * This can cause blocks to be larger than expected requiring a larger or smaller offset to be set to align
     * properly.</p>
     *
     * @param text the text to write.
     * @param referenceText the reference text to write relative to.
     * @param offset the offset to write relative to the reference text.
     * @param align the alignment.
     * @param findAll indicates if text should be written at every occurrence or only the first.
     * @throws IOException on I/O error.
     */
    public final void writeTextAtOffset(String text, String referenceText, float offset, final Alignment align, boolean findAll)
            throws IOException {
        PdfReader reader = getReader();

        PdfReaderContentParser parser = new PdfReaderContentParser(reader);

        TextChunkExtractionStrategy strategy;

        for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
            strategy = parser.processContent(pageNum, new TextChunkExtractionStrategy());

            if (findAll) {
                for (TextChunk textChunk : strategy.matchAllText(referenceText)) {
                    this.writeText(pageNum, text, align, textChunk.getEndLocation().get(Vector.I1) + offset, textChunk.getEndLocation().get(Vector.I2));
                }
            } else {
                TextChunk textChunk = strategy.matchText(referenceText);

                if (textChunk != null) {
                    this.writeText(pageNum, text, align, textChunk.getEndLocation().get(Vector.I1) + offset, textChunk.getEndLocation().get(Vector.I2));
                }
            }
        }
    }

}
