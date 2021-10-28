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
In Step 3 we will get the adapter to a point were the **count** method will be fully functional.  We will also begin adding code to the **retrieve** and **search** methods to support their operations.

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
    * Similar to testing the _invalidStructure_ follow the Steps in 3.1, open SampleAdapterTest, click into the body of _test\_count_, and run Run Focused Test Method.

## Step 4
In Step 4 we will complete and test the retrieve implementation method.
* **Important**: Step 3 added code to the retrieve method.  Step 4 assumes that the code has been added and is required for the method to function.

### Step 4.1 Get parameters from the request.
1. Add a **parse** and **getParameters** to get a Map of parameters.
    * Update search and retrieve methods by adding between the **fetchData** method and the _UnsupportedOperationException_:
    ```Java
        String parsedQueryString = parser.parse(request.getQuery(), request.getParameters()); 
        Map<String, String> parameters = getParameters(parsedQueryString);
    ```
    * **Additional Info**: The class field variable parser has a parse method that is used to replace the parameter placeholders with the values of the parameters.  A query string with "swapped" values is the output of the parse method.  It is common to structure a query similar to a URI standard query string.  The getParameters method is commonly used to brake the query into a map of parameter name to parameter value.  This enable easier access to the query parameters further down in the code sequence.

### Step 4.2 Complete the retrieve method by locating the desired data by Id.
1. Add a loop that will take the fetched data and find an element in that data by Id.
    * Replace the line that throws the _UnsupportedOperationException_ with:
    ```Java
        Record record = new Record();
        for (int i = 0; i < responseData.size(); i++) {
            JSONObject jsonObj = (JSONObject)responseData.get(i);
            
            int queryId = NumberUtils.toInt(parameters.get("Id"), -1); // -1 is not a valid id
            
            int itemId = ((Long)jsonObj.get("Id")).intValue();
            
            if (itemId == queryId) {
                record = buildRecord(jsonObj, request.getFields());
                break;
            } 
        }
        
        return record;
    ```
    * **Additional Info**: In the loop assumptions are being made about the data.  First the adapter assumes that the identifier of an element in the response data uses 'Id'.  Second the adapter also assumes that the parameter name that identifies the value that should be searched for is also 'Id'.  These kinds of assumptions are common in adapters and will very depending on the source system the adapter is integrating with.
    * **Additional Info**: Extracting the id from the item makes assumptions about the data that would not be safe to do in real scenarios.  First is assumes that the Id exists on the response object.  Second it assumes that data will be an integer which the Simple JSON library converts to a Lond data type.  Writing code in this fashion when the data is not guaranteed to conform to a schema is not recommended.  Since the data is static within the project this is safe.
1. Test the added code successfully preforms a retrieve.
    * Similar to testing the _invalidStructure_ follow the Steps in 3.1, open SampleAdapterTest, click into the body of _test\_retrieve_, and run Run Focused Test Method.
    * **Additional Info**: Looking at the _test\_retrieve_ method we can see it is two tests in one.  First it checks for an id that we know exists in the dataset.  Second it checks that an invalid id does not throw and error and also does not return a result.

## Step 5
In Step 5 we will complete and test the search implementation method.
* **Important**: Step 3 and Step 4 added code to the search method. Step 4 assumes that the code has been added and is required for the method to function.

### Step 5.1 Add the ability to do a search on a key.
1. Add codeblock that will grab the search key from the parameters.
    * Update search method by adding after the parameters are assigned and before the _UnsupportedOperationException_:
    ```Java
        String searchKey = null;
        if (parameters.containsKey("search_on")) {
            // Get the key for comparison
            searchKey = (String)parameters.get("search_on");
        }
    ```
    * **Additional Info**: The parameter **search_on** is made up for this example.  It is common for source systems to have a technique to pass a query.  The query is used to filter the response data to the desired set of results.
### Step 5.2 Get a list of fields.
1. Add codeblock to build a list of fields.
    * Update search method by adding after the searchKey is assigned and before the _UnsupportedOperationException_:
    ```Java
        List<String> fields = request.getFields();
        
        // If no fields were provided then all fields will be returned. This is 
        // done here so we return the correct fields from search.
        if (fields.isEmpty()){
            fields.addAll(((JSONObject)responseData.get(0)).keySet());
        }
    ```
    * **Additional Info**: The list of fields is passed to the **buildRecord** method.  If none are provided all fields are returned.  Providing fields limits the amount of data that is returned.  Some source systems allow for the return data to be reduced to a set of desired properties.  In this case passing the fields along to the source system may increase performance of the source system data fetch.
### Step 5.3 Complete the search method and support filtering the set.
1. Loop the results while filtering and build records.
    * Replace the line that throws the _UnsupportedOperationException_ with:
    ```Java         
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
    ```
    * **Additional Info**: In the loop there is a conditional that will build a record if no searchKey is provided or will only build a record if the searchKey value and the item value match.  It is uncommon to do this behavior in the adapter.  To reduce a set in the adapter deterministically requires that all results that could possibly match are held in memory.  This is inefficient and should only be done if absolutely necessary.  The data for this example is static so it is acceptable here.
1. Test the added code successfully preforms a search.
    * Similar to testing the _invalidStructure_ follow the Steps in 3.1, open SampleAdapterTest, click into the body of _test\_search_, and run Run Focused Test Method.
    * **Additional Info**: Looking at the _test\_search_ method we can see it is two tests in one.  First it that the full dataset is returned when no **search_on** parameter is provided.  Second it filters the dataset down to the items that have a value for the search_on parameter key that matches the search_on parameter value.