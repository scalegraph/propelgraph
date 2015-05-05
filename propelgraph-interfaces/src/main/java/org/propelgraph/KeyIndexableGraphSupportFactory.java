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

import com.tinkerpop.blueprints.Graph;

/**
 * Interface for classes that will create the 
 * KeyIndexableGraphSupport object. 
 *  
 * All implementations of this class should expect to be 
 * constructed with the default constructor.
 *  
 * Note: this interface is not of interest to most users.  It is 
 * used under the covers by 
 * {@KeyIndexableGraphSupportFactoryFactoryImpl}. 
 *  
 * @author ccjason
 *  
 * @see KeyIndexableGraphSupport 
 *  
 */
public interface KeyIndexableGraphSupportFactory {

	/**
	 * Returns a KeyIndexableGraphSupport object for the given 
	 * Graph. 
	 *  
	 * Design: The implementation of this method is not likely to 
	 * cache the value returned. 
	 * 
	 * @author ccjason (5/4/2015)
	 * 
	 * @param graph 
	 * 
	 * @return KeyIndexableGraphSupport 
	 */
	KeyIndexableGraphSupport getKeyIndexableGraphSupport( Graph graph );

}
