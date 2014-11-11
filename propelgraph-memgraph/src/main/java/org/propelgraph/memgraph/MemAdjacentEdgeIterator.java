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
package org.propelgraph.memgraph;

//TP3.0.0 import com.tinkerpop.gremlin.structure.Graph; //TP3.0.0
//TP3.0.0 import com.tinkerpop.gremlin.structure.Vertex; //TP3.0.0
//TP3.0.0 import com.tinkerpop.gremlin.structure.Edge; //TP3.0.0

import com.tinkerpop.blueprints.Graph;  // TP2.4
import com.tinkerpop.blueprints.Vertex;  // TP2.4
import com.tinkerpop.blueprints.Edge;  // TP2.4

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.HashSet;

public class MemAdjacentEdgeIterator implements Iterable<Edge>, /*Iterable<MemEdge>,*/ Iterator<Edge>/*, Iterator<MemEdge>*/ {
	int state; // 0 starting

	MemVertex vertTo;
	MemVertex vertFrom;
	//MemGraph g;
	String label;
	HashSet<String> hsLabels;

	MemEdge edgeCurrent;

	//MemGlobalEdgeIterator(MemGraph g, String label) {
	//	this.g = g;
	//	this.label = label;
	//	_firstCheck();
	//}
	/*MemAdjacentEdgeIterator(MemVertex vFrom, MemVertex vTo, String label) {
		vertFrom = vFrom;
		vertTo = vTo;
		this.label = label;
		_firstCheck();
	}*/

	MemAdjacentEdgeIterator(MemVertex vFrom, MemVertex vTo, String... labels) {
		vertFrom = vFrom;
		vertTo = vTo;
		if ((labels==null) || (labels.length==0)) {
		} else if (labels.length==1) {
			label = labels[0];
		} else {
			this.hsLabels = new HashSet<String>(labels.length);
			for (String label : labels) {
				hsLabels.add(label);
			}
		}
		_firstCheck();
	}

	private void _firstCheck() {
		//if (g!=null) {
		//	state = 100;
		//	edgeCurrent = g.edgeFirst;
		//	while ((edgeCurrent!=null) && (label!=null) && (!label.equals(edgeCurrent.label))) edgeCurrent = edgeCurrent.nextGlobal;
		//	if (edgeCurrent==null) state = 3; // done
		//	return;
		//}
		if (vertFrom!=null) {
			if (vertFrom.edgeOutFirst!=null) {
				edgeCurrent = vertFrom.edgeOutFirst;
				while ((edgeCurrent!=null) && (((label!=null) && (!label.equals(edgeCurrent.label))) || ((hsLabels!=null) && !hsLabels.contains(edgeCurrent.label)))) edgeCurrent = edgeCurrent.nextFromSrc;
				if (edgeCurrent!=null) {
					state = 1;
					return;
				}
			}
		}
		if (vertTo!=null) {
			if (vertTo.edgeInFirst!=null) {
				edgeCurrent = vertTo.edgeInFirst;
				while ((edgeCurrent!=null) && (((label!=null) && (!label.equals(edgeCurrent.label))) || ((hsLabels!=null) && !hsLabels.contains(edgeCurrent.label)))) {
					edgeCurrent = edgeCurrent.nextToTgt;
				}
				if (edgeCurrent!=null) {
					state = 2;
					return;
				}
			}
		}
		state = 3;  // done;
		return;
	}

	@Override
	public Iterator<Edge> iterator() { 
		_firstCheck();  // todo: technically this is not correct.  We're supposed to issue a new iterator for each call rather than reuse an old one which might be in use
		return this; 
	}

	@Override
	public boolean hasNext() {
		if (state==3) return false;
		return true;
	}

	@Override
	public Edge next() throws NoSuchElementException {
		if (state==3) throw new NoSuchElementException();
		MemEdge retval = edgeCurrent;
		if (false) {
		//} else if (state==100) {
		//	edgeCurrent = edgeCurrent.nextGlobal;
		//	while ((edgeCurrent!=null) && (label!=null) && (!label.equals(edgeCurrent.label))) edgeCurrent = edgeCurrent.nextGlobal;
		} else if (state==1) {
			edgeCurrent = edgeCurrent.nextFromSrc;
			while ((edgeCurrent!=null) && (((label!=null) && (!label.equals(edgeCurrent.label))) || ((hsLabels!=null) && !hsLabels.contains(edgeCurrent.label)))) edgeCurrent = edgeCurrent.nextFromSrc;
			if (edgeCurrent!=null) return retval;
			state = 2;
			if (vertTo==null) { state = 3; return retval; }
			edgeCurrent = vertTo.edgeInFirst;
			while ((edgeCurrent!=null) && (((label!=null) && (!label.equals(edgeCurrent.label))) || ((hsLabels!=null) && !hsLabels.contains(edgeCurrent.label)))) {
				edgeCurrent = edgeCurrent.nextToTgt;
			}
		} else if (state==2) {
			edgeCurrent = edgeCurrent.nextToTgt;
			while ((edgeCurrent!=null) && (((label!=null) && (!label.equals(edgeCurrent.label))) || ((hsLabels!=null) && !hsLabels.contains(edgeCurrent.label)))) {
				edgeCurrent = edgeCurrent.nextToTgt;
			}
		}
		if (edgeCurrent == null) state = 3;
		return retval;
	}

	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}