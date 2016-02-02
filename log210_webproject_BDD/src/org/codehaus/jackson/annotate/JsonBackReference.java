package org.codehaus.jackson.annotate;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonBackReference
{
  String value() default "defaultReference";
}


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\annotate\JsonBackReference.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */