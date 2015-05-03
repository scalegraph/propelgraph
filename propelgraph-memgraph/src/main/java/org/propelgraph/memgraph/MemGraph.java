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

import com.tinkerpop.blueprints.Graph;  // TP2.4
import com.tinkerpop.blueprints.Vertex;  // TP2.4
import com.tinkerpop.blueprints.Element;  // TP2.4
import com.tinkerpop.blueprints.Edge;  // TP2.4
import com.tinkerpop.blueprints.Parameter;  // TP2.4
import com.tinkerpop.blueprints.GraphQuery;  // TP2.4
import com.tinkerpop.blueprints.Features;  // TP2.4
import com.tinkerpop.blueprints.KeyIndexableGraph; // TP2.4
import com.tinkerpop.blueprints.util.DefaultGraphQuery; // TP2.4

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.propelgraph.LocatableGraph;

public class MemGraph implements Graph, KeyIndexableGraph, LocatableGraph {
	MemEdge edgeFirst;
	MemVertex vertFirst;
	HashMap<String,MemVertex> hmVerts = new HashMap<String,MemVertex>();
	HashMap<String,MemEdge> hmEdges = new HashMap<String,MemEdge>();

	String graphname;
	String dirpath;

	public enum MemGGraphType { INMEMORY, PERSISTENT };

	private static final Features FEATURES = new Features();

	static {
		FEATURES.supportsDuplicateEdges = true;
		FEATURES.supportsSelfLoops = true;
		FEATURES.supportsSerializableObjectProperty = true;
		FEATURES.supportsBooleanProperty = true;
		FEATURES.supportsDoubleProperty = true;
		FEATURES.supportsFloatProperty = true;
		FEATURES.supportsIntegerProperty = true;
		FEATURES.supportsPrimitiveArrayProperty = true;
		FEATURES.supportsUniformListProperty = true;
		FEATURES.supportsMixedListProperty = true;
		FEATURES.supportsLongProperty = true;
		FEATURES.supportsMapProperty = true;
		FEATURES.supportsStringProperty = true;
		
		FEATURES.ignoresSuppliedIds = false;
		FEATURES.isPersistent = false;
		FEATURES.isWrapper = false;
		
		FEATURES.supportsIndices = true;    // not yet supporting, but will
		FEATURES.supportsKeyIndices = true;
		FEATURES.supportsVertexKeyIndex = true;
		FEATURES.supportsEdgeKeyIndex = true;
		FEATURES.supportsVertexIndex = true;
		FEATURES.supportsEdgeIndex = true;
		FEATURES.supportsTransactions = false;
		FEATURES.supportsVertexIteration = true;
		FEATURES.supportsEdgeIteration = true;
		FEATURES.supportsEdgeRetrieval = true;
		FEATURES.supportsVertexProperties = true;
		FEATURES.supportsEdgeProperties = true;
		FEATURES.supportsThreadedTransactions = false;		
// prebop/postbop preprocessor used here:
/* $if TINKERPOPVERSION >= 2.6.0$ */
		FEATURES.supportsThreadIsolatedTransactions = false;  //TP2.6, we can formally add support for multithreading later
/* $endif$ */
	}

	public MemGraph( String dirpath, String graphname, MemGraph.MemGGraphType memggtype ) {
		this.dirpath = dirpath;
		this.graphname = graphname;
		if (memggtype==MemGGraphType.PERSISTENT) throw new RuntimeException("don't yet support persistent graphs");
	}

	@Override
	public Features getFeatures() {
		return FEATURES;
	}

	@Override
	public Iterable<Edge> getEdges() {
		return new MemGlobalEdgeIterator(this, null, null, null);
	}

	@Override
	public Iterable<Edge> getEdges(String propname, Object propval) {
		if ("label".equals(propname)) {
			return new MemGlobalEdgeIterator(this, propval.toString(), null, null );  // BP2.5 seems to treat label property differently
		} else {
			return new MemGlobalEdgeIterator(this, null, propname, propval.toString() );
		}
	}

	@Override
	public Iterable<Vertex> getVertices(String propname, Object propval) {
		return new MemGlobalVertexIterator(this, null, propname, propval.toString() );
	}

	@Override
	public Iterable<Vertex> getVertices() {
		return new MemGlobalVertexIterator(this, null, null, null );
	}

	@Override
	public void removeEdge(Edge edge) {
		edge.remove();
	}

	@Override
	public void removeVertex(Vertex vert) {
		vert.remove();
	}

	@Override
	public Edge getEdge(Object id) {
		if (null==id) throw new IllegalArgumentException();
		MemEdge retval = hmEdges.get(id);
		return retval;
	}

	@Override
	public Vertex getVertex(Object id) { //System.out.println("getVertex("+id+")"+" cls="+(id.getClass()));
		if (null==id) throw new IllegalArgumentException();
		MemVertex retval = hmVerts.get(id.toString());
		return retval;
	}

