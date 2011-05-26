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

import org.geotools.data.couchdb.client.CouchDBUtils;
import org.geotools.data.couchdb.client.CouchDBSpatialView;
import org.geotools.data.couchdb.client.CouchDBException;
import org.geotools.data.couchdb.client.CouchDBView;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.IOException;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Query;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.store.ContentFeatureStore;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Ian Schneider
 */
public class CouchDBFeatureStore extends ContentFeatureStore {
    private final String viewName;

    public CouchDBFeatureStore(ContentEntry entry, Query query) {
        super(entry, query);
        // local name is <dbname>.<view>
        // extract view part
        String localName =  entry.getName().getLocalPart();
        int idx = localName.indexOf('.');
        viewName = localName.substring(idx + 1);
    }

    @Override
    protected ReferencedEnvelope getBoundsInternal(Query query) throws IOException {
        // @todo ianschneider understand query
        
        // there appears to be no way to obtain the bbox from couch documents 
        // (aka features) without getting all the geometry as well.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CouchDBDataStore getDataStore() {
        return (CouchDBDataStore) super.getDataStore();
    }
    
    private CouchDBSpatialView spatialView() {
        return getDataStore().getConnection().spatialView(viewName);
    }
    
    private CouchDBView dataView() {
        String spatialView = getEntry().getName().getLocalPart();
        return getDataStore().getConnection().view(viewName);
    }

    @Override
    protected int getCountInternal(Query query) throws IOException {
        // @todo ianschneider understand query
        try {
            return (int) spatialView().count(-180, -90, 180, 90);
        } catch (CouchDBException ex) {
            throw ex.wrap();
        }
    }

    @Override
    protected FeatureReader<SimpleFeatureType, SimpleFeature> getReaderInternal(Query query) throws IOException {
        // @todo ianschneider understand query
        JSONObject features;
        try {
            features = spatialView().get(-180, -90, 180, 90);
        } catch (CouchDBException ex) {
            throw ex.wrap();
        }
        return new CouchDBFeatureReader(buildFeatureType(), (JSONArray) features.get("rows"));
    }

    @Override
    protected SimpleFeatureType buildFeatureType() throws IOException {
        // feature types could be
        // 1. created dynamically
        // 2. pointed to statically (schema or json)
        //    a. if design document had schema in it, that'd be conventional
        
        // for now, choice 1
        return createFeatureTypeFromData();
    }
    
    protected SimpleFeatureType createFeatureTypeFromData() throws IOException {
        JSONArray res;
        try {
             res = dataView().get(1);
        } catch (CouchDBException ex) {
            throw ex.wrap();
        }
        if (res.size() == 0) {
            throw new IOException("No features exist in this view");
        }
        JSONObject row = (JSONObject) res.get(0);
        row = (JSONObject) row.get("value");
        return CouchDBUtils.createFeatureType(row, getEntry().getName().toString());
    }
    
    @Override
    protected FeatureWriter<SimpleFeatureType, SimpleFeature> getWriterInternal(Query query, int flags) throws IOException {
        switch (flags) {
            case 0: throw new IllegalArgumentException( "no write flags set" );
            case WRITER_ADD:
                return new CouchDBAddFeatureWriter(buildFeatureType(),getDataStore());
            case WRITER_UPDATE:
                throw new UnsupportedOperationException("Update not supported");
            default:
                throw new IllegalArgumentException(" cannot handle flags " + flags);
        }
    }

    
}
