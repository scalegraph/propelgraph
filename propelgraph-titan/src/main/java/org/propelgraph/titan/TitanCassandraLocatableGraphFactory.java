package org.propelgraph.titan;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import org.apache.commons.configuration.BaseConfiguration;
import org.propelgraph.LocatableGraphFactory;
import org.propelgraph.DeleteableGraphFactory;
import org.propelgraph.LocatableGraphFactoryFactoryImpl;
import org.propelgraph.UnsupportedFActionException;

public class TitanCassandraLocatableGraphFactory implements LocatableGraphFactory, DeleteableGraphFactory {
	static final String PREFIX = LocatableGraphFactory.SCHEME_PGGRAPH+":"+TitanCassandraLocatableGraphFactory.class.getName()+"/?";


    @Override
    public void delete(String urlPath) {
	if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
	Graph g;
	try {
	    g = open(urlPath, org.propelgraph.LocatableGraphFactory.FACTION_CREATE_OPEN, org.propelgraph.LocatableGraphFactory.FMODE_WRITE);
	} catch (UnsupportedFActionException exc) {
	    throw new RuntimeException(exc);
	}
	TitanGraph gg = (TitanGraph)g;
	TitanCleanup tc = new TitanCleanup();
	gg.shutdown();
	tc.clear(gg);
    }

    Map<String,String> parseQueryParamsFromURL(String url) {
        java.net.URI uri = null;
        //System.out.println("url=="+url);
        int i  = url.indexOf('?');
        String qstring = url.substring(i+1);
        //System.out.println("qstring=="+qstring);
        String segs[] = qstring.split("&");
        Map<String,String> retval = new HashMap<String,String>();
        for (String seg : segs) {
            int idxEqual = seg.indexOf('=');
            if (idxEqual<0) {
                // skip
            } else {
                try {
                    String key = URLDecoder.decode(seg.substring(0,idxEqual),"UTF-8");
                    String value = URLDecoder.decode(seg.substring(idxEqual+1),"UTF-8");
                    retval.put(key,value);
                } catch (java.io.UnsupportedEncodingException exc) {
                    throw new RuntimeException( exc );
                }
            }
        }
        return retval;
    }
	
    @Override
	public Graph open(String urlPath, String faction, String fmode) throws UnsupportedFActionException {
		if (!urlPath.startsWith(PREFIX)) throw new RuntimeException("unsupported urlPath: "+urlPath);
		int idx0 = PREFIX.length();
		String graphname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&graphname=", urlPath);
		String store = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&store=", urlPath);
		if (!faction.equals(org.propelgraph.LocatableGraphFactory.FACTION_CREATE_OPEN))	throw new UnsupportedFActionException();
		if (store.equals("berkdb")) {
			if (null!=new Object()) throw new RuntimeException("dead code error");  // should not reach this code
			System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
			String dirpath = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&dirpath=", urlPath);
			File fiDirGraph = new File(dirpath+"/"+graphname);
			boolean boolAlreadyExists = fiDirGraph.exists();
			BaseConfiguration conf = new BaseConfiguration();
			conf.setProperty("storage.directory", fiDirGraph.getAbsoluteFile().toString());
			conf.setProperty("storage.backend", "berkeleyje");
			TitanGraph g = TitanFactory.open(conf);
			if (!boolAlreadyExists) {
				//g.createKeyIndex(idPropForId, Vertex.class);
			}
			//Logger log = LoggerFactory.getLogger(QueryProcessor.class);                                                                                                                       
			//log.setLevel()                                                                                                                                                                    
			System.setProperty("org.slf4j.simpleLogger.log.com.thinkaurelius.titan.graphdb.query.QueryProcessor", "info");
			return g;
        } else if (store.equals("hbase")) {
			String hostname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&hostname=", urlPath);
			if ("localhost".equals(hostname)) hostname = null;
			BaseConfiguration conf = new BaseConfiguration();
			conf.setProperty("storage.backend","hbase");
			conf.setProperty("storage.hostname",hostname);
			conf.setProperty("storage.tablename","titan"+graphname);
			TitanGraph g = TitanFactory.open(conf);
			//g.createKeyIndex(idPropForId, Vertex.class);
			//g.loadGraphML('data/onevertex.xml')                                                                                                                                       
			return g;
        } else if (store.equals("cassandra")) {
			String hostname = LocatableGraphFactoryFactoryImpl.parseForURLParameter("&hostname=", urlPath);
			if ("localhost".equals(hostname)) hostname = "127.0.0.1";
            if (null == hostname) hostname = "127.0.0.1";
            {
                BaseConfiguration conf = new BaseConfiguration();
                conf.setProperty("storage.backend","cassandra");
                conf.setProperty("storage.hostname",hostname);
                conf.setProperty("storage.cassandra.keyspace","pgcreategraph");
                Map<String,String> map2 = parseQueryParamsFromURL(urlPath);
                for (String key : map2.keySet()) {
                    String value = map2.get(key);
                    System.out.println("setting titan conf "+key+"="+value);
                    conf.setProperty(key,map2.get(key));
                }
                TitanGraph g = TitanFactory.open(conf);
                return g;
            }
        } else {
			throw new RuntimeException("unknown store");
		}
	}

	@Override
	public String getGraphURL(Graph graph) {
		// we don't know how to determine the graph file being used if all we have is the Graph object
		throw new RuntimeException("don't currently support this method");
	}

}
