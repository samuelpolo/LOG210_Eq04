/*    */ package org.codehaus.jackson.sym;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NameN
/*    */   extends Name
/*    */ {
/*    */   final int[] mQuads;
/*    */   
/*    */   final int mQuadLen;
/*    */   
/*    */ 
/*    */   NameN(String name, int hash, int[] quads, int quadLen)
/*    */   {
/* 15 */     super(name, hash);
/*    */     
/*    */ 
/*    */ 
/* 19 */     if (quadLen < 3) {
/* 20 */       throw new IllegalArgumentException("Qlen must >= 3");
/*    */     }
/* 22 */     this.mQuads = quads;
/* 23 */     this.mQuadLen = quadLen;
/*    */   }
/*    */   
/*    */   public boolean equals(int quad)
/*    */   {
/* 28 */     return false;
/*    */   }
/*    */   
/*    */   public boolean equals(int quad1, int quad2) {
/* 32 */     return false;
/*    */   }
/*    */   
/*    */   public boolean equals(int[] quads, int qlen)
/*    */   {
/* 37 */     if (qlen != this.mQuadLen) {
/* 38 */       return false;
/*    */     }
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
/* 61 */     for (int i = 0; i < qlen; i++) {
/* 62 */       if (quads[i] != this.mQuads[i]) {
/* 63 */         return false;
/*    */       }
/*    */     }
/* 66 */     return true;
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\sym\NameN.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */