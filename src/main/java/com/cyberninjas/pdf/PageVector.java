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

import com.itextpdf.text.pdf.parser.Vector;

/**
 * Represents a vector (i.e. a point in space) and a page number for locating content in a PDF document.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public class PageVector extends Vector {

    /**
     * The containing page number.
     */
    private final int pageNum;

    /**
     * Creates a new PageVector.
     *
     * @param pageNum the page number
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public PageVector(final int pageNum, final float x, final float y, final float z) {
        super(x, y, z);

        this.pageNum = pageNum;
    }

    /**
     * Creates a new PageVector.
     *
     * @param pageNum the page number
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public PageVector(final int pageNum, final float x, final float y) {
        this(pageNum, x, y, 0);
    }

    /**
     * Creates a new PageVector.
     *
     * @param pageNum the page number
     * @param vector the point in space.
     */
    public PageVector(final int pageNum, final Vector vector) {
        this(pageNum, vector.get(Vector.I1), vector.get(Vector.I2), vector.get(Vector.I3));
    }

    @Override
    public String toString() {
        return new StringBuilder("page [").append(pageNum).append("] at (").append(super.toString()).append(")").toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        
        hash = 47 * hash + this.pageNum + super.hashCode();
        
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final PageVector other = (PageVector) obj;
        
        if (this.pageNum != other.pageNum) {
            return false;
        }
        
        return super.equals(obj);
    }

    /**
     * Get the page number.
     *
     * @return the page number.
     */
    public int getPageNum() {
        return pageNum;
    }

}
