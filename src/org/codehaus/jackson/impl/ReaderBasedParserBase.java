/*     */ package org.codehaus.jackson.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import org.codehaus.jackson.JsonParseException;
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
/*     */ @Deprecated
/*     */ public abstract class ReaderBasedParserBase
/*     */   extends JsonParserBase
/*     */ {
/*     */   protected Reader _reader;
/*     */   protected char[] _inputBuffer;
/*     */   
/*     */   protected ReaderBasedParserBase(IOContext ctxt, int features, Reader r)
/*     */   {
/*  57 */     super(ctxt, features);
/*  58 */     this._reader = r;
/*  59 */     this._inputBuffer = ctxt.allocTokenBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int releaseBuffered(Writer w)
/*     */     throws IOException
/*     */   {
/*  71 */     int count = this._inputEnd - this._inputPtr;
/*  72 */     if (count < 1) {
/*  73 */       return 0;
/*     */     }
/*     */     
/*  76 */     int origPtr = this._inputPtr;
/*  77 */     w.write(this._inputBuffer, origPtr, count);
/*  78 */     return count;
/*     */   }
/*     */   
/*     */   public Object getInputSource()
/*     */   {
/*  83 */     return this._reader;
/*     */   }
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
/*  95 */     this._currInputProcessed += this._inputEnd;
/*  96 */     this._currInputRowStart -= this._inputEnd;
/*     */     
/*  98 */     if (this._reader != null) {
/*  99 */       int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
/* 100 */       if (count > 0) {
/* 101 */         this._inputPtr = 0;
/* 102 */         this._inputEnd = count;
/* 103 */         return true;
/*     */       }
/*     */       
/* 106 */       _closeInput();
/*     */       
/* 108 */       if (count == 0) {
/* 109 */         throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
/*     */       }
/*     */     }
/* 112 */     return false;
/*     */   }
/*     */   
/*     */   protected char getNextChar(String eofMsg)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 118 */     if ((this._inputPtr >= this._inputEnd) && 
/* 119 */       (!loadMore())) {
/* 120 */       _reportInvalidEOF(eofMsg);
/*     */     }
/*     */     
/* 123 */     return this._inputBuffer[(this._inputPtr++)];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _closeInput()
/*     */     throws IOException
/*     */   {
/* 136 */     if (this._reader != null) {
/* 137 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE))) {
/* 138 */         this._reader.close();
/*     */       }
/* 140 */       this._reader = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _releaseBuffers()
/*     */     throws IOException
/*     */   {
/* 154 */     super._releaseBuffers();
/* 155 */     char[] buf = this._inputBuffer;
/* 156 */     if (buf != null) {
/* 157 */       this._inputBuffer = null;
/* 158 */       this._ioContext.releaseTokenBuffer(buf);
/*     */     }
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
/*     */   protected final boolean _matchToken(String matchStr, int i)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 176 */     int len = matchStr.length();
/*     */     do
/*     */     {
/* 179 */       if ((this._inputPtr >= this._inputEnd) && 
/* 180 */         (!loadMore())) {
/* 181 */         _reportInvalidEOFInValue();
/*     */       }
/*     */       
/* 184 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 185 */         _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
/*     */       }
/* 187 */       this._inputPtr += 1;
/* 188 */       i++; } while (i < len);
/*     */     
/*     */ 
/* 191 */     if ((this._inputPtr >= this._inputEnd) && 
/* 192 */       (!loadMore())) {
/* 193 */       return true;
/*     */     }
/*     */     
/* 196 */     char c = this._inputBuffer[this._inputPtr];
/*     */     
/* 198 */     if (Character.isJavaIdentifierPart(c)) {
/* 199 */       this._inputPtr += 1;
/* 200 */       _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
/*     */     }
/* 202 */     return true;
/*     */   }
/*     */   
/*     */   protected void _reportInvalidToken(String matchedPart, String msg)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 208 */     StringBuilder sb = new StringBuilder(matchedPart);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */     while ((this._inputPtr < this._inputEnd) || 
/* 215 */       (loadMore()))
/*     */     {
/*     */ 
/*     */ 
/* 219 */       char c = this._inputBuffer[this._inputPtr];
/* 220 */       if (!Character.isJavaIdentifierPart(c)) {
/*     */         break;
/*     */       }
/* 223 */       this._inputPtr += 1;
/* 224 */       sb.append(c);
/*     */     }
/* 226 */     _reportError("Unrecognized token '" + sb.toString() + "': was expecting ");
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\ReaderBasedParserBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */