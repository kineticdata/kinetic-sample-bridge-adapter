/*
 * This class is to test the public and protected methods of the Simple Adapter
 */
package com.kineticdata.bridgehub.adapter.sample;

import com.kineticdata.bridgehub.adapter.BridgeAdapterTestBase;
import com.kineticdata.bridgehub.adapter.BridgeError;
import com.kineticdata.bridgehub.adapter.BridgeRequest;
import com.kineticdata.bridgehub.adapter.Count;
import com.kineticdata.bridgehub.adapter.Record;
import com.kineticdata.bridgehub.adapter.RecordList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author KineticData
 */
public class SampleAdapterTest extends BridgeAdapterTestBase {

    @Override
    public String getConfigFilePath() {
        return "src/test/resources/bridge-config.yml";
    }

    @Override
    public Class getAdapterClass() {
        return SampleAdapter.class;
    }
    
    @Test
    public void test_invalidStructure() {
        BridgeRequest request = new BridgeRequest();
        BridgeError error = null;
        
        // Set request properties.
        request.setStructure("xxx");
        
        Count count = new Count();
        try {
            // Simulate bridge call
            count = getAdapter().count(request);
        } catch (BridgeError e) {
            error = e;
        }
        
        // Check that no bridge error was thrown.
        assertNotNull(error);
    }
    
    @Test
    public void test_count() {
        BridgeRequest request = new BridgeRequest();
        BridgeError error = null;
        
        // Set request properties.
        request.setStructure("cars");
        
        Count count = new Count();
        try {
            // Simulate bridge call
            count = getAdapter().count(request);
        } catch (BridgeError e) {
            error = e;
        }
        
        // Check that no bridge error was thrown.
        assertNull(error);
        // We control the data so we know how many cars are returned.
        assertTrue(count.getValue() == 3825);
    }
    
    @Test 
    public void test_retrieve() {
        BridgeRequest request = new BridgeRequest();
        BridgeError error = null;
        
        // Make a list of fields
        List<String> fields = Arrays.asList("Make", "Model", "Year", "Id");
        
        // Set parameters with value
        Map<String, String> parameters = new HashMap<String, String>(){{
            put("Car Id", "2056");
        }};
        
        // Set request properties.
        request.setStructure("cars");
        request.setFields(fields);
        request.setParameters(parameters);
        request.setQuery("Id=<%=parameter[\"Car Id\"]%>");
        
        Record record = new Record();
        try {
            // Simulate bridge call
            record = getAdapter().retrieve(request);
        } catch (BridgeError e) {
            error = e;
        }
        
        // Check that no bridge error was thrown.
        assertNull(error);        
        // If a matching car is found the record will not be null.
        assertNotNull(record.getRecord());
        
        // Reset the Id we are searching for to an invalid id.
        parameters.replace("Car Id", "xx");
        
        try {
            // Simulate bridge call
            record = getAdapter().retrieve(request);
        } catch (BridgeError e) {
            error = e;
        }
      
        // Check that new bridge error was thrown
        assertNull(error);
        // If a matching car is not found the record will be null.
        assertNull(record.getRecord());
    }
    
    @Test
    public void test_search() {
        BridgeRequest request = new BridgeRequest();
        BridgeError error = null;
        
        // Make a list of fields
        List<String> fields = Arrays.asList("Make", "Model", "Year", "Id");
        
        Map<String, String> parameters = new HashMap<>();
        
        // Set request properties.
        request.setStructure("cars");
        request.setFields(fields);
        request.setParameters(parameters);
        request.setQuery("");
        
        RecordList records = null;
        
        try {
            records = getAdapter().search(request);
        } catch (BridgeError e) {
            error = e;
        }

        // Check that no bridge error was thrown.
        assertNull(error);
        // We control the data so we know how many cars are returned.
        assertTrue(records.getRecords().size() == 3825);
        
        // Set parameters use for searching.
        parameters.put("Make", "BMW");
        
        // Reset the properties on the request.
        request.setParameters(parameters);
        request.setQuery("Make=<%=parameter[\"Make\"]%>&search_on=Make");
                
        try {
            records = getAdapter().search(request);
        } catch (BridgeError e) {
            error = e;
        }
        // Check that no bridge error was thrown.
        assertNull(error);
        
        assertTrue(true);
    }
}
