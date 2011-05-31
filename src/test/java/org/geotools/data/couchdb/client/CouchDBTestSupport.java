/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.data.couchdb.client;

import java.util.Properties;
import org.geotools.data.couchdb.client.CouchDBUtils;
import org.geotools.data.couchdb.client.CouchDBClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.test.OnlineTestSupport;
import org.geotools.util.logging.Logging;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author Ian Schneider (OpenGeo)
 */
public class CouchDBTestSupport extends OnlineTestSupport {
    protected CouchDBClient client;
    protected Logger logger;
    
    protected final String getTestDB() {
        return getFixture().getProperty("dbname");
    }
    protected final String getTestHost() {
        return getFixture().getProperty("url");
    }
    
    @After
    public void tearDown() throws Exception {
        debug(false);
    }
    
    protected void debug(boolean on) {
        logger = Logging.getLogger(getClass());
        logger.setLevel(Level.FINEST);
        Logger.getLogger("").getHandlers()[0].setLevel(on ? Level.FINEST : Level.INFO);
    }
    
    protected File resolveFile(String path) {
        return new File("src/test/resources/org/geotools/data/couchdb/" + path);
    }
    
    protected JSONArray loadJSON(String path,String featureClass) throws Exception {
        String content = resolveContent(path);
        JSONObject data = (JSONObject) JSONValue.parseWithException(content);
        JSONArray features = (JSONArray) data.get("features");
        if (featureClass == null) {
            featureClass = path.split("\\.")[0];
        }
        for (int i = 0; i < features.size(); i++) {
            JSONObject f = (JSONObject) features.get(i);
            f.put("featureClass", featureClass);
        }
        return features;
    }
    
    protected String resolveContent(String path) throws FileNotFoundException {
        return CouchDBUtils.read(resolveFile(path));
    }
    
    protected void deleteIfExists(String db) throws Exception {
        List<String> databaseNames = client.getDatabaseNames();
        if (databaseNames.indexOf(db) >= 0) {
            client.openDBConnection(db).delete();
        }
        assertTrue("Expected db to be deleted", client.getDatabaseNames().indexOf(db) < 0);
    }

    @Override
    protected String getFixtureId() {
        return "couchdb";
    }

    @Override
    protected Properties createExampleFixture() {
        Properties props = new Properties();
        props.put("url", "http://localhost:5984");
        props.put("dbname", "gttestdb");
        return props;
    }

    @Override
    protected void connect() throws Exception {
        client = new CouchDBClient(getTestHost());
    }
    
}
