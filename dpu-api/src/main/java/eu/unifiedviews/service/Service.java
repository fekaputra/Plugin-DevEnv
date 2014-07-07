package eu.unifiedviews.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Škoda Petr
 */
public interface Service {
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Bind {
        
        public boolean required() default false;
        
    }

    /**
     * 
     * @return Uri that identifies this service.
     */
    public String getUri();
    
    
}
