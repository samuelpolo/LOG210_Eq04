/*    */ package org.codehaus.jackson.io;
/*    */ 
/*    */ import org.codehaus.jackson.SerializableString;
/*    */ import org.codehaus.jackson.util.CharTypes;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class CharacterEscapes
/*    */ {
/*    */   public static final int ESCAPE_NONE = 0;
/*    */   public static final int ESCAPE_STANDARD = -1;
/*    */   public static final int ESCAPE_CUSTOM = -2;
/*    */   
/*    */   public abstract int[] getEscapeCodesForAscii();
/*    */   
/*    */   public abstract SerializableString getEscapeSequence(int paramInt);
/*    */   
/*    */   public static int[] standardAsciiEscapesForJSON()
/*    */   {
/* 67 */     int[] esc = CharTypes.get7BitOutputEscapes();
/* 68 */     int len = esc.length;
/* 69 */     int[] result = new int[len];
/* 70 */     System.arraycopy(esc, 0, result, 0, esc.length);
/* 71 */     return result;
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\io\CharacterEscapes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */