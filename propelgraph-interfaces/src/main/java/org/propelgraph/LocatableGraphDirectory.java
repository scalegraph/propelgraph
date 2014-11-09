/*
 * This file is part of the ScaleGraph?PropelGraph project (http://scalegraph.org).
 *
 * This file is licensed to You under the Eclipse Public License (EPL);
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright ScaleGraph Team 2014.
 */
package org.propelgraph;

import java.util.List;

/**
 * Implementations of this interface and the LocatableGraph interface work together to help persist and rehydrate graph
 * objects.  This class focuses on collections of graphs.
 * 
 * @author drewvale
 *
 */
public interface LocatableGraphDirectory {
	static final String  SCHEME_PGGRAPHDIR = "pggraphdir";

	/**
	 * This method initializes the newly created object.
	 * 
	 * These classes will be created via the default constructor and need to be initialized.  
	 * This is the method that will be called to do that.
	 * 
	 * @param url
	 */
	public void init(String url); 
	
	/**
	 * This method returns a list of urls for locatable graphs
	 * 
	 * @return
	 */
	public List<LocatableGraphDirectoryRecord> getRecords();
	
}
