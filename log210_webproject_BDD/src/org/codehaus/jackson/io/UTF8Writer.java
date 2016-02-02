/*     */ package org.codehaus.jackson.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Writer;
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
/*     */ public final class UTF8Writer
/*     */   extends Writer
/*     */ {
/*     */   static final int SURR1_FIRST = 55296;
/*     */   static final int SURR1_LAST = 56319;
/*     */   static final int SURR2_FIRST = 56320;
/*     */   static final int SURR2_LAST = 57343;
/*     */   protected final IOContext _context;
/*     */   OutputStream _out;
/*     */   byte[] _outBuffer;
/*     */   final int _outBufferEnd;
/*     */   int _outPtr;
/*  29 */   int _surrogate = 0;
/*     */   
/*     */   public UTF8Writer(IOContext ctxt, OutputStream out)
/*     */   {
/*  33 */     this._context = ctxt;
/*  34 */     this._out = out;
/*     */     
/*  36 */     this._outBuffer = ctxt.allocWriteEncodingBuffer();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  41 */     this._outBufferEnd = (this._outBuffer.length - 4);
/*  42 */     this._outPtr = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public Writer append(char c)
/*     */     throws IOException
/*     */   {
/*  49 */     write(c);
/*  50 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  57 */     if (this._out != null) {
/*  58 */       if (this._outPtr > 0) {
/*  59 */         this._out.write(this._outBuffer, 0, this._outPtr);
/*  60 */         this._outPtr = 0;
/*     */       }
/*  62 */       OutputStream out = this._out;
/*  63 */       this._out = null;
/*     */       
/*  65 */       byte[] buf = this._outBuffer;
/*  66 */       if (buf != null) {
/*  67 */         this._outBuffer = null;
/*  68 */         this._context.releaseWriteEncodingBuffer(buf);
/*     */       }
/*     */       
/*  71 */       out.close();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  76 */       int code = this._surrogate;
/*  77 */       this._surrogate = 0;
/*  78 */       if (code > 0) {
/*  79 */         throwIllegal(code);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/*  88 */     if (this._out != null) {
/*  89 */       if (this._outPtr > 0) {
/*  90 */         this._out.write(this._outBuffer, 0, this._outPtr);
/*  91 */         this._outPtr = 0;
/*     */       }
/*  93 */       this._out.flush();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void write(char[] cbuf)
/*     */     throws IOException
/*     */   {
/* 101 */     write(cbuf, 0, cbuf.length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void write(char[] cbuf, int off, int len)
/*     */     throws IOException
/*     */   {
/* 108 */     if (len < 2) {
/* 109 */       if (len == 1) {
/* 110 */         write(cbuf[off]);
/*     */       }
/* 112 */       return;
/*     */     }
/*     */     
/*     */ 
/* 116 */     if (this._surrogate > 0) {
/* 117 */       char second = cbuf[(off++)];
/* 118 */       len--;
/* 119 */       write(convertSurrogate(second));
/*     */     }
/*     */     
/*     */ 
/* 123 */     int outPtr = this._outPtr;
/* 124 */     byte[] outBuf = this._outBuffer;
/* 125 */     int outBufLast = this._outBufferEnd;
/*     */     
/*     */ 
/* 128 */     len += off;
/*     */     
/*     */ 
/* 131 */     while (off < len)
/*     */     {
/*     */ 
/*     */ 
/* 135 */       if (outPtr >= outBufLast) {
/* 136 */         this._out.write(outBuf, 0, outPtr);
/* 137 */         outPtr = 0;
/*     */       }
/*     */       
/* 140 */       int c = cbuf[(off++)];
/*     */       
/* 142 */       if (c < 128) {
/* 143 */         outBuf[(outPtr++)] = ((byte)c);
/*     */         
/* 145 */         int maxInCount = len - off;
/* 146 */         int maxOutCount = outBufLast - outPtr;
/*     */         
/* 148 */         if (maxInCount > maxOutCount) {
/* 149 */           maxInCount = maxOutCount;
/*     */         }
/* 151 */         maxInCount += off;
/*     */         
/*     */ 
/* 154 */         while (off < maxInCount)
/*     */         {
/*     */ 
/* 157 */           c = cbuf[(off++)];
/* 158 */           if (c >= 128) {
/*     */             break label193;
/*     */           }
/* 161 */           outBuf[(outPtr++)] = ((byte)c);
/*     */         }
/*     */       }
/*     */       else {
/*     */         label193:
/* 166 */         if (c < 2048) {
/* 167 */           outBuf[(outPtr++)] = ((byte)(0xC0 | c >> 6));
/* 168 */           outBuf[(outPtr++)] = ((byte)(0x80 | c & 0x3F));
/*     */ 
/*     */         }
/* 171 */         else if ((c < 55296) || (c > 57343)) {
/* 172 */           outBuf[(outPtr++)] = ((byte)(0xE0 | c >> 12));
/* 173 */           outBuf[(outPtr++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 174 */           outBuf[(outPtr++)] = ((byte)(0x80 | c & 0x3F));
/*     */         }
/*     */         else
/*     */         {
/* 178 */           if (c > 56319) {
/* 179 */             this._outPtr = outPtr;
/* 180 */             throwIllegal(c);
/*     */           }
/* 182 */           this._surrogate = c;
/*     */           
/* 184 */           if (off >= len) {
/*     */             break;
/*     */           }
/* 187 */           c = convertSurrogate(cbuf[(off++)]);
/* 188 */           if (c > 1114111) {
/* 189 */             this._outPtr = outPtr;
/* 190 */             throwIllegal(c);
/*     */           }
/* 192 */           outBuf[(outPtr++)] = ((byte)(0xF0 | c >> 18));
/* 193 */           outBuf[(outPtr++)] = ((byte)(0x80 | c >> 12 & 0x3F));
/* 194 */           outBuf[(outPtr++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 195 */           outBuf[(outPtr++)] = ((byte)(0x80 | c & 0x3F));
/*     */         }
/*     */       } }
/* 198 */     this._outPtr = outPtr;
/*     */   }
/*     */   
/*     */ 
/*     */   public void write(int c)
/*     */     throws IOException
/*     */   {
/* 205 */     if (this._surrogate > 0) {
/* 206 */       c = convertSurrogate(c);
/*     */     }
/* 208 */     else if ((c >= 55296) && (c <= 57343))
/*     */     {
/* 210 */       if (c > 56319) {
/* 211 */         throwIllegal(c);
/*     */       }
/*     */       
/* 214 */       this._surrogate = c;
/* 215 */       return;
/*     */     }
/*     */     
/* 218 */     if (this._outPtr >= this._outBufferEnd) {
/* 219 */       this._out.write(this._outBuffer, 0, this._outPtr);
/* 220 */       this._outPtr = 0;
/*     */     }
/*     */     
/* 223 */     if (c < 128) {
/* 224 */       this._outBuffer[(this._outPtr++)] = ((byte)c);
/*     */     } else {
/* 226 */       int ptr = this._outPtr;
/* 227 */       if (c < 2048) {
/* 228 */         this._outBuffer[(ptr++)] = ((byte)(0xC0 | c >> 6));
/* 229 */         this._outBuffer[(ptr++)] = ((byte)(0x80 | c & 0x3F));
/* 230 */       } else if (c <= 65535) {
/* 231 */         this._outBuffer[(ptr++)] = ((byte)(0xE0 | c >> 12));
/* 232 */         this._outBuffer[(ptr++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 233 */         this._outBuffer[(ptr++)] = ((byte)(0x80 | c & 0x3F));
/*     */       } else {
/* 235 */         if (c > 1114111) {
/* 236 */           throwIllegal(c);
/*     */         }
/* 238 */         this._outBuffer[(ptr++)] = ((byte)(0xF0 | c >> 18));
/* 239 */         this._outBuffer[(ptr++)] = ((byte)(0x80 | c >> 12 & 0x3F));
/* 240 */         this._outBuffer[(ptr++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 241 */         this._outBuffer[(ptr++)] = ((byte)(0x80 | c & 0x3F));
/*     */       }
/* 243 */       this._outPtr = ptr;
/*     */     }
/*     */   }
/*     */   
/*     */   public void write(String str)
/*     */     throws IOException
/*     */   {
/* 250 */     write(str, 0, str.length());
/*     */   }
/*     */   
/*     */   public void write(String str, int off, int len)
/*     */     throws IOException
/*     */   {
/* 256 */     if (len < 2) {
/* 257 */       if (len == 1) {
/* 258 */         write(str.charAt(off));
/*     */       }
/* 260 */       return;
/*     */     }
/*     */     
/*     */ 
/* 264 */     if (this._surrogate > 0) {
/* 265 */       char second = str.charAt(off++);
/* 266 */       len--;
/* 267 */       write(convertSurrogate(second));
/*     */     }
/*     */     
/*     */ 
/* 271 */     int outPtr = this._outPtr;
/* 272 */     byte[] outBuf = this._outBuffer;
/* 273 */     int outBufLast = this._outBufferEnd;
/*     */     
/*     */ 
/* 276 */     len += off;
/*     */     
/*     */ 
/* 279 */     while (off < len)
/*     */     {
/*     */ 
/*     */ 
/* 283 */       if (outPtr >= outBufLast) {
/* 284 */         this._out.write(outBuf, 0, outPtr);
/* 285 */         outPtr = 0;
/*     */       }
/*     */       
/* 288 */       int c = str.charAt(off++);
/*     */       
/* 290 */       if (c < 128) {
/* 291 */         outBuf[(outPtr++)] = ((byte)c);
/*     */         
/* 293 */         int maxInCount = len - off;
/* 294 */         int maxOutCount = outBufLast - outPtr;
/*     */         
/* 296 */         if (maxInCount > maxOutCount) {
/* 297 */           maxInCount = maxOutCount;
/*     */         }
/* 299 */         maxInCount += off;
/*     */         
/*     */ 
/* 302 */         while (off < maxInCount)
/*     */         {
/*     */ 
/* 305 */           c = str.charAt(off++);
/* 306 */           if (c >= 128) {
/*     */             break label201;
/*     */           }
/* 309 */           outBuf[(outPtr++)] = ((byte)c);
/*     */         }
/*     */       }
/*     */       else {
/*     */         label201:
/* 314 */         if (c < 2048) {
/* 315 */           outBuf[(outPtr++)] = ((byte)(0xC0 | c >> 6));
/* 316 */           outBuf[(outPtr++)] = ((byte)(0x80 | c & 0x3F));
/*     */ 
/*     */         }
/* 319 */         else if ((c < 55296) || (c > 57343)) {
/* 320 */           outBuf[(outPtr++)] = ((byte)(0xE0 | c >> 12));
/* 321 */           outBuf[(outPtr++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 322 */           outBuf[(outPtr++)] = ((byte)(0x80 | c & 0x3F));
/*     */         }
/*     */         else
/*     */         {
/* 326 */           if (c > 56319) {
/* 327 */             this._outPtr = outPtr;
/* 328 */             throwIllegal(c);
/*     */           }
/* 330 */           this._surrogate = c;
/*     */           
/* 332 */           if (off >= len) {
/*     */             break;
/*     */           }
/* 335 */           c = convertSurrogate(str.charAt(off++));
/* 336 */           if (c > 1114111) {
/* 337 */             this._outPtr = outPtr;
/* 338 */             throwIllegal(c);
/*     */           }
/* 340 */           outBuf[(outPtr++)] = ((byte)(0xF0 | c >> 18));
/* 341 */           outBuf[(outPtr++)] = ((byte)(0x80 | c >> 12 & 0x3F));
/* 342 */           outBuf[(outPtr++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 343 */           outBuf[(outPtr++)] = ((byte)(0x80 | c & 0x3F));
/*     */         }
/*     */       } }
/* 346 */     this._outPtr = outPtr;
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
/*     */   private int convertSurrogate(int secondPart)
/*     */     throws IOException
/*     */   {
/* 361 */     int firstPart = this._surrogate;
/* 362 */     this._surrogate = 0;
/*     */     
/*     */ 
/* 365 */     if ((secondPart < 56320) || (secondPart > 57343)) {
/* 366 */       throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
/*     */     }
/* 368 */     return 65536 + (firstPart - 55296 << 10) + (secondPart - 56320);
/*     */   }
/*     */   
/*     */   private void throwIllegal(int code)
/*     */     throws IOException
/*     */   {
/* 374 */     if (code > 1114111) {
/* 375 */       throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627");
/*     */     }
/* 377 */     if (code >= 55296) {
/* 378 */       if (code <= 56319) {
/* 379 */         throw new IOException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
/*     */       }
/* 381 */       throw new IOException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
/*     */     }
/*     */     
/*     */ 
/* 385 */     throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\io\UTF8Writer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */