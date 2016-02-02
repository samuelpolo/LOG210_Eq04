/*      */ package org.codehaus.jackson.impl;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import org.codehaus.jackson.Base64Variant;
/*      */ import org.codehaus.jackson.JsonParseException;
/*      */ import org.codehaus.jackson.JsonParser.Feature;
/*      */ import org.codehaus.jackson.JsonToken;
/*      */ import org.codehaus.jackson.ObjectCodec;
/*      */ import org.codehaus.jackson.sym.BytesToNameCanonicalizer;
/*      */ import org.codehaus.jackson.sym.Name;
/*      */ import org.codehaus.jackson.util.ByteArrayBuilder;
/*      */ import org.codehaus.jackson.util.CharTypes;
/*      */ import org.codehaus.jackson.util.TextBuffer;
/*      */ 
/*      */ public final class Utf8StreamParser extends JsonParserBase
/*      */ {
/*      */   static final byte BYTE_LF = 10;
/*   19 */   private static final int[] sInputCodesUtf8 = ;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   25 */   private static final int[] sInputCodesLatin1 = CharTypes.getInputCodeLatin1();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectCodec _objectCodec;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final BytesToNameCanonicalizer _symbols;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   54 */   protected int[] _quadBuffer = new int[16];
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   61 */   protected boolean _tokenIncomplete = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int _quad1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected InputStream _inputStream;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] _inputBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _bufferRecyclable;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Utf8StreamParser(org.codehaus.jackson.io.IOContext ctxt, int features, InputStream in, ObjectCodec codec, BytesToNameCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable)
/*      */   {
/*  109 */     super(ctxt, features);
/*  110 */     this._inputStream = in;
/*  111 */     this._objectCodec = codec;
/*  112 */     this._symbols = sym;
/*  113 */     this._inputBuffer = inputBuffer;
/*  114 */     this._inputPtr = start;
/*  115 */     this._inputEnd = end;
/*  116 */     this._bufferRecyclable = bufferRecyclable;
/*      */     
/*  118 */     if (!JsonParser.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(features))
/*      */     {
/*  120 */       _throwInternal();
/*      */     }
/*      */   }
/*      */   
/*      */   public ObjectCodec getCodec()
/*      */   {
/*  126 */     return this._objectCodec;
/*      */   }
/*      */   
/*      */   public void setCodec(ObjectCodec c)
/*      */   {
/*  131 */     this._objectCodec = c;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int releaseBuffered(java.io.OutputStream out)
/*      */     throws IOException
/*      */   {
/*  143 */     int count = this._inputEnd - this._inputPtr;
/*  144 */     if (count < 1) {
/*  145 */       return 0;
/*      */     }
/*      */     
/*  148 */     int origPtr = this._inputPtr;
/*  149 */     out.write(this._inputBuffer, origPtr, count);
/*  150 */     return count;
/*      */   }
/*      */   
/*      */   public Object getInputSource()
/*      */   {
/*  155 */     return this._inputStream;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean loadMore()
/*      */     throws IOException
/*      */   {
/*  168 */     this._currInputProcessed += this._inputEnd;
/*  169 */     this._currInputRowStart -= this._inputEnd;
/*      */     
/*  171 */     if (this._inputStream != null) {
/*  172 */       int count = this._inputStream.read(this._inputBuffer, 0, this._inputBuffer.length);
/*  173 */       if (count > 0) {
/*  174 */         this._inputPtr = 0;
/*  175 */         this._inputEnd = count;
/*  176 */         return true;
/*      */       }
/*      */       
/*  179 */       _closeInput();
/*      */       
/*  181 */       if (count == 0) {
/*  182 */         throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
/*      */       }
/*      */     }
/*  185 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _loadToHaveAtLeast(int minAvailable)
/*      */     throws IOException
/*      */   {
/*  198 */     if (this._inputStream == null) {
/*  199 */       return false;
/*      */     }
/*      */     
/*  202 */     int amount = this._inputEnd - this._inputPtr;
/*  203 */     if ((amount > 0) && (this._inputPtr > 0)) {
/*  204 */       this._currInputProcessed += this._inputPtr;
/*  205 */       this._currInputRowStart -= this._inputPtr;
/*  206 */       System.arraycopy(this._inputBuffer, this._inputPtr, this._inputBuffer, 0, amount);
/*  207 */       this._inputEnd = amount;
/*      */     } else {
/*  209 */       this._inputEnd = 0;
/*      */     }
/*  211 */     this._inputPtr = 0;
/*  212 */     while (this._inputEnd < minAvailable) {
/*  213 */       int count = this._inputStream.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
/*  214 */       if (count < 1)
/*      */       {
/*  216 */         _closeInput();
/*      */         
/*  218 */         if (count == 0) {
/*  219 */           throw new IOException("InputStream.read() returned 0 characters when trying to read " + amount + " bytes");
/*      */         }
/*  221 */         return false;
/*      */       }
/*  223 */       this._inputEnd += count;
/*      */     }
/*  225 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _closeInput()
/*      */     throws IOException
/*      */   {
/*  235 */     if (this._inputStream != null) {
/*  236 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE))) {
/*  237 */         this._inputStream.close();
/*      */       }
/*  239 */       this._inputStream = null;
/*      */     }
/*      */   }
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
/*  252 */     super._releaseBuffers();
/*  253 */     if (this._bufferRecyclable) {
/*  254 */       byte[] buf = this._inputBuffer;
/*  255 */       if (buf != null) {
/*  256 */         this._inputBuffer = null;
/*  257 */         this._ioContext.releaseReadIOBuffer(buf);
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
/*      */   public String getText()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  272 */     JsonToken t = this._currToken;
/*  273 */     if (t == JsonToken.VALUE_STRING) {
/*  274 */       if (this._tokenIncomplete) {
/*  275 */         this._tokenIncomplete = false;
/*  276 */         _finishString();
/*      */       }
/*  278 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  280 */     return _getText2(t);
/*      */   }
/*      */   
/*      */   protected final String _getText2(JsonToken t)
/*      */   {
/*  285 */     if (t == null) {
/*  286 */       return null;
/*      */     }
/*  288 */     switch (t) {
/*      */     case FIELD_NAME: 
/*  290 */       return this._parsingContext.getCurrentName();
/*      */     
/*      */ 
/*      */     case VALUE_STRING: 
/*      */     case VALUE_NUMBER_INT: 
/*      */     case VALUE_NUMBER_FLOAT: 
/*  296 */       return this._textBuffer.contentsAsString();
/*      */     }
/*  298 */     return t.asString();
/*      */   }
/*      */   
/*      */ 
/*      */   public char[] getTextCharacters()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  305 */     if (this._currToken != null) {
/*  306 */       switch (this._currToken)
/*      */       {
/*      */       case FIELD_NAME: 
/*  309 */         if (!this._nameCopied) {
/*  310 */           String name = this._parsingContext.getCurrentName();
/*  311 */           int nameLen = name.length();
/*  312 */           if (this._nameCopyBuffer == null) {
/*  313 */             this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
/*  314 */           } else if (this._nameCopyBuffer.length < nameLen) {
/*  315 */             this._nameCopyBuffer = new char[nameLen];
/*      */           }
/*  317 */           name.getChars(0, nameLen, this._nameCopyBuffer, 0);
/*  318 */           this._nameCopied = true;
/*      */         }
/*  320 */         return this._nameCopyBuffer;
/*      */       
/*      */       case VALUE_STRING: 
/*  323 */         if (this._tokenIncomplete) {
/*  324 */           this._tokenIncomplete = false;
/*  325 */           _finishString();
/*      */         }
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*      */       case VALUE_NUMBER_FLOAT: 
/*  330 */         return this._textBuffer.getTextBuffer();
/*      */       }
/*      */       
/*  333 */       return this._currToken.asCharArray();
/*      */     }
/*      */     
/*  336 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTextLength()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  343 */     if (this._currToken != null) {
/*  344 */       switch (this._currToken)
/*      */       {
/*      */       case FIELD_NAME: 
/*  347 */         return this._parsingContext.getCurrentName().length();
/*      */       case VALUE_STRING: 
/*  349 */         if (this._tokenIncomplete) {
/*  350 */           this._tokenIncomplete = false;
/*  351 */           _finishString();
/*      */         }
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*      */       case VALUE_NUMBER_FLOAT: 
/*  356 */         return this._textBuffer.size();
/*      */       }
/*      */       
/*  359 */       return this._currToken.asCharArray().length;
/*      */     }
/*      */     
/*  362 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTextOffset()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  369 */     if (this._currToken != null) {
/*  370 */       switch (this._currToken) {
/*      */       case FIELD_NAME: 
/*  372 */         return 0;
/*      */       case VALUE_STRING: 
/*  374 */         if (this._tokenIncomplete) {
/*  375 */           this._tokenIncomplete = false;
/*  376 */           _finishString();
/*      */         }
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*      */       case VALUE_NUMBER_FLOAT: 
/*  381 */         return this._textBuffer.getTextOffset();
/*      */       }
/*      */     }
/*  384 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getBinaryValue(Base64Variant b64variant)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  391 */     if ((this._currToken != JsonToken.VALUE_STRING) && ((this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT) || (this._binaryValue == null)))
/*      */     {
/*  393 */       _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  398 */     if (this._tokenIncomplete) {
/*      */       try {
/*  400 */         this._binaryValue = _decodeBase64(b64variant);
/*      */       } catch (IllegalArgumentException iae) {
/*  402 */         throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  407 */       this._tokenIncomplete = false;
/*      */     }
/*  409 */     else if (this._binaryValue == null) {
/*  410 */       ByteArrayBuilder builder = _getByteArrayBuilder();
/*  411 */       _decodeBase64(getText(), builder, b64variant);
/*  412 */       this._binaryValue = builder.toByteArray();
/*      */     }
/*      */     
/*  415 */     return this._binaryValue;
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
/*  432 */     this._numTypesValid = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  437 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  438 */       return _nextAfterName();
/*      */     }
/*  440 */     if (this._tokenIncomplete) {
/*  441 */       _skipString();
/*      */     }
/*      */     
/*  444 */     int i = _skipWSOrEnd();
/*  445 */     if (i < 0)
/*      */     {
/*      */ 
/*      */ 
/*  449 */       close();
/*  450 */       return this._currToken = null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  456 */     this._tokenInputTotal = (this._currInputProcessed + this._inputPtr - 1L);
/*  457 */     this._tokenInputRow = this._currInputRow;
/*  458 */     this._tokenInputCol = (this._inputPtr - this._currInputRowStart - 1);
/*      */     
/*      */ 
/*  461 */     this._binaryValue = null;
/*      */     
/*      */ 
/*  464 */     if (i == 93) {
/*  465 */       if (!this._parsingContext.inArray()) {
/*  466 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  468 */       this._parsingContext = this._parsingContext.getParent();
/*  469 */       return this._currToken = JsonToken.END_ARRAY;
/*      */     }
/*  471 */     if (i == 125) {
/*  472 */       if (!this._parsingContext.inObject()) {
/*  473 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  475 */       this._parsingContext = this._parsingContext.getParent();
/*  476 */       return this._currToken = JsonToken.END_OBJECT;
/*      */     }
/*      */     
/*      */ 
/*  480 */     if (this._parsingContext.expectComma()) {
/*  481 */       if (i != 44) {
/*  482 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
/*      */       }
/*  484 */       i = _skipWS();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  491 */     if (!this._parsingContext.inObject()) {
/*  492 */       return _nextTokenNotInObject(i);
/*      */     }
/*      */     
/*  495 */     Name n = _parseFieldName(i);
/*  496 */     this._parsingContext.setCurrentName(n.getName());
/*  497 */     this._currToken = JsonToken.FIELD_NAME;
/*  498 */     i = _skipWS();
/*  499 */     if (i != 58) {
/*  500 */       _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */     }
/*  502 */     i = _skipWS();
/*      */     
/*      */ 
/*  505 */     if (i == 34) {
/*  506 */       this._tokenIncomplete = true;
/*  507 */       this._nextToken = JsonToken.VALUE_STRING;
/*  508 */       return this._currToken;
/*      */     }
/*      */     
/*      */     JsonToken t;
/*  512 */     switch (i) {
/*      */     case 91: 
/*  514 */       t = JsonToken.START_ARRAY;
/*  515 */       break;
/*      */     case 123: 
/*  517 */       t = JsonToken.START_OBJECT;
/*  518 */       break;
/*      */     
/*      */ 
/*      */     case 93: 
/*      */     case 125: 
/*  523 */       _reportUnexpectedChar(i, "expected a value");
/*      */     case 116: 
/*  525 */       _matchToken("true", 1);
/*  526 */       t = JsonToken.VALUE_TRUE;
/*  527 */       break;
/*      */     case 102: 
/*  529 */       _matchToken("false", 1);
/*  530 */       t = JsonToken.VALUE_FALSE;
/*  531 */       break;
/*      */     case 110: 
/*  533 */       _matchToken("null", 1);
/*  534 */       t = JsonToken.VALUE_NULL;
/*  535 */       break;
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
/*  552 */       t = parseNumberText(i);
/*  553 */       break;
/*      */     default: 
/*  555 */       t = _handleUnexpectedValue(i);
/*      */     }
/*  557 */     this._nextToken = t;
/*  558 */     return this._currToken;
/*      */   }
/*      */   
/*      */   private final JsonToken _nextTokenNotInObject(int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  564 */     if (i == 34) {
/*  565 */       this._tokenIncomplete = true;
/*  566 */       return this._currToken = JsonToken.VALUE_STRING;
/*      */     }
/*  568 */     switch (i) {
/*      */     case 91: 
/*  570 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  571 */       return this._currToken = JsonToken.START_ARRAY;
/*      */     case 123: 
/*  573 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*  574 */       return this._currToken = JsonToken.START_OBJECT;
/*      */     
/*      */ 
/*      */     case 93: 
/*      */     case 125: 
/*  579 */       _reportUnexpectedChar(i, "expected a value");
/*      */     case 116: 
/*  581 */       _matchToken("true", 1);
/*  582 */       return this._currToken = JsonToken.VALUE_TRUE;
/*      */     case 102: 
/*  584 */       _matchToken("false", 1);
/*  585 */       return this._currToken = JsonToken.VALUE_FALSE;
/*      */     case 110: 
/*  587 */       _matchToken("null", 1);
/*  588 */       return this._currToken = JsonToken.VALUE_NULL;
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
/*  604 */       return this._currToken = parseNumberText(i);
/*      */     }
/*  606 */     return this._currToken = _handleUnexpectedValue(i);
/*      */   }
/*      */   
/*      */   private final JsonToken _nextAfterName()
/*      */   {
/*  611 */     this._nameCopied = false;
/*  612 */     JsonToken t = this._nextToken;
/*  613 */     this._nextToken = null;
/*      */     
/*  615 */     if (t == JsonToken.START_ARRAY) {
/*  616 */       this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  617 */     } else if (t == JsonToken.START_OBJECT) {
/*  618 */       this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */     }
/*  620 */     return this._currToken = t;
/*      */   }
/*      */   
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  626 */     super.close();
/*      */     
/*  628 */     this._symbols.release();
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
/*      */   public boolean nextFieldName(org.codehaus.jackson.SerializableString str)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  643 */     this._numTypesValid = 0;
/*  644 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  645 */       _nextAfterName();
/*  646 */       return false;
/*      */     }
/*  648 */     if (this._tokenIncomplete) {
/*  649 */       _skipString();
/*      */     }
/*  651 */     int i = _skipWSOrEnd();
/*  652 */     if (i < 0) {
/*  653 */       close();
/*  654 */       this._currToken = null;
/*  655 */       return false;
/*      */     }
/*  657 */     this._tokenInputTotal = (this._currInputProcessed + this._inputPtr - 1L);
/*  658 */     this._tokenInputRow = this._currInputRow;
/*  659 */     this._tokenInputCol = (this._inputPtr - this._currInputRowStart - 1);
/*      */     
/*      */ 
/*  662 */     this._binaryValue = null;
/*      */     
/*      */ 
/*  665 */     if (i == 93) {
/*  666 */       if (!this._parsingContext.inArray()) {
/*  667 */         _reportMismatchedEndMarker(i, '}');
/*      */       }
/*  669 */       this._parsingContext = this._parsingContext.getParent();
/*  670 */       this._currToken = JsonToken.END_ARRAY;
/*  671 */       return false;
/*      */     }
/*  673 */     if (i == 125) {
/*  674 */       if (!this._parsingContext.inObject()) {
/*  675 */         _reportMismatchedEndMarker(i, ']');
/*      */       }
/*  677 */       this._parsingContext = this._parsingContext.getParent();
/*  678 */       this._currToken = JsonToken.END_OBJECT;
/*  679 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  683 */     if (this._parsingContext.expectComma()) {
/*  684 */       if (i != 44) {
/*  685 */         _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
/*      */       }
/*  687 */       i = _skipWS();
/*      */     }
/*      */     
/*  690 */     if (!this._parsingContext.inObject()) {
/*  691 */       _nextTokenNotInObject(i);
/*  692 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  696 */     if (i == 34)
/*      */     {
/*  698 */       byte[] nameBytes = str.asQuotedUTF8();
/*  699 */       int len = nameBytes.length;
/*  700 */       if (this._inputPtr + len < this._inputEnd)
/*      */       {
/*  702 */         int end = this._inputPtr + len;
/*  703 */         if (this._inputBuffer[end] == 34) {
/*  704 */           int offset = 0;
/*  705 */           int ptr = this._inputPtr;
/*      */           for (;;) {
/*  707 */             if (offset == len) {
/*  708 */               this._inputPtr = (end + 1);
/*      */               
/*  710 */               this._parsingContext.setCurrentName(str.getValue());
/*  711 */               this._currToken = JsonToken.FIELD_NAME;
/*      */               
/*  713 */               _isNextTokenNameYes();
/*  714 */               return true;
/*      */             }
/*  716 */             if (nameBytes[offset] != this._inputBuffer[(ptr + offset)]) {
/*      */               break;
/*      */             }
/*  719 */             offset++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  724 */     _isNextTokenNameNo(i);
/*  725 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _isNextTokenNameYes()
/*      */     throws IOException, JsonParseException
/*      */   {
/*      */     int i;
/*  733 */     if ((this._inputPtr < this._inputEnd) && (this._inputBuffer[this._inputPtr] == 58)) {
/*  734 */       this._inputPtr += 1;
/*  735 */       int i = this._inputBuffer[(this._inputPtr++)];
/*  736 */       if (i == 34) {
/*  737 */         this._tokenIncomplete = true;
/*  738 */         this._nextToken = JsonToken.VALUE_STRING;
/*  739 */         return;
/*      */       }
/*  741 */       if (i == 123) {
/*  742 */         this._nextToken = JsonToken.START_OBJECT;
/*  743 */         return;
/*      */       }
/*  745 */       if (i == 91) {
/*  746 */         this._nextToken = JsonToken.START_ARRAY;
/*  747 */         return;
/*      */       }
/*  749 */       i &= 0xFF;
/*  750 */       if ((i <= 32) || (i == 47)) {
/*  751 */         this._inputPtr -= 1;
/*  752 */         i = _skipWS();
/*      */       }
/*      */     } else {
/*  755 */       i = _skipColon();
/*      */     }
/*  757 */     switch (i) {
/*      */     case 34: 
/*  759 */       this._tokenIncomplete = true;
/*  760 */       this._nextToken = JsonToken.VALUE_STRING;
/*  761 */       return;
/*      */     case 91: 
/*  763 */       this._nextToken = JsonToken.START_ARRAY;
/*  764 */       return;
/*      */     case 123: 
/*  766 */       this._nextToken = JsonToken.START_OBJECT;
/*  767 */       return;
/*      */     case 93: 
/*      */     case 125: 
/*  770 */       _reportUnexpectedChar(i, "expected a value");
/*      */     case 116: 
/*  772 */       _matchToken("true", 1);
/*  773 */       this._nextToken = JsonToken.VALUE_TRUE;
/*  774 */       return;
/*      */     case 102: 
/*  776 */       _matchToken("false", 1);
/*  777 */       this._nextToken = JsonToken.VALUE_FALSE;
/*  778 */       return;
/*      */     case 110: 
/*  780 */       _matchToken("null", 1);
/*  781 */       this._nextToken = JsonToken.VALUE_NULL;
/*  782 */       return;
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
/*  794 */       this._nextToken = parseNumberText(i);
/*  795 */       return;
/*      */     }
/*  797 */     this._nextToken = _handleUnexpectedValue(i);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final void _isNextTokenNameNo(int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  805 */     Name n = _parseFieldName(i);
/*  806 */     this._parsingContext.setCurrentName(n.getName());
/*  807 */     this._currToken = JsonToken.FIELD_NAME;
/*  808 */     i = _skipWS();
/*  809 */     if (i != 58) {
/*  810 */       _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */     }
/*  812 */     i = _skipWS();
/*      */     
/*      */ 
/*  815 */     if (i == 34) {
/*  816 */       this._tokenIncomplete = true;
/*  817 */       this._nextToken = JsonToken.VALUE_STRING; return;
/*      */     }
/*      */     
/*      */     JsonToken t;
/*      */     
/*  822 */     switch (i) {
/*      */     case 91: 
/*  824 */       t = JsonToken.START_ARRAY;
/*  825 */       break;
/*      */     case 123: 
/*  827 */       t = JsonToken.START_OBJECT;
/*  828 */       break;
/*      */     case 93: 
/*      */     case 125: 
/*  831 */       _reportUnexpectedChar(i, "expected a value");
/*      */     case 116: 
/*  833 */       _matchToken("true", 1);
/*  834 */       t = JsonToken.VALUE_TRUE;
/*  835 */       break;
/*      */     case 102: 
/*  837 */       _matchToken("false", 1);
/*  838 */       t = JsonToken.VALUE_FALSE;
/*  839 */       break;
/*      */     case 110: 
/*  841 */       _matchToken("null", 1);
/*  842 */       t = JsonToken.VALUE_NULL;
/*  843 */       break;
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
/*  856 */       t = parseNumberText(i);
/*  857 */       break;
/*      */     default: 
/*  859 */       t = _handleUnexpectedValue(i);
/*      */     }
/*  861 */     this._nextToken = t;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String nextTextValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  869 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  870 */       this._nameCopied = false;
/*  871 */       JsonToken t = this._nextToken;
/*  872 */       this._nextToken = null;
/*  873 */       this._currToken = t;
/*  874 */       if (t == JsonToken.VALUE_STRING) {
/*  875 */         if (this._tokenIncomplete) {
/*  876 */           this._tokenIncomplete = false;
/*  877 */           _finishString();
/*      */         }
/*  879 */         return this._textBuffer.contentsAsString();
/*      */       }
/*  881 */       if (t == JsonToken.START_ARRAY) {
/*  882 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  883 */       } else if (t == JsonToken.START_OBJECT) {
/*  884 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  886 */       return null;
/*      */     }
/*      */     
/*  889 */     return nextToken() == JsonToken.VALUE_STRING ? getText() : null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int nextIntValue(int defaultValue)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  897 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  898 */       this._nameCopied = false;
/*  899 */       JsonToken t = this._nextToken;
/*  900 */       this._nextToken = null;
/*  901 */       this._currToken = t;
/*  902 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  903 */         return getIntValue();
/*      */       }
/*  905 */       if (t == JsonToken.START_ARRAY) {
/*  906 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  907 */       } else if (t == JsonToken.START_OBJECT) {
/*  908 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  910 */       return defaultValue;
/*      */     }
/*      */     
/*  913 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long nextLongValue(long defaultValue)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  921 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  922 */       this._nameCopied = false;
/*  923 */       JsonToken t = this._nextToken;
/*  924 */       this._nextToken = null;
/*  925 */       this._currToken = t;
/*  926 */       if (t == JsonToken.VALUE_NUMBER_INT) {
/*  927 */         return getLongValue();
/*      */       }
/*  929 */       if (t == JsonToken.START_ARRAY) {
/*  930 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  931 */       } else if (t == JsonToken.START_OBJECT) {
/*  932 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  934 */       return defaultValue;
/*      */     }
/*      */     
/*  937 */     return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : defaultValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Boolean nextBooleanValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  945 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  946 */       this._nameCopied = false;
/*  947 */       JsonToken t = this._nextToken;
/*  948 */       this._nextToken = null;
/*  949 */       this._currToken = t;
/*  950 */       if (t == JsonToken.VALUE_TRUE) {
/*  951 */         return Boolean.TRUE;
/*      */       }
/*  953 */       if (t == JsonToken.VALUE_FALSE) {
/*  954 */         return Boolean.FALSE;
/*      */       }
/*  956 */       if (t == JsonToken.START_ARRAY) {
/*  957 */         this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
/*  958 */       } else if (t == JsonToken.START_OBJECT) {
/*  959 */         this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
/*      */       }
/*  961 */       return null;
/*      */     }
/*  963 */     switch (nextToken()) {
/*      */     case VALUE_TRUE: 
/*  965 */       return Boolean.TRUE;
/*      */     case VALUE_FALSE: 
/*  967 */       return Boolean.FALSE;
/*      */     }
/*  969 */     return null;
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
/*      */   protected final JsonToken parseNumberText(int c)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  997 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*  998 */     int outPtr = 0;
/*  999 */     boolean negative = c == 45;
/*      */     
/*      */ 
/* 1002 */     if (negative) {
/* 1003 */       outBuf[(outPtr++)] = '-';
/*      */       
/* 1005 */       if (this._inputPtr >= this._inputEnd) {
/* 1006 */         loadMoreGuaranteed();
/*      */       }
/* 1008 */       c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       
/* 1010 */       if ((c < 48) || (c > 57)) {
/* 1011 */         return _handleInvalidNumberStart(c, true);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1016 */     if (c == 48) {
/* 1017 */       c = _verifyNoLeadingZeroes();
/*      */     }
/*      */     
/*      */ 
/* 1021 */     outBuf[(outPtr++)] = ((char)c);
/* 1022 */     int intLen = 1;
/*      */     
/*      */ 
/* 1025 */     int end = this._inputPtr + outBuf.length;
/* 1026 */     if (end > this._inputEnd) {
/* 1027 */       end = this._inputEnd;
/*      */     }
/*      */     
/*      */     for (;;)
/*      */     {
/* 1032 */       if (this._inputPtr >= end)
/*      */       {
/* 1034 */         return _parserNumber2(outBuf, outPtr, negative, intLen);
/*      */       }
/* 1036 */       c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1037 */       if ((c < 48) || (c > 57)) {
/*      */         break;
/*      */       }
/* 1040 */       intLen++;
/* 1041 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 1043 */     if ((c == 46) || (c == 101) || (c == 69)) {
/* 1044 */       return _parseFloatText(outBuf, outPtr, c, negative, intLen);
/*      */     }
/*      */     
/* 1047 */     this._inputPtr -= 1;
/* 1048 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/*      */ 
/* 1051 */     return resetInt(negative, intLen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final JsonToken _parserNumber2(char[] outBuf, int outPtr, boolean negative, int intPartLength)
/*      */     throws IOException, JsonParseException
/*      */   {
/*      */     for (;;)
/*      */     {
/* 1064 */       if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/* 1065 */         this._textBuffer.setCurrentLength(outPtr);
/* 1066 */         return resetInt(negative, intPartLength);
/*      */       }
/* 1068 */       int c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1069 */       if ((c > 57) || (c < 48)) {
/* 1070 */         if ((c != 46) && (c != 101) && (c != 69)) break;
/* 1071 */         return _parseFloatText(outBuf, outPtr, c, negative, intPartLength);
/*      */       }
/*      */       
/*      */ 
/* 1075 */       if (outPtr >= outBuf.length) {
/* 1076 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1077 */         outPtr = 0;
/*      */       }
/* 1079 */       outBuf[(outPtr++)] = ((char)c);
/* 1080 */       intPartLength++;
/*      */     }
/* 1082 */     this._inputPtr -= 1;
/* 1083 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/*      */ 
/* 1086 */     return resetInt(negative, intPartLength);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _verifyNoLeadingZeroes()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1098 */     if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/* 1099 */       return 48;
/*      */     }
/* 1101 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/*      */     
/* 1103 */     if ((ch < 48) || (ch > 57)) {
/* 1104 */       return 48;
/*      */     }
/*      */     
/* 1107 */     if (!isEnabled(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
/* 1108 */       reportInvalidNumber("Leading zeroes not allowed");
/*      */     }
/*      */     
/* 1111 */     this._inputPtr += 1;
/* 1112 */     if (ch == 48) {
/* 1113 */       while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 1114 */         ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 1115 */         if ((ch < 48) || (ch > 57)) {
/* 1116 */           return 48;
/*      */         }
/* 1118 */         this._inputPtr += 1;
/* 1119 */         if (ch != 48) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1124 */     return ch;
/*      */   }
/*      */   
/*      */ 
/*      */   private final JsonToken _parseFloatText(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1131 */     int fractLen = 0;
/* 1132 */     boolean eof = false;
/*      */     
/*      */ 
/* 1135 */     if (c == 46) {
/* 1136 */       outBuf[(outPtr++)] = ((char)c);
/*      */       
/*      */       for (;;)
/*      */       {
/* 1140 */         if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/* 1141 */           eof = true;
/* 1142 */           break;
/*      */         }
/* 1144 */         c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1145 */         if ((c < 48) || (c > 57)) {
/*      */           break;
/*      */         }
/* 1148 */         fractLen++;
/* 1149 */         if (outPtr >= outBuf.length) {
/* 1150 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1151 */           outPtr = 0;
/*      */         }
/* 1153 */         outBuf[(outPtr++)] = ((char)c);
/*      */       }
/*      */       
/* 1156 */       if (fractLen == 0) {
/* 1157 */         reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/* 1161 */     int expLen = 0;
/* 1162 */     if ((c == 101) || (c == 69)) {
/* 1163 */       if (outPtr >= outBuf.length) {
/* 1164 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1165 */         outPtr = 0;
/*      */       }
/* 1167 */       outBuf[(outPtr++)] = ((char)c);
/*      */       
/* 1169 */       if (this._inputPtr >= this._inputEnd) {
/* 1170 */         loadMoreGuaranteed();
/*      */       }
/* 1172 */       c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       
/* 1174 */       if ((c == 45) || (c == 43)) {
/* 1175 */         if (outPtr >= outBuf.length) {
/* 1176 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1177 */           outPtr = 0;
/*      */         }
/* 1179 */         outBuf[(outPtr++)] = ((char)c);
/*      */         
/* 1181 */         if (this._inputPtr >= this._inputEnd) {
/* 1182 */           loadMoreGuaranteed();
/*      */         }
/* 1184 */         c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       }
/*      */       
/*      */ 
/* 1188 */       while ((c <= 57) && (c >= 48)) {
/* 1189 */         expLen++;
/* 1190 */         if (outPtr >= outBuf.length) {
/* 1191 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1192 */           outPtr = 0;
/*      */         }
/* 1194 */         outBuf[(outPtr++)] = ((char)c);
/* 1195 */         if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/* 1196 */           eof = true;
/* 1197 */           break;
/*      */         }
/* 1199 */         c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */       }
/*      */       
/* 1202 */       if (expLen == 0) {
/* 1203 */         reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1208 */     if (!eof) {
/* 1209 */       this._inputPtr -= 1;
/*      */     }
/* 1211 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/*      */ 
/* 1214 */     return resetFloat(negative, integerPartLength, fractLen, expLen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Name _parseFieldName(int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1226 */     if (i != 34) {
/* 1227 */       return _handleUnusualFieldName(i);
/*      */     }
/*      */     
/* 1230 */     if (this._inputPtr + 9 > this._inputEnd) {
/* 1231 */       return slowParseFieldName();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1240 */     byte[] input = this._inputBuffer;
/* 1241 */     int[] codes = sInputCodesLatin1;
/*      */     
/* 1243 */     int q = input[(this._inputPtr++)] & 0xFF;
/*      */     
/* 1245 */     if (codes[q] == 0) {
/* 1246 */       i = input[(this._inputPtr++)] & 0xFF;
/* 1247 */       if (codes[i] == 0) {
/* 1248 */         q = q << 8 | i;
/* 1249 */         i = input[(this._inputPtr++)] & 0xFF;
/* 1250 */         if (codes[i] == 0) {
/* 1251 */           q = q << 8 | i;
/* 1252 */           i = input[(this._inputPtr++)] & 0xFF;
/* 1253 */           if (codes[i] == 0) {
/* 1254 */             q = q << 8 | i;
/* 1255 */             i = input[(this._inputPtr++)] & 0xFF;
/* 1256 */             if (codes[i] == 0) {
/* 1257 */               this._quad1 = q;
/* 1258 */               return parseMediumFieldName(i, codes);
/*      */             }
/* 1260 */             if (i == 34) {
/* 1261 */               return findName(q, 4);
/*      */             }
/* 1263 */             return parseFieldName(q, i, 4);
/*      */           }
/* 1265 */           if (i == 34) {
/* 1266 */             return findName(q, 3);
/*      */           }
/* 1268 */           return parseFieldName(q, i, 3);
/*      */         }
/* 1270 */         if (i == 34) {
/* 1271 */           return findName(q, 2);
/*      */         }
/* 1273 */         return parseFieldName(q, i, 2);
/*      */       }
/* 1275 */       if (i == 34) {
/* 1276 */         return findName(q, 1);
/*      */       }
/* 1278 */       return parseFieldName(q, i, 1);
/*      */     }
/* 1280 */     if (q == 34) {
/* 1281 */       return BytesToNameCanonicalizer.getEmptyName();
/*      */     }
/* 1283 */     return parseFieldName(0, q, 0);
/*      */   }
/*      */   
/*      */ 
/*      */   protected final Name parseMediumFieldName(int q2, int[] codes)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1290 */     int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1291 */     if (codes[i] != 0) {
/* 1292 */       if (i == 34) {
/* 1293 */         return findName(this._quad1, q2, 1);
/*      */       }
/* 1295 */       return parseFieldName(this._quad1, q2, i, 1);
/*      */     }
/* 1297 */     q2 = q2 << 8 | i;
/* 1298 */     i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1299 */     if (codes[i] != 0) {
/* 1300 */       if (i == 34) {
/* 1301 */         return findName(this._quad1, q2, 2);
/*      */       }
/* 1303 */       return parseFieldName(this._quad1, q2, i, 2);
/*      */     }
/* 1305 */     q2 = q2 << 8 | i;
/* 1306 */     i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1307 */     if (codes[i] != 0) {
/* 1308 */       if (i == 34) {
/* 1309 */         return findName(this._quad1, q2, 3);
/*      */       }
/* 1311 */       return parseFieldName(this._quad1, q2, i, 3);
/*      */     }
/* 1313 */     q2 = q2 << 8 | i;
/* 1314 */     i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1315 */     if (codes[i] != 0) {
/* 1316 */       if (i == 34) {
/* 1317 */         return findName(this._quad1, q2, 4);
/*      */       }
/* 1319 */       return parseFieldName(this._quad1, q2, i, 4);
/*      */     }
/* 1321 */     this._quadBuffer[0] = this._quad1;
/* 1322 */     this._quadBuffer[1] = q2;
/* 1323 */     return parseLongFieldName(i);
/*      */   }
/*      */   
/*      */ 
/*      */   protected Name parseLongFieldName(int q)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1330 */     int[] codes = sInputCodesLatin1;
/* 1331 */     int qlen = 2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 1338 */       if (this._inputEnd - this._inputPtr < 4) {
/* 1339 */         return parseEscapedFieldName(this._quadBuffer, qlen, 0, q, 0);
/*      */       }
/*      */       
/*      */ 
/* 1343 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1344 */       if (codes[i] != 0) {
/* 1345 */         if (i == 34) {
/* 1346 */           return findName(this._quadBuffer, qlen, q, 1);
/*      */         }
/* 1348 */         return parseEscapedFieldName(this._quadBuffer, qlen, q, i, 1);
/*      */       }
/*      */       
/* 1351 */       q = q << 8 | i;
/* 1352 */       i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1353 */       if (codes[i] != 0) {
/* 1354 */         if (i == 34) {
/* 1355 */           return findName(this._quadBuffer, qlen, q, 2);
/*      */         }
/* 1357 */         return parseEscapedFieldName(this._quadBuffer, qlen, q, i, 2);
/*      */       }
/*      */       
/* 1360 */       q = q << 8 | i;
/* 1361 */       i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1362 */       if (codes[i] != 0) {
/* 1363 */         if (i == 34) {
/* 1364 */           return findName(this._quadBuffer, qlen, q, 3);
/*      */         }
/* 1366 */         return parseEscapedFieldName(this._quadBuffer, qlen, q, i, 3);
/*      */       }
/*      */       
/* 1369 */       q = q << 8 | i;
/* 1370 */       i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1371 */       if (codes[i] != 0) {
/* 1372 */         if (i == 34) {
/* 1373 */           return findName(this._quadBuffer, qlen, q, 4);
/*      */         }
/* 1375 */         return parseEscapedFieldName(this._quadBuffer, qlen, q, i, 4);
/*      */       }
/*      */       
/*      */ 
/* 1379 */       if (qlen >= this._quadBuffer.length) {
/* 1380 */         this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
/*      */       }
/* 1382 */       this._quadBuffer[(qlen++)] = q;
/* 1383 */       q = i;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Name slowParseFieldName()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1395 */     if ((this._inputPtr >= this._inputEnd) && 
/* 1396 */       (!loadMore())) {
/* 1397 */       _reportInvalidEOF(": was expecting closing '\"' for name");
/*      */     }
/*      */     
/* 1400 */     int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1401 */     if (i == 34) {
/* 1402 */       return BytesToNameCanonicalizer.getEmptyName();
/*      */     }
/* 1404 */     return parseEscapedFieldName(this._quadBuffer, 0, 0, i, 0);
/*      */   }
/*      */   
/*      */   private final Name parseFieldName(int q1, int ch, int lastQuadBytes)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1410 */     return parseEscapedFieldName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final Name parseFieldName(int q1, int q2, int ch, int lastQuadBytes)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1416 */     this._quadBuffer[0] = q1;
/* 1417 */     return parseEscapedFieldName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
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
/*      */   protected Name parseEscapedFieldName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1436 */     int[] codes = sInputCodesLatin1;
/*      */     for (;;)
/*      */     {
/* 1439 */       if (codes[ch] != 0) {
/* 1440 */         if (ch == 34) {
/*      */           break;
/*      */         }
/*      */         
/* 1444 */         if (ch != 92)
/*      */         {
/* 1446 */           _throwUnquotedSpace(ch, "name");
/*      */         }
/*      */         else {
/* 1449 */           ch = _decodeEscaped();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1456 */         if (ch > 127)
/*      */         {
/* 1458 */           if (currQuadBytes >= 4) {
/* 1459 */             if (qlen >= quads.length) {
/* 1460 */               this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */             }
/* 1462 */             quads[(qlen++)] = currQuad;
/* 1463 */             currQuad = 0;
/* 1464 */             currQuadBytes = 0;
/*      */           }
/* 1466 */           if (ch < 2048) {
/* 1467 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1468 */             currQuadBytes++;
/*      */           }
/*      */           else {
/* 1471 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1472 */             currQuadBytes++;
/*      */             
/* 1474 */             if (currQuadBytes >= 4) {
/* 1475 */               if (qlen >= quads.length) {
/* 1476 */                 this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */               }
/* 1478 */               quads[(qlen++)] = currQuad;
/* 1479 */               currQuad = 0;
/* 1480 */               currQuadBytes = 0;
/*      */             }
/* 1482 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1483 */             currQuadBytes++;
/*      */           }
/*      */           
/* 1486 */           ch = 0x80 | ch & 0x3F;
/*      */         }
/*      */       }
/*      */       
/* 1490 */       if (currQuadBytes < 4) {
/* 1491 */         currQuadBytes++;
/* 1492 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1494 */         if (qlen >= quads.length) {
/* 1495 */           this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */         }
/* 1497 */         quads[(qlen++)] = currQuad;
/* 1498 */         currQuad = ch;
/* 1499 */         currQuadBytes = 1;
/*      */       }
/* 1501 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1502 */         (!loadMore())) {
/* 1503 */         _reportInvalidEOF(" in field name");
/*      */       }
/*      */       
/* 1506 */       ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */     }
/*      */     
/* 1509 */     if (currQuadBytes > 0) {
/* 1510 */       if (qlen >= quads.length) {
/* 1511 */         this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */       }
/* 1513 */       quads[(qlen++)] = currQuad;
/*      */     }
/* 1515 */     Name name = this._symbols.findName(quads, qlen);
/* 1516 */     if (name == null) {
/* 1517 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1519 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Name _handleUnusualFieldName(int ch)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1532 */     if ((ch == 39) && (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES))) {
/* 1533 */       return _parseApostropheFieldName();
/*      */     }
/*      */     
/* 1536 */     if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
/* 1537 */       _reportUnexpectedChar(ch, "was expecting double-quote to start field name");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1543 */     int[] codes = CharTypes.getInputCodeUtf8JsNames();
/*      */     
/* 1545 */     if (codes[ch] != 0) {
/* 1546 */       _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1553 */     int[] quads = this._quadBuffer;
/* 1554 */     int qlen = 0;
/* 1555 */     int currQuad = 0;
/* 1556 */     int currQuadBytes = 0;
/*      */     
/*      */     for (;;)
/*      */     {
/* 1560 */       if (currQuadBytes < 4) {
/* 1561 */         currQuadBytes++;
/* 1562 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1564 */         if (qlen >= quads.length) {
/* 1565 */           this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */         }
/* 1567 */         quads[(qlen++)] = currQuad;
/* 1568 */         currQuad = ch;
/* 1569 */         currQuadBytes = 1;
/*      */       }
/* 1571 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1572 */         (!loadMore())) {
/* 1573 */         _reportInvalidEOF(" in field name");
/*      */       }
/*      */       
/* 1576 */       ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 1577 */       if (codes[ch] != 0) {
/*      */         break;
/*      */       }
/* 1580 */       this._inputPtr += 1;
/*      */     }
/*      */     
/* 1583 */     if (currQuadBytes > 0) {
/* 1584 */       if (qlen >= quads.length) {
/* 1585 */         this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */       }
/* 1587 */       quads[(qlen++)] = currQuad;
/*      */     }
/* 1589 */     Name name = this._symbols.findName(quads, qlen);
/* 1590 */     if (name == null) {
/* 1591 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1593 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Name _parseApostropheFieldName()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1604 */     if ((this._inputPtr >= this._inputEnd) && 
/* 1605 */       (!loadMore())) {
/* 1606 */       _reportInvalidEOF(": was expecting closing ''' for name");
/*      */     }
/*      */     
/* 1609 */     int ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 1610 */     if (ch == 39) {
/* 1611 */       return BytesToNameCanonicalizer.getEmptyName();
/*      */     }
/* 1613 */     int[] quads = this._quadBuffer;
/* 1614 */     int qlen = 0;
/* 1615 */     int currQuad = 0;
/* 1616 */     int currQuadBytes = 0;
/*      */     
/*      */ 
/*      */ 
/* 1620 */     int[] codes = sInputCodesLatin1;
/*      */     
/*      */ 
/* 1623 */     while (ch != 39)
/*      */     {
/*      */ 
/*      */ 
/* 1627 */       if ((ch != 34) && (codes[ch] != 0)) {
/* 1628 */         if (ch != 92)
/*      */         {
/*      */ 
/* 1631 */           _throwUnquotedSpace(ch, "name");
/*      */         }
/*      */         else {
/* 1634 */           ch = _decodeEscaped();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1641 */         if (ch > 127)
/*      */         {
/* 1643 */           if (currQuadBytes >= 4) {
/* 1644 */             if (qlen >= quads.length) {
/* 1645 */               this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */             }
/* 1647 */             quads[(qlen++)] = currQuad;
/* 1648 */             currQuad = 0;
/* 1649 */             currQuadBytes = 0;
/*      */           }
/* 1651 */           if (ch < 2048) {
/* 1652 */             currQuad = currQuad << 8 | 0xC0 | ch >> 6;
/* 1653 */             currQuadBytes++;
/*      */           }
/*      */           else {
/* 1656 */             currQuad = currQuad << 8 | 0xE0 | ch >> 12;
/* 1657 */             currQuadBytes++;
/*      */             
/* 1659 */             if (currQuadBytes >= 4) {
/* 1660 */               if (qlen >= quads.length) {
/* 1661 */                 this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */               }
/* 1663 */               quads[(qlen++)] = currQuad;
/* 1664 */               currQuad = 0;
/* 1665 */               currQuadBytes = 0;
/*      */             }
/* 1667 */             currQuad = currQuad << 8 | 0x80 | ch >> 6 & 0x3F;
/* 1668 */             currQuadBytes++;
/*      */           }
/*      */           
/* 1671 */           ch = 0x80 | ch & 0x3F;
/*      */         }
/*      */       }
/*      */       
/* 1675 */       if (currQuadBytes < 4) {
/* 1676 */         currQuadBytes++;
/* 1677 */         currQuad = currQuad << 8 | ch;
/*      */       } else {
/* 1679 */         if (qlen >= quads.length) {
/* 1680 */           this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */         }
/* 1682 */         quads[(qlen++)] = currQuad;
/* 1683 */         currQuad = ch;
/* 1684 */         currQuadBytes = 1;
/*      */       }
/* 1686 */       if ((this._inputPtr >= this._inputEnd) && 
/* 1687 */         (!loadMore())) {
/* 1688 */         _reportInvalidEOF(" in field name");
/*      */       }
/*      */       
/* 1691 */       ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */     }
/*      */     
/* 1694 */     if (currQuadBytes > 0) {
/* 1695 */       if (qlen >= quads.length) {
/* 1696 */         this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */       }
/* 1698 */       quads[(qlen++)] = currQuad;
/*      */     }
/* 1700 */     Name name = this._symbols.findName(quads, qlen);
/* 1701 */     if (name == null) {
/* 1702 */       name = addName(quads, qlen, currQuadBytes);
/*      */     }
/* 1704 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final Name findName(int q1, int lastQuadBytes)
/*      */     throws JsonParseException
/*      */   {
/* 1717 */     Name name = this._symbols.findName(q1);
/* 1718 */     if (name != null) {
/* 1719 */       return name;
/*      */     }
/*      */     
/* 1722 */     this._quadBuffer[0] = q1;
/* 1723 */     return addName(this._quadBuffer, 1, lastQuadBytes);
/*      */   }
/*      */   
/*      */ 
/*      */   private final Name findName(int q1, int q2, int lastQuadBytes)
/*      */     throws JsonParseException
/*      */   {
/* 1730 */     Name name = this._symbols.findName(q1, q2);
/* 1731 */     if (name != null) {
/* 1732 */       return name;
/*      */     }
/*      */     
/* 1735 */     this._quadBuffer[0] = q1;
/* 1736 */     this._quadBuffer[1] = q2;
/* 1737 */     return addName(this._quadBuffer, 2, lastQuadBytes);
/*      */   }
/*      */   
/*      */   private final Name findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes)
/*      */     throws JsonParseException
/*      */   {
/* 1743 */     if (qlen >= quads.length) {
/* 1744 */       this._quadBuffer = (quads = growArrayBy(quads, quads.length));
/*      */     }
/* 1746 */     quads[(qlen++)] = lastQuad;
/* 1747 */     Name name = this._symbols.findName(quads, qlen);
/* 1748 */     if (name == null) {
/* 1749 */       return addName(quads, qlen, lastQuadBytes);
/*      */     }
/* 1751 */     return name;
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
/*      */   private final Name addName(int[] quads, int qlen, int lastQuadBytes)
/*      */     throws JsonParseException
/*      */   {
/* 1768 */     int byteLen = (qlen << 2) - 4 + lastQuadBytes;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     int lastQuad;
/*      */     
/*      */ 
/*      */ 
/* 1777 */     if (lastQuadBytes < 4) {
/* 1778 */       int lastQuad = quads[(qlen - 1)];
/*      */       
/* 1780 */       quads[(qlen - 1)] = (lastQuad << (4 - lastQuadBytes << 3));
/*      */     } else {
/* 1782 */       lastQuad = 0;
/*      */     }
/*      */     
/*      */ 
/* 1786 */     char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1787 */     int cix = 0;
/*      */     
/* 1789 */     for (int ix = 0; ix < byteLen;) {
/* 1790 */       int ch = quads[(ix >> 2)];
/* 1791 */       int byteIx = ix & 0x3;
/* 1792 */       ch = ch >> (3 - byteIx << 3) & 0xFF;
/* 1793 */       ix++;
/*      */       
/* 1795 */       if (ch > 127) { int needed;
/*      */         int needed;
/* 1797 */         if ((ch & 0xE0) == 192) {
/* 1798 */           ch &= 0x1F;
/* 1799 */           needed = 1; } else { int needed;
/* 1800 */           if ((ch & 0xF0) == 224) {
/* 1801 */             ch &= 0xF;
/* 1802 */             needed = 2; } else { int needed;
/* 1803 */             if ((ch & 0xF8) == 240) {
/* 1804 */               ch &= 0x7;
/* 1805 */               needed = 3;
/*      */             } else {
/* 1807 */               _reportInvalidInitial(ch);
/* 1808 */               needed = ch = 1;
/*      */             } } }
/* 1810 */         if (ix + needed > byteLen) {
/* 1811 */           _reportInvalidEOF(" in field name");
/*      */         }
/*      */         
/*      */ 
/* 1815 */         int ch2 = quads[(ix >> 2)];
/* 1816 */         byteIx = ix & 0x3;
/* 1817 */         ch2 >>= 3 - byteIx << 3;
/* 1818 */         ix++;
/*      */         
/* 1820 */         if ((ch2 & 0xC0) != 128) {
/* 1821 */           _reportInvalidOther(ch2);
/*      */         }
/* 1823 */         ch = ch << 6 | ch2 & 0x3F;
/* 1824 */         if (needed > 1) {
/* 1825 */           ch2 = quads[(ix >> 2)];
/* 1826 */           byteIx = ix & 0x3;
/* 1827 */           ch2 >>= 3 - byteIx << 3;
/* 1828 */           ix++;
/*      */           
/* 1830 */           if ((ch2 & 0xC0) != 128) {
/* 1831 */             _reportInvalidOther(ch2);
/*      */           }
/* 1833 */           ch = ch << 6 | ch2 & 0x3F;
/* 1834 */           if (needed > 2) {
/* 1835 */             ch2 = quads[(ix >> 2)];
/* 1836 */             byteIx = ix & 0x3;
/* 1837 */             ch2 >>= 3 - byteIx << 3;
/* 1838 */             ix++;
/* 1839 */             if ((ch2 & 0xC0) != 128) {
/* 1840 */               _reportInvalidOther(ch2 & 0xFF);
/*      */             }
/* 1842 */             ch = ch << 6 | ch2 & 0x3F;
/*      */           }
/*      */         }
/* 1845 */         if (needed > 2) {
/* 1846 */           ch -= 65536;
/* 1847 */           if (cix >= cbuf.length) {
/* 1848 */             cbuf = this._textBuffer.expandCurrentSegment();
/*      */           }
/* 1850 */           cbuf[(cix++)] = ((char)(55296 + (ch >> 10)));
/* 1851 */           ch = 0xDC00 | ch & 0x3FF;
/*      */         }
/*      */       }
/* 1854 */       if (cix >= cbuf.length) {
/* 1855 */         cbuf = this._textBuffer.expandCurrentSegment();
/*      */       }
/* 1857 */       cbuf[(cix++)] = ((char)ch);
/*      */     }
/*      */     
/*      */ 
/* 1861 */     String baseName = new String(cbuf, 0, cix);
/*      */     
/* 1863 */     if (lastQuadBytes < 4) {
/* 1864 */       quads[(qlen - 1)] = lastQuad;
/*      */     }
/* 1866 */     return this._symbols.addName(baseName, quads, qlen);
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
/*      */   protected void _finishString()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1880 */     int ptr = this._inputPtr;
/* 1881 */     if (ptr >= this._inputEnd) {
/* 1882 */       loadMoreGuaranteed();
/* 1883 */       ptr = this._inputPtr;
/*      */     }
/* 1885 */     int outPtr = 0;
/* 1886 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/* 1887 */     int[] codes = sInputCodesUtf8;
/*      */     
/* 1889 */     int max = Math.min(this._inputEnd, ptr + outBuf.length);
/* 1890 */     byte[] inputBuffer = this._inputBuffer;
/* 1891 */     while (ptr < max) {
/* 1892 */       int c = inputBuffer[ptr] & 0xFF;
/* 1893 */       if (codes[c] != 0) {
/* 1894 */         if (c != 34) break;
/* 1895 */         this._inputPtr = (ptr + 1);
/* 1896 */         this._textBuffer.setCurrentLength(outPtr);
/* 1897 */         return;
/*      */       }
/*      */       
/*      */ 
/* 1901 */       ptr++;
/* 1902 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 1904 */     this._inputPtr = ptr;
/* 1905 */     _finishString2(outBuf, outPtr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _finishString2(char[] outBuf, int outPtr)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1914 */     int[] codes = sInputCodesUtf8;
/* 1915 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 1922 */       int ptr = this._inputPtr;
/* 1923 */       if (ptr >= this._inputEnd) {
/* 1924 */         loadMoreGuaranteed();
/* 1925 */         ptr = this._inputPtr;
/*      */       }
/* 1927 */       if (outPtr >= outBuf.length) {
/* 1928 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1929 */         outPtr = 0;
/*      */       }
/* 1931 */       int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr));
/* 1932 */       while (ptr < max) {
/* 1933 */         int c = inputBuffer[(ptr++)] & 0xFF;
/* 1934 */         if (codes[c] != 0) {
/* 1935 */           this._inputPtr = ptr;
/*      */           break label124;
/*      */         }
/* 1938 */         outBuf[(outPtr++)] = ((char)c);
/*      */       }
/* 1940 */       this._inputPtr = ptr;
/* 1941 */       continue;
/*      */       label124:
/* 1943 */       int c; if (c == 34) {
/*      */         break;
/*      */       }
/*      */       
/* 1947 */       switch (codes[c]) {
/*      */       case 1: 
/* 1949 */         c = _decodeEscaped();
/* 1950 */         break;
/*      */       case 2: 
/* 1952 */         c = _decodeUtf8_2(c);
/* 1953 */         break;
/*      */       case 3: 
/* 1955 */         if (this._inputEnd - this._inputPtr >= 2) {
/* 1956 */           c = _decodeUtf8_3fast(c);
/*      */         } else {
/* 1958 */           c = _decodeUtf8_3(c);
/*      */         }
/* 1960 */         break;
/*      */       case 4: 
/* 1962 */         c = _decodeUtf8_4(c);
/*      */         
/* 1964 */         outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
/* 1965 */         if (outPtr >= outBuf.length) {
/* 1966 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 1967 */           outPtr = 0;
/*      */         }
/* 1969 */         c = 0xDC00 | c & 0x3FF;
/*      */         
/* 1971 */         break;
/*      */       default: 
/* 1973 */         if (c < 32)
/*      */         {
/* 1975 */           _throwUnquotedSpace(c, "string value");
/*      */         }
/*      */         else {
/* 1978 */           _reportInvalidChar(c);
/*      */         }
/*      */         break;
/*      */       }
/* 1982 */       if (outPtr >= outBuf.length) {
/* 1983 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 1984 */         outPtr = 0;
/*      */       }
/*      */       
/* 1987 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 1989 */     this._textBuffer.setCurrentLength(outPtr);
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
/* 2000 */     this._tokenIncomplete = false;
/*      */     
/*      */ 
/* 2003 */     int[] codes = sInputCodesUtf8;
/* 2004 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 2012 */       int ptr = this._inputPtr;
/* 2013 */       int max = this._inputEnd;
/* 2014 */       if (ptr >= max) {
/* 2015 */         loadMoreGuaranteed();
/* 2016 */         ptr = this._inputPtr;
/* 2017 */         max = this._inputEnd;
/*      */       }
/* 2019 */       while (ptr < max) {
/* 2020 */         int c = inputBuffer[(ptr++)] & 0xFF;
/* 2021 */         if (codes[c] != 0) {
/* 2022 */           this._inputPtr = ptr;
/*      */           break label92;
/*      */         }
/*      */       }
/* 2026 */       this._inputPtr = ptr;
/* 2027 */       continue;
/*      */       label92:
/* 2029 */       int c; if (c == 34) {
/*      */         break;
/*      */       }
/*      */       
/* 2033 */       switch (codes[c]) {
/*      */       case 1: 
/* 2035 */         _decodeEscaped();
/* 2036 */         break;
/*      */       case 2: 
/* 2038 */         _skipUtf8_2(c);
/* 2039 */         break;
/*      */       case 3: 
/* 2041 */         _skipUtf8_3(c);
/* 2042 */         break;
/*      */       case 4: 
/* 2044 */         _skipUtf8_4(c);
/* 2045 */         break;
/*      */       default: 
/* 2047 */         if (c < 32)
/*      */         {
/* 2049 */           _throwUnquotedSpace(c, "string value");
/*      */         }
/*      */         else {
/* 2052 */           _reportInvalidChar(c);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         break;
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected JsonToken _handleUnexpectedValue(int c)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2068 */     switch (c) {
/*      */     case 39: 
/* 2070 */       if (isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
/* 2071 */         return _handleApostropheValue();
/*      */       }
/*      */       break;
/*      */     case 78: 
/* 2075 */       _matchToken("NaN", 1);
/* 2076 */       if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 2077 */         return resetAsNaN("NaN", NaN.0D);
/*      */       }
/* 2079 */       _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 2080 */       break;
/*      */     case 43: 
/* 2082 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2083 */         (!loadMore())) {
/* 2084 */         _reportInvalidEOFInValue();
/*      */       }
/*      */       
/* 2087 */       return _handleInvalidNumberStart(this._inputBuffer[(this._inputPtr++)] & 0xFF, false);
/*      */     }
/*      */     
/* 2090 */     _reportUnexpectedChar(c, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
/* 2091 */     return null;
/*      */   }
/*      */   
/*      */   protected JsonToken _handleApostropheValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2097 */     int c = 0;
/*      */     
/* 2099 */     int outPtr = 0;
/* 2100 */     char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
/*      */     
/*      */ 
/* 2103 */     int[] codes = sInputCodesUtf8;
/* 2104 */     byte[] inputBuffer = this._inputBuffer;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 2111 */       if (this._inputPtr >= this._inputEnd) {
/* 2112 */         loadMoreGuaranteed();
/*      */       }
/* 2114 */       if (outPtr >= outBuf.length) {
/* 2115 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2116 */         outPtr = 0;
/*      */       }
/* 2118 */       int max = this._inputEnd;
/*      */       
/* 2120 */       int max2 = this._inputPtr + (outBuf.length - outPtr);
/* 2121 */       if (max2 < max) {
/* 2122 */         max = max2;
/*      */       }
/*      */       
/* 2125 */       while (this._inputPtr < max) {
/* 2126 */         c = inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2127 */         if ((c == 39) || (codes[c] != 0)) {
/*      */           break label140;
/*      */         }
/* 2130 */         outBuf[(outPtr++)] = ((char)c);
/*      */       }
/* 2132 */       continue;
/*      */       
/*      */       label140:
/* 2135 */       if (c == 39) {
/*      */         break;
/*      */       }
/*      */       
/* 2139 */       switch (codes[c]) {
/*      */       case 1: 
/* 2141 */         if (c != 34) {
/* 2142 */           c = _decodeEscaped();
/*      */         }
/*      */         break;
/*      */       case 2: 
/* 2146 */         c = _decodeUtf8_2(c);
/* 2147 */         break;
/*      */       case 3: 
/* 2149 */         if (this._inputEnd - this._inputPtr >= 2) {
/* 2150 */           c = _decodeUtf8_3fast(c);
/*      */         } else {
/* 2152 */           c = _decodeUtf8_3(c);
/*      */         }
/* 2154 */         break;
/*      */       case 4: 
/* 2156 */         c = _decodeUtf8_4(c);
/*      */         
/* 2158 */         outBuf[(outPtr++)] = ((char)(0xD800 | c >> 10));
/* 2159 */         if (outPtr >= outBuf.length) {
/* 2160 */           outBuf = this._textBuffer.finishCurrentSegment();
/* 2161 */           outPtr = 0;
/*      */         }
/* 2163 */         c = 0xDC00 | c & 0x3FF;
/*      */         
/* 2165 */         break;
/*      */       default: 
/* 2167 */         if (c < 32) {
/* 2168 */           _throwUnquotedSpace(c, "string value");
/*      */         }
/*      */         
/* 2171 */         _reportInvalidChar(c);
/*      */       }
/*      */       
/* 2174 */       if (outPtr >= outBuf.length) {
/* 2175 */         outBuf = this._textBuffer.finishCurrentSegment();
/* 2176 */         outPtr = 0;
/*      */       }
/*      */       
/* 2179 */       outBuf[(outPtr++)] = ((char)c);
/*      */     }
/* 2181 */     this._textBuffer.setCurrentLength(outPtr);
/*      */     
/* 2183 */     return JsonToken.VALUE_STRING;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonToken _handleInvalidNumberStart(int ch, boolean negative)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2193 */     if (ch == 73) {
/* 2194 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2195 */         (!loadMore())) {
/* 2196 */         _reportInvalidEOFInValue();
/*      */       }
/*      */       
/* 2199 */       ch = this._inputBuffer[(this._inputPtr++)];
/* 2200 */       if (ch == 78) {
/* 2201 */         String match = negative ? "-INF" : "+INF";
/* 2202 */         _matchToken(match, 3);
/* 2203 */         if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 2204 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 2206 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/* 2207 */       } else if (ch == 110) {
/* 2208 */         String match = negative ? "-Infinity" : "+Infinity";
/* 2209 */         _matchToken(match, 3);
/* 2210 */         if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
/* 2211 */           return resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
/*      */         }
/* 2213 */         _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
/*      */       }
/*      */     }
/* 2216 */     reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
/* 2217 */     return null;
/*      */   }
/*      */   
/*      */   protected final void _matchToken(String matchStr, int i)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2223 */     int len = matchStr.length();
/*      */     do
/*      */     {
/* 2226 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2227 */         (!loadMore())) {
/* 2228 */         _reportInvalidEOF(" in a value");
/*      */       }
/*      */       
/* 2231 */       if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
/* 2232 */         _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
/*      */       }
/* 2234 */       this._inputPtr += 1;
/* 2235 */       i++; } while (i < len);
/*      */     
/*      */ 
/* 2238 */     if ((this._inputPtr >= this._inputEnd) && 
/* 2239 */       (!loadMore())) {
/* 2240 */       return;
/*      */     }
/*      */     
/* 2243 */     int ch = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2244 */     if ((ch < 48) || (ch == 93) || (ch == 125)) {
/* 2245 */       return;
/*      */     }
/*      */     
/* 2248 */     char c = (char)_decodeCharForError(ch);
/* 2249 */     if (Character.isJavaIdentifierPart(c)) {
/* 2250 */       this._inputPtr += 1;
/* 2251 */       _reportInvalidToken(matchStr.substring(0, i), "'null', 'true', 'false' or NaN");
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _reportInvalidToken(String matchedPart, String msg)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2258 */     StringBuilder sb = new StringBuilder(matchedPart);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2264 */     while ((this._inputPtr < this._inputEnd) || (loadMore()))
/*      */     {
/*      */ 
/* 2267 */       int i = this._inputBuffer[(this._inputPtr++)];
/* 2268 */       char c = (char)_decodeCharForError(i);
/* 2269 */       if (!Character.isJavaIdentifierPart(c)) {
/*      */         break;
/*      */       }
/* 2272 */       sb.append(c);
/*      */     }
/* 2274 */     _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _skipWS()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2286 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 2287 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2288 */       if (i > 32) {
/* 2289 */         if (i != 47) {
/* 2290 */           return i;
/*      */         }
/* 2292 */         _skipComment();
/* 2293 */       } else if (i != 32) {
/* 2294 */         if (i == 10) {
/* 2295 */           _skipLF();
/* 2296 */         } else if (i == 13) {
/* 2297 */           _skipCR();
/* 2298 */         } else if (i != 9) {
/* 2299 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2303 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
/*      */   }
/*      */   
/*      */   private final int _skipWSOrEnd()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2309 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 2310 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2311 */       if (i > 32) {
/* 2312 */         if (i != 47) {
/* 2313 */           return i;
/*      */         }
/* 2315 */         _skipComment();
/* 2316 */       } else if (i != 32) {
/* 2317 */         if (i == 10) {
/* 2318 */           _skipLF();
/* 2319 */         } else if (i == 13) {
/* 2320 */           _skipCR();
/* 2321 */         } else if (i != 9) {
/* 2322 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2327 */     _handleEOF();
/* 2328 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _skipColon()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2340 */     if (this._inputPtr >= this._inputEnd) {
/* 2341 */       loadMoreGuaranteed();
/*      */     }
/*      */     
/* 2344 */     int i = this._inputBuffer[(this._inputPtr++)];
/* 2345 */     if (i == 58) {
/* 2346 */       if (this._inputPtr < this._inputEnd) {
/* 2347 */         i = this._inputBuffer[this._inputPtr] & 0xFF;
/* 2348 */         if ((i > 32) && (i != 47)) {
/* 2349 */           this._inputPtr += 1;
/* 2350 */           return i;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 2355 */       i &= 0xFF;
/*      */       
/*      */       for (;;)
/*      */       {
/* 2359 */         switch (i) {
/*      */         case 9: 
/*      */         case 13: 
/*      */         case 32: 
/* 2363 */           _skipCR();
/* 2364 */           break;
/*      */         case 10: 
/* 2366 */           _skipLF();
/* 2367 */           break;
/*      */         case 47: 
/* 2369 */           _skipComment();
/*      */         }
/*      */       }
/* 2372 */       if (i < 32) {
/* 2373 */         _throwInvalidSpace(i);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2378 */       if (this._inputPtr >= this._inputEnd) {
/* 2379 */         loadMoreGuaranteed();
/*      */       }
/* 2381 */       i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2382 */       if (i != 58) {
/* 2383 */         _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2388 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 2389 */       i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2390 */       if (i > 32) {
/* 2391 */         if (i != 47) {
/* 2392 */           return i;
/*      */         }
/* 2394 */         _skipComment();
/* 2395 */       } else if (i != 32) {
/* 2396 */         if (i == 10) {
/* 2397 */           _skipLF();
/* 2398 */         } else if (i == 13) {
/* 2399 */           _skipCR();
/* 2400 */         } else if (i != 9) {
/* 2401 */           _throwInvalidSpace(i);
/*      */         }
/*      */       }
/*      */     }
/* 2405 */     throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
/*      */   }
/*      */   
/*      */   private final void _skipComment()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2411 */     if (!isEnabled(JsonParser.Feature.ALLOW_COMMENTS)) {
/* 2412 */       _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
/*      */     }
/*      */     
/* 2415 */     if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/* 2416 */       _reportInvalidEOF(" in a comment");
/*      */     }
/* 2418 */     int c = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2419 */     if (c == 47) {
/* 2420 */       _skipCppComment();
/* 2421 */     } else if (c == 42) {
/* 2422 */       _skipCComment();
/*      */     } else {
/* 2424 */       _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _skipCComment()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2432 */     int[] codes = CharTypes.getInputCodeComment();
/*      */     
/*      */ 
/*      */ 
/* 2436 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 2437 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2438 */       int code = codes[i];
/* 2439 */       if (code != 0)
/* 2440 */         switch (code) {
/*      */         case 42: 
/* 2442 */           if ((this._inputPtr >= this._inputEnd) && (!loadMore())) {
/*      */             break label204;
/*      */           }
/* 2445 */           if (this._inputBuffer[this._inputPtr] == 47) {
/* 2446 */             this._inputPtr += 1; return;
/*      */           }
/*      */           
/*      */           break;
/*      */         case 10: 
/* 2451 */           _skipLF();
/* 2452 */           break;
/*      */         case 13: 
/* 2454 */           _skipCR();
/* 2455 */           break;
/*      */         case 2: 
/* 2457 */           _skipUtf8_2(i);
/* 2458 */           break;
/*      */         case 3: 
/* 2460 */           _skipUtf8_3(i);
/* 2461 */           break;
/*      */         case 4: 
/* 2463 */           _skipUtf8_4(i);
/* 2464 */           break;
/*      */         
/*      */         default: 
/* 2467 */           _reportInvalidChar(i);
/*      */         }
/*      */     }
/*      */     label204:
/* 2471 */     _reportInvalidEOF(" in a comment");
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _skipCppComment()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2478 */     int[] codes = CharTypes.getInputCodeComment();
/* 2479 */     while ((this._inputPtr < this._inputEnd) || (loadMore())) {
/* 2480 */       int i = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2481 */       int code = codes[i];
/* 2482 */       if (code != 0) {
/* 2483 */         switch (code) {
/*      */         case 10: 
/* 2485 */           _skipLF();
/* 2486 */           return;
/*      */         case 13: 
/* 2488 */           _skipCR(); return;
/*      */         case 42: 
/*      */           break;
/*      */         
/*      */         case 2: 
/* 2493 */           _skipUtf8_2(i);
/* 2494 */           break;
/*      */         case 3: 
/* 2496 */           _skipUtf8_3(i);
/* 2497 */           break;
/*      */         case 4: 
/* 2499 */           _skipUtf8_4(i);
/* 2500 */           break;
/*      */         
/*      */         default: 
/* 2503 */           _reportInvalidChar(i);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected final char _decodeEscaped()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2513 */     if ((this._inputPtr >= this._inputEnd) && 
/* 2514 */       (!loadMore())) {
/* 2515 */       _reportInvalidEOF(" in character escape sequence");
/*      */     }
/*      */     
/* 2518 */     int c = this._inputBuffer[(this._inputPtr++)];
/*      */     
/* 2520 */     switch (c)
/*      */     {
/*      */     case 98: 
/* 2523 */       return '\b';
/*      */     case 116: 
/* 2525 */       return '\t';
/*      */     case 110: 
/* 2527 */       return '\n';
/*      */     case 102: 
/* 2529 */       return '\f';
/*      */     case 114: 
/* 2531 */       return '\r';
/*      */     
/*      */ 
/*      */     case 34: 
/*      */     case 47: 
/*      */     case 92: 
/* 2537 */       return (char)c;
/*      */     
/*      */     case 117: 
/*      */       break;
/*      */     
/*      */     default: 
/* 2543 */       return _handleUnrecognizedCharacterEscape((char)_decodeCharForError(c));
/*      */     }
/*      */     
/*      */     
/* 2547 */     int value = 0;
/* 2548 */     for (int i = 0; i < 4; i++) {
/* 2549 */       if ((this._inputPtr >= this._inputEnd) && 
/* 2550 */         (!loadMore())) {
/* 2551 */         _reportInvalidEOF(" in character escape sequence");
/*      */       }
/*      */       
/* 2554 */       int ch = this._inputBuffer[(this._inputPtr++)];
/* 2555 */       int digit = CharTypes.charToHex(ch);
/* 2556 */       if (digit < 0) {
/* 2557 */         _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
/*      */       }
/* 2559 */       value = value << 4 | digit;
/*      */     }
/* 2561 */     return (char)value;
/*      */   }
/*      */   
/*      */   protected int _decodeCharForError(int firstByte)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2567 */     int c = firstByte;
/* 2568 */     if (c < 0)
/*      */     {
/*      */       int needed;
/*      */       int needed;
/* 2572 */       if ((c & 0xE0) == 192) {
/* 2573 */         c &= 0x1F;
/* 2574 */         needed = 1; } else { int needed;
/* 2575 */         if ((c & 0xF0) == 224) {
/* 2576 */           c &= 0xF;
/* 2577 */           needed = 2; } else { int needed;
/* 2578 */           if ((c & 0xF8) == 240)
/*      */           {
/* 2580 */             c &= 0x7;
/* 2581 */             needed = 3;
/*      */           } else {
/* 2583 */             _reportInvalidInitial(c & 0xFF);
/* 2584 */             needed = 1;
/*      */           }
/*      */         } }
/* 2587 */       int d = nextByte();
/* 2588 */       if ((d & 0xC0) != 128) {
/* 2589 */         _reportInvalidOther(d & 0xFF);
/*      */       }
/* 2591 */       c = c << 6 | d & 0x3F;
/*      */       
/* 2593 */       if (needed > 1) {
/* 2594 */         d = nextByte();
/* 2595 */         if ((d & 0xC0) != 128) {
/* 2596 */           _reportInvalidOther(d & 0xFF);
/*      */         }
/* 2598 */         c = c << 6 | d & 0x3F;
/* 2599 */         if (needed > 2) {
/* 2600 */           d = nextByte();
/* 2601 */           if ((d & 0xC0) != 128) {
/* 2602 */             _reportInvalidOther(d & 0xFF);
/*      */           }
/* 2604 */           c = c << 6 | d & 0x3F;
/*      */         }
/*      */       }
/*      */     }
/* 2608 */     return c;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _decodeUtf8_2(int c)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2620 */     if (this._inputPtr >= this._inputEnd) {
/* 2621 */       loadMoreGuaranteed();
/*      */     }
/* 2623 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 2624 */     if ((d & 0xC0) != 128) {
/* 2625 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2627 */     return (c & 0x1F) << 6 | d & 0x3F;
/*      */   }
/*      */   
/*      */   private final int _decodeUtf8_3(int c1)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2633 */     if (this._inputPtr >= this._inputEnd) {
/* 2634 */       loadMoreGuaranteed();
/*      */     }
/* 2636 */     c1 &= 0xF;
/* 2637 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 2638 */     if ((d & 0xC0) != 128) {
/* 2639 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2641 */     int c = c1 << 6 | d & 0x3F;
/* 2642 */     if (this._inputPtr >= this._inputEnd) {
/* 2643 */       loadMoreGuaranteed();
/*      */     }
/* 2645 */     d = this._inputBuffer[(this._inputPtr++)];
/* 2646 */     if ((d & 0xC0) != 128) {
/* 2647 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2649 */     c = c << 6 | d & 0x3F;
/* 2650 */     return c;
/*      */   }
/*      */   
/*      */   private final int _decodeUtf8_3fast(int c1)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2656 */     c1 &= 0xF;
/* 2657 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 2658 */     if ((d & 0xC0) != 128) {
/* 2659 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2661 */     int c = c1 << 6 | d & 0x3F;
/* 2662 */     d = this._inputBuffer[(this._inputPtr++)];
/* 2663 */     if ((d & 0xC0) != 128) {
/* 2664 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2666 */     c = c << 6 | d & 0x3F;
/* 2667 */     return c;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int _decodeUtf8_4(int c)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2677 */     if (this._inputPtr >= this._inputEnd) {
/* 2678 */       loadMoreGuaranteed();
/*      */     }
/* 2680 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 2681 */     if ((d & 0xC0) != 128) {
/* 2682 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2684 */     c = (c & 0x7) << 6 | d & 0x3F;
/*      */     
/* 2686 */     if (this._inputPtr >= this._inputEnd) {
/* 2687 */       loadMoreGuaranteed();
/*      */     }
/* 2689 */     d = this._inputBuffer[(this._inputPtr++)];
/* 2690 */     if ((d & 0xC0) != 128) {
/* 2691 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2693 */     c = c << 6 | d & 0x3F;
/* 2694 */     if (this._inputPtr >= this._inputEnd) {
/* 2695 */       loadMoreGuaranteed();
/*      */     }
/* 2697 */     d = this._inputBuffer[(this._inputPtr++)];
/* 2698 */     if ((d & 0xC0) != 128) {
/* 2699 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2705 */     return (c << 6 | d & 0x3F) - 65536;
/*      */   }
/*      */   
/*      */   private final void _skipUtf8_2(int c)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2711 */     if (this._inputPtr >= this._inputEnd) {
/* 2712 */       loadMoreGuaranteed();
/*      */     }
/* 2714 */     c = this._inputBuffer[(this._inputPtr++)];
/* 2715 */     if ((c & 0xC0) != 128) {
/* 2716 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _skipUtf8_3(int c)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2726 */     if (this._inputPtr >= this._inputEnd) {
/* 2727 */       loadMoreGuaranteed();
/*      */     }
/*      */     
/* 2730 */     c = this._inputBuffer[(this._inputPtr++)];
/* 2731 */     if ((c & 0xC0) != 128) {
/* 2732 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/* 2734 */     if (this._inputPtr >= this._inputEnd) {
/* 2735 */       loadMoreGuaranteed();
/*      */     }
/* 2737 */     c = this._inputBuffer[(this._inputPtr++)];
/* 2738 */     if ((c & 0xC0) != 128) {
/* 2739 */       _reportInvalidOther(c & 0xFF, this._inputPtr);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _skipUtf8_4(int c)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2746 */     if (this._inputPtr >= this._inputEnd) {
/* 2747 */       loadMoreGuaranteed();
/*      */     }
/* 2749 */     int d = this._inputBuffer[(this._inputPtr++)];
/* 2750 */     if ((d & 0xC0) != 128) {
/* 2751 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2753 */     if (this._inputPtr >= this._inputEnd) {
/* 2754 */       loadMoreGuaranteed();
/*      */     }
/* 2756 */     d = this._inputBuffer[(this._inputPtr++)];
/* 2757 */     if ((d & 0xC0) != 128) {
/* 2758 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
/*      */     }
/* 2760 */     if (this._inputPtr >= this._inputEnd) {
/* 2761 */       loadMoreGuaranteed();
/*      */     }
/* 2763 */     d = this._inputBuffer[(this._inputPtr++)];
/* 2764 */     if ((d & 0xC0) != 128) {
/* 2765 */       _reportInvalidOther(d & 0xFF, this._inputPtr);
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
/* 2781 */     if (((this._inputPtr < this._inputEnd) || (loadMore())) && 
/* 2782 */       (this._inputBuffer[this._inputPtr] == 10)) {
/* 2783 */       this._inputPtr += 1;
/*      */     }
/*      */     
/* 2786 */     this._currInputRow += 1;
/* 2787 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */   
/*      */   protected final void _skipLF() throws IOException
/*      */   {
/* 2792 */     this._currInputRow += 1;
/* 2793 */     this._currInputRowStart = this._inputPtr;
/*      */   }
/*      */   
/*      */   private int nextByte()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2799 */     if (this._inputPtr >= this._inputEnd) {
/* 2800 */       loadMoreGuaranteed();
/*      */     }
/* 2802 */     return this._inputBuffer[(this._inputPtr++)] & 0xFF;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _reportInvalidChar(int c)
/*      */     throws JsonParseException
/*      */   {
/* 2815 */     if (c < 32) {
/* 2816 */       _throwInvalidSpace(c);
/*      */     }
/* 2818 */     _reportInvalidInitial(c);
/*      */   }
/*      */   
/*      */   protected void _reportInvalidInitial(int mask)
/*      */     throws JsonParseException
/*      */   {
/* 2824 */     _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */   
/*      */   protected void _reportInvalidOther(int mask)
/*      */     throws JsonParseException
/*      */   {
/* 2830 */     _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
/*      */   }
/*      */   
/*      */   protected void _reportInvalidOther(int mask, int ptr)
/*      */     throws JsonParseException
/*      */   {
/* 2836 */     this._inputPtr = ptr;
/* 2837 */     _reportInvalidOther(mask);
/*      */   }
/*      */   
/*      */   public static int[] growArrayBy(int[] arr, int more)
/*      */   {
/* 2842 */     if (arr == null) {
/* 2843 */       return new int[more];
/*      */     }
/* 2845 */     int[] old = arr;
/* 2846 */     int len = arr.length;
/* 2847 */     arr = new int[len + more];
/* 2848 */     System.arraycopy(old, 0, arr, 0, len);
/* 2849 */     return arr;
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
/*      */   protected byte[] _decodeBase64(Base64Variant b64variant)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 2865 */     ByteArrayBuilder builder = _getByteArrayBuilder();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     for (;;)
/*      */     {
/* 2872 */       if (this._inputPtr >= this._inputEnd) {
/* 2873 */         loadMoreGuaranteed();
/*      */       }
/* 2875 */       int ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2876 */       if (ch > 32) {
/* 2877 */         int bits = b64variant.decodeBase64Char(ch);
/* 2878 */         if (bits < 0) {
/* 2879 */           if (ch == 34) {
/* 2880 */             return builder.toByteArray();
/*      */           }
/* 2882 */           bits = _decodeBase64Escape(b64variant, ch, 0);
/* 2883 */           if (bits < 0) {}
/*      */         }
/*      */         else
/*      */         {
/* 2887 */           int decodedData = bits;
/*      */           
/*      */ 
/*      */ 
/* 2891 */           if (this._inputPtr >= this._inputEnd) {
/* 2892 */             loadMoreGuaranteed();
/*      */           }
/* 2894 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2895 */           bits = b64variant.decodeBase64Char(ch);
/* 2896 */           if (bits < 0) {
/* 2897 */             bits = _decodeBase64Escape(b64variant, ch, 1);
/*      */           }
/* 2899 */           decodedData = decodedData << 6 | bits;
/*      */           
/*      */ 
/* 2902 */           if (this._inputPtr >= this._inputEnd) {
/* 2903 */             loadMoreGuaranteed();
/*      */           }
/* 2905 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2906 */           bits = b64variant.decodeBase64Char(ch);
/*      */           
/*      */ 
/* 2909 */           if (bits < 0) {
/* 2910 */             if (bits != -2)
/*      */             {
/* 2912 */               if ((ch == 34) && (!b64variant.usesPadding())) {
/* 2913 */                 decodedData >>= 4;
/* 2914 */                 builder.append(decodedData);
/* 2915 */                 return builder.toByteArray();
/*      */               }
/* 2917 */               bits = _decodeBase64Escape(b64variant, ch, 2);
/*      */             }
/* 2919 */             if (bits == -2)
/*      */             {
/* 2921 */               if (this._inputPtr >= this._inputEnd) {
/* 2922 */                 loadMoreGuaranteed();
/*      */               }
/* 2924 */               ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2925 */               if (!b64variant.usesPaddingChar(ch)) {
/* 2926 */                 throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
/*      */               }
/*      */               
/* 2929 */               decodedData >>= 4;
/* 2930 */               builder.append(decodedData);
/* 2931 */               continue;
/*      */             }
/*      */           }
/*      */           
/* 2935 */           decodedData = decodedData << 6 | bits;
/*      */           
/* 2937 */           if (this._inputPtr >= this._inputEnd) {
/* 2938 */             loadMoreGuaranteed();
/*      */           }
/* 2940 */           ch = this._inputBuffer[(this._inputPtr++)] & 0xFF;
/* 2941 */           bits = b64variant.decodeBase64Char(ch);
/* 2942 */           if (bits < 0) {
/* 2943 */             if (bits != -2)
/*      */             {
/* 2945 */               if ((ch == 34) && (!b64variant.usesPadding())) {
/* 2946 */                 decodedData >>= 2;
/* 2947 */                 builder.appendTwoBytes(decodedData);
/* 2948 */                 return builder.toByteArray();
/*      */               }
/* 2950 */               bits = _decodeBase64Escape(b64variant, ch, 3);
/*      */             }
/* 2952 */             if (bits == -2)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2959 */               decodedData >>= 2;
/* 2960 */               builder.appendTwoBytes(decodedData);
/* 2961 */               continue;
/*      */             }
/*      */           }
/*      */           
/* 2965 */           decodedData = decodedData << 6 | bits;
/* 2966 */           builder.appendThreeBytes(decodedData);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\Utf8StreamParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */