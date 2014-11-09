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
 * This interface allows the caller to check if the 
 * implementation of a generic interface has really implemented 
 * a method of that interface.  This saves the caller from
 * having to surround every call to that method with a try-catch 
 * block for the UnsupportedMethodException.
 *  
 * @author ccjason (11/7/2014)
 */
public interface OptionalMethodVerifier {

    
    //public static final String supmethod_examplemethod = "method:MyInterfaceName:ExampleMethod";

    /**
     * Returns true if the specified method is supported. This 
     * allows the caller to check if a particular method is 
     * supported.  The interface or class that declares that method 
     * should define the static final {@link String} that the caller
     * should pass to denote a given method. 
     *  
     * 
     * @author ccjason (11/7/2014)
     * 
     * @param methodid the identifier of the method being checked.  
     *                 For the sake of runtime performance, the
     *  	       implementations of this method are allowed to
     *                 do simple pointer checks rather than string
     *  	       comparisons. Callers should pass a pointer to
     *  	       the declared static final value rather than
     *  	       pass pointers to values they've constructed.
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

