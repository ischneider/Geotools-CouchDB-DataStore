/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.data.couchdb;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import org.geotools.data.AbstractDataStoreFactory;
import org.geotools.data.DataStore;

/**
 *
 * @author en
 */
public class CouchDBDataStoreFactory extends AbstractDataStoreFactory {
    
    public static final Param SERVER_URL = new Param("serverURL", String.class, "Server URL", true);
    public static final Param DB_NAME = new Param("dbName", String.class, "Database Name", true);
    public static final Param SCHEMA_URI = new Param("schemaURI", String.class, "Schema URI", false);

    public DataStore createDataStore(Map<String, Serializable> params) throws IOException {
        CouchDBDataStore dataStore = new CouchDBDataStore();
        dataStore.setCouchURL((String)SERVER_URL.lookUp(params));
        dataStore.setDatabaseName((String)DB_NAME.lookUp(params));
        dataStore.setSchemaURI((String)SCHEMA_URI.lookUp(params));
        return dataStore;
    }

    public DataStore createNewDataStore(Map<String, Serializable> params) throws IOException {
        // @todo - this could be possible
        throw new UnsupportedOperationException();
    }

    public String getDescription() {
        return "CouchDB/Geocouch Datastore";
    }

    public Param[] getParametersInfo() {
        return new Param[] {
            SERVER_URL,
            DB_NAME,
            SCHEMA_URI
        };
    }
}
