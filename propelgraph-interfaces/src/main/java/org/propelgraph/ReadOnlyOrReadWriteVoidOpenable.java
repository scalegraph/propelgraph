/*
 * This file is part of the ScaleGraph?PropelGraph project (http://scalegraph.org).
 *
 * This file is licensed to You under the Eclipse Public License (EPL);
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright ScaleGraph Team 2016.
 */
package org.propelgraph;

import com.tinkerpop.blueprints.Graph;

/**
 * This interface can be used by any class that needs a simple 
 * openForReadOnly and openForReadWrite methods.  The semantics
 * of ReadOnly and ReadWrite is not specified here.  
 * Subinteraces of this interface should be created if one wants
 * a more specific interface.
 *  
 * This is being used by soe implementations of TinkerPop3
 * Transaction interface to give callers a way to specify
 * the type of transaction they want to open.
 * 
 * @author ccjason
 *
 */
public interface ReadOnlyOrReadWriteVoidOpenable {
	
	void openReadOnly();
	void openReadWrite();

}
