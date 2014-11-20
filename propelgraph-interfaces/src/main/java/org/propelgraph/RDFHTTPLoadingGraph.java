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
 * that are particularly fast at loading RDF from HTTP streams. 
 *  Particularlly gzip compressed streams of Turtle format.
 *  
 * @author ccjason (8/19/2014) 
 * @see org.propelgraph.util.LoadCSV 
 */
public interface RDFHTTPLoadingGraph extends Graph {

    /**                                                                                                                                                   
     * populates a graph from a RDF resource at the specified HTTP 
     * url. That data should be in turtle or n-triple format.  It 
     * does not assume the graph is initially empty. 
     *  
     *                                                                                                                                                    
     * @author ccjason (8/19/2014)                                                                                                                        
     *                                                                                                                                                    
     * @param url 
     * @param maxlines number of lines of rdf file to read
     */
    public void add_turtle_rdf_data_from_http_url(String url, long maxlines);


}


