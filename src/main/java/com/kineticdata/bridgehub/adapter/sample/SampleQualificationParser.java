package com.kineticdata.bridgehub.adapter.sample;

import com.kineticdata.bridgehub.adapter.QualificationParser;

/**
 * This class extends QualificationParser. QualificationParser has methods for 
 * mapping request.query parameters to request.parameters values.
 * 
 * @author kineticdata
 */
public class SampleQualificationParser extends QualificationParser {

    @Override
    public String encodeParameter(String name, String value) {
        // The sample has no need to handle parameters differently because all of 
        // the data is static.
        return value;
    }
    
}
