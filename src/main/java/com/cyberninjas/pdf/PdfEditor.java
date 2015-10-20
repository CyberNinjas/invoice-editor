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
package com.cyberninjas.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An base class for parsing and writing content to existing PDF files.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public abstract class PdfEditor {

    /**
     * For reading PDF source file.
     */
    private final PdfReader reader;

    /**
     * For editing the PDF source file.
     */
    private final PdfStamper stamper;

    /**
     * A stream for buffering PDF content before outputting.
     */
    private final ByteArrayOutputStream baos;

    /**
     * Constructs an instance of {@code PdfEditor}.
     *
     * @param src the source PDF file location.
     * @throws IOException on I/O error reading PDF source file.
     */
    public PdfEditor(final String src)
            throws IOException {
        reader = new PdfReader(src);

        baos = new ByteArrayOutputStream();

        try {
            stamper = new PdfStamper(reader, baos);
        } catch (DocumentException ex) {
            throw new PdfException("Failed to iniatialize PdfStamper", ex);
        }
    }

    /**
     * Get {@link PdfReader} for the PDF source file.
     *
     * @return {@link PdfReader} for the PDF source file.
     */
    protected PdfReader getReader() {
        return reader;
    }

    /**
     * Get {@link PdfStamper} for the PDF destination file.
     *
     * @return {@link PdfStamper} for the PDF destination file.
     */
    protected PdfStamper getStamper() {
        return stamper;
    }

    /**
     * Write text to a PDF in given format.
     *
     * @param pageNum the page to write to.
     * @param text the text to write.
     * @param font to use.
     * @param size the size of font.
     * @param align the alignment.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param z the rotation.
     */
    protected void writeText(final int pageNum, final String text, final BaseFont font, final int size, final Alignment align, final float x, final float y, final float z) {
        PdfContentByte canvas = stamper.getOverContent(pageNum);

        canvas.beginText();
        canvas.setFontAndSize(font, size);
        canvas.showTextAligned(convertHorizontalAlignment(align), text, x, y, z);
        canvas.endText();
    }

    /**
     * Write the PDF bytes to an output stream.
     *
     * @param os the {@code OutputStream} to write content to.
     * @throws IOException on I/O error
     */
    public void writeTo(final OutputStream os)
            throws IOException {
        try {
            stamper.close();
        } catch (DocumentException ex) {
            throw new PdfException("Failed to close PdfStamper", ex);
        }

        reader.close();

        baos.writeTo(os);
    }

    /**
     * Converts an {@link Alignment} to a value recognized by iText.
     *
     * @param align the alignment type.
     * @return the horizontal alignment recognized by iText.
     */
    private int convertHorizontalAlignment(final Alignment align) {
        switch (align) {
            case LEFT:
                return Element.ALIGN_LEFT;

            case RIGHT:
                return Element.ALIGN_RIGHT;

            case CENTER:
                return Element.ALIGN_CENTER;

            default:
                return Element.ALIGN_UNDEFINED;
        }
    }

}
