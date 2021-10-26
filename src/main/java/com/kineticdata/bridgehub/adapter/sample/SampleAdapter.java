/*
 * This is a sample starting adapter used by kinetic data to train bridge adapter
 * development.
 */
package com.kineticdata.bridgehub.adapter.sample;

import com.kineticdata.bridgehub.adapter.BridgeAdapter;
import com.kineticdata.bridgehub.adapter.BridgeError;
import com.kineticdata.bridgehub.adapter.BridgeRequest;
import com.kineticdata.bridgehub.adapter.Count;
import com.kineticdata.bridgehub.adapter.Record;
import com.kineticdata.bridgehub.adapter.RecordList;
import com.kineticdata.commons.v1.config.ConfigurableProperty;
import com.kineticdata.commons.v1.config.ConfigurablePropertyMap;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author kineticdata
 * @date   10-26-2021
 */
public class SampleAdapter implements BridgeAdapter {
    private String server;
    private String username;
    private String password;
    
    /** Defines the adapter display name. */
    public static final String NAME = "Sample";

    /** Adapter version constant. */
    public static String VERSION;
    /** Load the properties version from the version.properties file. */
    static {
        try {
            java.util.Properties properties = new java.util.Properties();
            properties.load(SampleAdapter.class.getResourceAsStream("/" 
                + SampleAdapter.class.getName()+".version"));
            VERSION = properties.getProperty("version");
        } catch (IOException e) {
            System.out.println("Unable to load " + SampleAdapter.class.getName() 
                + " version properties.");
            VERSION = "Unknown";
        }
    }
    
    public static class Properties {
        public static final String SERVER = "Server Url";
        public static final String USERNAME = "Username";
        public static final String PASSWORD = "Password";
    } 
    
    private final ConfigurablePropertyMap properties = new ConfigurablePropertyMap(
        new ConfigurableProperty(Properties.SERVER).setIsRequired(true),
        new ConfigurableProperty(Properties.USERNAME).setIsRequired(true),
        new ConfigurableProperty(Properties.PASSWORD).setIsRequired(true)
            .setIsSensitive(true)
    );
    
    @Override
    public ConfigurablePropertyMap getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String,String> parameters) {
        properties.setValues(parameters);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize() throws BridgeError {
        this.server = properties.getValue(Properties.SERVER);
        this.username = properties.getValue(Properties.USERNAME);
        this.password = properties.getValue(Properties.PASSWORD);
    }
    
    @Override
    public Count count(BridgeRequest request) throws BridgeError {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Record retrieve(BridgeRequest request) throws BridgeError {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RecordList search(BridgeRequest request) throws BridgeError {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
