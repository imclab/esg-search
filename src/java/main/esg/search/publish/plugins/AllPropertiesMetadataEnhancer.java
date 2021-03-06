package esg.search.publish.plugins;

import java.util.List;
import java.util.Properties;

import esg.search.core.Record;

/**
 * Implementation of {@link MetadataEnhancer} 
 * that inserts all (key, value) pairs contained in the given property file.
 * 
 * @author Luca Cinquini
 *
 */
public class AllPropertiesMetadataEnhancer extends BaseMetadataEnhancerImpl {
    
    private final Properties properties;
    
    public AllPropertiesMetadataEnhancer(Properties properties) {
        this.properties = properties;
    }


    /**
     * This implementation inserts all (key,value) pairs contained in the property file.
     * The method arguments "name" and "values" are disregarded.
     */
    @Override
    public void enhance(String name, List<String> values, Record record) {
                
        // loop over properties
        for(String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            record.addField(key, value);
          }
        
    }

}
