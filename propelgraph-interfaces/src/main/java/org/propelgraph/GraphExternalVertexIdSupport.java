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
 * Note: we're still evolving this interface. 
 *  
 * This interface is designed to allow calling code to find a 
 * vertex based on the value that was specified when the Vertex 
 * was created. Blueprints suggests that this can be done with 
 * the getVertex command, but in most implementations it's 
 * actually the internal (implementation dependent numerical) id 
 * that is searched for and the paramater passed to the 
 * addVertex() method is ignored. Gremlin demos out there 
 * reflect this.  As a result we've create a new interface. 
 *
 * This interface is for Graph implementations that do respect the parameter passed to the addVertex method and provide                                       
 * a good/fast way to search for a vertex based on that value.                                                                                                
 *                                                                                                                                                            
 * Also see the ExternalVertexIdSubrangeQueryableGraph interface for similar functionality.                                                                   

 */
public interface GraphExternalVertexIdSupport extends Graph {


    public Vertex getVertexByExternalId(String exid);

    public String getVertexExternalId(Vertex v);

}

