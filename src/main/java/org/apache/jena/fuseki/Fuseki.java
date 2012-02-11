/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.fuseki;

import org.apache.jena.fuseki.server.SPARQLServer ;
import org.openjena.riot.RIOT ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;

import com.hp.hpl.jena.query.ARQ ;
import com.hp.hpl.jena.sparql.SystemARQ ;
import com.hp.hpl.jena.sparql.lib.Metadata ;
import com.hp.hpl.jena.sparql.mgt.ARQMgt ;
import com.hp.hpl.jena.sparql.mgt.SystemInfo ;
import com.hp.hpl.jena.sparql.util.Context ;
import com.hp.hpl.jena.sparql.util.MappingRegistry ;
import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.tdb.TDB ;
import com.hp.hpl.jena.util.FileManager ;

public class Fuseki
{
    // External log : operations, etc.
    static public String PATH = "org.apache.jena.fuseki" ;
    static public String FusekiIRI = "http://jena.apache.org/Fuseki" ;
    static public String FusekiHomeEnv = "FUSEKI_HOME" ;
    static public String FusekiSymbolIRI = "http://jena.apache.org/fuseki#" ;
    
    public static final Symbol xFusekiEnableGZipCompression = Symbol.create(FusekiSymbolIRI + "enableGZipCompression");
    
    static public String PagesPublish = "pages-publish" ;
    static public String PagesAll =     "pages-update" ;
    
    //static private String metadataDevLocation = "org/openjena/fuseki/fuseki-properties-dev.xml" ;
    static private String metadataLocation = "org/apache/jena/fuseki/fuseki-properties.xml" ;
    static private Metadata metadata = initMetadata() ;
    private static Metadata initMetadata()
    {
        Metadata m = new Metadata() ;
        //m.addMetadata(metadataDevLocation) ;
        m.addMetadata(metadataLocation) ;
        return m ;
    }
    
    static public final String NAME = "Fuseki" ;
    static public final String VERSION = metadata.get(PATH+".version", "development") ;
    static public final String BUILD_DATE = metadata.get(PATH+".build.datetime", "unknown") ; // call Date if unavailable.
    static public final String serverHttpName     = NAME+" ("+VERSION+")" ;    
    
    // Log for operations
    public static final String requestLogName = PATH+".Fuseki" ;
    public static final Logger requestLog = LoggerFactory.getLogger(requestLogName) ;
    public static final String serverLogName = PATH+".Server" ;
    public static final Logger serverLog = LoggerFactory.getLogger(serverLogName) ;
    public static final String configLogName = PATH+".Config" ;
    public static final Logger configLog = LoggerFactory.getLogger(configLogName) ;    
    // Log for general server messages.
    
    public static final FileManager webFileManager ;
    static {
        webFileManager = new FileManager() ;
        // Only know how to handle http URLs 
        webFileManager.addLocatorURL() ;
    }
    
    private static boolean initialized = false ;
    public synchronized static void init()
    {
        if ( initialized )
            return ;
        initialized = true ;
        ARQ.init() ;
        SystemInfo sysInfo = new SystemInfo(FusekiIRI, VERSION, BUILD_DATE) ;
        ARQMgt.register(PATH+".system:type=SystemInfo", sysInfo) ;
        SystemARQ.registerSubSystem(sysInfo) ;
        RIOT.init() ;
        TDB.init() ;
        MappingRegistry.addPrefixMapping("fuseki", FusekiSymbolIRI) ;
    }
  
    public static Context getContext()
    {
        return ARQ.getContext() ;
    }
    
    // Temporary ...
    private static SPARQLServer server ;
    public static void setServer(SPARQLServer _server)      { server = _server ; }
    public static SPARQLServer getServer()                  { return server ; }

    // Force a call to init.
    static { init() ; }
}
