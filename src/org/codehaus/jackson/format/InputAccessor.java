/*     */ package org.codehaus.jackson.format;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.codehaus.jackson.JsonFactory;
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
/*     */ public abstract interface InputAccessor
/*     */ {
/*     */   public abstract boolean hasMoreBytes()
/*     */     throws IOException;
/*     */   
/*     */   public abstract byte nextByte()
/*     */     throws IOException;
/*     */   
/*     */   public abstract void reset();
/*     */   
/*     */   public static class Std
/*     */     implements InputAccessor
/*     */   {
/*     */     protected final InputStream _in;
/*     */     protected final byte[] _buffer;
/*     */     protected int _bufferedAmount;
/*     */     protected int _ptr;
/*     */     
/*     */     public Std(InputStream in, byte[] buffer)
/*     */     {
/*  67 */       this._in = in;
/*  68 */       this._buffer = buffer;
/*  69 */       this._bufferedAmount = 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Std(byte[] inputDocument)
/*     */     {
/*  78 */       this._in = null;
/*  79 */       this._buffer = inputDocument;
/*     */       
/*  81 */       this._bufferedAmount = inputDocument.length;
/*     */     }
/*     */     
/*     */     public boolean hasMoreBytes()
/*     */       throws IOException
/*     */     {
/*  87 */       if (this._ptr < this._bufferedAmount) {
/*  88 */         return true;
/*     */       }
/*  90 */       int amount = this._buffer.length - this._ptr;
/*  91 */       if (amount < 1) {
/*  92 */         return false;
/*     */       }
/*  94 */       int count = this._in.read(this._buffer, this._ptr, amount);
/*  95 */       if (count <= 0) {
/*  96 */         return false;
/*     */       }
/*  98 */       this._bufferedAmount += count;
/*  99 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */     public byte nextByte()
/*     */       throws IOException
/*     */     {
/* 106 */       if ((this._ptr > -this._bufferedAmount) && 
/* 107 */         (!hasMoreBytes())) {
/* 108 */         throw new EOFException("Could not read more than " + this._ptr + " bytes (max buffer size: " + this._buffer.length + ")");
/*     */       }
/*     */       
/* 111 */       return this._buffer[(this._ptr++)];
/*     */     }
/*     */     
/*     */     public void reset()
/*     */     {
/* 116 */       this._ptr = 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public DataFormatMatcher createMatcher(JsonFactory match, MatchStrength matchStrength)
/*     */     {
/* 127 */       return new DataFormatMatcher(this._in, this._buffer, this._bufferedAmount, match, matchStrength);
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\format\InputAccessor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */