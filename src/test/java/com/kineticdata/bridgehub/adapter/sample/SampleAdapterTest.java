/*
 * This class is to test the public and protected methods of the Simple Adapter
 */
package com.kineticdata.bridgehub.adapter.sample;

import com.kineticdata.bridgehub.adapter.BridgeAdapterTestBase;
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
}
