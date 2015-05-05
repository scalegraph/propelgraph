/*
 * This file is part of the ScaleGraph?PropelGraph project (http://scalegraph.org).
 *
 * This file is licensed to You under the Eclipse Public License (EPL);
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright ScaleGraph Team 2015.
 */
package org.propelgraph;

import java.util.Set;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;

/**
 * This interface mirrors the TinkerPop KeyIndexableGraph
 * interface but allows the logic to reside separate from the
 * actual graph.  This can avoid proxying all methods just to
 * provide an alternate implementation of the three methods of
 * the KeyIndexableGraph interface.  We need this interface to
 * work around the fact that Titan 0.5.4 does support indexing,
 * but no longer supports the KeyIndexableGraph interface.
 * Additionally it purports to support the KeyIndexableGraph
 * interface, but thows an exception when called. PropelGraph
 * util works around this by calling an implementation of this
 * interface that is supports Titan.  It gets that
 * implementation from
 * KeyIndexableGraphSupportFactoryFactoryImpl.  That support is
 * implemented in the propelgraph-titan subproject.
 *  
 * @see KeyIndexableGraphSupportFactory
 * @see KeyIndexableGraphSupportFactoryFactoryImpl
 * 
 * @author ccjason
 *
 */
public interface KeyIndexableGraphSupport {
	
    /**
	 * @see 
	 *  	<a
	 *  	href=http://www.tinkerpop.com/docs/javadocs/blueprints/2.4.0/com/tinkerpop/blueprints/KeyIndexableGraph.html#dropKeyIndex(java.lang.String,
	 *  	java.lang.Class)">KeyIndexableGraph.dropKeyIndex</a>
     */
    public <T extends Element> void dropKeyIndex(String key, Class<T> elementClass);

	/**
	 * @see 
	 *  	<a
	 *  	href=http://www.tinkerpop.com/docs/javadocs/blueprints/2.4.0/com/tinkerpop/blueprints/KeyIndexableGraph.html#createKeyIndex(java.lang.String,
	 *  	java.lang.Class,
	 *  	com.tinkerpop.blueprints.Parameter...)">KeyIndexableGraph.createKeyIndex</a>
	 */
    public <T extends Element> void createKeyIndex(String key, Class<T> elementClass, final Parameter... indexParameters);

	/**
	 * @see 
	 *  	<a
	 *  	href=http://www.tinkerpop.com/docs/javadocs/blueprints/2.4.0/com/tinkerpop/blueprints/KeyIndexableGraph.html#getIndexedKeys(java.lang.Class)">KeyIndexableGraph.getIndexedKeys</a>
	 */
    public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass);
	
}
