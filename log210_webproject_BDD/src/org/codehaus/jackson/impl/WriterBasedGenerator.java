/*      */ package org.codehaus.jackson.impl;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.Writer;
/*      */ import java.math.BigInteger;
/*      */ import org.codehaus.jackson.Base64Variant;
/*      */ import org.codehaus.jackson.JsonGenerationException;
/*      */ import org.codehaus.jackson.JsonGenerator.Feature;
/*      */ import org.codehaus.jackson.JsonStreamContext;
/*      */ import org.codehaus.jackson.PrettyPrinter;
/*      */ import org.codehaus.jackson.SerializableString;
/*      */ import org.codehaus.jackson.io.CharacterEscapes;
/*      */ import org.codehaus.jackson.io.IOContext;
/*      */ import org.codehaus.jackson.io.NumberOutput;
/*      */ import org.codehaus.jackson.io.SerializedString;
/*      */ 
/*      */ public final class WriterBasedGenerator extends JsonGeneratorBase
/*      */ {
/*      */   protected static final int SHORT_WRITE = 32;
/*   20 */   protected static final char[] HEX_CHARS = ;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   26 */   protected static final int[] sOutputEscapes = org.codehaus.jackson.util.CharTypes.get7BitOutputEscapes();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final IOContext _ioContext;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Writer _writer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   50 */   protected int[] _outputEscapes = sOutputEscapes;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _maximumNonEscapedChar;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected CharacterEscapes _characterEscapes;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SerializableString _currentEscape;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected char[] _outputBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   95 */   protected int _outputHead = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  101 */   protected int _outputTail = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _outputEnd;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected char[] _entityBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public WriterBasedGenerator(IOContext ctxt, int features, org.codehaus.jackson.ObjectCodec codec, Writer w)
/*      */   {
/*  124 */     super(features, codec);
/*  125 */     this._ioContext = ctxt;
/*  126 */     this._writer = w;
/*  127 */     this._outputBuffer = ctxt.allocConcatBuffer();
/*  128 */     this._outputEnd = this._outputBuffer.length;
/*      */     
/*  130 */     if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
/*  131 */       setHighestNonEscapedChar(127);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public org.codehaus.jackson.JsonGenerator setHighestNonEscapedChar(int charCode)
/*      */   {
/*  143 */     this._maximumNonEscapedChar = (charCode < 0 ? 0 : charCode);
/*  144 */     return this;
/*      */   }
/*      */   
/*      */   public int getHighestEscapedChar()
/*      */   {
/*  149 */     return this._maximumNonEscapedChar;
/*      */   }
/*      */   
/*      */ 
/*      */   public org.codehaus.jackson.JsonGenerator setCharacterEscapes(CharacterEscapes esc)
/*      */   {
/*  155 */     this._characterEscapes = esc;
/*  156 */     if (esc == null) {
/*  157 */       this._outputEscapes = sOutputEscapes;
/*      */     } else {
/*  159 */       this._outputEscapes = esc.getEscapeCodesForAscii();
/*      */     }
/*  161 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public CharacterEscapes getCharacterEscapes()
/*      */   {
/*  172 */     return this._characterEscapes;
/*      */   }
/*      */   
/*      */   public Object getOutputTarget()
/*      */   {
/*  177 */     return this._writer;
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
/*      */   public final void writeFieldName(String name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  193 */     int status = this._writeContext.writeFieldName(name);
/*  194 */     if (status == 4) {
/*  195 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  197 */     _writeFieldName(name, status == 1);
/*      */   }
/*      */   
/*      */ 
/*      */   public final void writeStringField(String fieldName, String value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  204 */     writeFieldName(fieldName);
/*  205 */     writeString(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void writeFieldName(SerializedString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  213 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  214 */     if (status == 4) {
/*  215 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  217 */     _writeFieldName(name, status == 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void writeFieldName(SerializableString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  225 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  226 */     if (status == 4) {
/*  227 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  229 */     _writeFieldName(name, status == 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeStartArray()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  241 */     _verifyValueWrite("start an array");
/*  242 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  243 */     if (this._cfgPrettyPrinter != null) {
/*  244 */       this._cfgPrettyPrinter.writeStartArray(this);
/*      */     } else {
/*  246 */       if (this._outputTail >= this._outputEnd) {
/*  247 */         _flushBuffer();
/*      */       }
/*  249 */       this._outputBuffer[(this._outputTail++)] = '[';
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeEndArray()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  256 */     if (!this._writeContext.inArray()) {
/*  257 */       _reportError("Current context not an ARRAY but " + this._writeContext.getTypeDesc());
/*      */     }
/*  259 */     if (this._cfgPrettyPrinter != null) {
/*  260 */       this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  262 */       if (this._outputTail >= this._outputEnd) {
/*  263 */         _flushBuffer();
/*      */       }
/*  265 */       this._outputBuffer[(this._outputTail++)] = ']';
/*      */     }
/*  267 */     this._writeContext = this._writeContext.getParent();
/*      */   }
/*      */   
/*      */   public final void writeStartObject()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  273 */     _verifyValueWrite("start an object");
/*  274 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  275 */     if (this._cfgPrettyPrinter != null) {
/*  276 */       this._cfgPrettyPrinter.writeStartObject(this);
/*      */     } else {
/*  278 */       if (this._outputTail >= this._outputEnd) {
/*  279 */         _flushBuffer();
/*      */       }
/*  281 */       this._outputBuffer[(this._outputTail++)] = '{';
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeEndObject()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  288 */     if (!this._writeContext.inObject()) {
/*  289 */       _reportError("Current context not an object but " + this._writeContext.getTypeDesc());
/*      */     }
/*  291 */     if (this._cfgPrettyPrinter != null) {
/*  292 */       this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  294 */       if (this._outputTail >= this._outputEnd) {
/*  295 */         _flushBuffer();
/*      */       }
/*  297 */       this._outputBuffer[(this._outputTail++)] = '}';
/*      */     }
/*  299 */     this._writeContext = this._writeContext.getParent();
/*      */   }
/*      */   
/*      */   protected void _writeFieldName(String name, boolean commaBefore)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  305 */     if (this._cfgPrettyPrinter != null) {
/*  306 */       _writePPFieldName(name, commaBefore);
/*  307 */       return;
/*      */     }
/*      */     
/*  310 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  311 */       _flushBuffer();
/*      */     }
/*  313 */     if (commaBefore) {
/*  314 */       this._outputBuffer[(this._outputTail++)] = ',';
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  320 */     if (!isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
/*  321 */       _writeString(name);
/*  322 */       return;
/*      */     }
/*      */     
/*      */ 
/*  326 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */     
/*  328 */     _writeString(name);
/*      */     
/*  330 */     if (this._outputTail >= this._outputEnd) {
/*  331 */       _flushBuffer();
/*      */     }
/*  333 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */   public void _writeFieldName(SerializableString name, boolean commaBefore)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  339 */     if (this._cfgPrettyPrinter != null) {
/*  340 */       _writePPFieldName(name, commaBefore);
/*  341 */       return;
/*      */     }
/*      */     
/*  344 */     if (this._outputTail + 1 >= this._outputEnd) {
/*  345 */       _flushBuffer();
/*      */     }
/*  347 */     if (commaBefore) {
/*  348 */       this._outputBuffer[(this._outputTail++)] = ',';
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  353 */     char[] quoted = name.asQuotedChars();
/*  354 */     if (!isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
/*  355 */       writeRaw(quoted, 0, quoted.length);
/*  356 */       return;
/*      */     }
/*      */     
/*  359 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */     
/*  361 */     int qlen = quoted.length;
/*  362 */     if (this._outputTail + qlen + 1 >= this._outputEnd) {
/*  363 */       writeRaw(quoted, 0, qlen);
/*      */       
/*  365 */       if (this._outputTail >= this._outputEnd) {
/*  366 */         _flushBuffer();
/*      */       }
/*  368 */       this._outputBuffer[(this._outputTail++)] = '"';
/*      */     } else {
/*  370 */       System.arraycopy(quoted, 0, this._outputBuffer, this._outputTail, qlen);
/*  371 */       this._outputTail += qlen;
/*  372 */       this._outputBuffer[(this._outputTail++)] = '"';
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _writePPFieldName(String name, boolean commaBefore)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  383 */     if (commaBefore) {
/*  384 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  386 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*      */     
/*  389 */     if (isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
/*  390 */       if (this._outputTail >= this._outputEnd) {
/*  391 */         _flushBuffer();
/*      */       }
/*  393 */       this._outputBuffer[(this._outputTail++)] = '"';
/*  394 */       _writeString(name);
/*  395 */       if (this._outputTail >= this._outputEnd) {
/*  396 */         _flushBuffer();
/*      */       }
/*  398 */       this._outputBuffer[(this._outputTail++)] = '"';
/*      */     } else {
/*  400 */       _writeString(name);
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void _writePPFieldName(SerializableString name, boolean commaBefore)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  407 */     if (commaBefore) {
/*  408 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  410 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*      */     
/*  413 */     char[] quoted = name.asQuotedChars();
/*  414 */     if (isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
/*  415 */       if (this._outputTail >= this._outputEnd) {
/*  416 */         _flushBuffer();
/*      */       }
/*  418 */       this._outputBuffer[(this._outputTail++)] = '"';
/*  419 */       writeRaw(quoted, 0, quoted.length);
/*  420 */       if (this._outputTail >= this._outputEnd) {
/*  421 */         _flushBuffer();
/*      */       }
/*  423 */       this._outputBuffer[(this._outputTail++)] = '"';
/*      */     } else {
/*  425 */       writeRaw(quoted, 0, quoted.length);
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
/*      */   public void writeString(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  439 */     _verifyValueWrite("write text value");
/*  440 */     if (text == null) {
/*  441 */       _writeNull();
/*  442 */       return;
/*      */     }
/*  444 */     if (this._outputTail >= this._outputEnd) {
/*  445 */       _flushBuffer();
/*      */     }
/*  447 */     this._outputBuffer[(this._outputTail++)] = '"';
/*  448 */     _writeString(text);
/*      */     
/*  450 */     if (this._outputTail >= this._outputEnd) {
/*  451 */       _flushBuffer();
/*      */     }
/*  453 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeString(char[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  460 */     _verifyValueWrite("write text value");
/*  461 */     if (this._outputTail >= this._outputEnd) {
/*  462 */       _flushBuffer();
/*      */     }
/*  464 */     this._outputBuffer[(this._outputTail++)] = '"';
/*  465 */     _writeString(text, offset, len);
/*      */     
/*  467 */     if (this._outputTail >= this._outputEnd) {
/*  468 */       _flushBuffer();
/*      */     }
/*  470 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */ 
/*      */   public final void writeString(SerializableString sstr)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  477 */     _verifyValueWrite("write text value");
/*  478 */     if (this._outputTail >= this._outputEnd) {
/*  479 */       _flushBuffer();
/*      */     }
/*  481 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */     
/*  483 */     char[] text = sstr.asQuotedChars();
/*  484 */     int len = text.length;
/*      */     
/*  486 */     if (len < 32) {
/*  487 */       int room = this._outputEnd - this._outputTail;
/*  488 */       if (len > room) {
/*  489 */         _flushBuffer();
/*      */       }
/*  491 */       System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
/*  492 */       this._outputTail += len;
/*      */     }
/*      */     else {
/*  495 */       _flushBuffer();
/*  496 */       this._writer.write(text, 0, len);
/*      */     }
/*  498 */     if (this._outputTail >= this._outputEnd) {
/*  499 */       _flushBuffer();
/*      */     }
/*  501 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  509 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  517 */     _reportUnsupportedOperation();
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
/*      */   public void writeRaw(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  531 */     int len = text.length();
/*  532 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  534 */     if (room == 0) {
/*  535 */       _flushBuffer();
/*  536 */       room = this._outputEnd - this._outputTail;
/*      */     }
/*      */     
/*  539 */     if (room >= len) {
/*  540 */       text.getChars(0, len, this._outputBuffer, this._outputTail);
/*  541 */       this._outputTail += len;
/*      */     } else {
/*  543 */       writeRawLong(text);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeRaw(String text, int start, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  552 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  554 */     if (room < len) {
/*  555 */       _flushBuffer();
/*  556 */       room = this._outputEnd - this._outputTail;
/*      */     }
/*      */     
/*  559 */     if (room >= len) {
/*  560 */       text.getChars(start, start + len, this._outputBuffer, this._outputTail);
/*  561 */       this._outputTail += len;
/*      */     } else {
/*  563 */       writeRawLong(text.substring(start, start + len));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeRaw(char[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  572 */     if (len < 32) {
/*  573 */       int room = this._outputEnd - this._outputTail;
/*  574 */       if (len > room) {
/*  575 */         _flushBuffer();
/*      */       }
/*  577 */       System.arraycopy(text, offset, this._outputBuffer, this._outputTail, len);
/*  578 */       this._outputTail += len;
/*  579 */       return;
/*      */     }
/*      */     
/*  582 */     _flushBuffer();
/*  583 */     this._writer.write(text, offset, len);
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRaw(char c)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  590 */     if (this._outputTail >= this._outputEnd) {
/*  591 */       _flushBuffer();
/*      */     }
/*  593 */     this._outputBuffer[(this._outputTail++)] = c;
/*      */   }
/*      */   
/*      */   private void writeRawLong(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  599 */     int room = this._outputEnd - this._outputTail;
/*      */     
/*  601 */     text.getChars(0, room, this._outputBuffer, this._outputTail);
/*  602 */     this._outputTail += room;
/*  603 */     _flushBuffer();
/*  604 */     int offset = room;
/*  605 */     int len = text.length() - room;
/*      */     
/*  607 */     while (len > this._outputEnd) {
/*  608 */       int amount = this._outputEnd;
/*  609 */       text.getChars(offset, offset + amount, this._outputBuffer, 0);
/*  610 */       this._outputHead = 0;
/*  611 */       this._outputTail = amount;
/*  612 */       _flushBuffer();
/*  613 */       offset += amount;
/*  614 */       len -= amount;
/*      */     }
/*      */     
/*  617 */     text.getChars(offset, offset + len, this._outputBuffer, 0);
/*  618 */     this._outputHead = 0;
/*  619 */     this._outputTail = len;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  632 */     _verifyValueWrite("write binary value");
/*      */     
/*  634 */     if (this._outputTail >= this._outputEnd) {
/*  635 */       _flushBuffer();
/*      */     }
/*  637 */     this._outputBuffer[(this._outputTail++)] = '"';
/*  638 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  640 */     if (this._outputTail >= this._outputEnd) {
/*  641 */       _flushBuffer();
/*      */     }
/*  643 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeNumber(int i)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  656 */     _verifyValueWrite("write number");
/*  657 */     if (this._cfgNumbersAsStrings) {
/*  658 */       _writeQuotedInt(i);
/*  659 */       return;
/*      */     }
/*      */     
/*  662 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  663 */       _flushBuffer();
/*      */     }
/*  665 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedInt(int i) throws IOException {
/*  669 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  670 */       _flushBuffer();
/*      */     }
/*  672 */     this._outputBuffer[(this._outputTail++)] = '"';
/*  673 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  674 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(long l)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  681 */     _verifyValueWrite("write number");
/*  682 */     if (this._cfgNumbersAsStrings) {
/*  683 */       _writeQuotedLong(l);
/*  684 */       return;
/*      */     }
/*  686 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  688 */       _flushBuffer();
/*      */     }
/*  690 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedLong(long l) throws IOException {
/*  694 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  695 */       _flushBuffer();
/*      */     }
/*  697 */     this._outputBuffer[(this._outputTail++)] = '"';
/*  698 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*  699 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeNumber(BigInteger value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  708 */     _verifyValueWrite("write number");
/*  709 */     if (value == null) {
/*  710 */       _writeNull();
/*  711 */     } else if (this._cfgNumbersAsStrings) {
/*  712 */       _writeQuotedRaw(value);
/*      */     } else {
/*  714 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeNumber(double d)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  723 */     if ((this._cfgNumbersAsStrings) || (((Double.isNaN(d)) || (Double.isInfinite(d))) && (isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))))
/*      */     {
/*      */ 
/*      */ 
/*  727 */       writeString(String.valueOf(d));
/*  728 */       return;
/*      */     }
/*      */     
/*  731 */     _verifyValueWrite("write number");
/*  732 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(float f)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  739 */     if ((this._cfgNumbersAsStrings) || (((Float.isNaN(f)) || (Float.isInfinite(f))) && (isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))))
/*      */     {
/*      */ 
/*      */ 
/*  743 */       writeString(String.valueOf(f));
/*  744 */       return;
/*      */     }
/*      */     
/*  747 */     _verifyValueWrite("write number");
/*  748 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeNumber(java.math.BigDecimal value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  756 */     _verifyValueWrite("write number");
/*  757 */     if (value == null) {
/*  758 */       _writeNull();
/*  759 */     } else if (this._cfgNumbersAsStrings) {
/*  760 */       _writeQuotedRaw(value);
/*      */     } else {
/*  762 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(String encodedValue)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  770 */     _verifyValueWrite("write number");
/*  771 */     if (this._cfgNumbersAsStrings) {
/*  772 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/*  774 */       writeRaw(encodedValue);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _writeQuotedRaw(Object value) throws IOException
/*      */   {
/*  780 */     if (this._outputTail >= this._outputEnd) {
/*  781 */       _flushBuffer();
/*      */     }
/*  783 */     this._outputBuffer[(this._outputTail++)] = '"';
/*  784 */     writeRaw(value.toString());
/*  785 */     if (this._outputTail >= this._outputEnd) {
/*  786 */       _flushBuffer();
/*      */     }
/*  788 */     this._outputBuffer[(this._outputTail++)] = '"';
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeBoolean(boolean state)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  795 */     _verifyValueWrite("write boolean value");
/*  796 */     if (this._outputTail + 5 >= this._outputEnd) {
/*  797 */       _flushBuffer();
/*      */     }
/*  799 */     int ptr = this._outputTail;
/*  800 */     char[] buf = this._outputBuffer;
/*  801 */     if (state) {
/*  802 */       buf[ptr] = 't';
/*  803 */       buf[(++ptr)] = 'r';
/*  804 */       buf[(++ptr)] = 'u';
/*  805 */       buf[(++ptr)] = 'e';
/*      */     } else {
/*  807 */       buf[ptr] = 'f';
/*  808 */       buf[(++ptr)] = 'a';
/*  809 */       buf[(++ptr)] = 'l';
/*  810 */       buf[(++ptr)] = 's';
/*  811 */       buf[(++ptr)] = 'e';
/*      */     }
/*  813 */     this._outputTail = (ptr + 1);
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNull()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  820 */     _verifyValueWrite("write null value");
/*  821 */     _writeNull();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _verifyValueWrite(String typeMsg)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  834 */     int status = this._writeContext.writeValue();
/*  835 */     if (status == 5) {
/*  836 */       _reportError("Can not " + typeMsg + ", expecting field name");
/*      */     }
/*  838 */     if (this._cfgPrettyPrinter == null) {
/*      */       char c;
/*  840 */       switch (status) {
/*      */       case 1: 
/*  842 */         c = ',';
/*  843 */         break;
/*      */       case 2: 
/*  845 */         c = ':';
/*  846 */         break;
/*      */       case 3: 
/*  848 */         c = ' ';
/*  849 */         break;
/*      */       case 0: 
/*      */       default: 
/*  852 */         return;
/*      */       }
/*  854 */       if (this._outputTail >= this._outputEnd) {
/*  855 */         _flushBuffer();
/*      */       }
/*  857 */       this._outputBuffer[this._outputTail] = c;
/*  858 */       this._outputTail += 1;
/*  859 */       return;
/*      */     }
/*      */     
/*  862 */     _verifyPrettyValueWrite(typeMsg, status);
/*      */   }
/*      */   
/*      */ 
/*      */   protected final void _verifyPrettyValueWrite(String typeMsg, int status)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  869 */     switch (status) {
/*      */     case 1: 
/*  871 */       this._cfgPrettyPrinter.writeArrayValueSeparator(this);
/*  872 */       break;
/*      */     case 2: 
/*  874 */       this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
/*  875 */       break;
/*      */     case 3: 
/*  877 */       this._cfgPrettyPrinter.writeRootValueSeparator(this);
/*  878 */       break;
/*      */     
/*      */     case 0: 
/*  881 */       if (this._writeContext.inArray()) {
/*  882 */         this._cfgPrettyPrinter.beforeArrayValues(this);
/*  883 */       } else if (this._writeContext.inObject()) {
/*  884 */         this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */       }
/*      */       break;
/*      */     default: 
/*  888 */       _cantHappen();
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void flush()
/*      */     throws IOException
/*      */   {
/*  903 */     _flushBuffer();
/*  904 */     if ((this._writer != null) && 
/*  905 */       (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))) {
/*  906 */       this._writer.flush();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  915 */     super.close();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  921 */     if ((this._outputBuffer != null) && (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT))) {
/*      */       for (;;)
/*      */       {
/*  924 */         JsonStreamContext ctxt = getOutputContext();
/*  925 */         if (ctxt.inArray()) {
/*  926 */           writeEndArray();
/*  927 */         } else { if (!ctxt.inObject()) break;
/*  928 */           writeEndObject();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  934 */     _flushBuffer();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  942 */     if (this._writer != null) {
/*  943 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET))) {
/*  944 */         this._writer.close();
/*  945 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))
/*      */       {
/*  947 */         this._writer.flush();
/*      */       }
/*      */     }
/*      */     
/*  951 */     _releaseBuffers();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */   {
/*  957 */     char[] buf = this._outputBuffer;
/*  958 */     if (buf != null) {
/*  959 */       this._outputBuffer = null;
/*  960 */       this._ioContext.releaseConcatBuffer(buf);
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
/*      */   private void _writeString(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  978 */     int len = text.length();
/*  979 */     if (len > this._outputEnd) {
/*  980 */       _writeLongString(text);
/*  981 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  986 */     if (this._outputTail + len > this._outputEnd) {
/*  987 */       _flushBuffer();
/*      */     }
/*  989 */     text.getChars(0, len, this._outputBuffer, this._outputTail);
/*      */     
/*  991 */     if (this._characterEscapes != null) {
/*  992 */       _writeStringCustom(len);
/*  993 */     } else if (this._maximumNonEscapedChar != 0) {
/*  994 */       _writeStringASCII(len, this._maximumNonEscapedChar);
/*      */     } else {
/*  996 */       _writeString2(len);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void _writeString2(int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1004 */     int end = this._outputTail + len;
/* 1005 */     int[] escCodes = this._outputEscapes;
/* 1006 */     int escLen = escCodes.length;
/*      */     
/*      */ 
/* 1009 */     while (this._outputTail < end)
/*      */     {
/*      */       for (;;)
/*      */       {
/* 1013 */         char c = this._outputBuffer[this._outputTail];
/* 1014 */         if ((c < escLen) && (escCodes[c] != 0)) {
/*      */           break;
/*      */         }
/* 1017 */         if (++this._outputTail >= end) {
/*      */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1026 */       int flushLen = this._outputTail - this._outputHead;
/* 1027 */       if (flushLen > 0) {
/* 1028 */         this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1033 */       char c = this._outputBuffer[(this._outputTail++)];
/* 1034 */       _prependOrWriteCharacterEscape(c, escCodes[c]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _writeLongString(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1046 */     _flushBuffer();
/*      */     
/*      */ 
/* 1049 */     int textLen = text.length();
/* 1050 */     int offset = 0;
/*      */     do {
/* 1052 */       int max = this._outputEnd;
/* 1053 */       int segmentLen = offset + max > textLen ? textLen - offset : max;
/*      */       
/* 1055 */       text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
/* 1056 */       if (this._characterEscapes != null) {
/* 1057 */         _writeSegmentCustom(segmentLen);
/* 1058 */       } else if (this._maximumNonEscapedChar != 0) {
/* 1059 */         _writeSegmentASCII(segmentLen, this._maximumNonEscapedChar);
/*      */       } else {
/* 1061 */         _writeSegment(segmentLen);
/*      */       }
/* 1063 */       offset += segmentLen;
/* 1064 */     } while (offset < textLen);
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
/*      */   private final void _writeSegment(int end)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1079 */     int[] escCodes = this._outputEscapes;
/* 1080 */     int escLen = escCodes.length;
/*      */     
/* 1082 */     int ptr = 0;
/* 1083 */     int start = ptr;
/*      */     
/*      */ 
/* 1086 */     while (ptr < end)
/*      */     {
/*      */       char c;
/*      */       for (;;) {
/* 1090 */         c = this._outputBuffer[ptr];
/* 1091 */         if ((c >= escLen) || (escCodes[c] == 0))
/*      */         {
/*      */ 
/* 1094 */           ptr++; if (ptr >= end) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1103 */       int flushLen = ptr - start;
/* 1104 */       if (flushLen > 0) {
/* 1105 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1106 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1110 */       ptr++;
/*      */       
/* 1112 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCodes[c]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeString(char[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1123 */     if (this._characterEscapes != null) {
/* 1124 */       _writeStringCustom(text, offset, len);
/* 1125 */       return;
/*      */     }
/* 1127 */     if (this._maximumNonEscapedChar != 0) {
/* 1128 */       _writeStringASCII(text, offset, len, this._maximumNonEscapedChar);
/* 1129 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1136 */     len += offset;
/* 1137 */     int[] escCodes = this._outputEscapes;
/* 1138 */     int escLen = escCodes.length;
/* 1139 */     while (offset < len) {
/* 1140 */       int start = offset;
/*      */       for (;;)
/*      */       {
/* 1143 */         char c = text[offset];
/* 1144 */         if ((c < escLen) && (escCodes[c] != 0)) {
/*      */           break;
/*      */         }
/* 1147 */         offset++; if (offset >= len) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1153 */       int newAmount = offset - start;
/* 1154 */       if (newAmount < 32)
/*      */       {
/* 1156 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1157 */           _flushBuffer();
/*      */         }
/* 1159 */         if (newAmount > 0) {
/* 1160 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1161 */           this._outputTail += newAmount;
/*      */         }
/*      */       } else {
/* 1164 */         _flushBuffer();
/* 1165 */         this._writer.write(text, start, newAmount);
/*      */       }
/*      */       
/* 1168 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1172 */       char c = text[(offset++)];
/* 1173 */       _appendCharacterEscape(c, escCodes[c]);
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
/*      */   private void _writeStringASCII(int len, int maxNonEscaped)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1192 */     int end = this._outputTail + len;
/* 1193 */     int[] escCodes = this._outputEscapes;
/* 1194 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1195 */     int escCode = 0;
/*      */     
/*      */ 
/* 1198 */     while (this._outputTail < end)
/*      */     {
/*      */       char c;
/*      */       do
/*      */       {
/* 1203 */         c = this._outputBuffer[this._outputTail];
/* 1204 */         if (c < escLimit) {
/* 1205 */           escCode = escCodes[c];
/* 1206 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1209 */         } else if (c > maxNonEscaped) {
/* 1210 */           escCode = -1;
/* 1211 */           break;
/*      */         }
/* 1213 */       } while (++this._outputTail < end);
/* 1214 */       break;
/*      */       
/*      */ 
/* 1217 */       int flushLen = this._outputTail - this._outputHead;
/* 1218 */       if (flushLen > 0) {
/* 1219 */         this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */       }
/* 1221 */       this._outputTail += 1;
/* 1222 */       _prependOrWriteCharacterEscape(c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _writeSegmentASCII(int end, int maxNonEscaped)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1229 */     int[] escCodes = this._outputEscapes;
/* 1230 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1232 */     int ptr = 0;
/* 1233 */     int escCode = 0;
/* 1234 */     int start = ptr;
/*      */     
/*      */ 
/* 1237 */     while (ptr < end)
/*      */     {
/*      */       char c;
/*      */       for (;;) {
/* 1241 */         c = this._outputBuffer[ptr];
/* 1242 */         if (c < escLimit) {
/* 1243 */           escCode = escCodes[c];
/* 1244 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1247 */         } else if (c > maxNonEscaped) {
/* 1248 */           escCode = -1;
/* 1249 */           break;
/*      */         }
/* 1251 */         ptr++; if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1255 */       int flushLen = ptr - start;
/* 1256 */       if (flushLen > 0) {
/* 1257 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1258 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1262 */       ptr++;
/* 1263 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _writeStringASCII(char[] text, int offset, int len, int maxNonEscaped)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1271 */     len += offset;
/* 1272 */     int[] escCodes = this._outputEscapes;
/* 1273 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/*      */     
/* 1275 */     int escCode = 0;
/*      */     
/* 1277 */     while (offset < len) {
/* 1278 */       int start = offset;
/*      */       char c;
/*      */       for (;;)
/*      */       {
/* 1282 */         c = text[offset];
/* 1283 */         if (c < escLimit) {
/* 1284 */           escCode = escCodes[c];
/* 1285 */           if (escCode != 0) {
/*      */             break;
/*      */           }
/* 1288 */         } else if (c > maxNonEscaped) {
/* 1289 */           escCode = -1;
/* 1290 */           break;
/*      */         }
/* 1292 */         offset++; if (offset >= len) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1298 */       int newAmount = offset - start;
/* 1299 */       if (newAmount < 32)
/*      */       {
/* 1301 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1302 */           _flushBuffer();
/*      */         }
/* 1304 */         if (newAmount > 0) {
/* 1305 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1306 */           this._outputTail += newAmount;
/*      */         }
/*      */       } else {
/* 1309 */         _flushBuffer();
/* 1310 */         this._writer.write(text, start, newAmount);
/*      */       }
/*      */       
/* 1313 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1317 */       offset++;
/* 1318 */       _appendCharacterEscape(c, escCode);
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
/*      */   private void _writeStringCustom(int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1337 */     int end = this._outputTail + len;
/* 1338 */     int[] escCodes = this._outputEscapes;
/* 1339 */     int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
/* 1340 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1341 */     int escCode = 0;
/* 1342 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/*      */ 
/* 1345 */     while (this._outputTail < end)
/*      */     {
/*      */       char c;
/*      */       do
/*      */       {
/* 1350 */         c = this._outputBuffer[this._outputTail];
/* 1351 */         if (c < escLimit) {
/* 1352 */           escCode = escCodes[c];
/* 1353 */           if (escCode != 0)
/*      */             break;
/*      */         } else {
/* 1356 */           if (c > maxNonEscaped) {
/* 1357 */             escCode = -1;
/* 1358 */             break;
/*      */           }
/* 1360 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1361 */             escCode = -2;
/* 1362 */             break;
/*      */           }
/*      */         }
/* 1365 */       } while (++this._outputTail < end);
/* 1366 */       break;
/*      */       
/*      */ 
/* 1369 */       int flushLen = this._outputTail - this._outputHead;
/* 1370 */       if (flushLen > 0) {
/* 1371 */         this._writer.write(this._outputBuffer, this._outputHead, flushLen);
/*      */       }
/* 1373 */       this._outputTail += 1;
/* 1374 */       _prependOrWriteCharacterEscape(c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _writeSegmentCustom(int end)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1381 */     int[] escCodes = this._outputEscapes;
/* 1382 */     int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
/* 1383 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1384 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1386 */     int ptr = 0;
/* 1387 */     int escCode = 0;
/* 1388 */     int start = ptr;
/*      */     
/*      */ 
/* 1391 */     while (ptr < end)
/*      */     {
/*      */       char c;
/*      */       for (;;) {
/* 1395 */         c = this._outputBuffer[ptr];
/* 1396 */         if (c < escLimit) {
/* 1397 */           escCode = escCodes[c];
/* 1398 */           if (escCode != 0)
/*      */             break;
/*      */         } else {
/* 1401 */           if (c > maxNonEscaped) {
/* 1402 */             escCode = -1;
/* 1403 */             break;
/*      */           }
/* 1405 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1406 */             escCode = -2;
/* 1407 */             break;
/*      */           }
/*      */         }
/* 1410 */         ptr++; if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1414 */       int flushLen = ptr - start;
/* 1415 */       if (flushLen > 0) {
/* 1416 */         this._writer.write(this._outputBuffer, start, flushLen);
/* 1417 */         if (ptr >= end) {
/*      */           break;
/*      */         }
/*      */       }
/* 1421 */       ptr++;
/* 1422 */       start = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCode);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _writeStringCustom(char[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1429 */     len += offset;
/* 1430 */     int[] escCodes = this._outputEscapes;
/* 1431 */     int maxNonEscaped = this._maximumNonEscapedChar < 1 ? 65535 : this._maximumNonEscapedChar;
/* 1432 */     int escLimit = Math.min(escCodes.length, maxNonEscaped + 1);
/* 1433 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1435 */     int escCode = 0;
/*      */     
/* 1437 */     while (offset < len) {
/* 1438 */       int start = offset;
/*      */       char c;
/*      */       for (;;)
/*      */       {
/* 1442 */         c = text[offset];
/* 1443 */         if (c < escLimit) {
/* 1444 */           escCode = escCodes[c];
/* 1445 */           if (escCode != 0)
/*      */             break;
/*      */         } else {
/* 1448 */           if (c > maxNonEscaped) {
/* 1449 */             escCode = -1;
/* 1450 */             break;
/*      */           }
/* 1452 */           if ((this._currentEscape = customEscapes.getEscapeSequence(c)) != null) {
/* 1453 */             escCode = -2;
/* 1454 */             break;
/*      */           }
/*      */         }
/* 1457 */         offset++; if (offset >= len) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1463 */       int newAmount = offset - start;
/* 1464 */       if (newAmount < 32)
/*      */       {
/* 1466 */         if (this._outputTail + newAmount > this._outputEnd) {
/* 1467 */           _flushBuffer();
/*      */         }
/* 1469 */         if (newAmount > 0) {
/* 1470 */           System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
/* 1471 */           this._outputTail += newAmount;
/*      */         }
/*      */       } else {
/* 1474 */         _flushBuffer();
/* 1475 */         this._writer.write(text, start, newAmount);
/*      */       }
/*      */       
/* 1478 */       if (offset >= len) {
/*      */         break;
/*      */       }
/*      */       
/* 1482 */       offset++;
/* 1483 */       _appendCharacterEscape(c, escCode);
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
/*      */   protected void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1497 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1499 */     int safeOutputEnd = this._outputEnd - 6;
/* 1500 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/*      */ 
/* 1503 */     while (inputPtr <= safeInputEnd) {
/* 1504 */       if (this._outputTail > safeOutputEnd) {
/* 1505 */         _flushBuffer();
/*      */       }
/*      */       
/* 1508 */       int b24 = input[(inputPtr++)] << 8;
/* 1509 */       b24 |= input[(inputPtr++)] & 0xFF;
/* 1510 */       b24 = b24 << 8 | input[(inputPtr++)] & 0xFF;
/* 1511 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1512 */       chunksBeforeLF--; if (chunksBeforeLF <= 0)
/*      */       {
/* 1514 */         this._outputBuffer[(this._outputTail++)] = '\\';
/* 1515 */         this._outputBuffer[(this._outputTail++)] = 'n';
/* 1516 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1521 */     int inputLeft = inputEnd - inputPtr;
/* 1522 */     if (inputLeft > 0) {
/* 1523 */       if (this._outputTail > safeOutputEnd) {
/* 1524 */         _flushBuffer();
/*      */       }
/* 1526 */       int b24 = input[(inputPtr++)] << 16;
/* 1527 */       if (inputLeft == 2) {
/* 1528 */         b24 |= (input[(inputPtr++)] & 0xFF) << 8;
/*      */       }
/* 1530 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeNull()
/*      */     throws IOException
/*      */   {
/* 1542 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 1543 */       _flushBuffer();
/*      */     }
/* 1545 */     int ptr = this._outputTail;
/* 1546 */     char[] buf = this._outputBuffer;
/* 1547 */     buf[ptr] = 'n';
/* 1548 */     buf[(++ptr)] = 'u';
/* 1549 */     buf[(++ptr)] = 'l';
/* 1550 */     buf[(++ptr)] = 'l';
/* 1551 */     this._outputTail = (ptr + 1);
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
/*      */   private final void _prependOrWriteCharacterEscape(char ch, int escCode)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1568 */     if (escCode >= 0) {
/* 1569 */       if (this._outputTail >= 2) {
/* 1570 */         int ptr = this._outputTail - 2;
/* 1571 */         this._outputHead = ptr;
/* 1572 */         this._outputBuffer[(ptr++)] = '\\';
/* 1573 */         this._outputBuffer[ptr] = ((char)escCode);
/* 1574 */         return;
/*      */       }
/*      */       
/* 1577 */       char[] buf = this._entityBuffer;
/* 1578 */       if (buf == null) {
/* 1579 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1581 */       this._outputHead = this._outputTail;
/* 1582 */       buf[1] = ((char)escCode);
/* 1583 */       this._writer.write(buf, 0, 2);
/* 1584 */       return;
/*      */     }
/* 1586 */     if (escCode != -2) {
/* 1587 */       if (this._outputTail >= 6) {
/* 1588 */         char[] buf = this._outputBuffer;
/* 1589 */         int ptr = this._outputTail - 6;
/* 1590 */         this._outputHead = ptr;
/* 1591 */         buf[ptr] = '\\';
/* 1592 */         buf[(++ptr)] = 'u';
/*      */         
/* 1594 */         if (ch > 'ÿ') {
/* 1595 */           int hi = ch >> '\b' & 0xFF;
/* 1596 */           buf[(++ptr)] = HEX_CHARS[(hi >> 4)];
/* 1597 */           buf[(++ptr)] = HEX_CHARS[(hi & 0xF)];
/* 1598 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1600 */           buf[(++ptr)] = '0';
/* 1601 */           buf[(++ptr)] = '0';
/*      */         }
/* 1603 */         buf[(++ptr)] = HEX_CHARS[(ch >> '\004')];
/* 1604 */         buf[(++ptr)] = HEX_CHARS[(ch & 0xF)];
/* 1605 */         return;
/*      */       }
/*      */       
/* 1608 */       char[] buf = this._entityBuffer;
/* 1609 */       if (buf == null) {
/* 1610 */         buf = _allocateEntityBuffer();
/*      */       }
/* 1612 */       this._outputHead = this._outputTail;
/* 1613 */       if (ch > 'ÿ') {
/* 1614 */         int hi = ch >> '\b' & 0xFF;
/* 1615 */         int lo = ch & 0xFF;
/* 1616 */         buf[10] = HEX_CHARS[(hi >> 4)];
/* 1617 */         buf[11] = HEX_CHARS[(hi & 0xF)];
/* 1618 */         buf[12] = HEX_CHARS[(lo >> 4)];
/* 1619 */         buf[13] = HEX_CHARS[(lo & 0xF)];
/* 1620 */         this._writer.write(buf, 8, 6);
/*      */       } else {
/* 1622 */         buf[6] = HEX_CHARS[(ch >> '\004')];
/* 1623 */         buf[7] = HEX_CHARS[(ch & 0xF)];
/* 1624 */         this._writer.write(buf, 2, 6);
/*      */       }
/*      */       return;
/*      */     }
/*      */     String escape;
/*      */     String escape;
/* 1630 */     if (this._currentEscape == null) {
/* 1631 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1633 */       escape = this._currentEscape.getValue();
/* 1634 */       this._currentEscape = null;
/*      */     }
/* 1636 */     int len = escape.length();
/* 1637 */     if (this._outputTail >= len) {
/* 1638 */       int ptr = this._outputTail - len;
/* 1639 */       this._outputHead = ptr;
/* 1640 */       escape.getChars(0, len, this._outputBuffer, ptr);
/* 1641 */       return;
/*      */     }
/*      */     
/* 1644 */     this._outputHead = this._outputTail;
/* 1645 */     this._writer.write(escape);
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
/*      */   private final int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch, int escCode)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1659 */     if (escCode >= 0) {
/* 1660 */       if ((ptr > 1) && (ptr < end)) {
/* 1661 */         ptr -= 2;
/* 1662 */         buffer[ptr] = '\\';
/* 1663 */         buffer[(ptr + 1)] = ((char)escCode);
/*      */       } else {
/* 1665 */         char[] ent = this._entityBuffer;
/* 1666 */         if (ent == null) {
/* 1667 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1669 */         ent[1] = ((char)escCode);
/* 1670 */         this._writer.write(ent, 0, 2);
/*      */       }
/* 1672 */       return ptr;
/*      */     }
/* 1674 */     if (escCode != -2) {
/* 1675 */       if ((ptr > 5) && (ptr < end)) {
/* 1676 */         ptr -= 6;
/* 1677 */         buffer[(ptr++)] = '\\';
/* 1678 */         buffer[(ptr++)] = 'u';
/*      */         
/* 1680 */         if (ch > 'ÿ') {
/* 1681 */           int hi = ch >> '\b' & 0xFF;
/* 1682 */           buffer[(ptr++)] = HEX_CHARS[(hi >> 4)];
/* 1683 */           buffer[(ptr++)] = HEX_CHARS[(hi & 0xF)];
/* 1684 */           ch = (char)(ch & 0xFF);
/*      */         } else {
/* 1686 */           buffer[(ptr++)] = '0';
/* 1687 */           buffer[(ptr++)] = '0';
/*      */         }
/* 1689 */         buffer[(ptr++)] = HEX_CHARS[(ch >> '\004')];
/* 1690 */         buffer[ptr] = HEX_CHARS[(ch & 0xF)];
/* 1691 */         ptr -= 5;
/*      */       }
/*      */       else {
/* 1694 */         char[] ent = this._entityBuffer;
/* 1695 */         if (ent == null) {
/* 1696 */           ent = _allocateEntityBuffer();
/*      */         }
/* 1698 */         this._outputHead = this._outputTail;
/* 1699 */         if (ch > 'ÿ') {
/* 1700 */           int hi = ch >> '\b' & 0xFF;
/* 1701 */           int lo = ch & 0xFF;
/* 1702 */           ent[10] = HEX_CHARS[(hi >> 4)];
/* 1703 */           ent[11] = HEX_CHARS[(hi & 0xF)];
/* 1704 */           ent[12] = HEX_CHARS[(lo >> 4)];
/* 1705 */           ent[13] = HEX_CHARS[(lo & 0xF)];
/* 1706 */           this._writer.write(ent, 8, 6);
/*      */         } else {
/* 1708 */           ent[6] = HEX_CHARS[(ch >> '\004')];
/* 1709 */           ent[7] = HEX_CHARS[(ch & 0xF)];
/* 1710 */           this._writer.write(ent, 2, 6);
/*      */         }
/*      */       }
/* 1713 */       return ptr; }
/*      */     String escape;
/*      */     String escape;
/* 1716 */     if (this._currentEscape == null) {
/* 1717 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1719 */       escape = this._currentEscape.getValue();
/* 1720 */       this._currentEscape = null;
/*      */     }
/* 1722 */     int len = escape.length();
/* 1723 */     if ((ptr >= len) && (ptr < end)) {
/* 1724 */       ptr -= len;
/* 1725 */       escape.getChars(0, len, buffer, ptr);
/*      */     } else {
/* 1727 */       this._writer.write(escape);
/*      */     }
/* 1729 */     return ptr;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _appendCharacterEscape(char ch, int escCode)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1739 */     if (escCode >= 0) {
/* 1740 */       if (this._outputTail + 2 > this._outputEnd) {
/* 1741 */         _flushBuffer();
/*      */       }
/* 1743 */       this._outputBuffer[(this._outputTail++)] = '\\';
/* 1744 */       this._outputBuffer[(this._outputTail++)] = ((char)escCode);
/* 1745 */       return;
/*      */     }
/* 1747 */     if (escCode != -2) {
/* 1748 */       if (this._outputTail + 2 > this._outputEnd) {
/* 1749 */         _flushBuffer();
/*      */       }
/* 1751 */       int ptr = this._outputTail;
/* 1752 */       char[] buf = this._outputBuffer;
/* 1753 */       buf[(ptr++)] = '\\';
/* 1754 */       buf[(ptr++)] = 'u';
/*      */       
/* 1756 */       if (ch > 'ÿ') {
/* 1757 */         int hi = ch >> '\b' & 0xFF;
/* 1758 */         buf[(ptr++)] = HEX_CHARS[(hi >> 4)];
/* 1759 */         buf[(ptr++)] = HEX_CHARS[(hi & 0xF)];
/* 1760 */         ch = (char)(ch & 0xFF);
/*      */       } else {
/* 1762 */         buf[(ptr++)] = '0';
/* 1763 */         buf[(ptr++)] = '0';
/*      */       }
/* 1765 */       buf[(ptr++)] = HEX_CHARS[(ch >> '\004')];
/* 1766 */       buf[ptr] = HEX_CHARS[(ch & 0xF)];
/* 1767 */       this._outputTail = ptr; return;
/*      */     }
/*      */     String escape;
/*      */     String escape;
/* 1771 */     if (this._currentEscape == null) {
/* 1772 */       escape = this._characterEscapes.getEscapeSequence(ch).getValue();
/*      */     } else {
/* 1774 */       escape = this._currentEscape.getValue();
/* 1775 */       this._currentEscape = null;
/*      */     }
/* 1777 */     int len = escape.length();
/* 1778 */     if (this._outputTail + len > this._outputEnd) {
/* 1779 */       _flushBuffer();
/* 1780 */       if (len > this._outputEnd) {
/* 1781 */         this._writer.write(escape);
/* 1782 */         return;
/*      */       }
/*      */     }
/* 1785 */     escape.getChars(0, len, this._outputBuffer, this._outputTail);
/* 1786 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */   private char[] _allocateEntityBuffer()
/*      */   {
/* 1791 */     char[] buf = new char[14];
/*      */     
/* 1793 */     buf[0] = '\\';
/*      */     
/* 1795 */     buf[2] = '\\';
/* 1796 */     buf[3] = 'u';
/* 1797 */     buf[4] = '0';
/* 1798 */     buf[5] = '0';
/*      */     
/* 1800 */     buf[8] = '\\';
/* 1801 */     buf[9] = 'u';
/* 1802 */     this._entityBuffer = buf;
/* 1803 */     return buf;
/*      */   }
/*      */   
/*      */   protected final void _flushBuffer() throws IOException
/*      */   {
/* 1808 */     int len = this._outputTail - this._outputHead;
/* 1809 */     if (len > 0) {
/* 1810 */       int offset = this._outputHead;
/* 1811 */       this._outputTail = (this._outputHead = 0);
/* 1812 */       this._writer.write(this._outputBuffer, offset, len);
/*      */     }
/*      */   }
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\WriterBasedGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */