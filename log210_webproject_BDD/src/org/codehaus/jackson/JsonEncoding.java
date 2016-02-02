/*    */ package org.codehaus.jackson;
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
/*    */ public enum JsonEncoding
/*    */ {
/* 14 */   UTF8("UTF-8", false), 
/* 15 */   UTF16_BE("UTF-16BE", true), 
/* 16 */   UTF16_LE("UTF-16LE", false), 
/* 17 */   UTF32_BE("UTF-32BE", true), 
/* 18 */   UTF32_LE("UTF-32LE", false);
/*    */   
/*    */ 
/*    */   protected final String _javaName;
/*    */   
/*    */   protected final boolean _bigEndian;
/*    */   
/*    */   private JsonEncoding(String javaName, boolean bigEndian)
/*    */   {
/* 27 */     this._javaName = javaName;
/* 28 */     this._bigEndian = bigEndian;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getJavaName()
/*    */   {
/* 36 */     return this._javaName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean isBigEndian()
/*    */   {
/* 46 */     return this._bigEndian;
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\JsonEncoding.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */