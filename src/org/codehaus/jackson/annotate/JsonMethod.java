/*    */ package org.codehaus.jackson.annotate;
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
/*    */ public enum JsonMethod
/*    */ {
/* 26 */   GETTER, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 31 */   SETTER, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 37 */   CREATOR, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 47 */   FIELD, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 56 */   IS_GETTER, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 61 */   NONE, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 66 */   ALL;
/*    */   
/*    */   private JsonMethod() {}
/*    */   
/*    */   public boolean creatorEnabled()
/*    */   {
/* 72 */     return (this == CREATOR) || (this == ALL);
/*    */   }
/*    */   
/*    */   public boolean getterEnabled() {
/* 76 */     return (this == GETTER) || (this == ALL);
/*    */   }
/*    */   
/*    */   public boolean isGetterEnabled() {
/* 80 */     return (this == IS_GETTER) || (this == ALL);
/*    */   }
/*    */   
/*    */   public boolean setterEnabled() {
/* 84 */     return (this == SETTER) || (this == ALL);
/*    */   }
/*    */   
/*    */   public boolean fieldEnabled() {
/* 88 */     return (this == FIELD) || (this == ALL);
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\annotate\JsonMethod.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */