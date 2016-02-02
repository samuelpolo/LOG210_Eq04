/*     */ package org.codehaus.jackson;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JsonStreamContext
/*     */ {
/*     */   protected static final int TYPE_ROOT = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static final int TYPE_ARRAY = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static final int TYPE_OBJECT = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _type;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _index;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonStreamContext getParent();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean inArray()
/*     */   {
/*  71 */     return this._type == 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean inRoot()
/*     */   {
/*  78 */     return this._type == 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public final boolean inObject()
/*     */   {
/*  84 */     return this._type == 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String getTypeDesc()
/*     */   {
/*  92 */     switch (this._type) {
/*  93 */     case 0:  return "ROOT";
/*  94 */     case 1:  return "ARRAY";
/*  95 */     case 2:  return "OBJECT";
/*     */     }
/*  97 */     return "?";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int getEntryCount()
/*     */   {
/* 105 */     return this._index + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int getCurrentIndex()
/*     */   {
/* 113 */     return this._index < 0 ? 0 : this._index;
/*     */   }
/*     */   
/*     */   public abstract String getCurrentName();
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\JsonStreamContext.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */