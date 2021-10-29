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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kineticdata
 * @date   10-26-2021
 */
public class SampleAdapter implements BridgeAdapter {
    private String server;
    private String username;
    private String password;
    private SampleQualificationParser parser = new SampleQualificationParser();
    
    /* Defines the adapter display name. */
    public static final String NAME = "Sample";
    
    /* Defines the LOGGER */
    protected static final org.slf4j.Logger LOGGER = 
        LoggerFactory.getLogger(SampleAdapter.class);

    /* Adapter version constant. */
    public static String VERSION;
    /* Load the properties version from the version.properties file. */
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
    
    /* Supported Structures */
    private static final ArrayList<String> STRUCTURES = 
        new ArrayList( Arrays.asList("cars") );
    
    @Override
    public Count count(BridgeRequest request) throws BridgeError {
        if (!STRUCTURES.contains(request.getStructure())) {
            throw new BridgeError(String.format("Structure %s is not supported",
                request.getStructure()));
        }
        
        JSONArray responseData = fetchData(request);
        
        Count count = new Count();
        count.setValue(responseData.size());
        
        return count;
    }

    @Override
    public Record retrieve(BridgeRequest request) throws BridgeError {
        if (!STRUCTURES.contains(request.getStructure())) {
            throw new BridgeError(String.format("Structure %s is not supported",
                request.getStructure()));
        }
           
        JSONArray responseData = fetchData(request);
        
        // Replace parameter values in query string.
        String parsedQueryString = parser.parse(request.getQuery(), request.getParameters()); 
        Map<String, String> parameters = getParameters(parsedQueryString);
        
        List<String> fields = request.getFields() == null ? 
            new ArrayList() : 
            request.getFields();
        
        Record record = new Record();
        for (int i = 0; i < responseData.size(); i++) {
            JSONObject jsonObj = (JSONObject)responseData.get(i);
            
            // This is a safe way to parse the parameter value.
            int queryId = NumberUtils.toInt(parameters.get("Id"), -1); // -1 is not a valid id
            
            // We control the data so we know it is safe to cast.  In a real scenario
            // Exception handling would be required.
            int itemId = ((Long)jsonObj.get("Id")).intValue();
            
            // We are retrieving by 'Id.' Different adapters would solve this problem
            // differently.
            if (itemId == queryId) {
                // build record from json object.
                record = buildRecord(jsonObj, fields);
               
                // Found the object we are looking for break the loop.
                break;
            } 
        }
        
        return record;
    }

    @Override
    public RecordList search(BridgeRequest request) throws BridgeError {
        if (!STRUCTURES.contains(request.getStructure())) {
            throw new BridgeError(String.format("Structure %s is not supported",
                request.getStructure()));
        }        
        
        JSONArray responseData = fetchData(request);
                
        // Replace parameter values in query string.
        String parsedQueryString = parser.parse(request.getQuery(), request.getParameters()); 
        Map<String, String> parameters = getParameters(parsedQueryString);
        
        String searchKey = null;
        if (parameters.containsKey("search_on")) {
            // Get the key for comparison
            searchKey = (String)parameters.get("search_on");
        }
                
        List<String> fields = request.getFields() == null ? 
            new ArrayList() : 
            request.getFields();
        
        // If no fields were provided then all fields will be returned. This is 
        // done here so we return the correct fields from search.
        if (fields.isEmpty()){
            fields = new ArrayList<>(((JSONObject)responseData.get(0)).keySet());
        }
                
        List records = new ArrayList();
        for (int i = 0; i < responseData.size(); i++) {
            Record record = new Record();
            JSONObject jsonObj = (JSONObject)responseData.get(i);
            
            if (searchKey == null ||  
                (jsonObj.containsKey(searchKey) && 
                parameters.get(searchKey).equals(jsonObj.get(searchKey)))) {
                
                // build record from json object.
                record = buildRecord(jsonObj, fields);
                
                // add record to the list.
                records.add(record);
                
            }
        }
        
        return new RecordList(fields, records);
    }
    
    /*----------------------------------------------------------------------------------------------
     * PRIVATE HELPER METHODS
     *--------------------------------------------------------------------------------------------*/
    
    /* 
     * Method used to simulate the fetching of data.
     */
    private JSONArray fetchData(BridgeRequest request) throws BridgeError {
        
        LOGGER.debug(String.format("Simulating a call to %s/api/v1/%s as user %s",
            this.server, request.getStructure(), this.username));
        
        InputStream contentStream = SampleAdapter.class
            .getResourceAsStream("/META-INF/structures/" + request.getStructure() + ".json");
        
        JSONArray mockResponseData;
        try {
            JSONParser jsonParser = new JSONParser();
            mockResponseData = (JSONArray)jsonParser.parse(
                new InputStreamReader(contentStream, "UTF-8"));
        } catch (Exception e) {
            throw new BridgeError("There was a problem reading the form definition"
                + "In the Kinetic Task Sync adapter", e);            
        }
        
        return mockResponseData;
    }
    
    
    /*
     * Build a Record from a json object
     */
    private Record buildRecord(JSONObject jsonObj, List<String> fields) {
        Map<String, Object> record = new HashMap<>();
        
        // if no fields were provided then all fields will be returned. 
        if(fields.isEmpty()){
            fields = new ArrayList<>(jsonObj.keySet());
        }
        
        // Loop fields and build record
        fields.stream().forEach(field -> {
            Object value = null;
            
            if (jsonObj.containsKey(field)) {
                value = jsonObj.get(field);
            }
            
            record.put(field, value);
        });
        
        return new Record(record);
    }
    
    /*
     * Take a URL style query string and return a Map of parameters 
     */
    private Map getParameters(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        
        // Return empyt map if no query was provided from request.
        if (!queryString.isEmpty()) {
            // Regex allows for & to be in field names.
            String[] queries = queryString.split("&(?=[^&]*?=)");
            for (String query : queries) {
                // Split the query on the = to determine the field/value key-pair. 
                // Anything before the first = is considered to be the field and 
                // anything after (including more = signs if there are any) is 
                // considered to be part of the value
                String[] parameter = query.split("=",2);
                if (parameter.length == 2) {
                    // If the parameter is found more than once the values are 
                    // returned in csv form.
                    parameters.merge(parameter[0].trim(), parameter[1].trim(), 
                        (prev, curr) -> {
                            return String.join(",", prev, curr);
                        }
                    );
                } else if (parameter.length == 1) {
                    parameters.put(parameter[0].trim(), null);
                } else {
                    LOGGER.warn(
                        String.format("There was an issue parsing the %s parameter", 
                        parameter[0].trim())
                    );
                }
            }
        }
        
        return parameters;
    }
}
