/*     */ package org.codehaus.jackson.io;
/*     */ 
/*     */ import org.codehaus.jackson.SerializableString;
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
/*     */ public class SerializedString
/*     */   implements SerializableString
/*     */ {
/*     */   protected final String _value;
/*     */   protected byte[] _quotedUTF8Ref;
/*     */   protected byte[] _unquotedUTF8Ref;
/*     */   protected char[] _quotedChars;
/*     */   
/*     */   public SerializedString(String v)
/*     */   {
/*  36 */     this._value = v;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String getValue()
/*     */   {
/*  45 */     return this._value;
/*     */   }
/*     */   
/*     */ 
/*     */   public final int charLength()
/*     */   {
/*  51 */     return this._value.length();
/*     */   }
/*     */   
/*     */   public final char[] asQuotedChars()
/*     */   {
/*  56 */     char[] result = this._quotedChars;
/*  57 */     if (result == null) {
/*  58 */       result = JsonStringEncoder.getInstance().quoteAsString(this._value);
/*  59 */       this._quotedChars = result;
/*     */     }
/*  61 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] asUnquotedUTF8()
/*     */   {
/*  71 */     byte[] result = this._unquotedUTF8Ref;
/*  72 */     if (result == null) {
/*  73 */       result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
/*  74 */       this._unquotedUTF8Ref = result;
/*     */     }
/*  76 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] asQuotedUTF8()
/*     */   {
/*  86 */     byte[] result = this._quotedUTF8Ref;
/*  87 */     if (result == null) {
/*  88 */       result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
/*  89 */       this._quotedUTF8Ref = result;
/*     */     }
/*  91 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 101 */     return this._value;
/*     */   }
/*     */   
/* 104 */   public final int hashCode() { return this._value.hashCode(); }
/*     */   
/*     */ 
/*     */   public final boolean equals(Object o)
/*     */   {
/* 109 */     if (o == this) return true;
/* 110 */     if ((o == null) || (o.getClass() != getClass())) return false;
/* 111 */     SerializedString other = (SerializedString)o;
/* 112 */     return this._value.equals(other._value);
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\io\SerializedString.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */