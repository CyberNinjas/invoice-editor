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
package com.cyberninjas.invoice;

import java.util.List;

/**
 * An interface for editing invoices.
 *
 * @author Clifford Errickson
 * @since 1.0
 */
public interface InvoiceEditor {

    /**
     * Retrieves a list of ItemIds on an invoice.
     *
     * @return list of ItemIds
     */
    List<String> listItemIds();

    /**
     * Writes the cumulative cost amount associated with an ItemId on an invoice.
     *
     * @param itemId the ItemId to write to
     * @param amount the cumulative cost amount to write
     */
    void writeCumulativeCost(String itemId, double amount);

    /**
     * Writes the cumulative cost subtotal amount on an invoice.
     *
     * @param amount the cumulative cost subtotal to write
     */
    void writeCumulativeCostSubtotal(double amount);

    /**
     * Writes the total funded amount on an invoice.
     *
     * @param amount the total funded amount to write
     */
    void writeTotalFundedAmount(double amount);

}
