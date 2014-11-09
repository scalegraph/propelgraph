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
 * A graph that can be reconstituted from a graph url.   
 * 
 * @author drewvale
 *
 */
public interface LocatableGraph extends Graph {

	
	
	/**
	 * This method must return the classname of a class that can be used to reconstruct the graph object when
	 * given the value returned by getURLPath().
	 * 
	 * @return  The name of a class that implements the xxxx interface.
	 */
	String getFactoryClassName();
	
}
