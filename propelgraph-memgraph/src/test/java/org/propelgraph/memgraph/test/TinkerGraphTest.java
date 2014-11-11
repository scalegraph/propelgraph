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
package org.propelgraph.memgraph.test;

import com.tinkerpop.blueprints.impls.GraphTest; 
import com.tinkerpop.blueprints.KeyIndexableGraphTestSuite;
import com.tinkerpop.blueprints.GraphQueryTestSuite;
import com.tinkerpop.blueprints.VertexQueryTestSuite;
import com.tinkerpop.blueprints.VertexTestSuite;
import com.tinkerpop.blueprints.EdgeTestSuite;
import com.tinkerpop.blueprints.GraphTestSuite;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.TestSuite;
import com.tinkerpop.blueprints.IndexTestSuite;
import com.tinkerpop.blueprints.IndexableGraphTestSuite;
import com.tinkerpop.blueprints.util.io.gml.GMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReaderTestSuite;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import java.lang.reflect.Method;
import org.propelgraph.memgraph.*;
import java.io.File;
import java.util.HashSet;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TinkerGraphTest extends GraphTest {


    public void testVertexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", this.stopWatch());
    }

    public void testEdgeTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", this.stopWatch());
    }

    public void testGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", this.stopWatch());
    }

    public void testKeyIndexableGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new KeyIndexableGraphTestSuite(this));
        printTestPerformance("KeyIndexableGraphTestSuite", this.stopWatch());
    }

    /*//? not yet supporting this type of indexing
    public void testIndexableGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new IndexableGraphTestSuite(this));
        printTestPerformance("IndexableGraphTestSuite", this.stopWatch());
    }

    public void testIndexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new IndexTestSuite(this));
        printTestPerformance("IndexTestSuite", this.stopWatch());
    }
    */
    public void testGraphMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphMLReaderTestSuite(this));
        printTestPerformance("GraphMLReaderTestSuite", this.stopWatch());
    }

    public void testGMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GMLReaderTestSuite(this));
        printTestPerformance("GMLReaderTestSuite", this.stopWatch());
    }

    public void testGraphSONReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphSONReaderTestSuite(this));
        printTestPerformance("GraphSONReaderTestSuite", this.stopWatch());
    }

    public void testVertexQueryTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new VertexQueryTestSuite(this));
        printTestPerformance("VertexQueryTestSuite", this.stopWatch());
    }

    public void testGraphQueryTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphQueryTestSuite(this));
        printTestPerformance("GraphQueryTestSuite", this.stopWatch());
    }

    public Graph generateGraph(String directoryname) {
        return generateGraph(false, directoryname);
    }
    public Graph generateGraph() {
        return generateGraph(false);
    }
    public Graph generateGraph(boolean create) {
        return generateGraph(create, "blueprints_test");
    }
    public Graph generateGraph(boolean create, final String graphDirectoryName) {
        //String dirname = "testrawdir";
        //String dbname = "empty";
        File dir = this.computeTestDataRoot();
        String dirname = dir.getAbsolutePath();
        String dbname = graphDirectoryName;

        if (create) clearDir();

        System.out.flush();
        MemGraph graph = new MemGraph("memgraphs", "emptytest", MemGraph.MemGGraphType.INMEMORY); 
        ////graph.addVertex(null); // just for testing
        return graph;
    }
    void clearDir() {
        /* JNIGen.flushstdout(); System.out.flush(); // System.out.println("> J clearDir();\n");  System.out.flush();
        File dir = this.computeTestDataRoot(); // System.out.println(dir);
        if (false) {
            for (File fi : dir.listFiles()) {
                System.out.println( fi.getName()+" "+fi.length());
            }
        }
        deleteDirectory(dir);
        dir.mkdir(); */
    }

    public void doTestSuite(final TestSuite testSuite) throws Exception {
        HashSet<String> hsSkipPer = new HashSet<String>();
        HashSet<String> hsSkipMem = new HashSet<String>();
        hsSkipPer.add("testAutotypingOfProperties");  // tests Boolean even if explicitly not supported in Features
        hsSkipMem.add("testAutotypingOfProperties");  // tests Boolean even if explicitly not supported in Features
        hsSkipMem.add("testGraphDataPersists");  System.out.println("todo: add support to check if graph is persistent");

        String doTest = System.getProperty("testTinkerGraph");
        if (doTest == null || doTest.equals("true")) {
            clearDir();
            for (Method method : testSuite.getClass().getDeclaredMethods()) {
                String methodname = method.getName();
                if (methodname.startsWith("test")) {
		    if (true) {
			if (!hsSkipMem.contains(methodname)) {
			    System.out.flush();
			    if (true) {
				System.out.println("MemGraph Testing " + testSuite.getClass().getSimpleName()+"."+method.getName() + "...");  System.out.flush();
				method.invoke(testSuite);
				//clearDir();
			    }
			}
		    } else {
			System.out.println("turn back on the testing of NSMem !");
		    }
                }
            }
        }
    }


}