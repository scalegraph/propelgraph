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
 * Implementations of this interface and the LocatableGraph 
 * interface work together to help persist and rehydrate graph 
 * objects.  This class implements the concept of a group of 
 * graphs called a "directory".  This concept can be useful in 
 * user interfaces that want to list a set of graphs that the 
 * user can act on. 
 * 
 * @author drewvale
 *
 */
public interface LocatableGraphDirectory {
	static final String  SCHEME_PGGRAPHDIR = "pggraphdir";

	/**
	 * initializes the newly created directory object.
	 * 
	 * These classes will be created via the default constructor and need to be initialized.  
	 * This is the method that will be called to do that. 
	 *  
	 * Design: we are still refining this interface.  Changes to 
	 * this interface are likely. 
	 * 
	 * @param url that specifies the location of the directory of 
	 *            interest.
	 */
	public void init(String url); 
	
	/**
	 * 
	 * @return
	 */
	/**
	 * returns a list of url records for locatable Graphs in this
	 * directory. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @return List<LocatableGraphDirectoryRecord> a list of url 
	 *         records
	 */
	public List<LocatableGraphDirectoryRecord> getRecords();
	
}
