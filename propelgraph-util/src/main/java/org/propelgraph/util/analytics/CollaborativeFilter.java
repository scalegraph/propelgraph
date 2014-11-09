/*
* This file is part of the ScaleGraph/PropelGraph project (http://scalegraph.org).
*
* This file is licensed to You under the Eclipse Public License (EPL);
* You may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.opensource.org/licenses/eclipse-1.0.php
*
* (C) Copyright ScaleGraph Team 2014.
*/
package org.propelgraph.util.analytics;


import java.util.HashSet;
import java.util.Set;

import org.propelgraph.CollaborativeFilterSupport;
import org.propelgraph.UnsupportedMethodException;
//import com.tinkerpop.blueprints.Features;
//import com.tinkerpop.blueprints.KeyIndexableGraph;
//import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

/**
 * This class provides helper functions for doing collaborative
 * filtering.  It provides generic implementations, but where 
 * possible, delegates to the graph|vertex, which is likely to 
 * result in higher performance. 
 * 
 * @author ccjason (11/6/2014)
 */
public class CollaborativeFilter {


    static public Iterable<Vertex> simpleCF( Vertex vertSrc, Direction dirHop1, String label ) {
	Iterable<Vertex> retval = null;
	if (vertSrc instanceof CollaborativeFilterSupport) {
	    CollaborativeFilterSupport cf = (CollaborativeFilterSupport)vertSrc;
	    if (cf.supportedMethod(cf.supmethod_simpleCF)) {
		retval = cf.simpleCF(dirHop1,label);
	    }
	}
	if (retval==null) {
	    HashSet<Vertex> retset = new HashSet<Vertex>();
	    Direction dirHop2 = (dirHop1==Direction.BOTH) ? dirHop1 : (dirHop1==Direction.IN) ? Direction.OUT : Direction.IN;
	    Iterable<Vertex> itVerts1 = ((label==null) || (label.length()==0)) ? vertSrc.getVertices(dirHop1) : vertSrc.getVertices(dirHop1,label);
	    for (Vertex v1 : itVerts1) {
		Iterable<Vertex> itVerts2 = ((label==null) || (label.length()==0)) ? v1.getVertices(dirHop2) : v1.getVertices(dirHop2,label);
		for (Vertex v2 : itVerts2) {
		    retset.add(v2);
		}
	    }
	    retval = retset;
	}
	return retval;

    }

}
