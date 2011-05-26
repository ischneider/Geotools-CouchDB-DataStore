/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.data.couchdb.client;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.NameValuePair;
import org.json.simple.JSONObject;

/**
 *
 * @author Ian Schneider
 */
public abstract class CouchDBViewSupport extends CouchDBClient.Component {
    protected final CouchDBConnection connection;
    
    protected CouchDBViewSupport(CouchDBClient client,CouchDBConnection connection, String path) {
        super(path,client);
        this.connection = connection;
    }
    
    protected JSONObject get(NameValuePair... query) throws IOException, CouchDBException {
        CouchDBResponse resp = client.get(connection.uri(root),query);
        resp.checkOK("Error performing query");
        return resp.getBodyAsJSONObject();
    }
    
    protected InputStream getStream(NameValuePair... query) throws IOException, CouchDBException {
        CouchDBResponse resp = client.get(connection.uri(root),query);
        return resp.getResponseStream();
    }
    
}
