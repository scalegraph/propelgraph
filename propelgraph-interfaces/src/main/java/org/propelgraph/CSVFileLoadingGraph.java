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
 * This interface is generally for classes that have methods                                                                                                  
 * that are particularly fast at loading CSV files.  In the                                                                                                   
 * absense of those methods, one can use the generic methods                                                                                                  
 * provided by the SGTestUtils repo  LoadCSV class.                                                                                                           
 *                                                                                                                                                            
 * @author ccjason (8/19/2014)                                                                                                                                
 */
public interface CSVFileLoadingGraph extends Graph {

    /**                                                                                                                                                   
     * Populates a graph from a CSV Vertex File.  It does not assume                                                                                      
     * the graph is initially empty.                                                                                                                      
     *                                                                                                                                                    
     * @author ccjason (8/19/2014)                                                                                                                        
     *                                                                                                                                                    
     * @param fn                                                                                                                                          
     * @param maxlines                                                                                                                                    
     */
    public void addCSVVertexFile(String fn, long maxlines);


    /**                                                                                                                                                   
     * Populates a graph from a CSV Edge File.  It does not assume                                                                                        
     * the graph is empty, but it will create vertices if needed and                                                                                      
     * not present.                                                                                                                                       
     *                                                                                                                                                    
     * @author ccjason (8/19/2014)                                                                                                                        
     *                                                                                                                                                    
     * @param fn                                                                                                                                          
     * @param maxlines                                                                                                                                    
     */
    public void addCSVEdgeFile(String fn, long maxlines);

}


