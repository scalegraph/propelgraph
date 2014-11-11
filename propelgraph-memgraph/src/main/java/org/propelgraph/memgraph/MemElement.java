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
import com.tinkerpop.blueprints.Element;  // TP2.4
import com.tinkerpop.blueprints.Direction;  // TP2.4

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

abstract public class MemElement implements Element {
	//MemGraph g;
	String id;
	HashMap<String,Object> hm = new HashMap<String,Object>();


	protected MemElement(String id) {
		this.id = id;
	}

	@Override
	public Object getId() {
		return id;
	}

	@Override
	@SuppressWarnings ("unchecked")
	public <T> T getProperty(String propname) {
		Object retval = hm.get(propname);
		//if (retval instanceof T) return (T) retval;
		//String ss = retval.toString();
		//if (ss instanceof T) return ss;
		return (T)retval;
		//throw new ClassCastException();
	}

	@Override
	@SuppressWarnings ("unchecked")
	public <T> T removeProperty(String propname) {
		T retval = (T) hm.get(propname);
		hm.remove(propname);
		return (T)retval;
	}

	@Override
	public void setProperty(String propname, Object val) {
		if (propname.equals("")) throw new IllegalArgumentException();
		if (propname.equals("id")) throw new IllegalArgumentException();
		if (propname.equals("label")) throw new IllegalArgumentException(); // I think this restriction is only for edges in version 2.4, but in future versions I think it will totally go away or also apply to vertices
		if (null==val) throw new IllegalArgumentException();
		//if ("".equals(propname)) throw new IllegalArgumentException();
		hm.put(propname, val);
	}

	@Override
	public Set<String> getPropertyKeys() {
		return new HashSet<String>(hm.keySet()); // note: The TinkerPop 2.4 VertexTestSuite.testConcurrentModificationOnProperties requires that this method create a duplicate copy of the key set for the sake of concurrent update.  This is probably poor design.  IMHO the caller should do it in the rare occassions where it matters.
	}

}