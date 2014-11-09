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
 * This interface is used by Graph classes that support 
 * a LocatableGraphFactory. 
 *  
 * Of note, this interface need not be supported by a Graph 
 * class to be supported by a LocatableGraphFactory class. As 
 * part of the propelgraph-neo4j subproject we provide an 
 * example of one can work around the lack of LocatableGraph 
 * awareness in a Graph implemenation.  This is done with 
 * cooperation of the {@link LocatableGraphFactoryFactoryImpl} 
 * class. 
 * 
 * @author drewvale
 *
 */
public interface LocatableGraph extends Graph {
	
	/**
	 * returns the classname of a class that can be used as a 
	 * LocatableGraphFactory for this Graph.
	 * 
	 * @return  The name of a class that implements 
	 *          LocatableGraphFactory for this Graph.
	 */
	String getFactoryClassName();
	
}
