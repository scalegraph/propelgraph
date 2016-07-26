/*
 * This file is part of the ScaleGraph?PropelGraph project (http://scalegraph.org).
 *
 * This file is licensed to You under the Eclipse Public License (EPL);
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright ScaleGraph Team 2015.
 */
package org.propelgraph;

import com.tinkerpop.blueprints.Graph;

/**
 * Internal and external ids are used to refer to elements of a 
 * graph. 
 *  
 * Internal id's tend to provide very fast access to an element, 
 * but are generated by the graph implementation and not 
 * something a calling application can specify.  They are 
 * distinct from the Element itself in that they are small, keep 
 * no state of their own and are valid across commits. (Of note: 
 * the existance of an external id does not guarantee that the 
 * element still exists, but it does indicate that it has 
 * existed.)   They usually are only useful as parameters to 
 * methods like getVertex or getEdge. 
 *  
 * External id's are different because the external id value is 
 * specified by code that calls the graph implementation 
 * requesting that an Element be created. They also are ususally
 * persistable and generally useful even after graph process 
 * terminates and restarts. The mapping from an external id
 * to Element is usually not as fast as the mapping from an 
 * internal id to an Element. 
 *  
 * This class is provided to provide a simple cross engine 
 * interface for graphs that support internal ids and for
 * calling code that prefers to call methods that allow them to 
 * be more explicit about whether they want a persistable 
 * (external) id or if a more efficient id is sufficient. 
 *  
 * Of note: The TinkerPop 2 interface seems to suggest that the 
 * getId() methods return an external id, but we have found that 
 * some implementations tend to return an internal id instead. 
 * ((todo: cite a specific example)) 
 *  
 * @author ccjason
 *
 */
public interface ExternalIdSupport {
	
    public Object getExternalId();

    /**
     * Design note: Currently this method returns object.  We might
     * choose to provide a similar method that returns a long rather 
     * than an Object since the highly efficient nature of internal 
     * ids means they are usually a primitive type like integer or 
     * long. 
     * 
     * @author ccjason (6/20/2016)
     * 
     * @return long 
     */
    public long getInternalId();
}