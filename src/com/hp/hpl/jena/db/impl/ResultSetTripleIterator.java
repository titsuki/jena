/*
 *  (c) Copyright Hewlett-Packard Company 2003
 *  All rights reserved.
 *
 *
 */

//=======================================================================
// Package
package com.hp.hpl.jena.db.impl;

//=======================================================================
// Imports
import java.sql.*;

import com.hp.hpl.jena.db.RDFRDBException;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.util.Log;

//=======================================================================
/**
* Version of ResultSetIterator that extracts database rows as Triples.
*
* @author hkuno.  Based on ResultSetResource Iterator, by Dave Reynolds, HPLabs, Bristol <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a>
* @version $Revision: 1.1 $ on $Date: 2003-04-25 02:57:17 $
*/
public class ResultSetTripleIterator extends ResultSetIterator {

    /** The rdf model in which to instantiate any resources */
    protected IDBID m_graphID;

    /** The database driver, used to access namespace and resource caches */
    protected IPSet m_pset;
    
    /** Holds the current row as a triple */
    protected Triple m_triple;

	// Constructor
	public ResultSetTripleIterator(IPSet p, IDBID graphID) {
		m_pset = p;
		setGraphID(graphID);
	}
	
	/**
	 * Set m_graphID.
	 * @param gid is the id of the graph associated with this iterator.
	 */
	public void setGraphID(IDBID gid) {
		m_graphID = gid;
	}
	
	/**
	 * Reset an existing iterator to scan a new result set.
	 * @param resultSet the result set being iterated over
	 * @param sourceStatement The source Statement to be cleaned up when the iterator finishes - return it to cache or close it if no cache
	 * @param cache The originating SQLcache to return the statement to, can be null
	 * @param opname The name of the original operation that lead to this statement, can be null if SQLCache is null
	 */
	public void reset(ResultSet resultSet, PreparedStatement sourceStatement, SQLCache cache, String opname) {
		super.reset(resultSet, sourceStatement, cache, opname);
		m_triple = null;
	}

    /**
     * Extract the current row into a triple. 
     * Requires the row to be of the form:
     *   subject URI (String)
     *   predicate URI (String)
     *   object URI (String)
     *   object value (String)
     *   Object literal id (Object)
     * 
     * The object of the triple can be either a URI, a simple literal (in 
     * which case it will just have an object value, or a complex literal 
     * (in which case both the object value and the object literal id 
     * columns may be populated.
     */
    protected void extractRow() throws SQLException, RDFException {
        int rx = 1;
        ResultSet rs = m_resultSet;
        String subjURI = rs.getString(1);
		String predURI = rs.getString(2);
		String objURI = rs.getString(3);
		String objVal = rs.getString(4);
		Object litId = rs.getObject(5);
		
		String objRef = null;
		if (litId != null) {
			objRef = litId.toString();
			} 
		
		Triple t = null;
		
		try {
        t = m_pset.extractTripleFromRowData(subjURI, predURI, objURI, objVal, objRef);
		} catch (RDFRDBException e) {
			Log.debug("Extracting triple from row encountered exception: " + e);
		}
		
		m_triple = t;
		
	}
	
		/**
		 * Return the current row, which should have already been extracted.
		 */
		protected Object getRow() {
			return m_triple;
		}
		
		/** 
		 * Remove the current triple from the data store.
		 */
		public void remove() {
			if (m_triple == null)
				  throw new IllegalStateException();
			m_pset.deleteTriple(m_triple, m_graphID);
		}

} // End class

/*
 *  (c) Copyright Hewlett-Packard Company 2003
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

