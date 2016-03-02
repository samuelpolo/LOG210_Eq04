/*    */ package org.codehaus.jackson.sym;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Name
/*    */ {
/*    */   protected final String _name;
/*    */   
/*    */ 
/*    */   protected final int _hashCode;
/*    */   
/*    */ 
/*    */ 
/*    */   protected Name(String name, int hashCode)
/*    */   {
/* 17 */     this._name = name;
/* 18 */     this._hashCode = hashCode;
/*    */   }
/*    */   
/* 21 */   public String getName() { return this._name; }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract boolean equals(int paramInt);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract boolean equals(int paramInt1, int paramInt2);
/*    */   
/*    */ 
/*    */ 
/*    */   public abstract boolean equals(int[] paramArrayOfInt, int paramInt);
/*    */   
/*    */ 
/*    */ 
/* 41 */   public String toString() { return this._name; }
/*    */   
/* 43 */   public final int hashCode() { return this._hashCode; }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 48 */     return o == this;
/*    */   }
/*    */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\sym\Name.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */