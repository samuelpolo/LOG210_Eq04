/*    */ package org.codehaus.jackson;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JsonGenerationException
/*    */   extends JsonProcessingException
/*    */ {
/*    */   static final long serialVersionUID = 123L;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonGenerationException(Throwable rootCause)
/*    */   {
/* 16 */     super(rootCause);
/*    */   }
/*    */   
/*    */   public JsonGenerationException(String msg)
/*    */   {
/* 21 */     super(msg, (JsonLocation)null);
/*    */   }
/*    */   
/*    */   public JsonGenerationException(String msg, Throwable rootCause)
/*    */   {
/* 26 */     super(msg, (JsonLocation)null, rootCause);
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\JsonGenerationException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */