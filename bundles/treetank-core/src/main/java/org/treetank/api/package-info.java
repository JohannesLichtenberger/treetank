/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */

/**
 * TreeTank API
 * 
 * This package contains the public TreeTank API. Users will have to connect to any TreeTank through this API.
 * Note that for common usage, only access-interfaces provided by this package should be used.
 * 
 * <h2>Usage of Treetank</h2> Treetank is based on three layers of interaction:
 * <ul>
 * <li>IDatabase: This layer denotes a persistent database. Each database can be created using one specific
 * <code>DatabaseConfiguration</code>. Afterwards, this configuration is valid for the whole lifetime of the
 * database.</li>
 * <li>ISession: This layer denotes a runtime access on the database. Only one Session is allowed at one time.
 * The layer has ability to provide runtime-settings as well. Especially settings regarding the
 * transaction-handling can be provided. See <code>SessionConfiguration</code> for more information</li>
 * <li>IReadTransaction/IWriteTransaction: This layer provided direct access to the database. All access to
 * nodes used either a <code>IReadTransaction</code> or <code>IWriteTransaction</code>.
 * </ul>
 * Additional to these access-interfaces, this api-packages provides direct access-methods for the
 * node-structure:
 * <ul>
 * <li>IAxis: This interface is for providing common access to different axis. All axis are listed in the
 * axis-package. The idea is to provide a method for iterating over all nodes denoted by this axis.</li>
 * <li>IItem: Each node in the treestructure is encapsulated by this interface.</li>
 * <li>IItemList: To provide common usage of the included XPath 2.0-engine, this interface provides a liste
 * for the resulting items.</li>
 * <li>IFilter: Easy filtering of axis-based node-access is possible with the help if this interface.</li>
 * </ul>
 * 
 * 
 * 
 * @author Marc Kramis, University of Konstanz
 * @author Sebastian Graf, University of Konstanz
 */
package org.treetank.api;
