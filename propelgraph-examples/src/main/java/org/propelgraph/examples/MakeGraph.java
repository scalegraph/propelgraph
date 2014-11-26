package org.propelgraph.examples;

import org.propelgraph.util.CreateGraph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.Random;
import java.io.IOException;

/**
 * a simple program that opens/creates a graph and populates it. 
 * This class exists to simply demonstrate how to create a 
 * project that uses propelgraph.  This program doesn't actually 
 * persist the graph it creates, but it would if it chose a 
 * graph type that persists. 
 * 
 * @author ccjason (11/26/2014)
 */
public class MakeGraph {

    public static void main( String args[]) throws Exception {
	Graph g = CreateGraph.openGraph("propelmem","my_awesome_graph");

	for (int vn = 0; vn < 10; vn++) {
	    g.addVertex("vert"+vn);

	}

	Random rand = new Random();

	for (int en = 0; en < 20; en++) {
	    Edge e = g.addEdge("edge"+en, g.getVertex("vert"+rand.nextInt(10)), g.getVertex("vert"+rand.nextInt(10)), "reaches" );
	    e.setProperty("weight", rand.nextFloat());
	}

	g.shutdown();
    }
}
