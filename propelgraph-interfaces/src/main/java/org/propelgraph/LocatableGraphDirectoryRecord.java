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

/**
 * This interface works in conjuction with the {@link 
 * LocatableGraphDirectory} interface to provide a way for 
 * callers to get a list of graphs in a given directory and 
 * present a user interface describing the directory. 
 *  
 * This interface represents the directory information for a 
 * single Graph. 
 * 
 * @author ccjason (11/9/2014)
 */
public interface LocatableGraphDirectoryRecord {
	/**
	 * returns the url string that can be used to rehydrate the 
	 * given graph. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @return String 
	 */
	public String getURL();

	/**
	 * returns a name of the given graph that is suitable for a user 
	 * interface listing a directory of graphs. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @return String 
	 */
	public String getGraphName();
}
