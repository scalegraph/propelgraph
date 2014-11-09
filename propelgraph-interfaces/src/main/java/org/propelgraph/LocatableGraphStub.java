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

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import org.propelgraph.AlreadyExistsException;
import org.propelgraph.NotFoundException;


/**
 * This is an empty class that can be used as a place holder of 
 * a graph that might be too expensive to instatiate until one 
 * is certain it is needed.  All Graph methods are noops or 
 * return null;   getActualGraph() is the only implemented 
 * method besides the constructor. -- This class is useful for 
 * passing in a graph to an interface that doesn't really use 
 * the graph until later.  This class is not a WrapperGraph  
 * because a wrapper class actually delegates its methods. This 
 * class does not. All methods are noops or return null.
 * 
 * @author drewvale (12/25/2014)
 */
public class LocatableGraphStub implements Graph {

    String url;
    String faction;
    String fmode;

    /**
     * This is a very low cost constructor.  One simply passes in 
     * the locatablegraph url that can be used later to create the 
     * actual graph.   
     * 
     * @author drewvale (12/25/2014)
     * 
     * @param url - The url that can be used to instantiate the 
     *            actual graph.
     */
    public LocatableGraphStub(String url, String faction, String fmode) {
        this.url = url;
	this.faction = faction;
	this.fmode = fmode;
    }

    public Graph getActualGraph() throws InstantiationException, IllegalAccessException, ClassNotFoundException, AlreadyExistsException, NotFoundException, UnsupportedFActionException {
        Graph graph = LocatableGraphFactoryFactoryImpl.openGraph(url,faction,fmode);
        return graph;
    }

    public Features getFeatures() {return null;};

    public Vertex addVertex(Object id){return null;};

    public Vertex getVertex(Object id){return null;};

    public void removeVertex(Vertex vertex){};

    public Iterable<Vertex> getVertices(){return null;};

    public Iterable<Vertex> getVertices(String key, Object value){return null;};

    public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label){return null;};

    public Edge getEdge(Object id){return null;};

    public void removeEdge(Edge edge){};

    public Iterable<Edge> getEdges(){return null;};

    public Iterable<Edge> getEdges(String key, Object value){return null;};

    public GraphQuery query(){return null; };

    public void shutdown(){};

}

