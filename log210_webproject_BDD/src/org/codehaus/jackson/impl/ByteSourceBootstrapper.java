/*     */ package org.codehaus.jackson.impl;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.CharConversionException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import org.codehaus.jackson.JsonEncoding;
/*     */ import org.codehaus.jackson.JsonParseException;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ import org.codehaus.jackson.JsonParser.Feature;
/*     */ import org.codehaus.jackson.ObjectCodec;
/*     */ import org.codehaus.jackson.format.InputAccessor;
/*     */ import org.codehaus.jackson.format.MatchStrength;
/*     */ import org.codehaus.jackson.io.IOContext;
/*     */ import org.codehaus.jackson.io.MergedStream;
/*     */ import org.codehaus.jackson.io.UTF32Reader;
/*     */ import org.codehaus.jackson.sym.BytesToNameCanonicalizer;
/*     */ import org.codehaus.jackson.sym.CharsToNameCanonicalizer;
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
/*     */ public final class ByteSourceBootstrapper
/*     */ {
/*     */   static final byte UTF8_BOM_1 = -17;
/*     */   static final byte UTF8_BOM_2 = -69;
/*     */   static final byte UTF8_BOM_3 = -65;
/*     */   protected final IOContext _context;
/*     */   protected final InputStream _in;
/*     */   protected final byte[] _inputBuffer;
/*     */   private int _inputPtr;
/*     */   private int _inputEnd;
/*     */   private final boolean _bufferRecyclable;
/*     */   protected int _inputProcessed;
/*  74 */   protected boolean _bigEndian = true;
/*     */   
/*  76 */   protected int _bytesPerChar = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteSourceBootstrapper(IOContext ctxt, InputStream in)
/*     */   {
/*  86 */     this._context = ctxt;
/*  87 */     this._in = in;
/*  88 */     this._inputBuffer = ctxt.allocReadIOBuffer();
/*  89 */     this._inputEnd = (this._inputPtr = 0);
/*  90 */     this._inputProcessed = 0;
/*  91 */     this._bufferRecyclable = true;
/*     */   }
/*     */   
/*     */   public ByteSourceBootstrapper(IOContext ctxt, byte[] inputBuffer, int inputStart, int inputLen)
/*     */   {
/*  96 */     this._context = ctxt;
/*  97 */     this._in = null;
/*  98 */     this._inputBuffer = inputBuffer;
/*  99 */     this._inputPtr = inputStart;
/* 100 */     this._inputEnd = (inputStart + inputLen);
/*     */     
/* 102 */     this._inputProcessed = (-inputStart);
/* 103 */     this._bufferRecyclable = false;
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
/*     */   public JsonEncoding detectEncoding()
/*     */     throws IOException, JsonParseException
/*     */   {
/* 120 */     boolean foundEncoding = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 129 */     if (ensureLoaded(4)) {
/* 130 */       int quad = this._inputBuffer[this._inputPtr] << 24 | (this._inputBuffer[(this._inputPtr + 1)] & 0xFF) << 16 | (this._inputBuffer[(this._inputPtr + 2)] & 0xFF) << 8 | this._inputBuffer[(this._inputPtr + 3)] & 0xFF;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 135 */       if (handleBOM(quad)) {
/* 136 */         foundEncoding = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 144 */       else if (checkUTF32(quad)) {
/* 145 */         foundEncoding = true;
/* 146 */       } else if (checkUTF16(quad >>> 16)) {
/* 147 */         foundEncoding = true;
/*     */       }
/*     */     }
/* 150 */     else if (ensureLoaded(2)) {
/* 151 */       int i16 = (this._inputBuffer[this._inputPtr] & 0xFF) << 8 | this._inputBuffer[(this._inputPtr + 1)] & 0xFF;
/*     */       
/* 153 */       if (checkUTF16(i16)) {
/* 154 */         foundEncoding = true;
/*     */       }
/*     */     }
/*     */     
/*     */     JsonEncoding enc;
/*     */     
/*     */     JsonEncoding enc;
/* 161 */     if (!foundEncoding) {
/* 162 */       enc = JsonEncoding.UTF8;
/*     */     } else {
/* 164 */       switch (this._bytesPerChar) {
/*     */       case 1: 
/* 166 */         enc = JsonEncoding.UTF8;
/* 167 */         break;
/*     */       case 2: 
/* 169 */         enc = this._bigEndian ? JsonEncoding.UTF16_BE : JsonEncoding.UTF16_LE;
/* 170 */         break;
/*     */       case 4: 
/* 172 */         enc = this._bigEndian ? JsonEncoding.UTF32_BE : JsonEncoding.UTF32_LE;
/* 173 */         break;
/*     */       case 3: default: 
/* 175 */         throw new RuntimeException("Internal error");
/*     */       }
/*     */     }
/* 178 */     this._context.setEncoding(enc);
/* 179 */     return enc;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Reader constructReader()
/*     */     throws IOException
/*     */   {
/* 191 */     JsonEncoding enc = this._context.getEncoding();
/* 192 */     switch (enc) {
/*     */     case UTF32_BE: 
/*     */     case UTF32_LE: 
/* 195 */       return new UTF32Reader(this._context, this._in, this._inputBuffer, this._inputPtr, this._inputEnd, this._context.getEncoding().isBigEndian());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case UTF16_BE: 
/*     */     case UTF16_LE: 
/*     */     case UTF8: 
/* 203 */       InputStream in = this._in;
/*     */       
/* 205 */       if (in == null) {
/* 206 */         in = new ByteArrayInputStream(this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 211 */       else if (this._inputPtr < this._inputEnd) {
/* 212 */         in = new MergedStream(this._context, in, this._inputBuffer, this._inputPtr, this._inputEnd);
/*     */       }
/*     */       
/* 215 */       return new InputStreamReader(in, enc.getJavaName());
/*     */     }
/*     */     
/* 218 */     throw new RuntimeException("Internal error");
/*     */   }
/*     */   
/*     */   public JsonParser constructParser(int features, ObjectCodec codec, BytesToNameCanonicalizer rootByteSymbols, CharsToNameCanonicalizer rootCharSymbols)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 224 */     JsonEncoding enc = detectEncoding();
/*     */     
/*     */ 
/* 227 */     boolean canonicalize = JsonParser.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(features);
/* 228 */     boolean intern = JsonParser.Feature.INTERN_FIELD_NAMES.enabledIn(features);
/* 229 */     if (enc == JsonEncoding.UTF8)
/*     */     {
/*     */ 
/*     */ 
/* 233 */       if (canonicalize) {
/* 234 */         BytesToNameCanonicalizer can = rootByteSymbols.makeChild(canonicalize, intern);
/* 235 */         return new Utf8StreamParser(this._context, features, this._in, codec, can, this._inputBuffer, this._inputPtr, this._inputEnd, this._bufferRecyclable);
/*     */       }
/*     */     }
/* 238 */     return new ReaderBasedParser(this._context, features, constructReader(), codec, rootCharSymbols.makeChild(canonicalize, intern));
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
/*     */ 
/*     */   public static MatchStrength hasJSONFormat(InputAccessor acc)
/*     */     throws IOException
/*     */   {
/* 260 */     if (!acc.hasMoreBytes()) {
/* 261 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/* 263 */     byte b = acc.nextByte();
/*     */     
/* 265 */     if (b == -17) {
/* 266 */       if (!acc.hasMoreBytes()) {
/* 267 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 269 */       if (acc.nextByte() != -69) {
/* 270 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 272 */       if (!acc.hasMoreBytes()) {
/* 273 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 275 */       if (acc.nextByte() != -65) {
/* 276 */         return MatchStrength.NO_MATCH;
/*     */       }
/* 278 */       if (!acc.hasMoreBytes()) {
/* 279 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 281 */       b = acc.nextByte();
/*     */     }
/*     */     
/* 284 */     int ch = skipSpace(acc, b);
/* 285 */     if (ch < 0) {
/* 286 */       return MatchStrength.INCONCLUSIVE;
/*     */     }
/*     */     
/* 289 */     if (ch == 123)
/*     */     {
/* 291 */       ch = skipSpace(acc);
/* 292 */       if (ch < 0) {
/* 293 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 295 */       if ((ch == 34) || (ch == 125)) {
/* 296 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/*     */       
/* 299 */       return MatchStrength.NO_MATCH;
/*     */     }
/*     */     
/*     */ 
/* 303 */     if (ch == 91) {
/* 304 */       ch = skipSpace(acc);
/* 305 */       if (ch < 0) {
/* 306 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/*     */       
/* 309 */       if ((ch == 93) || (ch == 91)) {
/* 310 */         return MatchStrength.SOLID_MATCH;
/*     */       }
/* 312 */       return MatchStrength.SOLID_MATCH;
/*     */     }
/*     */     
/* 315 */     MatchStrength strength = MatchStrength.WEAK_MATCH;
/*     */     
/*     */ 
/* 318 */     if (ch == 34) {
/* 319 */       return strength;
/*     */     }
/* 321 */     if ((ch <= 57) && (ch >= 48)) {
/* 322 */       return strength;
/*     */     }
/* 324 */     if (ch == 45) {
/* 325 */       ch = skipSpace(acc);
/* 326 */       if (ch < 0) {
/* 327 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 329 */       return (ch <= 57) && (ch >= 48) ? strength : MatchStrength.NO_MATCH;
/*     */     }
/*     */     
/* 332 */     if (ch == 110) {
/* 333 */       return tryMatch(acc, "ull", strength);
/*     */     }
/* 335 */     if (ch == 116) {
/* 336 */       return tryMatch(acc, "rue", strength);
/*     */     }
/* 338 */     if (ch == 102) {
/* 339 */       return tryMatch(acc, "alse", strength);
/*     */     }
/* 341 */     return MatchStrength.NO_MATCH;
/*     */   }
/*     */   
/*     */   private static final MatchStrength tryMatch(InputAccessor acc, String matchStr, MatchStrength fullMatchStrength)
/*     */     throws IOException
/*     */   {
/* 347 */     int i = 0; for (int len = matchStr.length(); i < len; i++) {
/* 348 */       if (!acc.hasMoreBytes()) {
/* 349 */         return MatchStrength.INCONCLUSIVE;
/*     */       }
/* 351 */       if (acc.nextByte() != matchStr.charAt(i)) {
/* 352 */         return MatchStrength.NO_MATCH;
/*     */       }
/*     */     }
/* 355 */     return fullMatchStrength;
/*     */   }
/*     */   
/*     */   private static final int skipSpace(InputAccessor acc) throws IOException
/*     */   {
/* 360 */     if (!acc.hasMoreBytes()) {
/* 361 */       return -1;
/*     */     }
/* 363 */     return skipSpace(acc, acc.nextByte());
/*     */   }
/*     */   
/*     */   private static final int skipSpace(InputAccessor acc, byte b) throws IOException
/*     */   {
/*     */     for (;;) {
/* 369 */       int ch = b & 0xFF;
/* 370 */       if ((ch != 32) && (ch != 13) && (ch != 10) && (ch != 9)) {
/* 371 */         return ch;
/*     */       }
/* 373 */       if (!acc.hasMoreBytes()) {
/* 374 */         return -1;
/*     */       }
/* 376 */       b = acc.nextByte();
/* 377 */       ch = b & 0xFF;
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
/*     */ 
/*     */ 
/*     */   private boolean handleBOM(int quad)
/*     */     throws IOException
/*     */   {
/* 397 */     switch (quad) {
/*     */     case 65279: 
/* 399 */       this._bigEndian = true;
/* 400 */       this._inputPtr += 4;
/* 401 */       this._bytesPerChar = 4;
/* 402 */       return true;
/*     */     case -131072: 
/* 404 */       this._inputPtr += 4;
/* 405 */       this._bytesPerChar = 4;
/* 406 */       this._bigEndian = false;
/* 407 */       return true;
/*     */     case 65534: 
/* 409 */       reportWeirdUCS4("2143");
/*     */     case -16842752: 
/* 411 */       reportWeirdUCS4("3412");
/*     */     }
/*     */     
/* 414 */     int msw = quad >>> 16;
/* 415 */     if (msw == 65279) {
/* 416 */       this._inputPtr += 2;
/* 417 */       this._bytesPerChar = 2;
/* 418 */       this._bigEndian = true;
/* 419 */       return true;
/*     */     }
/* 421 */     if (msw == 65534) {
/* 422 */       this._inputPtr += 2;
/* 423 */       this._bytesPerChar = 2;
/* 424 */       this._bigEndian = false;
/* 425 */       return true;
/*     */     }
/*     */     
/* 428 */     if (quad >>> 8 == 15711167) {
/* 429 */       this._inputPtr += 3;
/* 430 */       this._bytesPerChar = 1;
/* 431 */       this._bigEndian = true;
/* 432 */       return true;
/*     */     }
/* 434 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean checkUTF32(int quad)
/*     */     throws IOException
/*     */   {
/* 443 */     if (quad >> 8 == 0) {
/* 444 */       this._bigEndian = true;
/* 445 */     } else if ((quad & 0xFFFFFF) == 0) {
/* 446 */       this._bigEndian = false;
/* 447 */     } else if ((quad & 0xFF00FFFF) == 0) {
/* 448 */       reportWeirdUCS4("3412");
/* 449 */     } else if ((quad & 0xFFFF00FF) == 0) {
/* 450 */       reportWeirdUCS4("2143");
/*     */     }
/*     */     else {
/* 453 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 457 */     this._bytesPerChar = 4;
/* 458 */     return true;
/*     */   }
/*     */   
/*     */   private boolean checkUTF16(int i16)
/*     */   {
/* 463 */     if ((i16 & 0xFF00) == 0) {
/* 464 */       this._bigEndian = true;
/* 465 */     } else if ((i16 & 0xFF) == 0) {
/* 466 */       this._bigEndian = false;
/*     */     } else {
/* 468 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 472 */     this._bytesPerChar = 2;
/* 473 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportWeirdUCS4(String type)
/*     */     throws IOException
/*     */   {
/* 485 */     throw new CharConversionException("Unsupported UCS-4 endianness (" + type + ") detected");
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
/*     */   protected boolean ensureLoaded(int minimum)
/*     */     throws IOException
/*     */   {
/* 500 */     int gotten = this._inputEnd - this._inputPtr;
/* 501 */     while (gotten < minimum) {
/*     */       int count;
/*     */       int count;
/* 504 */       if (this._in == null) {
/* 505 */         count = -1;
/*     */       } else {
/* 507 */         count = this._in.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
/*     */       }
/* 509 */       if (count < 1) {
/* 510 */         return false;
/*     */       }
/* 512 */       this._inputEnd += count;
/* 513 */       gotten += count;
/*     */     }
/* 515 */     return true;
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\ByteSourceBootstrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */