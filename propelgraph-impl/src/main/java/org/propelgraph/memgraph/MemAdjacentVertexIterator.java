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
import com.tinkerpop.blueprints.Direction;  // TP2.4

import java.util.NoSuchElementException;
import java.util.Iterator;

public class MemAdjacentVertexIterator implements Iterable<Vertex>, /*Iterable<MemEdge>,*/ Iterator<Vertex>/*, Iterator<MemEdge>*/ {
	int state; // 0 starting

	//MemVertex vertTo;
	//MemVertex vertFrom;
	//MemGraph g;
	//String label;

	//MemEdge edgeCurrent;
	MemAdjacentEdgeIterator iter;
	MemVertex vThis;

	/*MemEdgeIterable(MemGraph g, String label) {
		this.g = g;
		this.label = label;
		_firstCheck();
	}*/
	/*MemAdjacentVertexIterator(MemVertex vFrom, MemVertex vTo, String label) {
		iter = new MemAdjacentEdgeIterator(vFrom, vTo, label);
		if (vFrom!=null) vThis = vFrom;
		if (vTo!=null) vThis = vTo;
	}*/

	MemAdjacentVertexIterator(MemVertex vFrom, MemVertex vTo, String... labels) {
		iter = new MemAdjacentEdgeIterator(vFrom, vTo, labels);
		if (vFrom!=null) vThis = vFrom;
		if (vTo!=null) vThis = vTo;
	}


	@Override
	public Iterator<Vertex> iterator() { return this; }

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public Vertex next() throws NoSuchElementException {
		Edge e = iter.next();
		Vertex v = e.getVertex(Direction.OUT);
		if (v!=vThis) return v;
		return e.getVertex(Direction.IN);
	}

	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}