package org.codehaus.jackson.annotate;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSubTypes
{
  Type[] value();
  
  public static @interface Type
  {
    Class<?> value();
    
    String name() default "";
  }
}


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\annotate\JsonSubTypes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */