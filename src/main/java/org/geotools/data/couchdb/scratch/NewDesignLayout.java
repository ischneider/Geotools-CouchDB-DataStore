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
package org.geotools.data.couchdb.scratch;

import java.io.File;
import org.geotools.data.couchdb.client.CouchDBClient;
import org.geotools.data.couchdb.client.CouchDBConnection;
import org.geotools.data.couchdb.client.CouchDBUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Ian Schneider (OpenGeo)
 */
public class NewDesignLayout {
    static String TEST_DB_NAME = "demo";
    static File resolveFile(String path) {
        return new File("src/test/resources/org/geotools/data/couchdb/" + path);
    }
    static JSONArray loadJSON(String path) throws Exception {
        String content = CouchDBUtils.read(resolveFile(path));
        JSONObject data = (JSONObject) JSONValue.parseWithException(content);
        JSONArray features = (JSONArray) data.get("features");
        return features;
    }
    public static void main(String[] args) throws Exception {
        String local ="http://127.0.0.1:5984/";
        String prod = "http://ian:couchian@ian.iriscouch.com/demo";
        CouchDBClient client = new CouchDBClient(prod);
        CouchDBConnection db = client.openDBConnection(TEST_DB_NAME);
        //db.putDesignDocument(resolveFile("design-doc2.json"));
        db.postBulk(loadJSON("counties.json"));
    }
}
