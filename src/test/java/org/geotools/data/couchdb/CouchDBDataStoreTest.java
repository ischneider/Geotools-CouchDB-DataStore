/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.couchdb;

import org.geotools.data.couchdb.client.CouchDBTestSupport;
import org.geotools.data.couchdb.client.CouchDBConnectionTest;
import org.geotools.data.couchdb.client.CouchDBUtils;
import org.geotools.data.couchdb.client.CouchDBConnection;
import org.geotools.data.store.ContentFeatureCollection;
import org.junit.After;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.data.store.ContentFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.Filter;
import org.geotools.geojson.feature.FeatureJSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.spatial.BBOX;

/**
 *
 * @author Ian Schneider
 */
public class CouchDBDataStoreTest extends CouchDBTestSupport {
    static CouchDBConnection db;
    private CouchDBDataStore store;

    @BeforeClass
    public static void setUpClass() throws Exception {
        CouchDBConnectionTest test = new CouchDBConnectionTest();
        test.setUp();
        db = test.setupDB();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (db != null) {
            db.delete();
        }
    }
    
    @Before
    public void setup() throws Exception {
        store = new CouchDBDataStore();
        store.setCouchURL(TEST_HOST); // @todo fixture location, etc.
        store.setDatabaseName(TEST_DB_NAME);
    }
    
    @After
    public void tearDown() throws IOException {
    }

    @Test
    public void testTypeNames() throws Exception {
        String[] typeNames = store.getTypeNames();
        assertEquals(2, typeNames.length);
        assertEquals("gttestdb.counties",typeNames[0]);
        assertEquals("gttestdb.countries",typeNames[1]);
    }
    
    @Test
    public void testFeatureType() throws Exception {        
        ContentFeatureSource featureSource = store.getFeatureSource("gttestdb.counties");
        SimpleFeatureType schema = featureSource.getSchema();
        assertNotNull(schema.getDescriptor("Name"));
        assertNotNull(schema.getDescriptor("State"));
    }
    
    @Test
    public void testFeatureReader() throws Exception {
        ContentFeatureSource featureSource = store.getFeatureSource("gttestdb.counties");
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = featureSource.getReader();
        int cnt = 0;
        while (reader.hasNext()) {
            SimpleFeature nf = reader.next();
            cnt++;
        }
        assertEquals(24, cnt);
    }
    
    private int count(ContentFeatureCollection c) {
        // count by forcing reads
        int cnt = 0;
        for (Iterator it = c.iterator(); it.hasNext();) {
            Object o = it.next();
            System.out.println(o);
            cnt++;
        }
        return cnt;
    }
    
    @Test
    public void testBBoxQuery() throws Exception {
        ContentFeatureSource featureSource = store.getFeatureSource("gttestdb.counties");
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        // pueblo county
        BBOX bbox = ff.bbox("geometry", -105.050678,37.734704,-104.053921,38.522548,null);
        
        Query query = new Query("geometry", bbox);
        assertEquals(1, featureSource.getCount(query));
        
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = featureSource.getReader(query);
        int cnt = 0;
        while (reader.hasNext()) {
            SimpleFeature nf = reader.next();
            cnt++;
        }
        assertEquals(1, cnt);
        
        ContentFeatureCollection features = featureSource.getFeatures(query);
        assertEquals(1, count(features));
    }
    
    @Test
    public void testFilter() throws Exception {
        ContentFeatureSource featureSource = store.getFeatureSource("gttestdb.counties");
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        PropertyIsEqualTo isPueblo = ff.equal(ff.property("Name"),ff.literal("Pueblo"),true);
        ContentFeatureCollection features = featureSource.getFeatures(isPueblo);
        assertEquals(1, count(features));
    }

    @Test
    public void testFeatureWriterAdd() throws Exception {
        // currently require some data to exist due to construction of featuretype from existing feature...
        db.postBulk(loadJSON("morocco.json", "countries"));
        
        JSONArray data = loadJSON("italy.json", "countries");
        JSONObject italy = (JSONObject) data.get(0);
        FeatureJSON json = new FeatureJSON();
        json.setFeatureType(CouchDBUtils.createFeatureType(italy, "countries"));
        Feature feature = json.readFeature(italy.toString());
        
        ContentFeatureStore featureStore = (ContentFeatureStore) store.getFeatureSource("gttestdb.countries");
        featureStore.addFeatures(Collections.singleton(feature));
        
        ContentFeatureCollection features = featureStore.getFeatures();
        assertEquals(2,features.size());
    }
}
