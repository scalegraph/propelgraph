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

import com.tinkerpop.blueprints.Graph;

/**
 * This interface exists to provide a generic interface for a 
 * method to quickly clear out the vertices and edges of a graph
 * without deleting the graph.   It can be useful for unit 
 * testing and regression testing. 
 * 
 * @author ccjason (11/9/2014)
 */
public interface ClearableGraph extends Graph {
    /**                                                                                                                                                   
     * Quickly remove the edges and vertices of the graph.  Meta 
     * info will be retained. 
     *                                                                                                                                                    
     * @throws IOException                                                                                                                                
     */
    public void clear() throws IOException;
}

