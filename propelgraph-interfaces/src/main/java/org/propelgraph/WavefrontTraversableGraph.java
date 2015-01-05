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
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;
import java.util.Set;
import java.io.IOException;

/**
 * This interface exists to provide a generic interface for a 
 * method to do breadth-first search while maximizing any 
 * parallelism that the graph implementation might have.
 * 
 * @author ccjason (11/9/2014)
 */
public interface WavefrontTraversableGraph extends Graph {
    /**
     * Obtains the vertices that are adjacent to the specified 
     * vertices in the specified direction along edges with the 
     * specified label.  This method should work in a fashion 
     * similar to the TinkerPop Blueprints Vertex.getVertices() 
     * method. 
     *  
     * @author ccjason (1/5/2015)
     * 
     * @param setRootVertices  the set of vertices from which the 
     *  		       traversal will be made.
     * @param direction   direction of the traversal from the 
     *  		  specified vertices.
     * @param label label of edges that should be considered.
     * 
     * @return Set<Vertex> 
     */
    public Set<Vertex> getVertices( Set<Vertex> setRootVertices, Direction direction, String label ) throws IOException;

    /**
     * behaves as {@link getVertices} except that the 
     * setRootVertices parameter specifies the id of the vertices 
     * rather than the Vertex pointer. 
     * 
     * @author ccjason (1/5/2015)
     * 
     * @param setRootVertices 
     * @param direction 
     * @param label 
     * 
     * @return Set<Vertex> 
     */
    public Set<Vertex> getVerticesByRootIds( Set<String> setRootVertices, Direction direction, String label) throws IOException;
}

