/*     */ package org.codehaus.jackson.impl;
/*     */ 
/*     */ import org.codehaus.jackson.JsonLocation;
/*     */ import org.codehaus.jackson.JsonStreamContext;
/*     */ import org.codehaus.jackson.util.CharTypes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JsonReadContext
/*     */   extends JsonStreamContext
/*     */ {
/*     */   protected final JsonReadContext _parent;
/*     */   protected int _lineNr;
/*     */   protected int _columnNr;
/*     */   protected String _currentName;
/*  34 */   protected JsonReadContext _child = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonReadContext(JsonReadContext parent, int type, int lineNr, int colNr)
/*     */   {
/*  45 */     this._type = type;
/*  46 */     this._parent = parent;
/*  47 */     this._lineNr = lineNr;
/*  48 */     this._columnNr = colNr;
/*  49 */     this._index = -1;
/*     */   }
/*     */   
/*     */   protected final void reset(int type, int lineNr, int colNr)
/*     */   {
/*  54 */     this._type = type;
/*  55 */     this._index = -1;
/*  56 */     this._lineNr = lineNr;
/*  57 */     this._columnNr = colNr;
/*  58 */     this._currentName = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static JsonReadContext createRootContext(int lineNr, int colNr)
/*     */   {
/*  65 */     return new JsonReadContext(null, 0, lineNr, colNr);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JsonReadContext createRootContext()
/*     */   {
/*  73 */     return new JsonReadContext(null, 0, 1, 0);
/*     */   }
/*     */   
/*     */   public final JsonReadContext createChildArrayContext(int lineNr, int colNr)
/*     */   {
/*  78 */     JsonReadContext ctxt = this._child;
/*  79 */     if (ctxt == null) {
/*  80 */       this._child = (ctxt = new JsonReadContext(this, 1, lineNr, colNr));
/*  81 */       return ctxt;
/*     */     }
/*  83 */     ctxt.reset(1, lineNr, colNr);
/*  84 */     return ctxt;
/*     */   }
/*     */   
/*     */   public final JsonReadContext createChildObjectContext(int lineNr, int colNr)
/*     */   {
/*  89 */     JsonReadContext ctxt = this._child;
/*  90 */     if (ctxt == null) {
/*  91 */       this._child = (ctxt = new JsonReadContext(this, 2, lineNr, colNr));
/*  92 */       return ctxt;
/*     */     }
/*  94 */     ctxt.reset(2, lineNr, colNr);
/*  95 */     return ctxt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String getCurrentName()
/*     */   {
/* 105 */     return this._currentName;
/*     */   }
/*     */   
/* 108 */   public final JsonReadContext getParent() { return this._parent; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final JsonLocation getStartLocation(Object srcRef)
/*     */   {
/* 125 */     long totalChars = -1L;
/*     */     
/* 127 */     return new JsonLocation(srcRef, totalChars, this._lineNr, this._columnNr);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean expectComma()
/*     */   {
/* 142 */     int ix = ++this._index;
/* 143 */     return (this._type != 0) && (ix > 0);
/*     */   }
/*     */   
/*     */   public void setCurrentName(String name)
/*     */   {
/* 148 */     this._currentName = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 164 */     StringBuilder sb = new StringBuilder(64);
/* 165 */     switch (this._type) {
/*     */     case 0: 
/* 167 */       sb.append("/");
/* 168 */       break;
/*     */     case 1: 
/* 170 */       sb.append('[');
/* 171 */       sb.append(getCurrentIndex());
/* 172 */       sb.append(']');
/* 173 */       break;
/*     */     case 2: 
/* 175 */       sb.append('{');
/* 176 */       if (this._currentName != null) {
/* 177 */         sb.append('"');
/* 178 */         CharTypes.appendQuoted(sb, this._currentName);
/* 179 */         sb.append('"');
/*     */       } else {
/* 181 */         sb.append('?');
/*     */       }
/* 183 */       sb.append('}');
/*     */     }
/*     */     
/* 186 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\JsonReadContext.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */