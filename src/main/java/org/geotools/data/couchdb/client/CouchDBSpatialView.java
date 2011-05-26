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
package org.geotools.data.couchdb.client;

import java.io.IOException;
import org.apache.commons.httpclient.NameValuePair;
import org.json.simple.JSONObject;

/**
 *
 * @author Ian Schneider
 */
public class CouchDBSpatialView extends CouchDBViewSupport {

    public CouchDBSpatialView(CouchDBClient client, CouchDBConnection connection, String path) {
        super(client, connection, path);
    }
    
    private NameValuePair bbox(float llx,float lly,float urx,float ury) {
        return new NameValuePair("bbox",llx + "," + lly + "," + urx + "," + ury);
    }
    
    public JSONObject get(float llx,float lly,float urx,float ury) throws IOException, CouchDBException {
        return get(bbox(llx, lly, urx, ury));
    }
    
    public long count(float llx,float lly,float urx,float ury) throws IOException, CouchDBException {
        JSONObject count = get(bbox(llx, lly, urx, ury),new NameValuePair("count","true"));
        return (Long) count.get("count");
    }
    
}
