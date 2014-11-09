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

public interface ClearableGraph extends Graph {
    /**                                                                                                                                                   
     * Clear out all edges and vertices of graph.  Meta info might be retained.                                                                           
     *                                                                                                                                                    
     * @throws IOException                                                                                                                                
     */
    public void clear() throws IOException;
}

