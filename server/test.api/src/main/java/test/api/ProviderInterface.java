package test.api;

import org.osgi.annotation.versioning.ProviderType;

/**
 * This is an interface which is designed to be implemented by providers
 * of this API rather than by users of the API. A good example of a
 * provider interface is <code>javax.servlet.ServletContext</code>
 */

@ProviderType
public interface ProviderInterface {
    
	String getName();
    
}
