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
 * Implementations of this interface and the {@link 
 * LocatableGraphFactory} interface work together to delete 
 * graphs objects. Usually instances of this interface are also 
 * an instance of LocatableGraphFactory. 
 * 
 * @author ccjason
 *
 */
public interface DeleteableGraphFactory {

	/**
	 * deletes the graph indicated by the provided urlPath. 
	 * Subsequent attempts to open that graph should result in a 
	 * totally empty graph. 
	 * 
	 * @param urlstring
	 *  
	 * @see LocatableGraphFactory 
	 */
	void delete(String urlstring);
	
}
