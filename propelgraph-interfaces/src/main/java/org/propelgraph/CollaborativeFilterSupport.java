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
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

/**
 * This interface allows one to declare support for various 
 * collaborative filtering methods. 
 *  
 * @author ccjason (11/7/2014)
 */
public interface CollaborativeFilterSupport {

    
    public static final String supmethod_simpleCF = "method:CollaborativeFilterSupport:simpleCF";

    /**
     * This method allows the caller to check if a particular 
     * collaborative filter method is supported. The caller should 
     * pass one of the constants listed above.  For the sake of 
     * runtime performance, the implementations of this method are 
     * permitted to simply do a pointer match on the methodid 
     * parameter rather than a string match.
     * 
     * @author ccjason (11/7/2014)
     * 
     * @param methodid 
     * 
     * @return boolean 
     */
    public boolean supportedMethod(String methodid );

    /**
     * This method implements a very simple form of collaborative 
     * filtering.   This method usually would be a method on the 
     * source vertex. 
     *  
     * @author ccjason (11/7/2014)
     * 
     * @param dirHop1 
     * @param label 
     *  
     * @return Set<Vertex> 
     */
    public Iterable<Vertex> simpleCF( Direction dirHop1, String label ) throws UnsupportedMethodException;
}

