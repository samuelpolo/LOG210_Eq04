package org.codehaus.jackson.annotate;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIgnoreProperties
{
  String[] value() default {};
  
  boolean ignoreUnknown() default false;
}


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\annotate\JsonIgnoreProperties.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */