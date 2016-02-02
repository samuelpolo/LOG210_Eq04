/*     */ package org.codehaus.jackson.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.codehaus.jackson.JsonParser.Feature;
/*     */ import org.codehaus.jackson.io.IOContext;
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
/*     */ @Deprecated
/*     */ public abstract class StreamBasedParserBase
/*     */   extends JsonParserBase
/*     */ {
/*     */   protected InputStream _inputStream;
/*     */   protected byte[] _inputBuffer;
/*     */   protected boolean _bufferRecyclable;
/*     */   
/*     */   protected StreamBasedParserBase(IOContext ctxt, int features, InputStream in, byte[] inputBuffer, int start, int end, boolean bufferRecyclable)
/*     */   {
/*  68 */     super(ctxt, features);
/*  69 */     this._inputStream = in;
/*  70 */     this._inputBuffer = inputBuffer;
/*  71 */     this._inputPtr = start;
/*  72 */     this._inputEnd = end;
/*  73 */     this._bufferRecyclable = bufferRecyclable;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int releaseBuffered(OutputStream out)
/*     */     throws IOException
/*     */   {
/*  85 */     int count = this._inputEnd - this._inputPtr;
/*  86 */     if (count < 1) {
/*  87 */       return 0;
/*     */     }
/*     */     
/*  90 */     int origPtr = this._inputPtr;
/*  91 */     out.write(this._inputBuffer, origPtr, count);
/*  92 */     return count;
/*     */   }
/*     */   
/*     */   public Object getInputSource()
/*     */   {
/*  97 */     return this._inputStream;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean loadMore()
/*     */     throws IOException
/*     */   {
/* 110 */     this._currInputProcessed += this._inputEnd;
/* 111 */     this._currInputRowStart -= this._inputEnd;
/*     */     
/* 113 */     if (this._inputStream != null) {
/* 114 */       int count = this._inputStream.read(this._inputBuffer, 0, this._inputBuffer.length);
/* 115 */       if (count > 0) {
/* 116 */         this._inputPtr = 0;
/* 117 */         this._inputEnd = count;
/* 118 */         return true;
/*     */       }
/*     */       
/* 121 */       _closeInput();
/*     */       
/* 123 */       if (count == 0) {
/* 124 */         throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
/*     */       }
/*     */     }
/* 127 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _loadToHaveAtLeast(int minAvailable)
/*     */     throws IOException
/*     */   {
/* 140 */     if (this._inputStream == null) {
/* 141 */       return false;
/*     */     }
/*     */     
/* 144 */     int amount = this._inputEnd - this._inputPtr;
/* 145 */     if ((amount > 0) && (this._inputPtr > 0)) {
/* 146 */       this._currInputProcessed += this._inputPtr;
/* 147 */       this._currInputRowStart -= this._inputPtr;
/* 148 */       System.arraycopy(this._inputBuffer, this._inputPtr, this._inputBuffer, 0, amount);
/* 149 */       this._inputEnd = amount;
/*     */     } else {
/* 151 */       this._inputEnd = 0;
/*     */     }
/* 153 */     this._inputPtr = 0;
/* 154 */     while (this._inputEnd < minAvailable) {
/* 155 */       int count = this._inputStream.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
/* 156 */       if (count < 1)
/*     */       {
/* 158 */         _closeInput();
/*     */         
/* 160 */         if (count == 0) {
/* 161 */           throw new IOException("InputStream.read() returned 0 characters when trying to read " + amount + " bytes");
/*     */         }
/* 163 */         return false;
/*     */       }
/* 165 */       this._inputEnd += count;
/*     */     }
/* 167 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _closeInput()
/*     */     throws IOException
/*     */   {
/* 177 */     if (this._inputStream != null) {
/* 178 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE))) {
/* 179 */         this._inputStream.close();
/*     */       }
/* 181 */       this._inputStream = null;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void _releaseBuffers()
/*     */     throws IOException
/*     */   {
/* 188 */     super._releaseBuffers();
/* 189 */     if (this._bufferRecyclable) {
/* 190 */       byte[] buf = this._inputBuffer;
/* 191 */       if (buf != null) {
/* 192 */         this._inputBuffer = null;
/* 193 */         this._ioContext.releaseReadIOBuffer(buf);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\StreamBasedParserBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */