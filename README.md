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
1. Assign the VERSION variable the version number defined in the __pom.xml__ file. 
    * **Addition Info**: The adapter will try to read from __.version__ file that is in __src/main/resources__.  In that file a version number is set during the build execution.  The version is driven of the version number set on the project in the pom.xml file.
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
    * Don't forget to leave the __@Override__ annotation.
1. Update **getProperties** method.
    * Replace the placeholder content of the method with: 
    ```Java
        return properties;
    ```

