/*    */ package org.codehaus.jackson.annotate;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.annotation.Retention;
/*    */ import java.lang.annotation.RetentionPolicy;
/*    */ import java.lang.annotation.Target;
/*    */ import java.lang.reflect.Member;
/*    */ import java.lang.reflect.Modifier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Target({java.lang.annotation.ElementType.TYPE})
/*    */ @Retention(RetentionPolicy.RUNTIME)
/*    */ @JacksonAnnotation
/*    */ public @interface JsonAutoDetect
/*    */ {
/*    */   JsonMethod[] value() default {JsonMethod.ALL};
/*    */   
/*    */   Visibility getterVisibility() default Visibility.DEFAULT;
/*    */   
/*    */   Visibility isGetterVisibility() default Visibility.DEFAULT;
/*    */   
/*    */   Visibility setterVisibility() default Visibility.DEFAULT;
/*    */   
/*    */   Visibility creatorVisibility() default Visibility.DEFAULT;
/*    */   
/*    */   Visibility fieldVisibility() default Visibility.DEFAULT;
/*    */   
/*    */   public static enum Visibility
/*    */   {
/* 50 */     ANY, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 55 */     NON_PRIVATE, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 61 */     PROTECTED_AND_PUBLIC, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 66 */     PUBLIC_ONLY, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 72 */     NONE, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 79 */     DEFAULT;
/*    */     
/*    */     private Visibility() {}
/* 82 */     public boolean isVisible(Member m) { switch (JsonAutoDetect.1.$SwitchMap$org$codehaus$jackson$annotate$JsonAutoDetect$Visibility[ordinal()]) {
/*    */       case 1: 
/* 84 */         return true;
/*    */       case 2: 
/* 86 */         return false;
/*    */       case 3: 
/* 88 */         return !Modifier.isPrivate(m.getModifiers());
/*    */       case 4: 
/* 90 */         if (Modifier.isProtected(m.getModifiers())) {
/* 91 */           return true;
/*    */         }
/*    */       
/*    */       case 5: 
/* 95 */         return Modifier.isPublic(m.getModifiers());
/*    */       }
/* 97 */       return false;
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\annotate\JsonAutoDetect.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */