/*    */ package org.codehaus.jackson;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JsonParseException
/*    */   extends JsonProcessingException
/*    */ {
/*    */   static final long serialVersionUID = 123L;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonParseException(String msg, JsonLocation loc)
/*    */   {
/* 16 */     super(msg, loc);
/*    */   }
/*    */   
/*    */   public JsonParseException(String msg, JsonLocation loc, Throwable root)
/*    */   {
/* 21 */     super(msg, loc, root);
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\JsonParseException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */