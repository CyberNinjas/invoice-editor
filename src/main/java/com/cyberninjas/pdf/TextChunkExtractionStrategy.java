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

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.LineSegment;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A base class for extracting {@link TextChunk}s (text and its location) from a PDF source file.
 *
 * This class is based on (and a near duplicate of) iTexts {@link LocationTextExtractionStrategy} with the difference
 * that it gives subclasses access to the list of {@link TextChunk}s that are found to enable additional processing.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public class TextChunkExtractionStrategy implements RenderListener {

    /**
     * A list of text chunks found in the document.
     */
    private final List<TextChunk> textChunks = new ArrayList();

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
     */
    @Override
    public void beginTextBlock() {
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
     */
    @Override
    public void endTextBlock() {
        Collections.sort(textChunks);
    }

    /**
     * Retrieve the chunks of text that were found.
     *
     * @return the chunks of text that were found.
     */
    protected List<TextChunk> getTextChunks() {
        return textChunks;
    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {
        LineSegment segment = renderInfo.getBaseline();

        if (renderInfo.getRise() != 0) {
            // remove the rise from the baseline - we do this because the text from a super/subscript render operations should probably be considered as part of the baseline of the text the super/sub is relative to 
            segment = segment.transformBy(new Matrix(0, -renderInfo.getRise()));
        }

        textChunks.add(new TextChunk(renderInfo.getText(), segment.getStartPoint(), segment.getEndPoint(), renderInfo.getSingleSpaceWidth()));
    }

    /**
     * this renderer isn't interested in image events.
     *
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(com.itextpdf.text.pdf.parser.ImageRenderInfo)
     */
    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
        // do nothing
    }

    /**
     * Determine if string starts with a space.
     *
     * @param str the string.
     * @return true if the string starts with a space character, false if the string is empty or starts with a non-space
     * character
     */
    protected boolean startsWithSpace(String str) {
        if (str.length() == 0) {
            return false;
        }
        return str.charAt(0) == ' ';
    }

    /**
     * Determine if string ends with a space.
     *
     * @param str the string.
     * @return true if the string ends with a space character, false if the string is empty or ends with a non-space
     * character
     */
    protected boolean endsWithSpace(String str) {
        if (str.length() == 0) {
            return false;
        }
        return str.charAt(str.length() - 1) == ' ';
    }

    /**
     * Determines if a space character should be inserted between a previous chunk and the current chunk. This method is
     * exposed as a callback so subclasses can fine time the algorithm for determining whether a space should be
     * inserted or not. By default, this method will insert a space if the there is a gap of more than half the font
     * space character width between the end of the previous chunk and the beginning of the current chunk. It will also
     * indicate that a space is needed if the starting point of the new chunk appears *before* the end of the previous
     * chunk (i.e. overlapping text).
     *
     * @param chunk the new chunk being evaluated
     * @param previousChunk the chunk that appeared immediately before the current chunk
     * @return true if the two chunks represent different words (i.e. should have a space between them). False
     * otherwise.
     */
    protected boolean isChunkAtWordBoundary(TextChunk chunk, TextChunk previousChunk) {

        /**
         * Here we handle a very specific case which in PDF may look like: -.232 Tc [(
         * P)-226.2(r)-231.8(e)-230.8(f)-238(a)-238.9(c)-228.9(e)]TJ The font's charSpace width is 0.232 and it's
         * compensated with charSpacing of 0.232. And a resultant TextChunk.charSpaceWidth comes to TextChunk
         * constructor as 0. In this case every chunk is considered as a word boundary and space is added. We should
         * consider charSpaceWidth equal (or close) to zero as a no-space.
         */
        if (chunk.getCharSpaceWidth() < 0.1f) {
            return false;
        }

        float dist = chunk.distanceFromEndOf(previousChunk);

        if (dist < -chunk.getCharSpaceWidth() || dist > chunk.getCharSpaceWidth() / 2.0f) {
            return true;
        }

        return false;
    }

    /**
     * Retrieves all {@link TextChunk}s matching the given text.
     *
     * Note: Because iText has no concept of "words" this may return coordinates that represent a block of text
     * containing the desired text, not necessarily the text itself.
     *
     * @param str the text to match
     * @return the list of {@link TextChunk}s for the matching text.
     */
    public List<TextChunk> matchAllText(final String str) {
        if (str == null
                || str.isEmpty()) {
            return null;
        }

        List<TextChunk> textChunkList = new ArrayList();

        StringBuilder sb = new StringBuilder();

        Vector startLocation = null;

        TextChunk lastChunk = null;

        for (TextChunk chunk : getTextChunks()) {
            if (lastChunk == null) {
                if (eitherContainsText(chunk.getText(), str)) {
                    sb.append(chunk.getText());

                    startLocation = chunk.getStartLocation();

                    lastChunk = chunk;
                }
            } else {
                if (chunk.sameLine(lastChunk)) {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (isChunkAtWordBoundary(chunk, lastChunk) && !startsWithSpace(chunk.getText()) && !endsWithSpace(lastChunk.getText())) {
                        sb.append(' ');
                    }

                    sb.append(chunk.getText());

                    if (eitherContainsText(sb.toString(), str)) {
                        lastChunk = chunk;
                    } else {
                        lastChunk = null;
                        sb.delete(0, sb.length());
                        startLocation = null;
                    }
                } else {
                    lastChunk = null;
                    sb.delete(0, sb.length());
                    startLocation = null;

                    if (eitherContainsText(chunk.getText(), str)) {
                        sb.append(chunk.getText());

                        startLocation = chunk.getStartLocation();
                        lastChunk = chunk;
                    }
                }
            }

            if (sb.toString().contains(str)) {
                textChunkList.add(new TextChunk(sb.toString(), startLocation, chunk.getEndLocation(), chunk.getCharSpaceWidth()));

                sb.delete(0, sb.length());
                startLocation = null;
            }
        }

        return textChunkList;
    }

    /**
     * Retrieves first {@link TextChunk} matching the given text.
     *
     * Note: Because iText has no concept of "words" this may return coordinates that represent a block of text
     * containing the desired text, not necessarily the text itself.
     *
     * @param str the text to match
     * @return the {@link TextChunk} for the matching text or {@code null} if not found.
     */
    public TextChunk matchText(final String str) {
        if (str == null
                || str.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        Vector startLocation = null;

        TextChunk lastChunk = null;

        for (TextChunk chunk : getTextChunks()) {
            if (lastChunk == null) {
                if (eitherContainsText(chunk.getText(), str)) {
                    sb.append(chunk.getText());

                    startLocation = chunk.getStartLocation();

                    lastChunk = chunk;
                }
            } else {
                if (chunk.sameLine(lastChunk)) {
                    // we only insert a blank space if the trailing character of the previous string wasn't a space, and the leading character of the current string isn't a space
                    if (isChunkAtWordBoundary(chunk, lastChunk) && !startsWithSpace(chunk.getText()) && !endsWithSpace(lastChunk.getText())) {
                        sb.append(' ');
                    }

                    sb.append(chunk.getText());

                    if (eitherContainsText(sb.toString(), str)) {
                        lastChunk = chunk;
                    } else {
                        lastChunk = null;
                        sb.delete(0, sb.length());
                        startLocation = null;
                    }
                } else {
                    lastChunk = null;
                    sb.delete(0, sb.length());
                    startLocation = null;

                    if (eitherContainsText(chunk.getText(), str)) {
                        sb.append(chunk.getText());

                        startLocation = chunk.getStartLocation();
                        lastChunk = chunk;
                    }
                }
            }

            if (sb.toString().contains(str)) {
                return new TextChunk(sb.toString(), startLocation, chunk.getEndLocation(), chunk.getCharSpaceWidth());
            }
        }

        return null;
    }

    /**
     * Determine if either of the given strings contains the text of the other.
     *
     * @param s1 first string
     * @param s2 second string
     * @return true if either of the given strings contains the text of the other, false otherwise.
     */
    private boolean eitherContainsText(String s1, String s2) {
        return s1.contains(s2) || s2.contains(s1);
    }

    /**
     * Represents a chunk of text, it's orientation, and location relative to the orientation vector.
     */
    public static class TextChunk implements Comparable<TextChunk> {

        /**
         * the text of the chunk
         */
        private final String text;

        /**
         * the starting location of the chunk
         */
        private final Vector startLocation;

        /**
         * the ending location of the chunk
         */
        private final Vector endLocation;

        /**
         * unit vector in the orientation of the chunk
         */
        private final Vector orientationVector;

        /**
         * the orientation as a scalar for quick sorting
         */
        private final int orientationMagnitude;

        /**
         * perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
         * we round to the nearest integer to handle the fuzziness of comparing floats
         */
        private final int distPerpendicular;

        /**
         * distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an
         * unrotated coordinate system)
         */
        private final float distParallelStart;

        /**
         * distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated
         * coordinate system)
         */
        private final float distParallelEnd;

        /**
         * the width of a single space character in the font of the chunk
         */
        private final float charSpaceWidth;

        public TextChunk(String string, Vector startLocation, Vector endLocation, float charSpaceWidth) {
            this.text = string;
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.charSpaceWidth = charSpaceWidth;

            Vector oVector = endLocation.subtract(startLocation);
            if (oVector.length() == 0) {
                oVector = new Vector(1, 0, 0);
            }
            orientationVector = oVector.normalize();
            orientationMagnitude = (int) (Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1)) * 1000);

            // see http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
            // the two vectors we are crossing are in the same plane, so the result will be purely
            // in the z-axis (out of plane) direction, so we just take the I3 component of the result
            Vector origin = new Vector(0, 0, 1);
            distPerpendicular = (int) (startLocation.subtract(origin)).cross(orientationVector).get(Vector.I3);

            distParallelStart = orientationVector.dot(startLocation);
            distParallelEnd = orientationVector.dot(endLocation);
        }

        /**
         * @return the start location of the text
         */
        public Vector getStartLocation() {
            return startLocation;
        }

        /**
         * @return the end location of the text
         */
        public Vector getEndLocation() {
            return endLocation;
        }

        /**
         * @return the text captured by this chunk
         */
        public String getText() {
            return text;
        }

        /**
         * @return the width of a single space character as rendered by this chunk
         */
        public float getCharSpaceWidth() {
            return charSpaceWidth;
        }

        /**
         * @param as the location to compare to
         * @return true is this location is on the the same line as the other
         */
        public boolean sameLine(TextChunk as) {
            if (orientationMagnitude != as.orientationMagnitude) {
                return false;
            }

            if (distPerpendicular != as.distPerpendicular) {
                return false;
            }

            return true;
        }

        /**
         * Computes the distance between the end of 'other' and the beginning of this chunk in the direction of this
         * chunk's orientation vector. Note that it's a bad idea to call this for chunks that aren't on the same line
         * and orientation, but we don't explicitly check for that condition for performance reasons.
         *
         * @param other
         * @return the number of spaces between the end of 'other' and the beginning of this chunk
         */
        public float distanceFromEndOf(TextChunk other) {
            return distParallelStart - other.distParallelEnd;
        }

        /**
         * Compares based on orientation, perpendicular distance, then parallel distance
         *
         * @param rhs other
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(TextChunk rhs) {
            if (this == rhs) {
                return 0; // not really needed, but just in case
            }
            int rslt;
            rslt = compareInts(orientationMagnitude, rhs.orientationMagnitude);
            if (rslt != 0) {
                return rslt;
            }

            rslt = compareInts(distPerpendicular, rhs.distPerpendicular);
            if (rslt != 0) {
                return rslt;
            }

            return Float.compare(distParallelStart, rhs.distParallelStart);
        }

        /**
         * Compares two integers.
         *
         * @param int1 first integer
         * @param int2 second integer
         * @return comparison of the two integers
         */
        private static int compareInts(int int1, int int2) {
            return int1 == int2 ? 0 : int1 < int2 ? -1 : 1;
        }
    }

}
