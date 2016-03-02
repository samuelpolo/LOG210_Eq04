/*     */ package org.codehaus.jackson.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.codehaus.jackson.Base64Variant;
/*     */ import org.codehaus.jackson.JsonParseException;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ import org.codehaus.jackson.JsonParser.Feature;
/*     */ import org.codehaus.jackson.JsonProcessingException;
/*     */ import org.codehaus.jackson.JsonStreamContext;
/*     */ import org.codehaus.jackson.JsonToken;
/*     */ import org.codehaus.jackson.io.NumberInput;
/*     */ import org.codehaus.jackson.util.ByteArrayBuilder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JsonParserMinimalBase
/*     */   extends JsonParser
/*     */ {
/*     */   protected static final int INT_TAB = 9;
/*     */   protected static final int INT_LF = 10;
/*     */   protected static final int INT_CR = 13;
/*     */   protected static final int INT_SPACE = 32;
/*     */   protected static final int INT_LBRACKET = 91;
/*     */   protected static final int INT_RBRACKET = 93;
/*     */   protected static final int INT_LCURLY = 123;
/*     */   protected static final int INT_RCURLY = 125;
/*     */   protected static final int INT_QUOTE = 34;
/*     */   protected static final int INT_BACKSLASH = 92;
/*     */   protected static final int INT_SLASH = 47;
/*     */   protected static final int INT_COLON = 58;
/*     */   protected static final int INT_COMMA = 44;
/*     */   protected static final int INT_ASTERISK = 42;
/*     */   protected static final int INT_APOSTROPHE = 39;
/*     */   protected static final int INT_b = 98;
/*     */   protected static final int INT_f = 102;
/*     */   protected static final int INT_n = 110;
/*     */   protected static final int INT_r = 114;
/*     */   protected static final int INT_t = 116;
/*     */   protected static final int INT_u = 117;
/*     */   
/*     */   protected JsonParserMinimalBase() {}
/*     */   
/*     */   protected JsonParserMinimalBase(int features)
/*     */   {
/*  61 */     super(features);
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
/*     */   public abstract JsonToken nextToken()
/*     */     throws IOException, JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonParser skipChildren()
/*     */     throws IOException, JsonParseException
/*     */   {
/*  91 */     if ((this._currToken != JsonToken.START_OBJECT) && (this._currToken != JsonToken.START_ARRAY))
/*     */     {
/*  93 */       return this;
/*     */     }
/*  95 */     int open = 1;
/*     */     
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/* 101 */       JsonToken t = nextToken();
/* 102 */       if (t == null) {
/* 103 */         _handleEOF();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 108 */         return this;
/*     */       }
/* 110 */       switch (t) {
/*     */       case START_OBJECT: 
/*     */       case START_ARRAY: 
/* 113 */         open++;
/* 114 */         break;
/*     */       case END_OBJECT: 
/*     */       case END_ARRAY: 
/* 117 */         open--; if (open == 0) {
/* 118 */           return this;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void _handleEOF()
/*     */     throws JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String getCurrentName()
/*     */     throws IOException, JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void close()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean isClosed();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract JsonStreamContext getParsingContext();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract String getText()
/*     */     throws IOException, JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract char[] getTextCharacters()
/*     */     throws IOException, JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean hasTextCharacters();
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract int getTextLength()
/*     */     throws IOException, JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract int getTextOffset()
/*     */     throws IOException, JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract byte[] getBinaryValue(Base64Variant paramBase64Variant)
/*     */     throws IOException, JsonParseException;
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getValueAsBoolean(boolean defaultValue)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 190 */     if (this._currToken != null) {
/* 191 */       switch (this._currToken) {
/*     */       case VALUE_NUMBER_INT: 
/* 193 */         return getIntValue() != 0;
/*     */       case VALUE_TRUE: 
/* 195 */         return true;
/*     */       case VALUE_FALSE: 
/*     */       case VALUE_NULL: 
/* 198 */         return false;
/*     */       
/*     */       case VALUE_EMBEDDED_OBJECT: 
/* 201 */         Object value = getEmbeddedObject();
/* 202 */         if ((value instanceof Boolean)) {
/* 203 */           return ((Boolean)value).booleanValue();
/*     */         }
/*     */       
/*     */       case VALUE_STRING: 
/* 207 */         String str = getText().trim();
/* 208 */         if ("true".equals(str)) {
/* 209 */           return true;
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 214 */     return defaultValue;
/*     */   }
/*     */   
/*     */   public int getValueAsInt(int defaultValue)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 220 */     if (this._currToken != null) {
/* 221 */       switch (this._currToken) {
/*     */       case VALUE_NUMBER_INT: 
/*     */       case VALUE_NUMBER_FLOAT: 
/* 224 */         return getIntValue();
/*     */       case VALUE_TRUE: 
/* 226 */         return 1;
/*     */       case VALUE_FALSE: 
/*     */       case VALUE_NULL: 
/* 229 */         return 0;
/*     */       case VALUE_STRING: 
/* 231 */         return NumberInput.parseAsInt(getText(), defaultValue);
/*     */       
/*     */       case VALUE_EMBEDDED_OBJECT: 
/* 234 */         Object value = getEmbeddedObject();
/* 235 */         if ((value instanceof Number)) {
/* 236 */           return ((Number)value).intValue();
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 241 */     return defaultValue;
/*     */   }
/*     */   
/*     */   public long getValueAsLong(long defaultValue)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 247 */     if (this._currToken != null) {
/* 248 */       switch (this._currToken) {
/*     */       case VALUE_NUMBER_INT: 
/*     */       case VALUE_NUMBER_FLOAT: 
/* 251 */         return getLongValue();
/*     */       case VALUE_TRUE: 
/* 253 */         return 1L;
/*     */       case VALUE_FALSE: 
/*     */       case VALUE_NULL: 
/* 256 */         return 0L;
/*     */       case VALUE_STRING: 
/* 258 */         return NumberInput.parseAsLong(getText(), defaultValue);
/*     */       
/*     */       case VALUE_EMBEDDED_OBJECT: 
/* 261 */         Object value = getEmbeddedObject();
/* 262 */         if ((value instanceof Number)) {
/* 263 */           return ((Number)value).longValue();
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 268 */     return defaultValue;
/*     */   }
/*     */   
/*     */   public double getValueAsDouble(double defaultValue)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 274 */     if (this._currToken != null) {
/* 275 */       switch (this._currToken) {
/*     */       case VALUE_NUMBER_INT: 
/*     */       case VALUE_NUMBER_FLOAT: 
/* 278 */         return getDoubleValue();
/*     */       case VALUE_TRUE: 
/* 280 */         return 1.0D;
/*     */       case VALUE_FALSE: 
/*     */       case VALUE_NULL: 
/* 283 */         return 0.0D;
/*     */       case VALUE_STRING: 
/* 285 */         return NumberInput.parseAsDouble(getText(), defaultValue);
/*     */       
/*     */       case VALUE_EMBEDDED_OBJECT: 
/* 288 */         Object value = getEmbeddedObject();
/* 289 */         if ((value instanceof Number)) {
/* 290 */           return ((Number)value).doubleValue();
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 295 */     return defaultValue;
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
/*     */   protected void _decodeBase64(String str, ByteArrayBuilder builder, Base64Variant b64variant)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 313 */     int ptr = 0;
/* 314 */     int len = str.length();
/*     */     
/*     */ 
/* 317 */     while (ptr < len)
/*     */     {
/*     */       do
/*     */       {
/* 321 */         ch = str.charAt(ptr++);
/* 322 */         if (ptr >= len) {
/*     */           break;
/*     */         }
/* 325 */       } while (ch <= ' ');
/* 326 */       int bits = b64variant.decodeBase64Char(ch);
/* 327 */       if (bits < 0) {
/* 328 */         _reportInvalidBase64(b64variant, ch, 0, null);
/*     */       }
/* 330 */       int decodedData = bits;
/*     */       
/* 332 */       if (ptr >= len) {
/* 333 */         _reportBase64EOF();
/*     */       }
/* 335 */       char ch = str.charAt(ptr++);
/* 336 */       bits = b64variant.decodeBase64Char(ch);
/* 337 */       if (bits < 0) {
/* 338 */         _reportInvalidBase64(b64variant, ch, 1, null);
/*     */       }
/* 340 */       decodedData = decodedData << 6 | bits;
/*     */       
/* 342 */       if (ptr >= len)
/*     */       {
/* 344 */         if (!b64variant.usesPadding()) {
/* 345 */           decodedData >>= 4;
/* 346 */           builder.append(decodedData);
/* 347 */           break;
/*     */         }
/* 349 */         _reportBase64EOF();
/*     */       }
/* 351 */       ch = str.charAt(ptr++);
/* 352 */       bits = b64variant.decodeBase64Char(ch);
/*     */       
/*     */ 
/* 355 */       if (bits < 0) {
/* 356 */         if (bits != -2) {
/* 357 */           _reportInvalidBase64(b64variant, ch, 2, null);
/*     */         }
/*     */         
/* 360 */         if (ptr >= len) {
/* 361 */           _reportBase64EOF();
/*     */         }
/* 363 */         ch = str.charAt(ptr++);
/* 364 */         if (!b64variant.usesPaddingChar(ch)) {
/* 365 */           _reportInvalidBase64(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*     */         }
/*     */         
/* 368 */         decodedData >>= 4;
/* 369 */         builder.append(decodedData);
/*     */       }
/*     */       else
/*     */       {
/* 373 */         decodedData = decodedData << 6 | bits;
/*     */         
/* 375 */         if (ptr >= len)
/*     */         {
/* 377 */           if (!b64variant.usesPadding()) {
/* 378 */             decodedData >>= 2;
/* 379 */             builder.appendTwoBytes(decodedData);
/* 380 */             break;
/*     */           }
/* 382 */           _reportBase64EOF();
/*     */         }
/* 384 */         ch = str.charAt(ptr++);
/* 385 */         bits = b64variant.decodeBase64Char(ch);
/* 386 */         if (bits < 0) {
/* 387 */           if (bits != -2) {
/* 388 */             _reportInvalidBase64(b64variant, ch, 3, null);
/*     */           }
/* 390 */           decodedData >>= 2;
/* 391 */           builder.appendTwoBytes(decodedData);
/*     */         }
/*     */         else {
/* 394 */           decodedData = decodedData << 6 | bits;
/* 395 */           builder.appendThreeBytes(decodedData);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void _reportInvalidBase64(Base64Variant b64variant, char ch, int bindex, String msg)
/*     */     throws JsonParseException
/*     */   {
/*     */     String base;
/*     */     
/*     */     String base;
/* 408 */     if (ch <= ' ') {
/* 409 */       base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units"; } else { String base;
/* 410 */       if (b64variant.usesPaddingChar(ch)) {
/* 411 */         base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character"; } else { String base;
/* 412 */         if ((!Character.isDefined(ch)) || (Character.isISOControl(ch)))
/*     */         {
/* 414 */           base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */         } else
/* 416 */           base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */       } }
/* 418 */     if (msg != null) {
/* 419 */       base = base + ": " + msg;
/*     */     }
/* 421 */     throw _constructError(base);
/*     */   }
/*     */   
/*     */   protected void _reportBase64EOF() throws JsonParseException {
/* 425 */     throw _constructError("Unexpected end-of-String in base64 content");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _reportUnexpectedChar(int ch, String comment)
/*     */     throws JsonParseException
/*     */   {
/* 438 */     String msg = "Unexpected character (" + _getCharDesc(ch) + ")";
/* 439 */     if (comment != null) {
/* 440 */       msg = msg + ": " + comment;
/*     */     }
/* 442 */     _reportError(msg);
/*     */   }
/*     */   
/*     */   protected void _reportInvalidEOF()
/*     */     throws JsonParseException
/*     */   {
/* 448 */     _reportInvalidEOF(" in " + this._currToken);
/*     */   }
/*     */   
/*     */   protected void _reportInvalidEOF(String msg)
/*     */     throws JsonParseException
/*     */   {
/* 454 */     _reportError("Unexpected end-of-input" + msg);
/*     */   }
/*     */   
/*     */   protected void _reportInvalidEOFInValue() throws JsonParseException
/*     */   {
/* 459 */     _reportInvalidEOF(" in a value");
/*     */   }
/*     */   
/*     */   protected void _throwInvalidSpace(int i)
/*     */     throws JsonParseException
/*     */   {
/* 465 */     char c = (char)i;
/* 466 */     String msg = "Illegal character (" + _getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
/* 467 */     _reportError(msg);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _throwUnquotedSpace(int i, String ctxtDesc)
/*     */     throws JsonParseException
/*     */   {
/* 479 */     if ((!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)) || (i >= 32)) {
/* 480 */       char c = (char)i;
/* 481 */       String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
/* 482 */       _reportError(msg);
/*     */     }
/*     */   }
/*     */   
/*     */   protected char _handleUnrecognizedCharacterEscape(char ch)
/*     */     throws JsonProcessingException
/*     */   {
/* 489 */     if (isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
/* 490 */       return ch;
/*     */     }
/*     */     
/* 493 */     if ((ch == '\'') && (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES))) {
/* 494 */       return ch;
/*     */     }
/* 496 */     _reportError("Unrecognized character escape " + _getCharDesc(ch));
/* 497 */     return ch;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static final String _getCharDesc(int ch)
/*     */   {
/* 508 */     char c = (char)ch;
/* 509 */     if (Character.isISOControl(c)) {
/* 510 */       return "(CTRL-CHAR, code " + ch + ")";
/*     */     }
/* 512 */     if (ch > 255) {
/* 513 */       return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
/*     */     }
/* 515 */     return "'" + c + "' (code " + ch + ")";
/*     */   }
/*     */   
/*     */   protected final void _reportError(String msg)
/*     */     throws JsonParseException
/*     */   {
/* 521 */     throw _constructError(msg);
/*     */   }
/*     */   
/*     */   protected final void _wrapError(String msg, Throwable t)
/*     */     throws JsonParseException
/*     */   {
/* 527 */     throw _constructError(msg, t);
/*     */   }
/*     */   
/*     */   protected final void _throwInternal()
/*     */   {
/* 532 */     throw new RuntimeException("Internal error: this code path should never get executed");
/*     */   }
/*     */   
/*     */   protected final JsonParseException _constructError(String msg, Throwable t)
/*     */   {
/* 537 */     return new JsonParseException(msg, getCurrentLocation(), t);
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\JsonParserMinimalBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */