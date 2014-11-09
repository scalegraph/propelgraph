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

import com.tinkerpop.blueprints.Graph;  // TP2.4
import com.tinkerpop.blueprints.Vertex;  // TP2.4
import com.tinkerpop.blueprints.Edge;  // TP2.4
import com.tinkerpop.blueprints.Direction;  // TP2.4
import com.tinkerpop.blueprints.VertexQuery;  // TP2.4
import com.tinkerpop.blueprints.util.DefaultVertexQuery; // TP2.4

import java.util.HashMap;
import java.util.HashSet;

public class MemVertex extends MemElement implements Vertex {
	MemGraph g;
	MemEdge edgeOutFirst = null;
	MemEdge edgeInFirst = null;
	MemVertex vertNextGlobal;
	MemVertex vertPrevGlobal;

	MemVertex(MemGraph g, String id) { 
		super(id); //System.out.println("creating vertex with id: "+id+" cls="+(id.getClass()));
		this.g = g;
		if (id!=null) g.hmVerts.put(id, this);
		vertNextGlobal = g.vertFirst;
		if (vertNextGlobal!=null) vertNextGlobal.vertPrevGlobal = this;
		vertPrevGlobal = null;
		g.vertFirst = this;
	}

	public Iterable<Edge> getEdges( Direction dir, String ... labels) {
		if (labels.length==0) {
			Iterable<Edge> retval = (Iterable<Edge>) new MemAdjacentEdgeIterator(dir==Direction.IN ? null : this,  dir==Direction.OUT ? null : this, null);
			return retval;
		} else if (labels.length==1) {
			Iterable<Edge> retval = (Iterable<Edge>) new MemAdjacentEdgeIterator(dir==Direction.IN ? null : this,  dir==Direction.OUT ? null : this, labels[0]);
			return retval;
		} else {
			Iterable<Edge> retval = (Iterable<Edge>) new MemAdjacentEdgeIterator(dir==Direction.IN ? null : this,  dir==Direction.OUT ? null : this, labels);
			return retval;
		}
	}

	public void remove() {
		MemEdge e = edgeOutFirst;
		while (e!=null) {
			MemEdge eNext = e.nextFromSrc;
			e.remove();
			e = eNext;
		}
		e = edgeInFirst;
		while (e!=null) {
			MemEdge eNext = e.nextToTgt;
			e.remove();
			e = eNext;
		}
		if (vertPrevGlobal==null) {
			g.vertFirst = vertNextGlobal;
			if (vertNextGlobal != null) vertNextGlobal.vertPrevGlobal = null;
		} else {
			vertPrevGlobal.vertNextGlobal = vertNextGlobal;
			if (vertNextGlobal!=null) vertNextGlobal.vertPrevGlobal = vertPrevGlobal;
		}
	}

	@Override
	public Edge addEdge(String label, Vertex vertTgt) {
		if (null==label) throw new IllegalArgumentException();
		//MemEdge retval = new MemEdge(g, null, this, (MemVertex)vertTgt, label);
		MemEdge retval = g.addMemEdge(null, this, (MemVertex)vertTgt, label);
		return retval;
	}

	@Override
	public VertexQuery query() {
		return new DefaultVertexQuery(this);
	}

	@Override
	public Iterable<Vertex> getVertices(Direction dir, String ... labels ) {
		if (labels.length==0) {
			Iterable<Vertex> retval = (Iterable<Vertex>) new MemAdjacentVertexIterator(dir==Direction.IN ? null : this,  dir==Direction.OUT ? null : this, null);
			return retval;
		} else if (labels.length==1) {
			Iterable<Vertex> retval = (Iterable<Vertex>) new MemAdjacentVertexIterator(dir==Direction.IN ? null : this,  dir==Direction.OUT ? null : this, labels[0]);
			return retval;
		} else {
			Iterable<Vertex> retval = (Iterable<Vertex>) new MemAdjacentVertexIterator(dir==Direction.IN ? null : this,  dir==Direction.OUT ? null : this, labels);
			return retval;
		}
	}

	public void setProperty(String propname, Object val) {
		HashMap<Object,HashSet<MemVertex>> index = g.indicesForVerts.get(propname);
		if (index != null) {
			Object oldval = getProperty(propname);
			super.setProperty(propname, val);
			if (oldval!=null) {
				if (oldval.equals(val)) return;
				index.get(oldval).remove(this);
			}
			HashSet<MemVertex> hsM = index.get(val);
			if (null==hsM) { 
				hsM = new HashSet<MemVertex>();
				index.put(val,hsM);
			}
			hsM.add(this);
		} else {
			super.setProperty(propname, val);
		}
	}
}