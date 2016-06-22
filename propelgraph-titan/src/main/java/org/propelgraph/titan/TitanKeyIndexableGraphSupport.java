package org.propelgraph.titan;

import java.io.File;
import java.io.IOException;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;
import com.thinkaurelius.titan.core.TitanGraph;
/* $if TINKERPOPVERSION >= 2.5.0$ */
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement; //Titan 0.5...
/* $endif$ */
import org.propelgraph.KeyIndexableGraphSupport;
import java.util.Set;
import static org.jasonnet.logln.Logln.logln; import org.jasonnet.logln.Logln;

public class TitanKeyIndexableGraphSupport implements KeyIndexableGraphSupport {

    TitanGraph gr;

    static void logln(String ss) {	
        //Logln.logln(ss,1); // comment out this line if you want the full featured logln
    }  


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
		//logln("t  key="+key+"  ");
/* $if TINKERPOPVERSION >= 2.5.0$ */
		String indexname = "pg:"+key+":"+elementClass; //logln("indexname will be "+indexname);
		TitanManagement mgmt = gr.getManagementSystem();
		for (TitanGraphIndex idx : mgmt.getGraphIndexes(elementClass) ) {
			//logln("t");
			PropertyKey fk[] = idx.getFieldKeys();
			//logln(""+(fk.length));
			//logln(""+(fk[0].getName()));
			if ((fk.length==1) && fk[0].getName().equals(key)) {
				//logln("exists");
				return ; // it already exists
			}
		}
		//logln("making index starting with the key: "+key);
		PropertyKey pk = mgmt.makePropertyKey(key).dataType(String.class).make();
		mgmt.buildIndex( indexname,elementClass).addKey(pk).buildCompositeIndex();
		mgmt.commit();
/* $else$
		gr.createKeyIndex(key,elementClass,indexParameters);
$endif$ */
    }

    @Override
	public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
		throw new UnsupportedOperationException("Not yet supported");
    }



}