	public MemEdge addMemEdge(Object id, MemVertex vsrc, MemVertex vtgt, String label) {
		if (null==label) throw new IllegalArgumentException();
		String id2 = (id==null) ? null : id.toString();
		if (id2==null) {
			id2 = "_e"+hmEdges.size();
			while (hmEdges.containsKey(id2)) id2 = "_"+id2;
		}
		MemVertex vsrc1 = (MemVertex)vsrc;
		MemVertex vtgt1 = (MemVertex)vtgt;
		MemEdge retval = new MemEdge(this, id2, vsrc1, vtgt1, label);
		return retval;
	}

	@Override
	public Edge addEdge(Object id, Vertex vsrc, Vertex vtgt, String label) {
		return addMemEdge(id, (MemVertex)vsrc, (MemVertex)vtgt, label);
	}

	/**
	 * Add a vertex.  If you'd like to intercept the construction of
	 * vertex object, override this method.  It is the method that 
	 * is called internally. 
	 *  
	 * @param id - we do allow this value to be null in which case 
	 *           this implementation will chose a String value.
	 *  
	 * Of note: MemGraph uses String's for Element id's regardless 
	 * of the Object type passed in here.
	 *  
	 */
	public MemVertex addMemVertex(Object id ) {
		String id2 = (id==null) ? null : id.toString();
		if (id2==null) {
			id2 = "_v"+hmVerts.size();
			while (hmVerts.containsKey(id2)) id2 = "_"+id2;
		}
		MemVertex retval = new MemVertex(this, id2);
		return retval;
	}

	@Override
	public Vertex addVertex(Object id ) {
		return addMemVertex(id);
	}

	@Override
	public GraphQuery query() {
		return new DefaultGraphQuery(this);
	}

	@Override
	public void shutdown() {
	}

	@Override
	public String toString() {
	    // the test suite seems to think that the string produced must begin with the following....
	    String sw = this.getClass().getSimpleName().toLowerCase();
	    // so until we understand exactly why that is, let's just return exactly that....
	    String msg = "";
	    return sw+"["+msg+"]";
	}

	HashMap<String,HashMap<Object,HashSet<MemVertex>>> indicesForVerts = new HashMap<String,HashMap<Object,HashSet<MemVertex>>>();
	HashMap<String,HashMap<Object,HashSet<MemEdge>>> indicesForEdges = new HashMap<String,HashMap<Object,HashSet<MemEdge>>>();

	@Override
	public <T extends Element> Set<String>  getIndexedKeys(Class<T> cls) {
		if (cls==null) throw new IllegalArgumentException("cls must not be zero");
		if (cls == Vertex.class) {
			// todo: we can probably implement this faster by (1) caching the result or sometimes (2) use one call rather than polling
			Set<String> retval = indicesForVerts.keySet();
			return retval;
		} else {
			// todo: we can probably implement this faster by (1) caching the result or sometimes (2) use one call rather than polling
			Set<String> retval = indicesForEdges.keySet();
			return retval;
		}
	}
	@Override
	public <T extends Element> void dropKeyIndex(String key, Class<T> cls) {
		if (cls==null) throw new IllegalArgumentException("cls can not be null"); // TP2.5 restriction
		System.out.println("dropKeyIndex called");
		//return null;
	}
	@Override
	public <T extends Element> void createKeyIndex(String key, Class<T> cls, Parameter ...parms) {
		System.out.println("createKeyIndex(key="+key+", cls="+cls+" params.length="+parms.length);
		if (cls==null) throw new IllegalArgumentException("cls can not be null"); // TP2.5 restriction
		if (cls == Vertex.class) {
			if (indicesForVerts.containsKey(key)) throw new RuntimeException("key already exists");
			//if (vertFirst!=null) throw new RuntimeException("don't yet support creating an index after elements have been created");
			HashMap<Object,HashSet<MemVertex>> index = new HashMap<Object,HashSet<MemVertex>>();
			indicesForVerts.put(key, index);
			MemVertex vertCurrent = vertFirst;
			while (vertCurrent!=null) {
				Object val = vertCurrent.getProperty(key);
				if (val!=null) {
					HashSet<MemVertex> hsM = index.get(val);
					if (null==hsM) { 
						hsM = new HashSet<MemVertex>();
						index.put(val,hsM);
					}
					hsM.add(vertCurrent);
				}
				vertCurrent = vertCurrent.vertNextGlobal;
			}
		} else {
			if (indicesForEdges.containsKey(key)) throw new RuntimeException("key already exists");
			//if (edgeFirst!=null) throw new RuntimeException("don't yet support creating an index after elements have been created");
			HashMap<Object,HashSet<MemEdge>> index = new HashMap<Object,HashSet<MemEdge>>();
			indicesForEdges.put(key, index);
			MemEdge edgeCurrent = edgeFirst;
			while (edgeCurrent!=null) {
				Object val = edgeCurrent.getProperty(key);
				if (val!=null) {
					HashSet<MemEdge> hsM = index.get(val);
					if (null==hsM) { 
						hsM = new HashSet<MemEdge>();
						index.put(val,hsM);
					}
					hsM.add(edgeCurrent);
				}
				edgeCurrent = edgeCurrent.nextGlobal;
			}
		}
		//return null;
	}

	@Override
	public String getFactoryClassName() {
		return "org.propelgraph.memgraph.MemLocatableGraphFactory";
	}

	public String getGraphName() { return graphname; }
	public String getDirPathString() { return dirpath; }

}
