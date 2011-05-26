/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.data.couchdb.scratch;

import org.geotools.data.couchdb.client.CouchDBClient;
import org.geotools.data.couchdb.client.CouchDBConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.geotools.geojson.GeoJSONUtil;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author en
 */
public class TryJSONParser {
    public static void main(String[] args) throws Exception {
        CouchDBClient client = new CouchDBClient("http://127.0.0.1:5984/");
        CouchDBConnection db = client.openDBConnection("gttestdb");
        InputStream stream = db.view("counties").getStream(25);
        Reader reader = GeoJSONUtil.toReader(stream);
        JSONParser parser = new JSONParser();
        try {
            parser.parse(reader, new GeocouchHandler());
        } 
        catch (ParseException e) {
            throw (IOException) new IOException().initCause(e);
        }
    }

    private static class GeocouchHandler implements ContentHandler {
        
        private String fid;

        public GeocouchHandler() {
        }

        public void startJSON() throws ParseException, IOException {
        }

        public void endJSON() throws ParseException, IOException {
        }

        public boolean startObject() throws ParseException, IOException {
            return true;
        }

        public boolean endObject() throws ParseException, IOException {
            return true;
        }

        public boolean startObjectEntry(String string) throws ParseException, IOException {
            return true;
        }

        public boolean endObjectEntry() throws ParseException, IOException {
            return true;
        }

        public boolean startArray() throws ParseException, IOException {
            return true;
        }

        public boolean endArray() throws ParseException, IOException {
            return true;
        }

        public boolean primitive(Object o) throws ParseException, IOException {
            System.out.println("primitive : " + o);
            return true;
        }
    }
}
