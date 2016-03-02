/*     */ package org.codehaus.jackson.type;
/*     */ 
/*     */ import java.lang.reflect.Modifier;
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
/*     */ public abstract class JavaType
/*     */ {
/*     */   protected final Class<?> _class;
/*     */   protected final int _hashCode;
/*     */   protected Object _valueHandler;
/*     */   protected Object _typeHandler;
/*     */   
/*     */   protected JavaType(Class<?> raw, int additionalHash)
/*     */   {
/*  65 */     this._class = raw;
/*  66 */     this._hashCode = (raw.getName().hashCode() + additionalHash);
/*  67 */     this._valueHandler = null;
/*  68 */     this._typeHandler = null;
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
/*     */   public abstract JavaType withTypeHandler(Object paramObject);
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
/*     */   public abstract JavaType withContentTypeHandler(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType withValueHandler(Object h)
/*     */   {
/* 103 */     setValueHandler(h);
/* 104 */     return this;
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
/*     */   public JavaType withContentValueHandler(Object h)
/*     */   {
/* 118 */     getContentType().setValueHandler(h);
/* 119 */     return this;
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
/*     */   @Deprecated
/*     */   public void setValueHandler(Object h)
/*     */   {
/* 135 */     if ((h != null) && (this._valueHandler != null)) {
/* 136 */       throw new IllegalStateException("Trying to reset value handler for type [" + toString() + "]; old handler of type " + this._valueHandler.getClass().getName() + ", new handler of type " + h.getClass().getName());
/*     */     }
/*     */     
/* 139 */     this._valueHandler = h;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType narrowBy(Class<?> subclass)
/*     */   {
/* 159 */     if (subclass == this._class) {
/* 160 */       return this;
/*     */     }
/*     */     
/* 163 */     _assertSubclass(subclass, this._class);
/* 164 */     JavaType result = _narrow(subclass);
/*     */     
/*     */ 
/* 167 */     if (this._valueHandler != result.getValueHandler()) {
/* 168 */       result = result.withValueHandler(this._valueHandler);
/*     */     }
/* 170 */     if (this._typeHandler != result.getTypeHandler()) {
/* 171 */       result = result.withTypeHandler(this._typeHandler);
/*     */     }
/* 173 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType forcedNarrowBy(Class<?> subclass)
/*     */   {
/* 185 */     if (subclass == this._class) {
/* 186 */       return this;
/*     */     }
/* 188 */     JavaType result = _narrow(subclass);
/*     */     
/* 190 */     if (this._valueHandler != result.getValueHandler()) {
/* 191 */       result = result.withValueHandler(this._valueHandler);
/*     */     }
/* 193 */     if (this._typeHandler != result.getTypeHandler()) {
/* 194 */       result = result.withTypeHandler(this._typeHandler);
/*     */     }
/* 196 */     return result;
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
/*     */   public JavaType widenBy(Class<?> superclass)
/*     */   {
/* 211 */     if (superclass == this._class) {
/* 212 */       return this;
/*     */     }
/*     */     
/* 215 */     _assertSubclass(this._class, superclass);
/* 216 */     return _widen(superclass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract JavaType _narrow(Class<?> paramClass);
/*     */   
/*     */ 
/*     */ 
/*     */   protected JavaType _widen(Class<?> superclass)
/*     */   {
/* 227 */     return _narrow(superclass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JavaType narrowContentsBy(Class<?> paramClass);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract JavaType widenContentsBy(Class<?> paramClass);
/*     */   
/*     */ 
/*     */ 
/*     */   public final Class<?> getRawClass()
/*     */   {
/* 243 */     return this._class;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean hasRawClass(Class<?> clz)
/*     */   {
/* 251 */     return this._class == clz;
/*     */   }
/*     */   
/*     */   public boolean isAbstract() {
/* 255 */     return Modifier.isAbstract(this._class.getModifiers());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isConcrete()
/*     */   {
/* 262 */     int mod = this._class.getModifiers();
/* 263 */     if ((mod & 0x600) == 0) {
/* 264 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 269 */     if (this._class.isPrimitive()) {
/* 270 */       return true;
/*     */     }
/* 272 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isThrowable() {
/* 276 */     return Throwable.class.isAssignableFrom(this._class);
/*     */   }
/*     */   
/* 279 */   public boolean isArrayType() { return false; }
/*     */   
/* 281 */   public final boolean isEnumType() { return this._class.isEnum(); }
/*     */   
/* 283 */   public final boolean isInterface() { return this._class.isInterface(); }
/*     */   
/* 285 */   public final boolean isPrimitive() { return this._class.isPrimitive(); }
/*     */   
/* 287 */   public final boolean isFinal() { return Modifier.isFinal(this._class.getModifiers()); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean isContainerType();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCollectionLikeType()
/*     */   {
/* 302 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isMapLikeType()
/*     */   {
/* 311 */     return false;
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
/*     */   public boolean hasGenericTypes()
/*     */   {
/* 327 */     return containedTypeCount() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JavaType getKeyType()
/*     */   {
/* 334 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public JavaType getContentType()
/*     */   {
/* 341 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int containedTypeCount()
/*     */   {
/* 350 */     return 0;
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
/*     */   public JavaType containedType(int index)
/*     */   {
/* 363 */     return null;
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
/*     */   public String containedTypeName(int index)
/*     */   {
/* 378 */     return null;
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
/*     */   public <T> T getValueHandler()
/*     */   {
/* 392 */     return (T)this._valueHandler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T getTypeHandler()
/*     */   {
/* 400 */     return (T)this._typeHandler;
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
/*     */ 
/*     */   public abstract String toCanonical();
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
/*     */   public String getGenericSignature()
/*     */   {
/* 431 */     StringBuilder sb = new StringBuilder(40);
/* 432 */     getGenericSignature(sb);
/* 433 */     return sb.toString();
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
/*     */   public abstract StringBuilder getGenericSignature(StringBuilder paramStringBuilder);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getErasedSignature()
/*     */   {
/* 456 */     StringBuilder sb = new StringBuilder(40);
/* 457 */     getErasedSignature(sb);
/* 458 */     return sb.toString();
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
/*     */   public abstract StringBuilder getErasedSignature(StringBuilder paramStringBuilder);
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
/*     */   protected void _assertSubclass(Class<?> subclass, Class<?> superClass)
/*     */   {
/* 484 */     if (!this._class.isAssignableFrom(subclass)) {
/* 485 */       throw new IllegalArgumentException("Class " + subclass.getName() + " is not assignable to " + this._class.getName());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String toString();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean equals(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 502 */     return this._hashCode;
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\type\JavaType.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */