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
 * absense of those methods, one can use the generic helper 
 * methods provided by propelgraph-utils. 
 *  
 * The CSV format that is preferred is the format that is used 
 * by LibreOffice's CSV support's default setting.  It treats 
 * spaces as significant.  Adjacent commas as a value with zero
 * length. Double quotes can be used to enclose values that 
 * contain commas. To put double quotes inside the quoted area, 
 * a pair of quotes are used. (Ex. ,"The president said, ""Ich 
 * bin ein Berliner"" when he visited Berlin",)  Backward 
 * leaning slashes do not have special meaning.
 *                                                                                                                                                            
 * @author ccjason (8/19/2014) 
 * @see org.propelgraph.util.LoadCSV 
 */
public interface CSVFileLoadingGraph extends Graph {

    /**                                                                                                                                                   
     * populates a graph from a CSV Vertex File.  It does not assume
     * the graph is initially empty. 
     *  
     * A CSV vertex file is a CSV file where the first line 
     * specifies labels for the columns and each of the remaining 
     * rows specifies a vertex external id in the first column and 
     * vertex properties in the remaining columns. 
     *                                                                                                                                                    
     * @author ccjason (8/19/2014)                                                                                                                        
     *                                                                                                                                                    
     * @param fn                                                                                                                                          
     * @param maxlines                                                                                                                                    
     */
    public void addCSVVertexFile(String fn, long maxlines);


    /**                                                                                                                                                   
     * populates a graph from a CSV Edge File.  It does not assume  
     * the graph is empty, but it will create vertices if needed and                                                                                      
     * not present.                                                                                                                                       
     *                                                                                                                                                    
     * A CSV edge file is a CSV file where the first line specifies
     * labels for the columns and each of the remaining rows 
     * specifies an edge.  Each of those rows specifies in order the
     * source vertex external id, the target vertex external id, the
     * edge label, and each of the edge properties.
     *                                                                                                                                                    
     * @author ccjason (8/19/2014)                                                                                                                        
     *                                                                                                                                                    
     * @param fn                                                                                                                                          
     * @param maxlines                                                                                                                                    
     */
    public void addCSVEdgeFile(String fn, long maxlines);

}


