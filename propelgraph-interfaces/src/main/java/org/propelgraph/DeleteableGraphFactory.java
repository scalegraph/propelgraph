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
 * Implementations of this interface and the LocatableGraphFactory interface work together to delete graphs
 * objects.
 * 
 * @author drewvale
 *
 */
public interface DeleteableGraphFactory {

	/**
	 * This method to delete a LocatableGraph object indicated by the provided urlPath. Usually this interface 
	 * is an object that is also a LocatableGraphFactory.
	 * 
	 * @param urlPath
	 * @return
	 */
	void delete(String urlPath);
	
}
