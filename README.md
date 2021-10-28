# kinetic-sample-bridge-adapter
A sample bridge adapter project used with training activities that can be found on Kinetic Data community site.

# Activities
* These activities will be done with NetBeans 12.1.
* Commands, including git commands, are run from the terminal.
* **IMPORTANT**: After adding some code snippets new libraries will need to be imported into the project.  In NetBeans an error identified by a red line under the offending code.  To import the library click the indicator on the line number to the left of the erroring code.  The activities point out what libraries need to be imported.   

## Initial Step Clone project and open in IDE
After this step you will have the project local to your machine and open in an IDE.  At this point the adapter only implements the **BridgeAdapter** interface and Overrides all of the abstract methods.
1. Clone this repository.
1. Change directory into the cloned repo.
    * ```cd kinetic-sample-bridge-adapter```
1. Check out the **Start** branch.
    * ```git checkout start```
1. Create new branch to work on.
    * ```git checkout -b feature/training-start```
1. Open project in IDE 
    * In NetBeans go file > open project > navigate to location where the repo is cloned.

## Step 1
In step 1 we Name the adapter which will show in the Agent UI dropdown list.  We implement the getVersion which has no UI use at this time.  The adapter properties are also implemented allowing the adapter to get and set configurable properties.  Once step 1 has been completed it is possible to deploy the adapter to an agent without breaking the UI, but the adapter will not be currently functional. 
### Step 1.1 Name the adapter
1. Name the bridge adapter (this will show in the Agent UI).  
    * Add this to the top of the SimpleAdapter class: 
    ```Java
        public static final String NAME = "Sample";
    ```
1. Update **getName** method.  
    * Replace the placeholder content of the method with: 
    ```Java
        return NAME;
    ```

### Step 1.2 Get the adapter version number
1. Add a **VERSION** variable.
    * Add this after **NAME** variable declaration: 
    ```Java
        public static String VERSION;
    ```
1. Assign the VERSION variable the version number defined in the _pom.xml_ file. 
    * **Addition Info**: The adapter will try to read from _.version_ file that is in _src/main/resources_.  In that file a version number is set during the build execution.  The version is driven of the version number set on the project in the pom.xml file.
    * Add this after **VERSION** variable declaration: 
    ```Java    
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
    ``` 
    * Make sure to import **java.io.IOException**.
1. Update **getVersion** method.  
    * Replace the placeholder content of the method with: 
    ```Java
        return VERSION;
    ```

### Step 1.3 Get and Set adapter properties
1. Add properties.
    * Add this after the Version code from the previous step:
    ```Java
        private final ConfigurablePropertyMap properties = new ConfigurablePropertyMap(
        );
    ```
    * Make sure to import **com.kineticdata.commons.v1.config.ConfigurableProperty**
1. Update **setProperties** method.
    * Replace the full method with: 
    ```Java
        public void setProperties(Map<String,String> parameters) {
            properties.setValues(parameters);
        }
    ```
    * Don't forget to leave the _@Override_ annotation.
1. Update **getProperties** method.
    * Replace the placeholder content of the method with: 
    ```Java
        return properties;
    ```

## Step 2
In step 2 we define a couple of configurable properties and update the initialize method.  Configurable properties are not need for this training but will be implemented for learning purposes.  The initialize method is called when the adapter properties have been set and the adapter is being tested.

### Step 2.1 Add Configurable Properties
1. Add _Server_, _Username_, and _Password_ configurable properties.
    * Add between the Version code from step 1.2 and the ConfigurablePropertyMap from step 1.3:
    ```Java
        public static class Properties {
            public static final String SERVER = "Server Url";
            public static final String USERNAME = "Username";
            public static final String PASSWORD = "Password";
        }    
    ```
    * **Additional Info**: The above code creates a _Properties_ inner class with a _SERVER_, _USERNAME_, and _PASSWORD_ variables.  
    * **Additional Info**: One of the most common uses of properties is source system connection information i.e. username / password.
1. Update the ConfigurablePropertyMap with properties.
    * Add between the parenthesis: ex _new ConfigurablePropertyMap(_ **Add Code Here** _)_
    ```Java
        new ConfigurableProperty(Properties.SERVER).setIsRequired(true),
        new ConfigurableProperty(Properties.USERNAME).setIsRequired(true),
        new ConfigurableProperty(Properties.PASSWORD).setIsRequired(true)
            .setIsSensitive(true)
    ```
    * **Additional Info**: Notice that configurable properties can have attributes set.  Above we are making all of the properties required and the PASSWORD property is set to sensitive.  These attributes are respected by the Agent UI.
1. Add fields to the SampleAdapter class for the properties.
    * Add class fields (Java standards put class fields at the top of the class):
    ```Java
        private String server;
        private String username;
        private String password;
    ```
### Step 2.2 Implement initialize method
**Additional Info**: The initialize method is commonly used to assign class field values from the properties.  In addition it is common to try to make a call to the source system as a test that the credentials are good and the system is up.
**Additional Info**: When step 2 is complete the adapter can be deployed to an agent without throwing an exception.  Since not of the implementation methods have been implemented the adapter will not be functional.

