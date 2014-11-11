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

import java.util.HashMap;
import java.util.HashSet;

public class MemEdge extends MemElement implements Edge {
	String label;
	String id;
	MemVertex vertSrc, vertTgt;
	MemEdge nextFromSrc, prevFromSrc;
	MemEdge nextToTgt, prevToTgt;
	MemEdge nextGlobal, prevGlobal;


	protected MemEdge(MemGraph g, String id, MemVertex vertSrc, MemVertex vertTgt, String label ) {
		super(id);//  System.out.println("adding edge  "+vertSrc.getId()+"--"+label+"-->"+vertTgt.getId()+"  id="+id);
		this.label = label;
		this.vertSrc = vertSrc;
		this.vertTgt = vertTgt;
		nextFromSrc = vertSrc.edgeOutFirst;
		if (nextFromSrc!=null) nextFromSrc.prevFromSrc = this;
		vertSrc.edgeOutFirst = this;
		prevFromSrc = null;
		nextToTgt = vertTgt.edgeInFirst;
		if (nextToTgt!=null) nextToTgt.prevToTgt = this;
		vertTgt.edgeInFirst = this;
		prevToTgt = null;
		nextGlobal = g.edgeFirst;
		if (nextGlobal!=null) nextGlobal.prevGlobal = this;
		g.edgeFirst = this;
		prevGlobal = null; 
		if (id!=null) g.hmEdges.put(id, this);
	}

	@Override 
	public void remove() {
		if (prevFromSrc==null) {
			vertSrc.edgeOutFirst = nextFromSrc;
			if (nextFromSrc!=null) nextFromSrc.prevFromSrc = null;
		} else {
			prevFromSrc.nextFromSrc = nextFromSrc;
			if (nextFromSrc!=null) nextFromSrc.prevFromSrc = prevFromSrc;
		}
		if (prevToTgt==null) {
			vertTgt.edgeInFirst = nextToTgt;
			if (nextToTgt!=null) nextToTgt.prevToTgt = null;
		} else {
			prevToTgt.nextToTgt = nextToTgt;
			if (nextToTgt!=null) nextToTgt.prevToTgt = prevToTgt;
		}
		if (prevGlobal==null) {
			vertSrc.g.edgeFirst = nextGlobal;
			if (nextGlobal!=null) nextGlobal.prevGlobal = null;
		} else {
			prevGlobal.nextGlobal = nextGlobal;
			if (nextGlobal!=null) nextGlobal.prevGlobal = prevGlobal;
		}
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Vertex getVertex(Direction dir) throws IllegalArgumentException {
		if (dir == Direction.OUT ) {
			return vertSrc;
		} else if (dir == Direction.IN ) {
			return vertTgt;
		} else {
			throw new IllegalArgumentException("bad Direction parameter");
		}
	}

	public void setProperty(String propname, Object val) {
		MemGraph g = vertSrc.g;
		HashMap<Object,HashSet<MemEdge>> index = g.indicesForEdges.get(propname);
		if (index != null) {
			Object oldval = getProperty(propname);
			super.setProperty(propname, val);
			if (oldval!=null) {
				if (oldval.equals(val)) return;
				index.get(oldval).remove(this);
			}
			HashSet<MemEdge> hsM = index.get(val);
			if (null==hsM) { 
				hsM = new HashSet<MemEdge>();
				index.put(val,hsM);
			}
			hsM.add(this);
		} else {
			super.setProperty(propname, val);
		}
	}


}