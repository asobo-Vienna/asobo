package at.msm.asobo.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PatchUtils {
    public static <T> void copyNonNullProperties(T source, T target, String... ignoreProperties) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        Set<String> ignoredProps = new HashSet<>(Arrays.asList(ignoreProperties));

        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String propertyName = pd.getName();

            // Skip ignored properties
            if (ignoredProps.contains(propertyName)) {
                continue;
            }

            Object srcValue = src.getPropertyValue(propertyName);
            if (srcValue != null && pd.getWriteMethod() != null) {
                // Check if target has this property
                try {
                    trg.setPropertyValue(propertyName, srcValue);
                } catch (Exception e) {
                    // Skip properties that don't exist or can't be set
                    System.out.println("Skipping property: " + propertyName);
                }
            }
        }
    }
}