package org.propelgraph.titan;

import java.io.File;
import java.io.IOException;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement; //Titan 0.5...
import org.apache.commons.configuration.BaseConfiguration;
import org.propelgraph.KeyIndexableGraphSupport;
import java.util.Set;

public class TitanKeyIndexableGraphSupport implements KeyIndexableGraphSupport {


    TitanGraph gr;

    // default constructor
    public TitanKeyIndexableGraphSupport( ) {}

    @Override
	public void setGraph( Graph graph ) {
		if (gr!=null) throw new RuntimeException("the graph has not been set");
		gr = (TitanGraph)graph;
    }

    @Override
	public <T extends Element> void dropKeyIndex(String key, Class<T> elementClass) {
        throw new UnsupportedOperationException("Key indexes cannot currently be dropped. Create a new key instead.");
    }

    @Override
	public <T extends Element> void createKeyIndex(String key, Class<T> elementClass, final Parameter... indexParameters) {
		TitanManagement mgmt = gr.getManagementSystem();
		for (TitanGraphIndex idx : mgmt.getGraphIndexes(elementClass) ) {
			PropertyKey fk[] = idx.getFieldKeys();
			if ((fk.length==1) && fk[0].getName().equals(key)) {
				return ; // it already exists
			}
		}
		PropertyKey pk = mgmt.makePropertyKey(key).dataType(String.class).make();
		mgmt.buildIndex("pg:"+key+":"+elementClass,elementClass).addKey(pk).buildCompositeIndex();
    }

    @Override
	public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
		throw new UnsupportedOperationException("Not yet supported");
    }



}
