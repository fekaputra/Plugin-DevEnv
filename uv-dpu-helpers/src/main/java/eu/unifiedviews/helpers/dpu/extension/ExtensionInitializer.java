/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package eu.unifiedviews.helpers.dpu.extension;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.unifiedviews.helpers.dpu.context.Context;
import eu.unifiedviews.dpu.DPUException;

/**
 * Based on annotation performs DPU initialization.
 *
 * @author Škoda Petr
 */
public class ExtensionInitializer {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Init {

        /**
         *
         * @return String parameter.
         */
        String param() default "";

    }

    /**
     * Use can register this call back to be notified whenever field is set during initialization.
     */
    public interface FieldSetListener {
        
        /**
         * Called on every field that has been set.
         * 
         * @param field
         * @param value 
         */
        public void onField(Field field, Object value);
        
    }

    private final List<FieldSetListener> listeners = new LinkedList<>();

    /**
     * Store initialized objects.
     */
    private final List<Extension> initializables = new ArrayList<>();

    /**
     * Object to initialize.
     */
    private final Object object;

    public ExtensionInitializer(Object object) {
        this.object = object;
    }


    /**
     * Initialize given object and call preInit.
     *
     * @param object
     * @param context Context used during initialization.
     * @throws DPUException
     */
    public void preInit() throws DPUException {
        final List<Field> fields = scanForFields(object.getClass(), Init.class);        
        for (Field field : fields) {
            initializables.add(initField(field));
        }
    }

    /**
     * Call afterInit on initialized objects, configuration should be available in context for DPU's
     * execution.
     *
     * In case of dialog:
     * - Configuration is not accessible and it will be set through out proper interface.
     * - Layout will be called by proper interface.
     * - Use only to store copy of context or to search for other add-ons.
     *
     * @param object
     * @param context Context used during initialization.
     * @throws DPUException
     */
    public void afterInit(Context context) throws DPUException {
        // Call afterInit.
        for (Extension initializable : initializables) {
            initializable.afterInit(context);
        }
    }

    /**
     * Register callback.
     *
     * @param callback
     */
    public void addCallback(FieldSetListener callback) {
        this.listeners.add(callback);
    }

    /**
     * Return fields with given annotation.
     *
     * @param <T>
     * @param clazz
     * @param annotationClass
     * @return All field with given annotation.
     */
    private <T extends Annotation >List<Field> scanForFields(Class<?> clazz, Class<T> annotationClass) {
        final Field[] allFields = clazz.getFields();
        final List<Field> result = new LinkedList<>();
        for (Field field : allFields) {
            if (field.getAnnotation(annotationClass) != null) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * Initialize a single field on given object.
     *
     * @param field
     * @return Newly created instance.
     */
    private Extension initField(Field field) throws DPUException {
        final Init annotation = field.getAnnotation(Init.class);
        if (annotation == null) {
            throw new DPUException("Missing annotation for: " + field.getName());
        }
        if (!Extension.class.isAssignableFrom(field.getType())) {
            throw new DPUException("Init annotation miss used for non Initializable type, field: "
                    + field.getName());            
        }
        // Create object instance.
        final Extension fieldValue;
        try {
            fieldValue = (Extension)field.getType().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DPUException(e);
        }
        // Call init.
        fieldValue.preInit(annotation.param());
        // Set new value to given object.
        if ((field.getModifiers() & Modifier.PUBLIC) > 0) {
            // It's public we set it directly.
            try {
                field.set(object, fieldValue);
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                throw new DPUException("Can't set public field: " + field.getName() + " with class: "
                        + fieldValue.getClass().getSimpleName(), ex);
            }
        } else {
            // It's private or protected we need to use setter.
            final PropertyDescriptor fieldDesc;
            try {
                fieldDesc = new PropertyDescriptor(field.getName(), object.getClass());
            } catch (IntrospectionException ex) {
                throw new DPUException("Can't get property descriptor for: " + field.getName(), ex);
            }
            final Method fieldSetter = fieldDesc.getWriteMethod();
            try {
                fieldSetter.invoke(object, fieldValue);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new DPUException("Can't set field: " + field.getName() + " with class: "
                        + fieldValue.getClass().getSimpleName(), ex);
            }
        }
        // Notify others.
        for (FieldSetListener listener : listeners) {
            listener.onField(field, fieldValue);
        }
        return fieldValue;
    }

}