1. Update **initialize** method.
    * Replace the placeholder content of the method with: 
    ```Java
        this.server = properties.getValue(Properties.SERVER);
        this.username = properties.getValue(Properties.USERNAME);
        this.password = properties.getValue(Properties.PASSWORD);
    ```
    * **Additional Info**: In Java _this_ refers to the class.  Notice how we are assigning the class fields that were defined in step 2.1.  The Plugins section of the Platform UI is where _properties_ values are defined.  The values get save to a local storage or a database that the agent is connected to.

## Midpoint
At this point we are going to checkout a new branch before completing the implementation methods count, retrieve, and search.  There are several additions to the project that will be pointed out below.  

### Start by checking out the new branch
1. Optionally commit the work done in previous steps
    * ```git add .```
    * ```git commit -m "starting steps completed"```
1. Checkout new branch.
    * ```git checkout midpoint```
1. Create new branch to work on.
    * ```git checkout -b feature/training-midpoint```

To start you will notice that the project now includes a new class called _SampleQualificationParser_ and the SampleAdapter class has a new class field
```Java
    private SampleQualificationParser parser = new SampleQualificationParser();
```
The SampleQualificationParser class extends _QualificationParser_ from the kinetic-agent-adapter project.  The _parser_ class field has a **parse** method that we will use to replace parameter placeholders.

Several helper methods have been added to the SampleAdapter class to assist in common functions that the implementation methods leverage.  Creating helper methods is a good coding practice that reduces code duplication. 

There is a LOGGER class field that is used to log messages.  Two of the helper methods have logging statements.  The **fetchData** helper method logs a message that simulates a Http style call to get data.  To enable the logger to log in NetBeans during logging tests log4j2.xml was added to _src/test/resources_ directory and additional test dependencies were added to the pom.xml file.  A **build with dependencies** of the project maybe required.

There is a json file in _src/main/resources/META-INF/structures_ that is acting as a simulated database for testing.  When the fetchData method is called the method will retrieve a dataset from the json file.

Unit tests have been added to enable testing of the adapter without the need to deploy each time.  The unit tests can be found in the _SampleAdapterTest_ file.  None of the tests will pass initially, but as we continue with the next steps the tests will begin to pass.

The final addition was added to the _bridge-config.yml_ file.  This file is used to the adapter during unit testing and build operations.  The bridge-config.yml allows for user input to be entered into configurable properties and metadata.  This training does not go over metadata yet, but in step 1.3 we added configurable properties.  The properties added were also added to the bridge-config.yml file to allow for unit testing.

## Step 3
In this step we will get the adapter to a point were the **count** method will be fully functional.  We will also begin adding code to the **retrieve** and **search** methods to support their operations.

### Step 3.1 Add and test supported structures
1. Add structure array.
    * For simplicity add this code after the initialize method and before the count method:
    ```Java
        private static final ArrayList<String> STRUCTURES = 
            new ArrayList( Arrays.asList("cars") );
    ``` 
1. Add a supported structure test check to each method.
    * Update count, retrieve, search methods by adding supported structure check above existing code: 
    ```Java
        if (!STRUCTURES.contains(request.getStructure())) {
            throw new BridgeError(String.format("Structure %s is not supported",
                request.getStructure()));
        }
    ```
    * **Additional Info**: Note that we are throwing our first BridgeError.  If this code is executed in the agent a statement will be added to the log.  Throwing errors halts the execution of the program preventing additional functionality form being executed.
1. Test that the added code throws a new BridgeError.
    * Open the SampleAdapterTest file: 
        * In NetBeans it can be found under the kinetic-sample-bridge-adapter > Test Packages > com.kineticdata.bridgehub.adapter.sample in the Projects Tab.  
        * If the Project Tab is not displayed select Window > Projects from the menu bar.
    * Find and test the _test\_invalidStructure_ method.
        * In NetBeans click the curser inside the method body.  Right click mouse and select **Run Focused Test Method**
    * There should be _Output_ and _Test Results_ pain. If everything is successful the Test Results pain will display **Tests passed* with in a green banner. 

### Step 3.2 Prepare to fetch simulated data.
1. Add the fetchData method to count, retrieve, and search methods.
    * Update the methods to fetch data by adding this between the structures test and the _UnsupportedOperationException_:
    ```Java
        JSONArray responseData = fetchData(request);
    ```
    * **Additional Info**: The request is passed to fetchData where the structure is used to build the file name to the simulated json database.  Checking that the structure was valid prevents an error in the fetchData method.  It is common to use the structure as part of the data fetch process.
1. Add values to the bridge-config.yml file
    * In NetBeans the file can be found under kinetic-sample-bridge-adapter > Other Test Sources > _src/test/resources.
    * Populate the **Server Url** fields **Username** with any values you like.

### Step 3.3 Complete and test the count implementation method.
1. Add code to count the results returned from the fetch data simulated request.
    * Replace the line that throws the _UnsupportedOperationException_ with:
    ```Java        
        Count count = new Count();
        count.setValue(responseData.size());
        
        return count;
    ```
    * **Additional Info**: The code grabs all of the results from the simulated request call.  In the search method we implement a way to reduce the results for the call.  In reality the source system would likely have some pattern for executing a _search_ that would filter the results before sending a response to the adapter.
1. Test the added code successfully preforms a count.
    * Similar to testing the _invalidStructure_ follow the Steps in 3.1, open SampleAdapterTest and run Run Focused Test Method.