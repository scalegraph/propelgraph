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

import java.io.IOException;
import java.util.Set;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/** 
 *    
 *  
 * This interface is designed to allow calling code to find a 
 * vertex based on the value that was specified when the Vertex 
 * was created. The Blueprints specificiation uggests that this 
 * can be done with the getVertex command, but in many/most
 * implementations the id parameter passed to addVertex is 
 * ignored and it's an internal id that is returned by the id() 
 * {@link Vertex} method.  (One can see this in many of the 
 * Gremlin demos on the internet.)  The use of internal id has a 
 * variety of advantages that explains why so many graph 
 * implementations return an internal id when Vertex.id() is 
 * called.
 *  
 * As a result of this, we've created a new interface for {@link
 * Graph} classes that (1) do not ignore the id parameter 
 * passed in to the addVertex method, but (2) have an 
 * Vertex.id() method that returns an internal id rather than 
 * the value that was passed to addVertex. 
 *
 * Also see the ExternalVertexIdSubrangeQueryableGraph interface 
 * for similar functionality.  ([sic] This class has not yet 
 * been added.) 
 *  
 * Design: we're still evolving this interface. 
 *  
 */

public interface GraphExternalVertexIdSupport extends Graph {

	/**
	 * returns the Vertex that was created by calling 
	 * Graph.addVertex() with the same exid value. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param exid 
	 * 
	 * @return Vertex 
	 */
	public Vertex getVertexByExternalId(String exid);

	/**
	 * returns the value that was passed to addVertex() when the 
	 * specified vertex was first created. 
	 * 
	 * @author ccjason (11/9/2014)
	 * 
	 * @param v 
	 * 
	 * @return String 
	 */
	public String getVertexExternalId(Vertex v);

}
