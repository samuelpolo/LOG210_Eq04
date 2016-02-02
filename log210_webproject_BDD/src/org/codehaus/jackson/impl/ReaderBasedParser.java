/*      */ package org.codehaus.jackson.impl;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ import org.codehaus.jackson.Base64Variant;
/*      */ import org.codehaus.jackson.JsonParseException;
/*      */ import org.codehaus.jackson.JsonParser.Feature;
/*      */ import org.codehaus.jackson.JsonToken;
/*      */ import org.codehaus.jackson.ObjectCodec;
/*      */ import org.codehaus.jackson.io.IOContext;
/*      */ import org.codehaus.jackson.sym.CharsToNameCanonicalizer;
/*      */ import org.codehaus.jackson.util.ByteArrayBuilder;
/*      */ import org.codehaus.jackson.util.CharTypes;
/*      */ import org.codehaus.jackson.util.TextBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ReaderBasedParser
/*      */   extends JsonParserBase
/*      */ {
/*      */   protected Reader _reader;
/*      */   protected char[] _inputBuffer;
/*      */   protected ObjectCodec _objectCodec;
/*      */   protected final CharsToNameCanonicalizer _symbols;
/*   58 */   protected boolean _tokenIncomplete = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ReaderBasedParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st)
/*      */   {
/*   69 */     super(ctxt, features);
/*   70 */     this._reader = r;
/*   71 */     this._inputBuffer = ctxt.allocTokenBuffer();
/*   72 */     this._objectCodec = codec;
/*   73 */     this._symbols = st;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectCodec getCodec()
/*      */   {
/*   84 */     return this._objectCodec;
/*      */   }
/*      */   
/*      */   public void setCodec(ObjectCodec c)
/*      */   {
/*   89 */     this._objectCodec = c;
/*      */   }
/*      */   
/*      */   public int releaseBuffered(Writer w)
/*      */     throws IOException
/*      */   {
/*   95 */     int count = this._inputEnd - this._inputPtr;
/*   96 */     if (count < 1) {
/*   97 */       return 0;
/*      */     }
/*      */     
/*  100 */     int origPtr = this._inputPtr;
/*  101 */     w.write(this._inputBuffer, origPtr, count);
/*  102 */     return count;
/*      */   }
/*      */   
/*      */   public Object getInputSource()
/*      */   {
/*  107 */     return this._reader;
/*      */   }
/*      */   
/*      */   protected final boolean loadMore()
/*      */     throws IOException
/*      */   {
/*  113 */     this._currInputProcessed += this._inputEnd;
/*  114 */     this._currInputRowStart -= this._inputEnd;
/*      */     
/*  116 */     if (this._reader != null) {
/*  117 */       int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
/*  118 */       if (count > 0) {
/*  119 */         this._inputPtr = 0;
/*  120 */         this._inputEnd = count;
/*  121 */         return true;
/*      */       }
/*      */       
/*  124 */       _closeInput();
/*      */       
/*  126 */       if (count == 0) {
/*  127 */         throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
/*      */       }
/*      */     }
/*  130 */     return false;
/*      */   }
/*      */   
/*      */   protected char getNextChar(String eofMsg)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  136 */     if ((this._inputPtr >= this._inputEnd) && 
/*  137 */       (!loadMore())) {
/*  138 */       _reportInvalidEOF(eofMsg);
/*      */     }
/*      */     
/*  141 */     return this._inputBuffer[(this._inputPtr++)];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _closeInput()
/*      */     throws IOException
/*      */   {
/*  154 */     if (this._reader != null) {
/*  155 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE))) {
/*  156 */         this._reader.close();
/*      */       }
/*  158 */       this._reader = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */     throws IOException
/*      */   {
/*  172 */     super._releaseBuffers();
/*  173 */     char[] buf = this._inputBuffer;
/*  174 */     if (buf != null) {
/*  175 */       this._inputBuffer = null;
/*  176 */       this._ioContext.releaseTokenBuffer(buf);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final String getText()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  196 */     JsonToken t = this._currToken;
/*  197 */     if (t == JsonToken.VALUE_STRING) {
/*  198 */       if (this._tokenIncomplete) {
/*  199 */         this._tokenIncomplete = false;
/*  200 */         _finishString();
/*      */       }
/*  202 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  204 */     return _getText2(t);
/*      */   }
/*      */   
/*      */   protected final String _getText2(JsonToken t)
/*      */   {
/*  209 */     if (t == null) {
/*  210 */       return null;
/*      */     }
/*  212 */     switch (t) {
/*      */     case FIELD_NAME: 
/*  214 */       return this._parsingContext.getCurrentName();
/*      */     
/*      */ 
/*      */     case VALUE_STRING: 
/*      */     case VALUE_NUMBER_INT: 
/*      */     case VALUE_NUMBER_FLOAT: 
/*  220 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  222 */     return t.asString();
/*      */   }
/*      */   
/*      */ 
/*      */   public char[] getTextCharacters()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  229 */     if (this._currToken != null) {
/*  230 */       switch (this._currToken)
/*      */       {
/*      */       case FIELD_NAME: 
/*  233 */         if (!this._nameCopied) {
/*  234 */           String name = this._parsingContext.getCurrentName();
/*  235 */           int nameLen = name.length();
/*  236 */           if (this._nameCopyBuffer == null) {
/*  237 */             this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  238 */           } else if (this._nameCopyBuffer.length < nameLen) {
/*  239 */             this._nameCopyBuffer = new char[nameLen];
/*      */           }
/*  241 */           name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  242 */           this._nameCopied = true;
/*      */         }
/*  244 */         return this._nameCopyBuffer;
/*      */       
/*      */       case VALUE_STRING: 
/*  247 */         if (this._tokenIncomplete) {
/*  248 */           this._tokenIncomplete = false;
/*  249 */           _finishString();
/*      */         }
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*      */       case VALUE_NUMBER_FLOAT: 
/*  254 */         return this._textBuffer.getTextBuffer();
/*      */       }
/*      */       
/*  257 */       return this._currToken.asCharArray();
/*      */     }
/*      */     
/*  260 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTextLength()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  267 */     if (this._currToken != null) {
/*  268 */       switch (this._currToken)
/*      */       {
/*      */       case FIELD_NAME: 
/*  271 */         return this._parsingContext.getCurrentName().length();
/*      */       case VALUE_STRING: 
/*  273 */         if (this._tokenIncomplete) {
/*  274 */           this._tokenIncomplete = false;
/*  275 */           _finishString();
/*      */         }
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*      */       case VALUE_NUMBER_FLOAT: 
/*  280 */         return this._textBuffer.size();
/*      */       }
/*      */       
/*  283 */       return this._currToken.asCharArray().length;
/*      */     }
/*      */     
/*  286 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTextOffset()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  293 */     if (this._currToken != null) {
/*  294 */       switch (this._currToken) {
/*      */       case FIELD_NAME: 
/*  296 */         return 0;
/*      */       case VALUE_STRING: 
/*  298 */         if (this._tokenIncomplete) {
/*  299 */           this._tokenIncomplete = false;
/*  300 */           _finishString();
/*      */         }
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*      */       case VALUE_NUMBER_FLOAT: 
/*  305 */         return this._textBuffer.getTextOffset();
/*      */       }
/*      */     }
/*  308 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getBinaryValue(Base64Variant b64variant)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  315 */     if ((this._currToken != JsonToken.VALUE_STRING) && ((this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT) || (this._binaryValue == null)))
/*      */     {
/*  317 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  322 */     if (this._tokenIncomplete) {
/*      */       try {
/*  324 */         this._binaryValue = _decodeBase64(b64variant);
/*      */       } catch (IllegalArgumentException iae) {
/*  326 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  331 */       this._tokenIncomplete = false;
/*      */     }
/*  333 */     else if (this._binaryValue == null) {
/*  334 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  335 */       _decodeBase64(getText(), builder, b64variant);
/*  336 */       this._binaryValue = builder.toByteArray();
/*      */     }
/*      */     
/*  339 */     return this._binaryValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonToken nextToken()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  356 */     this._numTypesValid = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  362 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  363 */       return _nextAfterName();
/*      */     }
/*  365 */     if (this._tokenIncomplete) {
/*  366 */       _skipString();
/*      */     }
/*  368 */     int i = _skipWSOrEnd();
/*  369 */     if (i < 0)
/*      */     {
/*      */ 
/*      */ 
/*  373 */       close();
/*  374 */       return this._currToken = null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  380 */     this._tokenInputTotal = (this._currInputProcessed + this._inputPtr - 1L);
/*  381 */     this._tokenInputRow = this._currInputRow;
/*  382 */     this._tokenInputCol = (this._inputPtr - this._currInputRowStart - 1);
/*      */     
/*      */ 
/*  385 */     this._binaryValue = null;
/*      */     
/*      */ 
/*  388 */     if (i == 93) {
/*  389 */       if (!this._parsingContext.inArray()) {
/*  390 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  392 */       this._parsingContext = this._parsingContext.getParent();
/*  393 */       return this._currToken = JsonToken.END_ARRAY;
/*      */     }
/*  395 */     if (i == 125) {
/*  396 */       if (!this._parsingContext.inObject()) {
/*  397 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  399 */       this._parsingContext = this._parsingContext.getParent();
/*  400 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     }
/*      */     
/*      */ 
/*  404 */     if (this._parsingContext.expectComma()) {
/*  405 */       if (i != 44) {
/*  406 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
/*      */       }
/*  408 */       i = _skipWS();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  415 */     boolean inObject = this._parsingContext.inObject();
/*  416 */     if (inObject)
/*      */     {
/*  418 */       String name = _parseFieldName(i);
/*  419 */       this._parsingContext.setCurrentName(name);
/*  420 */       this._currToken = JsonToken.FIELD_NAME;
/*  421 */       i = _skipWS();
/*  422 */       if (i != 58) {
/*  423 */         _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */       }
/*  425 */       i = _skipWS();
/*      */     }
/*      */     
/*      */ 
/*      */     JsonToken t;
/*      */     
/*      */ 
/*  432 */     switch (i) {
/*      */     case 34: 
/*  434 */       this._tokenIncomplete = true;
/*  435 */       t = JsonToken.VALUE_STRING;
/*  436 */       break;
/*      */     case 91: 
/*  438 */       if (!inObject) {
/*  439 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  441 */       t = JsonToken.START_ARRAY;
/*  442 */       break;
/*      */     case 123: 
/*  444 */       if (!inObject) {
/*  445 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  447 */       t = JsonToken.START_OBJECT;
/*  448 */       break;
/*      */     
/*      */ 
/*      */     case 93: 
/*      */     case 125: 
/*  453 */       _reportUnexpectedChar(i, "expected a value");
/*      */     case 116: 
/*  455 */       _matchToken("true", 1);
/*  456 */       t = JsonToken.VALUE_TRUE;
/*  457 */       break;
/*      */     case 102: 
/*  459 */       _matchToken("false", 1);
/*  460 */       t = JsonToken.VALUE_FALSE;
/*  461 */       break;
/*      */     case 110: 
/*  463 */       _matchToken("null", 1);
/*  464 */       t = JsonToken.VALUE_NULL;
/*  465 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 45: 
/*      */     case 48: 
/*      */     case 49: 
/*      */     case 50: 
/*      */     case 51: 
/*      */     case 52: 
/*      */     case 53: 
/*      */     case 54: 
/*      */     case 55: 
/*      */     case 56: 
/*      */     case 57: 
/*  482 */       t = parseNumberText(i);
/*  483 */       break;
/*      */     default: 
/*  485 */       t = _handleUnexpectedValue(i);
/*      */     }
/*      */     
/*      */     
/*  489 */     if (inObject) {
/*  490 */       this._nextToken = t;
/*  491 */       return this._currToken;
/*      */     }
/*  493 */     this._currToken = t;
/*  494 */     return t;
/*      */   }
/*      */   
/*      */   private final JsonToken _nextAfterName()
/*      */   {
/*  499 */     this._nameCopied = false;
/*  500 */     JsonToken t = this._nextToken;
/*  501 */     this._nextToken = null;
/*      */     
/*  503 */     if (t == JsonToken.START_ARRAY) {
/*  504 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  505 */     } else if (t == JsonToken.START_OBJECT) {
/*  506 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     }
/*  508 */     return this._currToken = t;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String nextTextValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  522 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  523 */       this._nameCopied = false;
/*  524 */       JsonToken t = this._nextToken;
/*  525 */       this._nextToken = null;
/*  526 */       this._currToken = t;
/*  527 */       if (t == JsonToken.VALUE_STRING) {
/*  528 */         if (this._tokenIncomplete) {
/*  529 */           this._tokenIncomplete = false;
/*  530 */           _finishString();
/*      */         }
/*  532 */         return this._textBuffer.contentsAsString();
/*      */       }
/*  534 */       if (t == JsonToken.START_ARRAY) {
/*  535 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  536 */       } else if (t == JsonToken.START_OBJECT) {
/*  537 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  539 */       return null;
/*      */     }
/*      */     
/*  542 */     return nextToken() == JsonToken.VALUE_STRING ? getText() : null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int nextIntValue(int defaultValue)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  550 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  551 */       this._nameCopied = false;
/*  552 */       JsonToken t = this._nextToken;
/*  553 */       this._nextToken = null;
/*  554 */       this._currToken = t;
/*  555 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  556 */         return getIntValue();
/*      */       }
/*  558 */       if (t == JsonToken.START_ARRAY) {
/*  559 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  560 */       } else if (t == JsonToken.START_OBJECT) {
/*  561 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  563 */       return defaultValue;
/*      */     }
/*      */     
/*  566 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long nextLongValue(long defaultValue)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  574 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  575 */       this._nameCopied = false;
/*  576 */       JsonToken t = this._nextToken;
/*  577 */       this._nextToken = null;
/*  578 */       this._currToken = t;
/*  579 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  580 */         return getLongValue();
/*      */       }
/*  582 */       if (t == JsonToken.START_ARRAY) {
/*  583 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  584 */       } else if (t == JsonToken.START_OBJECT) {
/*  585 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  587 */       return defaultValue;
/*      */     }
/*      */     
/*  590 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Boolean nextBooleanValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  598 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  599 */       this._nameCopied = false;
/*  600 */       JsonToken t = this._nextToken;
/*  601 */       this._nextToken = null;
/*  602 */       this._currToken = t;
/*  603 */       if (t == JsonToken.VALUE_TRUE) {
/*  604 */         return Boolean.TRUE;
/*      */       }
/*  606 */       if (t == JsonToken.VALUE_FALSE) {
/*  607 */         return Boolean.FALSE;
/*      */       }
/*  609 */       if (t == JsonToken.START_ARRAY) {
/*  610 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  611 */       } else if (t == JsonToken.START_OBJECT) {
/*  612 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  614 */       return null;
/*      */     }
/*  616 */     switch (nextToken()) {
/*      */     case VALUE_TRUE: 
/*  618 */       return Boolean.TRUE;
/*      */     case VALUE_FALSE: 
/*  620 */       return Boolean.FALSE;
/*      */     }
/*  622 */     return null;
/*      */   }
/*      */   
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  628 */     super.close();
/*  629 */     this._symbols.release();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JsonToken parseNumberText(int ch)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  662 */     boolean negative = ch == 45;
/*  663 */     int ptr = this._inputPtr;
/*  664 */     int startPtr = ptr - 1;
/*  665 */     int inputLen = this._inputEnd;
/*      */     
/*      */ 
/*      */ 
/*  669 */     if (negative) {
/*  670 */       if (ptr >= this._inputEnd) {
/*      */         break label347;
/*      */       }
/*  673 */       ch = this._inputBuffer[(ptr++)];
/*      */       
/*  675 */       if ((ch > 57) || (ch < 48)) {
/*  676 */         this._inputPtr = ptr;
/*  677 */         return _handleInvalidNumberStart(ch, true);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  685 */     if (ch != 48)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  695 */       int intLen = 1;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  701 */       while (ptr < this._inputEnd)
/*      */       {
/*      */ 
/*  704 */         ch = this._inputBuffer[(ptr++)];
/*  705 */         if ((ch >= 48) && (ch <= 57))
/*      */         {
/*      */ 
/*  708 */           intLen++;
/*      */         }
/*      */         else {
/*  711 */           int fractLen = 0;
/*      */           
/*      */ 
/*  714 */           if (ch == 46)
/*      */           {
/*      */             for (;;) {
/*  717 */               if (ptr >= inputLen) {
/*      */                 break label347;
/*      */               }
/*  720 */               ch = this._inputBuffer[(ptr++)];
/*  721 */               if ((ch < 48) || (ch > 57)) {
/*      */                 break;
/*      */               }
/*  724 */               fractLen++;
/*      */             }
/*      */             
/*  727 */             if (fractLen == 0) {
/*  728 */               reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
/*      */             }
/*      */           }
/*      */           
/*  732 */           int expLen = 0;
/*  733 */           if ((ch == 101) || (ch == 69)) {
/*  734 */             if (ptr >= inputLen) {
/*      */               break;
/*      */             }
/*      */             
/*  738 */             ch = this._inputBuffer[(ptr++)];
/*  739 */             if ((ch == 45) || (ch == 43)) {
/*  740 */               if (ptr >= inputLen) {
/*      */                 break;
/*      */               }
/*  743 */               ch = this._inputBuffer[(ptr++)];
/*      */             }
/*  745 */             while ((ch <= 57) && (ch >= 48)) {
/*  746 */               expLen++;
/*  747 */               if (ptr >= inputLen) {
/*      */                 break label347;
/*      */               }
/*  750 */               ch = this._inputBuffer[(ptr++)];
/*      */             }
/*      */             
/*  753 */             if (expLen == 0) {
/*  754 */               reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  759 */           ptr--;
/*  760 */           this._inputPtr = ptr;
/*  761 */           int len = ptr - startPtr;
/*  762 */           this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
/*  763 */           return reset(negative, intLen, fractLen, expLen);
/*      */         } } }
/*      */     label347:
/*  766 */     this._inputPtr = (negative ? startPtr + 1 : startPtr);
/*  767 */     return parseNumberText2(negative);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final JsonToken parseNumberText2(boolean negative)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  780 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*  781 */     int outPtr = 0;
/*      */     
/*      */ 
/*  784 */     if (negative) {
/*  785 */       outBuf[(outPtr++)] = '-';
/*      */     }
/*      */     
/*      */ 
/*  789 */     int intLen = 0;
/*  790 */     char c = this._inputPtr < this._inputEnd ? this._inputBuffer[(this._inputPtr++)] : getNextChar("No digit following minus sign");
/*  791 */     if (c == '0') {
/*  792 */       c = _verifyNoLeadingZeroes();
/*      */     }
/*  794 */     boolean eof = false;
/*      */     
/*      */ 
/*      */ 
/*  798 */     while ((c >= '0') && (c <= '9')) {
/*  799 */       intLen++;
/*  800 */       if (outPtr >= outBuf.length) {
/*  801 */         outBuf = this._textBuffer.finishCurrentSegment();
/*  802 */         outPtr = 0;
/*      */       }
/*  804 */       outBuf[(outPtr++)] = c;
/*  805 */       if ((this._inputPtr >= this._inputEnd) && (!loadMore()))
/*      */       {
/*  807 */         c = '\000';
/*  808 */         eof = true;
/*  809 */         break;
/*      */       }
/*  811 */       c = this._inputBuffer[(this._inputPtr++)];
/*      */     }
/*      */     
/*  814 */     if (intLen == 0) {
/*  815 */       reportInvalidNumber("Missing integer part (next char " + _getCharDesc(c) + ")");
/*      */     }
/*      */     
/*  818 */     int fractLen = 0;
/*      */     
/*  820 */     if (c == '.') {
/*  821 */       outBuf[(outPtr++)] = c;
/*      */       
/*      */       for (;;)
/*      */       {
/*  825 */         if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/*  826 */           eof = true;
/*  827 */           break;
/*      */         }
/*  829 */         c = this._inputBuffer[(this._inputPtr++)];
/*  830 */         if ((c < '0') || (c > '9')) {
/*      */           break;
/*      */         }
/*  833 */         fractLen++;
/*  834 */         if (outPtr >= outBuf.length) {
/*  835 */           outBuf = this._textBuffer.finishCurrentSegment();
/*  836 */           outPtr = 0;
/*      */         }
/*  838 */         outBuf[(outPtr++)] = c;
/*      */       }
/*      */       
/*  841 */       if (fractLen == 0) {
/*  842 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/*  846 */     int expLen = 0;
/*  847 */     if ((c == 'e') || (c == 'E')) {
/*  848 */       if (outPtr >= outBuf.length) {
/*  849 */         outBuf = this._textBuffer.finishCurrentSegment();
/*  850 */         outPtr = 0;
/*      */       }
/*  852 */       outBuf[(outPtr++)] = c;
/*      */       
/*  854 */       c = this._inputPtr < this._inputEnd ? this._inputBuffer[(this._inputPtr++)] : getNextChar("expected a digit for number exponent");
/*      */       
/*      */ 
/*  857 */       if ((c == '-') || (c == '+')) {
/*  858 */         if (outPtr >= outBuf.length) {
/*  859 */           outBuf = this._textBuffer.finishCurrentSegment();
/*  860 */           outPtr = 0;
/*      */         }
/*  862 */         outBuf[(outPtr++)] = c;
/*      */         
/*  864 */         c = this._inputPtr < this._inputEnd ? this._inputBuffer[(this._inputPtr++)] : getNextChar("expected a digit for number exponent");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  869 */       while ((c <= '9') && (c >= '0')) {
/*  870 */         expLen++;
/*  871 */         if (outPtr >= outBuf.length) {
/*  872 */           outBuf = this._textBuffer.finishCurrentSegment();
/*  873 */           outPtr = 0;
/*      */         }
/*  875 */         outBuf[(outPtr++)] = c;
/*  876 */         if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/*  877 */           eof = true;
/*  878 */           break;
/*      */         }
/*  880 */         c = this._inputBuffer[(this._inputPtr++)];
/*      */       }
/*      */       
/*  883 */       if (expLen == 0) {
/*  884 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  889 */     if (!eof) {
/*  890 */       this._inputPtr -= 1;
/*      */     }
/*  892 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/*  894 */     return reset(negative, intLen, fractLen, expLen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final char _verifyNoLeadingZeroes()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  905 */     if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/*  906 */       return '0';
/*      */     }
/*  908 */     char ch = this._inputBuffer[this._inputPtr];
/*      */     
/*  910 */     if ((ch < '0') || (ch > '9')) {
/*  911 */       return '0';
/*      */     }
/*  913 */     if (!isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
/*  914 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/*  917 */     this._inputPtr += 1;
/*  918 */     if (ch == '0') {
/*  919 */       while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/*  920 */         ch = this._inputBuffer[this._inputPtr];
/*  921 */         if ((ch < '0') || (ch > '9')) {
/*  922 */           return '0';
/*      */         }
/*  924 */         this._inputPtr += 1;
/*  925 */         if (ch != '0') {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*  930 */     return ch;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean negative)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  940 */     if (ch == 73) {
/*  941 */       if ((this._inputPtr >= this._inputEnd) && 
/*  942 */         (!loadMore())) {
/*  943 */         _reportInvalidEOFInValue();
/*      */       }
/*      */       
/*  946 */       ch = this._inputBuffer[(this._inputPtr++)];
/*  947 */       if (ch == 78) {
/*  948 */         String match = negative ? "-INF" : "+INF";
/*  949 */         _matchToken(match, 3);
/*  950 */         if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/*  951 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/*  953 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*  954 */       } else if (ch == 110) {
/*  955 */         String match = negative ? "-Infinity" : "+Infinity";
/*  956 */         _matchToken(match, 3);
/*  957 */         if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/*  958 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/*  960 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */       }
/*      */     }
/*  963 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/*  964 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final String _parseFieldName(int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  976 */     if (i != 34) {
/*  977 */       return _handleUnusualFieldName(i);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  983 */     int ptr = this._inputPtr;
/*  984 */     int hash = 0;
/*  985 */     int inputLen = this._inputEnd;
/*      */     
/*  987 */     if (ptr < inputLen) {
/*  988 */       int[] codes = CharTypes.getInputCodeLatin1();
/*  989 */       int maxCode = codes.length;
/*      */       do
/*      */       {
/*  992 */         int ch = this._inputBuffer[ptr];
/*  993 */         if ((ch < maxCode) && (codes[ch] != 0)) {
/*  994 */           if (ch != 34) break;
/*  995 */           int start = this._inputPtr;
/*  996 */           this._inputPtr = (ptr + 1);
/*  997 */           return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */         }
/*      */         
/*      */ 
/* 1001 */         hash = hash * 31 + ch;
/* 1002 */         ptr++;
/* 1003 */       } while (ptr < inputLen);
/*      */     }
/*      */     
/* 1006 */     int start = this._inputPtr;
/* 1007 */     this._inputPtr = ptr;
/* 1008 */     return _parseFieldName2(start, hash, 34);
/*      */   }
/*      */   
/*      */   private String _parseFieldName2(int startPtr, int hash, int endChar)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1014 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1019 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1020 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     for (;;)
/*      */     {
/* 1023 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1024 */         (!loadMore())) {
/* 1025 */         _reportInvalidEOF(": was expecting closing '" + (char)endChar + "' for name");
/*      */       }
/*      */       
/* 1028 */       char c = this._inputBuffer[(this._inputPtr++)];
/* 1029 */       int i = c;
/* 1030 */       if (i <= 92) {
/* 1031 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1036 */           c = _decodeEscaped();
/* 1037 */         } else if (i <= endChar) {
/* 1038 */           if (i == endChar) {
/*      */             break;
/*      */           }
/* 1041 */           if (i < 32) {
/* 1042 */             _throwUnquotedSpace(i, "name");
/*      */           }
/*      */         }
/*      */       }
/* 1046 */       hash = hash * 31 + i;
/*      */       
/* 1048 */       outBuf[(outPtr++)] = c;
/*      */       
/*      */ 
/* 1051 */       if (outPtr >= outBuf.length) {
/* 1052 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1053 */         outPtr = 0;
/*      */       }
/*      */     }
/* 1056 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1058 */     TextBuffer tb = this._textBuffer;
/* 1059 */     char[] buf = tb.getTextBuffer();
/* 1060 */     int start = tb.getTextOffset();
/* 1061 */     int len = tb.size();
/*      */     
/* 1063 */     return this._symbols.findSymbol(buf, start, len, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final String _handleUnusualFieldName(int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1079 */     if ((i == 39) && (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES))) {
/* 1080 */       return _parseApostropheFieldName();
/*      */     }
/*      */     
/* 1083 */     if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
/* 1084 */       _reportUnexpectedChar(i, "was expecting double-quote to start field name");
/*      */     }
/* 1086 */     int[] codes = CharTypes.getInputCodeLatin1JsNames();
/* 1087 */     int maxCode = codes.length;
/*      */     
/*      */     boolean firstOk;
/*      */     
/*      */     boolean firstOk;
/* 1092 */     if (i < maxCode) {
/* 1093 */       firstOk = (codes[i] == 0) && ((i < 48) || (i > 57));
/*      */     } else {
/* 1095 */       firstOk = Character.isJavaIdentifierPart((char)i);
/*      */     }
/* 1097 */     if (!firstOk) {
/* 1098 */       _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/* 1100 */     int ptr = this._inputPtr;
/* 1101 */     int hash = 0;
/* 1102 */     int inputLen = this._inputEnd;
/*      */     
/* 1104 */     if (ptr < inputLen) {
/*      */       do {
/* 1106 */         int ch = this._inputBuffer[ptr];
/* 1107 */         if (ch < maxCode) {
/* 1108 */           if (codes[ch] != 0) {
/* 1109 */             int start = this._inputPtr - 1;
/* 1110 */             this._inputPtr = ptr;
/* 1111 */             return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */           }
/* 1113 */         } else if (!Character.isJavaIdentifierPart((char)ch)) {
/* 1114 */           int start = this._inputPtr - 1;
/* 1115 */           this._inputPtr = ptr;
/* 1116 */           return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */         }
/* 1118 */         hash = hash * 31 + ch;
/* 1119 */         ptr++;
/* 1120 */       } while (ptr < inputLen);
/*      */     }
/* 1122 */     int start = this._inputPtr - 1;
/* 1123 */     this._inputPtr = ptr;
/* 1124 */     return _parseUnusualFieldName2(start, hash, codes);
/*      */   }
/*      */   
/*      */ 
/*      */   protected final String _parseApostropheFieldName()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1131 */     int ptr = this._inputPtr;
/* 1132 */     int hash = 0;
/* 1133 */     int inputLen = this._inputEnd;
/*      */     
/* 1135 */     if (ptr < inputLen) {
/* 1136 */       int[] codes = CharTypes.getInputCodeLatin1();
/* 1137 */       int maxCode = codes.length;
/*      */       do
/*      */       {
/* 1140 */         int ch = this._inputBuffer[ptr];
/* 1141 */         if (ch == 39) {
/* 1142 */           int start = this._inputPtr;
/* 1143 */           this._inputPtr = (ptr + 1);
/* 1144 */           return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
/*      */         }
/* 1146 */         if ((ch < maxCode) && (codes[ch] != 0)) {
/*      */           break;
/*      */         }
/* 1149 */         hash = hash * 31 + ch;
/* 1150 */         ptr++;
/* 1151 */       } while (ptr < inputLen);
/*      */     }
/*      */     
/* 1154 */     int start = this._inputPtr;
/* 1155 */     this._inputPtr = ptr;
/*      */     
/* 1157 */     return _parseFieldName2(start, hash, 39);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JsonToken _handleUnexpectedValue(int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1170 */     switch (i)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 39: 
/* 1179 */       if (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/* 1180 */         return _handleApostropheValue();
/*      */       }
/*      */       break;
/*      */     case 78: 
/* 1184 */       _matchToken("NaN", 1);
/* 1185 */       if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 1186 */         return resetAsNaN("NaN", NaN.0D);
/*      */       }
/* 1188 */       _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 1189 */       break;
/*      */     case 43: 
/* 1191 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1192 */         (!loadMore())) {
/* 1193 */         _reportInvalidEOFInValue();
/*      */       }
/*      */       
/* 1196 */       return _handleInvalidNumberStart(this._inputBuffer[(this._inputPtr++)], false); }
/*      */     
/* 1198 */     _reportUnexpectedChar(i, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
/* 1199 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JsonToken _handleApostropheValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1208 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1209 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     for (;;)
/*      */     {
/* 1212 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1213 */         (!loadMore())) {
/* 1214 */         _reportInvalidEOF(": was expecting closing quote for a string value");
/*      */       }
/*      */       
/* 1217 */       char c = this._inputBuffer[(this._inputPtr++)];
/* 1218 */       int i = c;
/* 1219 */       if (i <= 92) {
/* 1220 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1225 */           c = _decodeEscaped();
/* 1226 */         } else if (i <= 39) {
/* 1227 */           if (i == 39) {
/*      */             break;
/*      */           }
/* 1230 */           if (i < 32) {
/* 1231 */             _throwUnquotedSpace(i, "string value");
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1236 */       if (outPtr >= outBuf.length) {
/* 1237 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1238 */         outPtr = 0;
/*      */       }
/*      */       
/* 1241 */       outBuf[(outPtr++)] = c;
/*      */     }
/* 1243 */     this._textBuffer.setCurrentLength(outPtr);
/* 1244 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String _parseUnusualFieldName2(int startPtr, int hash, int[] codes)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1253 */     this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
/* 1254 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1255 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/* 1256 */     int maxCode = codes.length;
/*      */     
/*      */ 
/* 1259 */     while ((this._inputPtr < this._inputEnd) || 
/* 1260 */       (loadMore()))
/*      */     {
/*      */ 
/*      */ 
/* 1264 */       char c = this._inputBuffer[this._inputPtr];
/* 1265 */       int i = c;
/* 1266 */       if (i <= maxCode ? 
/* 1267 */         codes[i] != 0 : 
/*      */         
/*      */ 
/* 1270 */         !Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 1273 */       this._inputPtr += 1;
/* 1274 */       hash = hash * 31 + i;
/*      */       
/* 1276 */       outBuf[(outPtr++)] = c;
/*      */       
/*      */ 
/* 1279 */       if (outPtr >= outBuf.length) {
/* 1280 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1281 */         outPtr = 0;
/*      */       }
/*      */     }
/* 1284 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 1286 */     TextBuffer tb = this._textBuffer;
/* 1287 */     char[] buf = tb.getTextBuffer();
/* 1288 */     int start = tb.getTextOffset();
/* 1289 */     int len = tb.size();
/*      */     
/* 1291 */     return this._symbols.findSymbol(buf, start, len, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _finishString()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1303 */     int ptr = this._inputPtr;
/* 1304 */     int inputLen = this._inputEnd;
/*      */     
/* 1306 */     if (ptr < inputLen) {
/* 1307 */       int[] codes = CharTypes.getInputCodeLatin1();
/* 1308 */       int maxCode = codes.length;
/*      */       do
/*      */       {
/* 1311 */         int ch = this._inputBuffer[ptr];
/* 1312 */         if ((ch < maxCode) && (codes[ch] != 0)) {
/* 1313 */           if (ch != 34) break;
/* 1314 */           this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 1315 */           this._inputPtr = (ptr + 1);
/*      */           
/* 1317 */           return;
/*      */         }
/*      */         
/*      */ 
/* 1321 */         ptr++;
/* 1322 */       } while (ptr < inputLen);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1328 */     this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
/* 1329 */     this._inputPtr = ptr;
/* 1330 */     _finishString2();
/*      */   }
/*      */   
/*      */   protected void _finishString2()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1336 */     char[] outBuf = this._textBuffer.getCurrentSegment();
/* 1337 */     int outPtr = this._textBuffer.getCurrentSegmentSize();
/*      */     for (;;)
/*      */     {
/* 1340 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1341 */         (!loadMore())) {
/* 1342 */         _reportInvalidEOF(": was expecting closing quote for a string value");
/*      */       }
/*      */       
/* 1345 */       char c = this._inputBuffer[(this._inputPtr++)];
/* 1346 */       int i = c;
/* 1347 */       if (i <= 92) {
/* 1348 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1353 */           c = _decodeEscaped();
/* 1354 */         } else if (i <= 34) {
/* 1355 */           if (i == 34) {
/*      */             break;
/*      */           }
/* 1358 */           if (i < 32) {
/* 1359 */             _throwUnquotedSpace(i, "string value");
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1364 */       if (outPtr >= outBuf.length) {
/* 1365 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1366 */         outPtr = 0;
/*      */       }
/*      */       
/* 1369 */       outBuf[(outPtr++)] = c;
/*      */     }
/* 1371 */     this._textBuffer.setCurrentLength(outPtr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _skipString()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1382 */     this._tokenIncomplete = false;
/*      */     
/* 1384 */     int inputPtr = this._inputPtr;
/* 1385 */     int inputLen = this._inputEnd;
/* 1386 */     char[] inputBuffer = this._inputBuffer;
/*      */     for (;;)
/*      */     {
/* 1389 */       if (inputPtr >= inputLen) {
/* 1390 */         this._inputPtr = inputPtr;
/* 1391 */         if (!loadMore()) {
/* 1392 */           _reportInvalidEOF(": was expecting closing quote for a string value");
/*      */         }
/* 1394 */         inputPtr = this._inputPtr;
/* 1395 */         inputLen = this._inputEnd;
/*      */       }
/* 1397 */       char c = inputBuffer[(inputPtr++)];
/* 1398 */       int i = c;
/* 1399 */       if (i <= 92) {
/* 1400 */         if (i == 92)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1405 */           this._inputPtr = inputPtr;
/* 1406 */           c = _decodeEscaped();
/* 1407 */           inputPtr = this._inputPtr;
/* 1408 */           inputLen = this._inputEnd;
/* 1409 */         } else if (i <= 34) {
/* 1410 */           if (i == 34) {
/* 1411 */             this._inputPtr = inputPtr;
/* 1412 */             break;
/*      */           }
/* 1414 */           if (i < 32) {
/* 1415 */             this._inputPtr = inputPtr;
/* 1416 */             _throwUnquotedSpace(i, "string value");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _skipCR()
/*      */     throws IOException
/*      */   {
/* 1435 */     if (((this._inputPtr < this._inputEnd) || (loadMore())) && 
/* 1436 */       (this._inputBuffer[this._inputPtr] == '\n')) {
/* 1437 */       this._inputPtr += 1;
/*      */     }
/*      */     
/* 1440 */     this._currInputRow += 1;
/* 1441 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */   
/*      */   protected final void _skipLF() throws IOException
/*      */   {
/* 1446 */     this._currInputRow += 1;
/* 1447 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */   
/*      */   private final int _skipWS()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1453 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 1454 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 1455 */       if (i > 32) {
/* 1456 */         if (i != 47) {
/* 1457 */           return i;
/*      */         }
/* 1459 */         _skipComment();
/* 1460 */       } else if (i != 32) {
/* 1461 */         if (i == 10) {
/* 1462 */           _skipLF();
/* 1463 */         } else if (i == 13) {
/* 1464 */           _skipCR();
/* 1465 */         } else if (i != 9) {
/* 1466 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 1470 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
/*      */   }
/*      */   
/*      */   private final int _skipWSOrEnd()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1476 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 1477 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 1478 */       if (i > 32) {
/* 1479 */         if (i == 47) {
/* 1480 */           _skipComment();
/*      */         }
/*      */         else {
/* 1483 */           return i;
/*      */         }
/* 1485 */       } else if (i != 32) {
/* 1486 */         if (i == 10) {
/* 1487 */           _skipLF();
/* 1488 */         } else if (i == 13) {
/* 1489 */           _skipCR();
/* 1490 */         } else if (i != 9) {
/* 1491 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1496 */     _handleEOF();
/* 1497 */     return -1;
/*      */   }
/*      */   
/*      */   private final void _skipComment()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1503 */     if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
/* 1504 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 1507 */     if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/* 1508 */       _reportInvalidEOF(" in a comment");
/*      */     }
/* 1510 */     char c = this._inputBuffer[(this._inputPtr++)];
/* 1511 */     if (c == '/') {
/* 1512 */       _skipCppComment();
/* 1513 */     } else if (c == '*') {
/* 1514 */       _skipCComment();
/*      */     } else {
/* 1516 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final void _skipCComment()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1525 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 1526 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 1527 */       if (i <= 42) {
/* 1528 */         if (i == 42) {
/* 1529 */           if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/*      */             break;
/*      */           }
/* 1532 */           if (this._inputBuffer[this._inputPtr] == '/') {
/* 1533 */             this._inputPtr += 1;
/*      */           }
/*      */           
/*      */ 
/*      */         }
/* 1538 */         else if (i < 32) {
/* 1539 */           if (i == 10) {
/* 1540 */             _skipLF();
/* 1541 */           } else if (i == 13) {
/* 1542 */             _skipCR();
/* 1543 */           } else if (i != 9) {
/* 1544 */             _throwInvalidSpace(i);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1549 */     _reportInvalidEOF(" in a comment");
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _skipCppComment()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1556 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 1557 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 1558 */       if (i < 32) {
/* 1559 */         if (i == 10) {
/* 1560 */           _skipLF();
/* 1561 */           break; }
/* 1562 */         if (i == 13) {
/* 1563 */           _skipCR();
/* 1564 */           break; }
/* 1565 */         if (i != 9) {
/* 1566 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected final char _decodeEscaped()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1576 */     if ((this._inputPtr >= this._inputEnd) && 
/* 1577 */       (!loadMore())) {
/* 1578 */       _reportInvalidEOF(" in character escape sequence");
/*      */     }
/*      */     
/* 1581 */     char c = this._inputBuffer[(this._inputPtr++)];
/*      */     
/* 1583 */     switch (c)
/*      */     {
/*      */     case 'b': 
/* 1586 */       return '\b';
/*      */     case 't': 
/* 1588 */       return '\t';
/*      */     case 'n': 
/* 1590 */       return '\n';
/*      */     case 'f': 
/* 1592 */       return '\f';
/*      */     case 'r': 
/* 1594 */       return '\r';
/*      */     
/*      */ 
/*      */     case '"': 
/*      */     case '/': 
/*      */     case '\\': 
/* 1600 */       return c;
/*      */     
/*      */     case 'u': 
/*      */       break;
/*      */     
/*      */     default: 
/* 1606 */       return _handleUnrecognizedCharacterEscape(c);
/*      */     }
/*      */     
/*      */     
/* 1610 */     int value = 0;
/* 1611 */     for (int i = 0; i < 4; i++) {
/* 1612 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1613 */         (!loadMore())) {
/* 1614 */         _reportInvalidEOF(" in character escape sequence");
/*      */       }
/*      */       
/* 1617 */       int ch = this._inputBuffer[(this._inputPtr++)];
/* 1618 */       int digit = CharTypes.charToHex(ch);
/* 1619 */       if (digit < 0) {
/* 1620 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 1622 */       value = value << 4 | digit;
/*      */     }
/* 1624 */     return (char)value;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _matchToken(String matchStr, int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1635 */     int len = matchStr.length();
/*      */     do
/*      */     {
/* 1638 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1639 */         (!loadMore())) {
/* 1640 */         _reportInvalidEOFInValue();
/*      */       }
/*      */       
/* 1643 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 1644 */         _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
/*      */       }
/* 1646 */       this._inputPtr += 1;
/* 1647 */       i++; } while (i < len);
/*      */     
/*      */ 
/* 1650 */     if ((this._inputPtr >= this._inputEnd) && 
/* 1651 */       (!loadMore())) {
/* 1652 */       return;
/*      */     }
/*      */     
/* 1655 */     char c = this._inputBuffer[this._inputPtr];
/* 1656 */     if ((c < '0') || (c == ']') || (c == '}')) {
/* 1657 */       return;
/*      */     }
/*      */     
/* 1660 */     if (Character.isJavaIdentifierPart(c)) {
/* 1661 */       _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] _decodeBase64(Base64Variant b64variant)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1679 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 1686 */       if (this._inputPtr >= this._inputEnd) {
/* 1687 */         loadMoreGuaranteed();
/*      */       }
/* 1689 */       char ch = this._inputBuffer[(this._inputPtr++)];
/* 1690 */       if (ch > ' ') {
/* 1691 */         int bits = b64variant.decodeBase64Char(ch);
/* 1692 */         if (bits < 0) {
/* 1693 */           if (ch == '"') {
/* 1694 */             return builder.toByteArray();
/*      */           }
/* 1696 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 1697 */           if (bits < 0) {}
/*      */         }
/*      */         else
/*      */         {
/* 1701 */           int decodedData = bits;
/*      */           
/*      */ 
/*      */ 
/* 1705 */           if (this._inputPtr >= this._inputEnd) {
/* 1706 */             loadMoreGuaranteed();
/*      */           }
/* 1708 */           ch = this._inputBuffer[(this._inputPtr++)];
/* 1709 */           bits = b64variant.decodeBase64Char(ch);
/* 1710 */           if (bits < 0) {
/* 1711 */             bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */           }
/* 1713 */           decodedData = decodedData << 6 | bits;
/*      */           
/*      */ 
/* 1716 */           if (this._inputPtr >= this._inputEnd) {
/* 1717 */             loadMoreGuaranteed();
/*      */           }
/* 1719 */           ch = this._inputBuffer[(this._inputPtr++)];
/* 1720 */           bits = b64variant.decodeBase64Char(ch);
/*      */           
/*      */ 
/* 1723 */           if (bits < 0) {
/* 1724 */             if (bits != -2)
/*      */             {
/* 1726 */               if ((ch == '"') && (!b64variant.usesPadding())) {
/* 1727 */                 decodedData >>= 4;
/* 1728 */                 builder.append(decodedData);
/* 1729 */                 return builder.toByteArray();
/*      */               }
/* 1731 */               bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */             }
/* 1733 */             if (bits == -2)
/*      */             {
/* 1735 */               if (this._inputPtr >= this._inputEnd) {
/* 1736 */                 loadMoreGuaranteed();
/*      */               }
/* 1738 */               ch = this._inputBuffer[(this._inputPtr++)];
/* 1739 */               if (!b64variant.usesPaddingChar(ch)) {
/* 1740 */                 throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */               }
/*      */               
/* 1743 */               decodedData >>= 4;
/* 1744 */               builder.append(decodedData);
/* 1745 */               continue;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1750 */           decodedData = decodedData << 6 | bits;
/*      */           
/* 1752 */           if (this._inputPtr >= this._inputEnd) {
/* 1753 */             loadMoreGuaranteed();
/*      */           }
/* 1755 */           ch = this._inputBuffer[(this._inputPtr++)];
/* 1756 */           bits = b64variant.decodeBase64Char(ch);
/* 1757 */           if (bits < 0) {
/* 1758 */             if (bits != -2)
/*      */             {
/* 1760 */               if ((ch == '"') && (!b64variant.usesPadding())) {
/* 1761 */                 decodedData >>= 2;
/* 1762 */                 builder.appendTwoBytes(decodedData);
/* 1763 */                 return builder.toByteArray();
/*      */               }
/* 1765 */               bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */             }
/* 1767 */             if (bits == -2)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1773 */               decodedData >>= 2;
/* 1774 */               builder.appendTwoBytes(decodedData);
/* 1775 */               continue;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1780 */           decodedData = decodedData << 6 | bits;
/* 1781 */           builder.appendThreeBytes(decodedData);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _reportInvalidToken(String matchedPart, String msg)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1794 */     StringBuilder sb = new StringBuilder(matchedPart);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1800 */     while ((this._inputPtr < this._inputEnd) || 
/* 1801 */       (loadMore()))
/*      */     {
/*      */ 
/*      */ 
/* 1805 */       char c = this._inputBuffer[this._inputPtr];
/* 1806 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 1809 */       this._inputPtr += 1;
/* 1810 */       sb.append(c);
/*      */     }
/* 1812 */     _reportError("Unrecognized token '" + sb.toString() + "': was expecting ");
/*      */   }
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\ReaderBasedParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */