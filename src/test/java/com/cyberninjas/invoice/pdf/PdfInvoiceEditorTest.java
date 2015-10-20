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
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit test for {@code PdfInvoiceEditorTest}.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public class PdfInvoiceEditorTest {

    final String font = "src/test/resources/calibri.ttf";
    final int size = 9;
    final String src_sample1 = "src/test/resources/samples/sample1.pdf";
    final String src_sample2 = "src/test/resources/samples/sample2.pdf";
    final String dest_sample1 = "src/test/resources/generated/sample1_filled.pdf";
    final String dest_sample2 = "src/test/resources/generated/sample2_filled.pdf";

    public PdfInvoiceEditorTest() {
    }

    @org.junit.Test
    public void testListItemIds_sample1()
            throws IOException, DocumentException {
        PdfInvoiceEditor pdfInvoiceEditor = new PdfInvoiceEditor(src_sample1);

        pdfInvoiceEditor.setFontAndSize(font, size);

        pdfInvoiceEditor.parseContent();

        List<String> itemIdList = pdfInvoiceEditor.listItemIds();

        assertNotNull(itemIdList);
        assertTrue(!itemIdList.isEmpty());
        assertEquals(1, itemIdList.size());
        assertTrue(itemIdList.containsAll(Arrays.asList("BOOK")));
    }

    @org.junit.Test
    public void testListItemIds_sample2()
            throws IOException, DocumentException {
        PdfInvoiceEditor pdfInvoiceEditor = new PdfInvoiceEditor(src_sample2);

        pdfInvoiceEditor.setFontAndSize(font, size);

        pdfInvoiceEditor.parseContent();

        List<String> itemIdList = pdfInvoiceEditor.listItemIds();

        assertNotNull(itemIdList);
        assertTrue(!itemIdList.isEmpty());
        assertEquals(18, itemIdList.size());
        assertTrue(itemIdList.containsAll(Arrays.asList("12312312", "112233", "GB3-White", "BOOK", "GB6-White", "PM-BR", "TSS - Black", "TSM - Black", "TSL - Black", "Train-MS", "Support-M", "COOKIE", "GB9-White", "SPX-321-I", "GB1-White", "CIO-SP3-TRACK3", "WAEH", "32-55R32")));
    }

    @org.junit.Test
    public void testWriteTo_sample1()
            throws IOException, DocumentException {
        PdfInvoiceEditor pdfInvoiceEditor = new PdfInvoiceEditor(src_sample1);

        pdfInvoiceEditor.setFontAndSize(font, size);

        pdfInvoiceEditor.parseContent();

        pdfInvoiceEditor.writeCumulativeCost("BOOK", 100);
        pdfInvoiceEditor.writeCumulativeCostSubtotal(100);
        pdfInvoiceEditor.writeTotalFundedAmount(999.99);

        pdfInvoiceEditor.writeTo(new FileOutputStream(dest_sample1));
    }

    @org.junit.Test
    public void testWriteTo_sample2()
            throws IOException, DocumentException {
        PdfInvoiceEditor pdfInvoiceEditor = new PdfInvoiceEditor(src_sample2);

        pdfInvoiceEditor.setFontAndSize(font, size);

        pdfInvoiceEditor.parseContent();

        pdfInvoiceEditor.writeCumulativeCost("12312312", 99.01);
        pdfInvoiceEditor.writeCumulativeCost("112233", 24.02);
        pdfInvoiceEditor.writeCumulativeCost("GB3-White", 443.03);
        pdfInvoiceEditor.writeCumulativeCost("BOOK", 3245.04);
        pdfInvoiceEditor.writeCumulativeCost("GB6-White", 34.05);
        pdfInvoiceEditor.writeCumulativeCost("PM-BR", 60.06);
        pdfInvoiceEditor.writeCumulativeCost("TSS - Black", 8.07);
        pdfInvoiceEditor.writeCumulativeCost("TSM - Black", 54.08);
        pdfInvoiceEditor.writeCumulativeCost("TSL - Black", 443.09);
        pdfInvoiceEditor.writeCumulativeCost("Train-MS", 121.10);
        pdfInvoiceEditor.writeCumulativeCost("Support-M", 167.11);
        pdfInvoiceEditor.writeCumulativeCost("COOKIE", 668.12);
        pdfInvoiceEditor.writeCumulativeCost("GB9-White", 4.13);
        pdfInvoiceEditor.writeCumulativeCost("SPX-321-I", 89.14);
        pdfInvoiceEditor.writeCumulativeCost("GB1-White", 6.15);
        pdfInvoiceEditor.writeCumulativeCost("CIO-SP3-TRACK3", 90.16);
        pdfInvoiceEditor.writeCumulativeCost("WAEH", 949.17);
        pdfInvoiceEditor.writeCumulativeCost("32-55R32", 9.18);
        pdfInvoiceEditor.writeCumulativeCostSubtotal(6514.71);
        pdfInvoiceEditor.writeTotalFundedAmount(14573.00);

        pdfInvoiceEditor.writeTo(new FileOutputStream(dest_sample2));
    }

    @org.junit.Test
    public void testWriteAtOffset()
            throws IOException {
        PdfInvoiceEditor pdfInvoiceEditor = new PdfInvoiceEditor(src_sample1);

        pdfInvoiceEditor.setFontAndSize(font, size);

        pdfInvoiceEditor.writeTextAtOffset("Hello World!", "INVOICE", 10, Alignment.LEFT);

        pdfInvoiceEditor.writeTo(new FileOutputStream("src/test/resources/generated/testWriteAtOffset.pdf"));
    }

}
