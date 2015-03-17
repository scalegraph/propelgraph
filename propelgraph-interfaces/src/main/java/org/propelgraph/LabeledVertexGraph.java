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
import com.tinkerpop.blueprints.Vertex;


/**                                                                                                                                                           
 * This interface is generally for graphs that support labels
 * (aka class specifier) on vertices.
 *  
 * @author ccjason (3/16/2015) 
 */
public interface LabeledVertexGraph extends Graph {

    /**                                                                                                                                                   
	 * adds a vertex with the specified id and label to the graph 
     *                                                                                                                                                    
     * @author ccjason (8/19/2014)                                                                                                                        
     *                                                                                                                                                    
	 * @param id 
	 *    This parameter can be ignored if the graph ignores the id
	 *    parameter in the addVertex method.
	 *  
     * @param label
     */
    public Vertex addLabeledVertex( Object id, String label);

}


