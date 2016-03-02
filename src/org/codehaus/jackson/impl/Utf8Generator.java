/*      */ package org.codehaus.jackson.impl;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import org.codehaus.jackson.Base64Variant;
/*      */ import org.codehaus.jackson.JsonGenerationException;
/*      */ import org.codehaus.jackson.JsonGenerator;
/*      */ import org.codehaus.jackson.JsonGenerator.Feature;
/*      */ import org.codehaus.jackson.JsonStreamContext;
/*      */ import org.codehaus.jackson.ObjectCodec;
/*      */ import org.codehaus.jackson.PrettyPrinter;
/*      */ import org.codehaus.jackson.SerializableString;
/*      */ import org.codehaus.jackson.io.CharacterEscapes;
/*      */ import org.codehaus.jackson.io.IOContext;
/*      */ import org.codehaus.jackson.io.NumberOutput;
/*      */ import org.codehaus.jackson.io.SerializedString;
/*      */ import org.codehaus.jackson.util.CharTypes;
/*      */ 
/*      */ public class Utf8Generator
/*      */   extends JsonGeneratorBase
/*      */ {
/*      */   private static final byte BYTE_u = 117;
/*      */   private static final byte BYTE_0 = 48;
/*      */   private static final byte BYTE_LBRACKET = 91;
/*      */   private static final byte BYTE_RBRACKET = 93;
/*      */   private static final byte BYTE_LCURLY = 123;
/*      */   private static final byte BYTE_RCURLY = 125;
/*      */   private static final byte BYTE_BACKSLASH = 92;
/*      */   private static final byte BYTE_SPACE = 32;
/*      */   private static final byte BYTE_COMMA = 44;
/*      */   private static final byte BYTE_COLON = 58;
/*      */   private static final byte BYTE_QUOTE = 34;
/*      */   protected static final int SURR1_FIRST = 55296;
/*      */   protected static final int SURR1_LAST = 56319;
/*      */   protected static final int SURR2_FIRST = 56320;
/*      */   protected static final int SURR2_LAST = 57343;
/*      */   private static final int MAX_BYTES_TO_BUFFER = 512;
/*   40 */   static final byte[] HEX_CHARS = ;
/*      */   
/*   42 */   private static final byte[] NULL_BYTES = { 110, 117, 108, 108 };
/*   43 */   private static final byte[] TRUE_BYTES = { 116, 114, 117, 101 };
/*   44 */   private static final byte[] FALSE_BYTES = { 102, 97, 108, 115, 101 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   50 */   protected static final int[] sOutputEscapes = CharTypes.get7BitOutputEscapes();
/*      */   
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
/*      */ 
/*      */   protected final OutputStream _outputStream;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   77 */   protected int[] _outputEscapes = sOutputEscapes;
/*      */   
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
/*      */   protected byte[] _outputBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  115 */   protected int _outputTail = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int _outputEnd;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int _outputMaxContiguous;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected char[] _charBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final int _charBufferLength;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] _entityBuffer;
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
/*      */   public Utf8Generator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out)
/*      */   {
/*  162 */     super(features, codec);
/*  163 */     this._ioContext = ctxt;
/*  164 */     this._outputStream = out;
/*  165 */     this._bufferRecyclable = true;
/*  166 */     this._outputBuffer = ctxt.allocWriteEncodingBuffer();
/*  167 */     this._outputEnd = this._outputBuffer.length;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  172 */     this._outputMaxContiguous = (this._outputEnd >> 3);
/*  173 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  174 */     this._charBufferLength = this._charBuffer.length;
/*      */     
/*      */ 
/*  177 */     if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
/*  178 */       setHighestNonEscapedChar(127);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Utf8Generator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable)
/*      */   {
/*  186 */     super(features, codec);
/*  187 */     this._ioContext = ctxt;
/*  188 */     this._outputStream = out;
/*  189 */     this._bufferRecyclable = bufferRecyclable;
/*  190 */     this._outputTail = outputOffset;
/*  191 */     this._outputBuffer = outputBuffer;
/*  192 */     this._outputEnd = this._outputBuffer.length;
/*      */     
/*  194 */     this._outputMaxContiguous = (this._outputEnd >> 3);
/*  195 */     this._charBuffer = ctxt.allocConcatBuffer();
/*  196 */     this._charBufferLength = this._charBuffer.length;
/*      */     
/*  198 */     if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
/*  199 */       setHighestNonEscapedChar(127);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonGenerator setHighestNonEscapedChar(int charCode)
/*      */   {
/*  211 */     this._maximumNonEscapedChar = (charCode < 0 ? 0 : charCode);
/*  212 */     return this;
/*      */   }
/*      */   
/*      */   public int getHighestEscapedChar()
/*      */   {
/*  217 */     return this._maximumNonEscapedChar;
/*      */   }
/*      */   
/*      */ 
/*      */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc)
/*      */   {
/*  223 */     this._characterEscapes = esc;
/*  224 */     if (esc == null) {
/*  225 */       this._outputEscapes = sOutputEscapes;
/*      */     } else {
/*  227 */       this._outputEscapes = esc.getEscapeCodesForAscii();
/*      */     }
/*  229 */     return this;
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
/*  240 */     return this._characterEscapes;
/*      */   }
/*      */   
/*      */   public Object getOutputTarget()
/*      */   {
/*  245 */     return this._outputStream;
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
/*      */   public final void writeStringField(String fieldName, String value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  261 */     writeFieldName(fieldName);
/*  262 */     writeString(value);
/*      */   }
/*      */   
/*      */   public final void writeFieldName(String name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  268 */     int status = this._writeContext.writeFieldName(name);
/*  269 */     if (status == 4) {
/*  270 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  272 */     if (this._cfgPrettyPrinter != null) {
/*  273 */       _writePPFieldName(name, status == 1);
/*  274 */       return;
/*      */     }
/*  276 */     if (status == 1) {
/*  277 */       if (this._outputTail >= this._outputEnd) {
/*  278 */         _flushBuffer();
/*      */       }
/*  280 */       this._outputBuffer[(this._outputTail++)] = 44;
/*      */     }
/*  282 */     _writeFieldName(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void writeFieldName(SerializedString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  290 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  291 */     if (status == 4) {
/*  292 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  294 */     if (this._cfgPrettyPrinter != null) {
/*  295 */       _writePPFieldName(name, status == 1);
/*  296 */       return;
/*      */     }
/*  298 */     if (status == 1) {
/*  299 */       if (this._outputTail >= this._outputEnd) {
/*  300 */         _flushBuffer();
/*      */       }
/*  302 */       this._outputBuffer[(this._outputTail++)] = 44;
/*      */     }
/*  304 */     _writeFieldName(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final void writeFieldName(SerializableString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  312 */     int status = this._writeContext.writeFieldName(name.getValue());
/*  313 */     if (status == 4) {
/*  314 */       _reportError("Can not write a field name, expecting a value");
/*      */     }
/*  316 */     if (this._cfgPrettyPrinter != null) {
/*  317 */       _writePPFieldName(name, status == 1);
/*  318 */       return;
/*      */     }
/*  320 */     if (status == 1) {
/*  321 */       if (this._outputTail >= this._outputEnd) {
/*  322 */         _flushBuffer();
/*      */       }
/*  324 */       this._outputBuffer[(this._outputTail++)] = 44;
/*      */     }
/*  326 */     _writeFieldName(name);
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
/*  338 */     _verifyValueWrite("start an array");
/*  339 */     this._writeContext = this._writeContext.createChildArrayContext();
/*  340 */     if (this._cfgPrettyPrinter != null) {
/*  341 */       this._cfgPrettyPrinter.writeStartArray(this);
/*      */     } else {
/*  343 */       if (this._outputTail >= this._outputEnd) {
/*  344 */         _flushBuffer();
/*      */       }
/*  346 */       this._outputBuffer[(this._outputTail++)] = 91;
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeEndArray()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  353 */     if (!this._writeContext.inArray()) {
/*  354 */       _reportError("Current context not an ARRAY but " + this._writeContext.getTypeDesc());
/*      */     }
/*  356 */     if (this._cfgPrettyPrinter != null) {
/*  357 */       this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  359 */       if (this._outputTail >= this._outputEnd) {
/*  360 */         _flushBuffer();
/*      */       }
/*  362 */       this._outputBuffer[(this._outputTail++)] = 93;
/*      */     }
/*  364 */     this._writeContext = this._writeContext.getParent();
/*      */   }
/*      */   
/*      */   public final void writeStartObject()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  370 */     _verifyValueWrite("start an object");
/*  371 */     this._writeContext = this._writeContext.createChildObjectContext();
/*  372 */     if (this._cfgPrettyPrinter != null) {
/*  373 */       this._cfgPrettyPrinter.writeStartObject(this);
/*      */     } else {
/*  375 */       if (this._outputTail >= this._outputEnd) {
/*  376 */         _flushBuffer();
/*      */       }
/*  378 */       this._outputBuffer[(this._outputTail++)] = 123;
/*      */     }
/*      */   }
/*      */   
/*      */   public final void writeEndObject()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  385 */     if (!this._writeContext.inObject()) {
/*  386 */       _reportError("Current context not an object but " + this._writeContext.getTypeDesc());
/*      */     }
/*  388 */     if (this._cfgPrettyPrinter != null) {
/*  389 */       this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
/*      */     } else {
/*  391 */       if (this._outputTail >= this._outputEnd) {
/*  392 */         _flushBuffer();
/*      */       }
/*  394 */       this._outputBuffer[(this._outputTail++)] = 125;
/*      */     }
/*  396 */     this._writeContext = this._writeContext.getParent();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _writeFieldName(String name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  405 */     if (!isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
/*  406 */       _writeStringSegments(name);
/*  407 */       return;
/*      */     }
/*  409 */     if (this._outputTail >= this._outputEnd) {
/*  410 */       _flushBuffer();
/*      */     }
/*  412 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */     
/*  414 */     int len = name.length();
/*  415 */     if (len <= this._charBufferLength) {
/*  416 */       name.getChars(0, len, this._charBuffer, 0);
/*      */       
/*  418 */       if (len <= this._outputMaxContiguous) {
/*  419 */         if (this._outputTail + len > this._outputEnd) {
/*  420 */           _flushBuffer();
/*      */         }
/*  422 */         _writeStringSegment(this._charBuffer, 0, len);
/*      */       } else {
/*  424 */         _writeStringSegments(this._charBuffer, 0, len);
/*      */       }
/*      */     } else {
/*  427 */       _writeStringSegments(name);
/*      */     }
/*      */     
/*      */ 
/*  431 */     if (this._outputTail >= this._outputEnd) {
/*  432 */       _flushBuffer();
/*      */     }
/*  434 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */   protected final void _writeFieldName(SerializableString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  440 */     byte[] raw = name.asQuotedUTF8();
/*  441 */     if (!isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
/*  442 */       _writeBytes(raw);
/*  443 */       return;
/*      */     }
/*  445 */     if (this._outputTail >= this._outputEnd) {
/*  446 */       _flushBuffer();
/*      */     }
/*  448 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */     
/*      */ 
/*  451 */     int len = raw.length;
/*  452 */     if (this._outputTail + len + 1 < this._outputEnd) {
/*  453 */       System.arraycopy(raw, 0, this._outputBuffer, this._outputTail, len);
/*  454 */       this._outputTail += len;
/*  455 */       this._outputBuffer[(this._outputTail++)] = 34;
/*      */     } else {
/*  457 */       _writeBytes(raw);
/*  458 */       if (this._outputTail >= this._outputEnd) {
/*  459 */         _flushBuffer();
/*      */       }
/*  461 */       this._outputBuffer[(this._outputTail++)] = 34;
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
/*  472 */     if (commaBefore) {
/*  473 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  475 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*      */     
/*  478 */     if (isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES)) {
/*  479 */       if (this._outputTail >= this._outputEnd) {
/*  480 */         _flushBuffer();
/*      */       }
/*  482 */       this._outputBuffer[(this._outputTail++)] = 34;
/*  483 */       int len = name.length();
/*  484 */       if (len <= this._charBufferLength) {
/*  485 */         name.getChars(0, len, this._charBuffer, 0);
/*      */         
/*  487 */         if (len <= this._outputMaxContiguous) {
/*  488 */           if (this._outputTail + len > this._outputEnd) {
/*  489 */             _flushBuffer();
/*      */           }
/*  491 */           _writeStringSegment(this._charBuffer, 0, len);
/*      */         } else {
/*  493 */           _writeStringSegments(this._charBuffer, 0, len);
/*      */         }
/*      */       } else {
/*  496 */         _writeStringSegments(name);
/*      */       }
/*  498 */       if (this._outputTail >= this._outputEnd) {
/*  499 */         _flushBuffer();
/*      */       }
/*  501 */       this._outputBuffer[(this._outputTail++)] = 34;
/*      */     } else {
/*  503 */       _writeStringSegments(name);
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void _writePPFieldName(SerializableString name, boolean commaBefore)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  510 */     if (commaBefore) {
/*  511 */       this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
/*      */     } else {
/*  513 */       this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */     }
/*      */     
/*  516 */     boolean addQuotes = isEnabled(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
/*  517 */     if (addQuotes) {
/*  518 */       if (this._outputTail >= this._outputEnd) {
/*  519 */         _flushBuffer();
/*      */       }
/*  521 */       this._outputBuffer[(this._outputTail++)] = 34;
/*      */     }
/*  523 */     _writeBytes(name.asQuotedUTF8());
/*  524 */     if (addQuotes) {
/*  525 */       if (this._outputTail >= this._outputEnd) {
/*  526 */         _flushBuffer();
/*      */       }
/*  528 */       this._outputBuffer[(this._outputTail++)] = 34;
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
/*  542 */     _verifyValueWrite("write text value");
/*  543 */     if (text == null) {
/*  544 */       _writeNull();
/*  545 */       return;
/*      */     }
/*      */     
/*  548 */     int len = text.length();
/*  549 */     if (len > this._charBufferLength) {
/*  550 */       _writeLongString(text);
/*  551 */       return;
/*      */     }
/*      */     
/*  554 */     text.getChars(0, len, this._charBuffer, 0);
/*      */     
/*  556 */     if (len > this._outputMaxContiguous) {
/*  557 */       _writeLongString(this._charBuffer, 0, len);
/*  558 */       return;
/*      */     }
/*  560 */     if (this._outputTail + len >= this._outputEnd) {
/*  561 */       _flushBuffer();
/*      */     }
/*  563 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  564 */     _writeStringSegment(this._charBuffer, 0, len);
/*      */     
/*      */ 
/*      */ 
/*  568 */     if (this._outputTail >= this._outputEnd) {
/*  569 */       _flushBuffer();
/*      */     }
/*  571 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */   private final void _writeLongString(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  577 */     if (this._outputTail >= this._outputEnd) {
/*  578 */       _flushBuffer();
/*      */     }
/*  580 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  581 */     _writeStringSegments(text);
/*  582 */     if (this._outputTail >= this._outputEnd) {
/*  583 */       _flushBuffer();
/*      */     }
/*  585 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */   private final void _writeLongString(char[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  591 */     if (this._outputTail >= this._outputEnd) {
/*  592 */       _flushBuffer();
/*      */     }
/*  594 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  595 */     _writeStringSegments(this._charBuffer, 0, len);
/*  596 */     if (this._outputTail >= this._outputEnd) {
/*  597 */       _flushBuffer();
/*      */     }
/*  599 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeString(char[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  606 */     _verifyValueWrite("write text value");
/*  607 */     if (this._outputTail >= this._outputEnd) {
/*  608 */       _flushBuffer();
/*      */     }
/*  610 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */     
/*  612 */     if (len <= this._outputMaxContiguous) {
/*  613 */       if (this._outputTail + len > this._outputEnd) {
/*  614 */         _flushBuffer();
/*      */       }
/*  616 */       _writeStringSegment(text, offset, len);
/*      */     } else {
/*  618 */       _writeStringSegments(text, offset, len);
/*      */     }
/*      */     
/*  621 */     if (this._outputTail >= this._outputEnd) {
/*  622 */       _flushBuffer();
/*      */     }
/*  624 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */ 
/*      */   public final void writeString(SerializableString text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  631 */     _verifyValueWrite("write text value");
/*  632 */     if (this._outputTail >= this._outputEnd) {
/*  633 */       _flushBuffer();
/*      */     }
/*  635 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  636 */     _writeBytes(text.asQuotedUTF8());
/*  637 */     if (this._outputTail >= this._outputEnd) {
/*  638 */       _flushBuffer();
/*      */     }
/*  640 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  647 */     _verifyValueWrite("write text value");
/*  648 */     if (this._outputTail >= this._outputEnd) {
/*  649 */       _flushBuffer();
/*      */     }
/*  651 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  652 */     _writeBytes(text, offset, length);
/*  653 */     if (this._outputTail >= this._outputEnd) {
/*  654 */       _flushBuffer();
/*      */     }
/*  656 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeUTF8String(byte[] text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  663 */     _verifyValueWrite("write text value");
/*  664 */     if (this._outputTail >= this._outputEnd) {
/*  665 */       _flushBuffer();
/*      */     }
/*  667 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */     
/*  669 */     if (len <= this._outputMaxContiguous) {
/*  670 */       _writeUTF8Segment(text, offset, len);
/*      */     } else {
/*  672 */       _writeUTF8Segments(text, offset, len);
/*      */     }
/*  674 */     if (this._outputTail >= this._outputEnd) {
/*  675 */       _flushBuffer();
/*      */     }
/*  677 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
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
/*  690 */     int start = 0;
/*  691 */     int len = text.length();
/*  692 */     while (len > 0) {
/*  693 */       char[] buf = this._charBuffer;
/*  694 */       int blen = buf.length;
/*  695 */       int len2 = len < blen ? len : blen;
/*  696 */       text.getChars(start, start + len2, buf, 0);
/*  697 */       writeRaw(buf, 0, len2);
/*  698 */       start += len2;
/*  699 */       len -= len2;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRaw(String text, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  707 */     while (len > 0) {
/*  708 */       char[] buf = this._charBuffer;
/*  709 */       int blen = buf.length;
/*  710 */       int len2 = len < blen ? len : blen;
/*  711 */       text.getChars(offset, offset + len2, buf, 0);
/*  712 */       writeRaw(buf, 0, len2);
/*  713 */       offset += len2;
/*  714 */       len -= len2;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeRaw(char[] cbuf, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  725 */     int len3 = len + len + len;
/*  726 */     if (this._outputTail + len3 > this._outputEnd)
/*      */     {
/*  728 */       if (this._outputEnd < len3) {
/*  729 */         _writeSegmentedRaw(cbuf, offset, len);
/*  730 */         return;
/*      */       }
/*      */       
/*  733 */       _flushBuffer();
/*      */     }
/*      */     
/*  736 */     len += offset;
/*      */     
/*      */ 
/*      */ 
/*  740 */     while (offset < len)
/*      */     {
/*      */       for (;;) {
/*  743 */         int ch = cbuf[offset];
/*  744 */         if (ch > 127) {
/*      */           break;
/*      */         }
/*  747 */         this._outputBuffer[(this._outputTail++)] = ((byte)ch);
/*  748 */         offset++; if (offset >= len) {
/*      */           return;
/*      */         }
/*      */       }
/*  752 */       char ch = cbuf[(offset++)];
/*  753 */       if (ch < 'ࠀ') {
/*  754 */         this._outputBuffer[(this._outputTail++)] = ((byte)(0xC0 | ch >> '\006'));
/*  755 */         this._outputBuffer[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/*  757 */         _outputRawMultiByteChar(ch, cbuf, offset, len);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeRaw(char ch)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  766 */     if (this._outputTail + 3 >= this._outputEnd) {
/*  767 */       _flushBuffer();
/*      */     }
/*  769 */     byte[] bbuf = this._outputBuffer;
/*  770 */     if (ch <= '') {
/*  771 */       bbuf[(this._outputTail++)] = ((byte)ch);
/*  772 */     } else if (ch < 'ࠀ') {
/*  773 */       bbuf[(this._outputTail++)] = ((byte)(0xC0 | ch >> '\006'));
/*  774 */       bbuf[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/*      */     } else {
/*  776 */       _outputRawMultiByteChar(ch, null, 0, 0);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeSegmentedRaw(char[] cbuf, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  787 */     int end = this._outputEnd;
/*  788 */     byte[] bbuf = this._outputBuffer;
/*      */     
/*      */ 
/*  791 */     while (offset < len)
/*      */     {
/*      */       for (;;) {
/*  794 */         int ch = cbuf[offset];
/*  795 */         if (ch >= 128) {
/*      */           break;
/*      */         }
/*      */         
/*  799 */         if (this._outputTail >= end) {
/*  800 */           _flushBuffer();
/*      */         }
/*  802 */         bbuf[(this._outputTail++)] = ((byte)ch);
/*  803 */         offset++; if (offset >= len) {
/*      */           return;
/*      */         }
/*      */       }
/*  807 */       if (this._outputTail + 3 >= this._outputEnd) {
/*  808 */         _flushBuffer();
/*      */       }
/*  810 */       char ch = cbuf[(offset++)];
/*  811 */       if (ch < 'ࠀ') {
/*  812 */         bbuf[(this._outputTail++)] = ((byte)(0xC0 | ch >> '\006'));
/*  813 */         bbuf[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/*  815 */         _outputRawMultiByteChar(ch, cbuf, offset, len);
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
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  830 */     _verifyValueWrite("write binary value");
/*      */     
/*  832 */     if (this._outputTail >= this._outputEnd) {
/*  833 */       _flushBuffer();
/*      */     }
/*  835 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  836 */     _writeBinary(b64variant, data, offset, offset + len);
/*      */     
/*  838 */     if (this._outputTail >= this._outputEnd) {
/*  839 */       _flushBuffer();
/*      */     }
/*  841 */     this._outputBuffer[(this._outputTail++)] = 34;
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
/*  854 */     _verifyValueWrite("write number");
/*      */     
/*  856 */     if (this._outputTail + 11 >= this._outputEnd) {
/*  857 */       _flushBuffer();
/*      */     }
/*  859 */     if (this._cfgNumbersAsStrings) {
/*  860 */       _writeQuotedInt(i);
/*  861 */       return;
/*      */     }
/*  863 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedInt(int i) throws IOException {
/*  867 */     if (this._outputTail + 13 >= this._outputEnd) {
/*  868 */       _flushBuffer();
/*      */     }
/*  870 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  871 */     this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
/*  872 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(long l)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  879 */     _verifyValueWrite("write number");
/*  880 */     if (this._cfgNumbersAsStrings) {
/*  881 */       _writeQuotedLong(l);
/*  882 */       return;
/*      */     }
/*  884 */     if (this._outputTail + 21 >= this._outputEnd)
/*      */     {
/*  886 */       _flushBuffer();
/*      */     }
/*  888 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*      */   }
/*      */   
/*      */   private final void _writeQuotedLong(long l) throws IOException {
/*  892 */     if (this._outputTail + 23 >= this._outputEnd) {
/*  893 */       _flushBuffer();
/*      */     }
/*  895 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  896 */     this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
/*  897 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(BigInteger value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  904 */     _verifyValueWrite("write number");
/*  905 */     if (value == null) {
/*  906 */       _writeNull();
/*  907 */     } else if (this._cfgNumbersAsStrings) {
/*  908 */       _writeQuotedRaw(value);
/*      */     } else {
/*  910 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeNumber(double d)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  919 */     if ((this._cfgNumbersAsStrings) || (((Double.isNaN(d)) || (Double.isInfinite(d))) && (isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))))
/*      */     {
/*      */ 
/*      */ 
/*  923 */       writeString(String.valueOf(d));
/*  924 */       return;
/*      */     }
/*      */     
/*  927 */     _verifyValueWrite("write number");
/*  928 */     writeRaw(String.valueOf(d));
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(float f)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  935 */     if ((this._cfgNumbersAsStrings) || (((Float.isNaN(f)) || (Float.isInfinite(f))) && (isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))))
/*      */     {
/*      */ 
/*      */ 
/*  939 */       writeString(String.valueOf(f));
/*  940 */       return;
/*      */     }
/*      */     
/*  943 */     _verifyValueWrite("write number");
/*  944 */     writeRaw(String.valueOf(f));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeNumber(BigDecimal value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  952 */     _verifyValueWrite("write number");
/*  953 */     if (value == null) {
/*  954 */       _writeNull();
/*  955 */     } else if (this._cfgNumbersAsStrings) {
/*  956 */       _writeQuotedRaw(value);
/*      */     } else {
/*  958 */       writeRaw(value.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNumber(String encodedValue)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  966 */     _verifyValueWrite("write number");
/*  967 */     if (this._cfgNumbersAsStrings) {
/*  968 */       _writeQuotedRaw(encodedValue);
/*      */     } else {
/*  970 */       writeRaw(encodedValue);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _writeQuotedRaw(Object value) throws IOException
/*      */   {
/*  976 */     if (this._outputTail >= this._outputEnd) {
/*  977 */       _flushBuffer();
/*      */     }
/*  979 */     this._outputBuffer[(this._outputTail++)] = 34;
/*  980 */     writeRaw(value.toString());
/*  981 */     if (this._outputTail >= this._outputEnd) {
/*  982 */       _flushBuffer();
/*      */     }
/*  984 */     this._outputBuffer[(this._outputTail++)] = 34;
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeBoolean(boolean state)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  991 */     _verifyValueWrite("write boolean value");
/*  992 */     if (this._outputTail + 5 >= this._outputEnd) {
/*  993 */       _flushBuffer();
/*      */     }
/*  995 */     byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
/*  996 */     int len = keyword.length;
/*  997 */     System.arraycopy(keyword, 0, this._outputBuffer, this._outputTail, len);
/*  998 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeNull()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1005 */     _verifyValueWrite("write null value");
/* 1006 */     _writeNull();
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
/* 1019 */     int status = this._writeContext.writeValue();
/* 1020 */     if (status == 5) {
/* 1021 */       _reportError("Can not " + typeMsg + ", expecting field name");
/*      */     }
/* 1023 */     if (this._cfgPrettyPrinter == null) {
/*      */       byte b;
/* 1025 */       switch (status) {
/*      */       case 1: 
/* 1027 */         b = 44;
/* 1028 */         break;
/*      */       case 2: 
/* 1030 */         b = 58;
/* 1031 */         break;
/*      */       case 3: 
/* 1033 */         b = 32;
/* 1034 */         break;
/*      */       case 0: 
/*      */       default: 
/* 1037 */         return;
/*      */       }
/* 1039 */       if (this._outputTail >= this._outputEnd) {
/* 1040 */         _flushBuffer();
/*      */       }
/* 1042 */       this._outputBuffer[this._outputTail] = b;
/* 1043 */       this._outputTail += 1;
/* 1044 */       return;
/*      */     }
/*      */     
/* 1047 */     _verifyPrettyValueWrite(typeMsg, status);
/*      */   }
/*      */   
/*      */ 
/*      */   protected final void _verifyPrettyValueWrite(String typeMsg, int status)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1054 */     switch (status) {
/*      */     case 1: 
/* 1056 */       this._cfgPrettyPrinter.writeArrayValueSeparator(this);
/* 1057 */       break;
/*      */     case 2: 
/* 1059 */       this._cfgPrettyPrinter.writeObjectFieldValueSeparator(this);
/* 1060 */       break;
/*      */     case 3: 
/* 1062 */       this._cfgPrettyPrinter.writeRootValueSeparator(this);
/* 1063 */       break;
/*      */     
/*      */     case 0: 
/* 1066 */       if (this._writeContext.inArray()) {
/* 1067 */         this._cfgPrettyPrinter.beforeArrayValues(this);
/* 1068 */       } else if (this._writeContext.inObject()) {
/* 1069 */         this._cfgPrettyPrinter.beforeObjectEntries(this);
/*      */       }
/*      */       break;
/*      */     default: 
/* 1073 */       _cantHappen();
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
/* 1088 */     _flushBuffer();
/* 1089 */     if ((this._outputStream != null) && 
/* 1090 */       (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))) {
/* 1091 */       this._outputStream.flush();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/* 1100 */     super.close();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1106 */     if ((this._outputBuffer != null) && (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT))) {
/*      */       for (;;)
/*      */       {
/* 1109 */         JsonStreamContext ctxt = getOutputContext();
/* 1110 */         if (ctxt.inArray()) {
/* 1111 */           writeEndArray();
/* 1112 */         } else { if (!ctxt.inObject()) break;
/* 1113 */           writeEndObject();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1119 */     _flushBuffer();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1127 */     if (this._outputStream != null) {
/* 1128 */       if ((this._ioContext.isResourceManaged()) || (isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET))) {
/* 1129 */         this._outputStream.close();
/* 1130 */       } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM))
/*      */       {
/* 1132 */         this._outputStream.flush();
/*      */       }
/*      */     }
/*      */     
/* 1136 */     _releaseBuffers();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */   {
/* 1142 */     byte[] buf = this._outputBuffer;
/* 1143 */     if ((buf != null) && (this._bufferRecyclable)) {
/* 1144 */       this._outputBuffer = null;
/* 1145 */       this._ioContext.releaseWriteEncodingBuffer(buf);
/*      */     }
/* 1147 */     char[] cbuf = this._charBuffer;
/* 1148 */     if (cbuf != null) {
/* 1149 */       this._charBuffer = null;
/* 1150 */       this._ioContext.releaseConcatBuffer(cbuf);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeBytes(byte[] bytes)
/*      */     throws IOException
/*      */   {
/* 1162 */     int len = bytes.length;
/* 1163 */     if (this._outputTail + len > this._outputEnd) {
/* 1164 */       _flushBuffer();
/*      */       
/* 1166 */       if (len > 512) {
/* 1167 */         this._outputStream.write(bytes, 0, len);
/* 1168 */         return;
/*      */       }
/*      */     }
/* 1171 */     System.arraycopy(bytes, 0, this._outputBuffer, this._outputTail, len);
/* 1172 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */   private final void _writeBytes(byte[] bytes, int offset, int len) throws IOException
/*      */   {
/* 1177 */     if (this._outputTail + len > this._outputEnd) {
/* 1178 */       _flushBuffer();
/*      */       
/* 1180 */       if (len > 512) {
/* 1181 */         this._outputStream.write(bytes, offset, len);
/* 1182 */         return;
/*      */       }
/*      */     }
/* 1185 */     System.arraycopy(bytes, offset, this._outputBuffer, this._outputTail, len);
/* 1186 */     this._outputTail += len;
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
/*      */   private final void _writeStringSegments(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1205 */     int left = text.length();
/* 1206 */     int offset = 0;
/* 1207 */     char[] cbuf = this._charBuffer;
/*      */     
/* 1209 */     while (left > 0) {
/* 1210 */       int len = Math.min(this._outputMaxContiguous, left);
/* 1211 */       text.getChars(offset, offset + len, cbuf, 0);
/* 1212 */       if (this._outputTail + len > this._outputEnd) {
/* 1213 */         _flushBuffer();
/*      */       }
/* 1215 */       _writeStringSegment(cbuf, 0, len);
/* 1216 */       offset += len;
/* 1217 */       left -= len;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeStringSegments(char[] cbuf, int offset, int totalLen)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*      */     do
/*      */     {
/* 1231 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1232 */       if (this._outputTail + len > this._outputEnd) {
/* 1233 */         _flushBuffer();
/*      */       }
/* 1235 */       _writeStringSegment(cbuf, offset, len);
/* 1236 */       offset += len;
/* 1237 */       totalLen -= len;
/* 1238 */     } while (totalLen > 0);
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
/*      */   private final void _writeStringSegment(char[] cbuf, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1261 */     len += offset;
/*      */     
/* 1263 */     int outputPtr = this._outputTail;
/* 1264 */     byte[] outputBuffer = this._outputBuffer;
/* 1265 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1267 */     while (offset < len) {
/* 1268 */       int ch = cbuf[offset];
/*      */       
/* 1270 */       if ((ch > 127) || (escCodes[ch] != 0)) {
/*      */         break;
/*      */       }
/* 1273 */       outputBuffer[(outputPtr++)] = ((byte)ch);
/* 1274 */       offset++;
/*      */     }
/* 1276 */     this._outputTail = outputPtr;
/* 1277 */     if (offset < len)
/*      */     {
/* 1279 */       if (this._characterEscapes != null) {
/* 1280 */         _writeCustomStringSegment2(cbuf, offset, len);
/*      */       }
/* 1282 */       else if (this._maximumNonEscapedChar == 0) {
/* 1283 */         _writeStringSegment2(cbuf, offset, len);
/*      */       } else {
/* 1285 */         _writeStringSegmentASCII2(cbuf, offset, len);
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
/*      */   private final void _writeStringSegment2(char[] cbuf, int offset, int end)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1299 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1300 */       _flushBuffer();
/*      */     }
/*      */     
/* 1303 */     int outputPtr = this._outputTail;
/*      */     
/* 1305 */     byte[] outputBuffer = this._outputBuffer;
/* 1306 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1308 */     while (offset < end) {
/* 1309 */       int ch = cbuf[(offset++)];
/* 1310 */       if (ch <= 127) {
/* 1311 */         if (escCodes[ch] == 0) {
/* 1312 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1315 */           int escape = escCodes[ch];
/* 1316 */           if (escape > 0) {
/* 1317 */             outputBuffer[(outputPtr++)] = 92;
/* 1318 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */           }
/*      */           else {
/* 1321 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1325 */       else if (ch <= 2047) {
/* 1326 */         outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1327 */         outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/* 1329 */         outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */       }
/*      */     }
/* 1332 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeStringSegmentASCII2(char[] cbuf, int offset, int end)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1353 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1354 */       _flushBuffer();
/*      */     }
/*      */     
/* 1357 */     int outputPtr = this._outputTail;
/*      */     
/* 1359 */     byte[] outputBuffer = this._outputBuffer;
/* 1360 */     int[] escCodes = this._outputEscapes;
/* 1361 */     int maxUnescaped = this._maximumNonEscapedChar;
/*      */     
/* 1363 */     while (offset < end) {
/* 1364 */       int ch = cbuf[(offset++)];
/* 1365 */       if (ch <= 127) {
/* 1366 */         if (escCodes[ch] == 0) {
/* 1367 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1370 */           int escape = escCodes[ch];
/* 1371 */           if (escape > 0) {
/* 1372 */             outputBuffer[(outputPtr++)] = 92;
/* 1373 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */           }
/*      */           else {
/* 1376 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1380 */       else if (ch > maxUnescaped) {
/* 1381 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */ 
/*      */       }
/* 1384 */       else if (ch <= 2047) {
/* 1385 */         outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1386 */         outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */       } else {
/* 1388 */         outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */       }
/*      */     }
/* 1391 */     this._outputTail = outputPtr;
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
/*      */   private final void _writeCustomStringSegment2(char[] cbuf, int offset, int end)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1411 */     if (this._outputTail + 6 * (end - offset) > this._outputEnd) {
/* 1412 */       _flushBuffer();
/*      */     }
/* 1414 */     int outputPtr = this._outputTail;
/*      */     
/* 1416 */     byte[] outputBuffer = this._outputBuffer;
/* 1417 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1419 */     int maxUnescaped = this._maximumNonEscapedChar <= 0 ? 65535 : this._maximumNonEscapedChar;
/* 1420 */     CharacterEscapes customEscapes = this._characterEscapes;
/*      */     
/* 1422 */     while (offset < end) {
/* 1423 */       int ch = cbuf[(offset++)];
/* 1424 */       if (ch <= 127) {
/* 1425 */         if (escCodes[ch] == 0) {
/* 1426 */           outputBuffer[(outputPtr++)] = ((byte)ch);
/*      */         }
/*      */         else {
/* 1429 */           int escape = escCodes[ch];
/* 1430 */           if (escape > 0) {
/* 1431 */             outputBuffer[(outputPtr++)] = 92;
/* 1432 */             outputBuffer[(outputPtr++)] = ((byte)escape);
/* 1433 */           } else if (escape == -2) {
/* 1434 */             SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1435 */             if (esc == null) {
/* 1436 */               throw new JsonGenerationException("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(ch) + ", although was supposed to have one");
/*      */             }
/*      */             
/* 1439 */             outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */           }
/*      */           else {
/* 1442 */             outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */           }
/*      */         }
/*      */       }
/* 1446 */       else if (ch > maxUnescaped) {
/* 1447 */         outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */       }
/*      */       else {
/* 1450 */         SerializableString esc = customEscapes.getEscapeSequence(ch);
/* 1451 */         if (esc != null) {
/* 1452 */           outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
/*      */ 
/*      */         }
/* 1455 */         else if (ch <= 2047) {
/* 1456 */           outputBuffer[(outputPtr++)] = ((byte)(0xC0 | ch >> 6));
/* 1457 */           outputBuffer[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */         } else {
/* 1459 */           outputPtr = _outputMultiByteChar(ch, outputPtr);
/*      */         }
/*      */       } }
/* 1462 */     this._outputTail = outputPtr;
/*      */   }
/*      */   
/*      */   private int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1468 */     byte[] raw = esc.asUnquotedUTF8();
/* 1469 */     int len = raw.length;
/* 1470 */     if (len > 6) {
/* 1471 */       return _handleLongCustomEscape(outputBuffer, outputPtr, this._outputEnd, raw, remainingChars);
/*      */     }
/*      */     
/* 1474 */     System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1475 */     return outputPtr + len;
/*      */   }
/*      */   
/*      */ 
/*      */   private int _handleLongCustomEscape(byte[] outputBuffer, int outputPtr, int outputEnd, byte[] raw, int remainingChars)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1482 */     int len = raw.length;
/* 1483 */     if (outputPtr + len > outputEnd) {
/* 1484 */       this._outputTail = outputPtr;
/* 1485 */       _flushBuffer();
/* 1486 */       outputPtr = this._outputTail;
/* 1487 */       if (len > outputBuffer.length) {
/* 1488 */         this._outputStream.write(raw, 0, len);
/* 1489 */         return outputPtr;
/*      */       }
/* 1491 */       System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
/* 1492 */       outputPtr += len;
/*      */     }
/*      */     
/* 1495 */     if (outputPtr + 6 * remainingChars > outputEnd) {
/* 1496 */       _flushBuffer();
/* 1497 */       return this._outputTail;
/*      */     }
/* 1499 */     return outputPtr;
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
/*      */   private final void _writeUTF8Segments(byte[] utf8, int offset, int totalLen)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*      */     do
/*      */     {
/* 1517 */       int len = Math.min(this._outputMaxContiguous, totalLen);
/* 1518 */       _writeUTF8Segment(utf8, offset, len);
/* 1519 */       offset += len;
/* 1520 */       totalLen -= len;
/* 1521 */     } while (totalLen > 0);
/*      */   }
/*      */   
/*      */ 
/*      */   private final void _writeUTF8Segment(byte[] utf8, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1528 */     int[] escCodes = this._outputEscapes;
/*      */     
/* 1530 */     int ptr = offset; for (int end = offset + len; ptr < end;)
/*      */     {
/* 1532 */       int ch = utf8[(ptr++)];
/* 1533 */       if ((ch >= 0) && (escCodes[ch] != 0)) {
/* 1534 */         _writeUTF8Segment2(utf8, offset, len);
/* 1535 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1540 */     if (this._outputTail + len > this._outputEnd) {
/* 1541 */       _flushBuffer();
/*      */     }
/* 1543 */     System.arraycopy(utf8, offset, this._outputBuffer, this._outputTail, len);
/* 1544 */     this._outputTail += len;
/*      */   }
/*      */   
/*      */   private final void _writeUTF8Segment2(byte[] utf8, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1550 */     int outputPtr = this._outputTail;
/*      */     
/*      */ 
/* 1553 */     if (outputPtr + len * 6 > this._outputEnd) {
/* 1554 */       _flushBuffer();
/* 1555 */       outputPtr = this._outputTail;
/*      */     }
/*      */     
/* 1558 */     byte[] outputBuffer = this._outputBuffer;
/* 1559 */     int[] escCodes = this._outputEscapes;
/* 1560 */     len += offset;
/*      */     
/* 1562 */     while (offset < len) {
/* 1563 */       byte b = utf8[(offset++)];
/* 1564 */       int ch = b;
/* 1565 */       if ((ch < 0) || (escCodes[ch] == 0)) {
/* 1566 */         outputBuffer[(outputPtr++)] = b;
/*      */       }
/*      */       else {
/* 1569 */         int escape = escCodes[ch];
/* 1570 */         if (escape > 0) {
/* 1571 */           outputBuffer[(outputPtr++)] = 92;
/* 1572 */           outputBuffer[(outputPtr++)] = ((byte)escape);
/*      */         }
/*      */         else {
/* 1575 */           outputPtr = _writeGenericEscape(ch, outputPtr);
/*      */         }
/*      */       } }
/* 1578 */     this._outputTail = outputPtr;
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
/* 1591 */     int safeInputEnd = inputEnd - 3;
/*      */     
/* 1593 */     int safeOutputEnd = this._outputEnd - 6;
/* 1594 */     int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */     
/*      */ 
/* 1597 */     while (inputPtr <= safeInputEnd) {
/* 1598 */       if (this._outputTail > safeOutputEnd) {
/* 1599 */         _flushBuffer();
/*      */       }
/*      */       
/* 1602 */       int b24 = input[(inputPtr++)] << 8;
/* 1603 */       b24 |= input[(inputPtr++)] & 0xFF;
/* 1604 */       b24 = b24 << 8 | input[(inputPtr++)] & 0xFF;
/* 1605 */       this._outputTail = b64variant.encodeBase64Chunk(b24, this._outputBuffer, this._outputTail);
/* 1606 */       chunksBeforeLF--; if (chunksBeforeLF <= 0)
/*      */       {
/* 1608 */         this._outputBuffer[(this._outputTail++)] = 92;
/* 1609 */         this._outputBuffer[(this._outputTail++)] = 110;
/* 1610 */         chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1615 */     int inputLeft = inputEnd - inputPtr;
/* 1616 */     if (inputLeft > 0) {
/* 1617 */       if (this._outputTail > safeOutputEnd) {
/* 1618 */         _flushBuffer();
/*      */       }
/* 1620 */       int b24 = input[(inputPtr++)] << 16;
/* 1621 */       if (inputLeft == 2) {
/* 1622 */         b24 |= (input[(inputPtr++)] & 0xFF) << 8;
/*      */       }
/* 1624 */       this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
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
/*      */   private final int _outputRawMultiByteChar(int ch, char[] cbuf, int inputOffset, int inputLen)
/*      */     throws IOException
/*      */   {
/* 1643 */     if ((ch >= 55296) && 
/* 1644 */       (ch <= 57343))
/*      */     {
/* 1646 */       if (inputOffset >= inputLen) {
/* 1647 */         _reportError("Split surrogate on writeRaw() input (last character)");
/*      */       }
/* 1649 */       _outputSurrogates(ch, cbuf[inputOffset]);
/* 1650 */       return inputOffset + 1;
/*      */     }
/*      */     
/* 1653 */     byte[] bbuf = this._outputBuffer;
/* 1654 */     bbuf[(this._outputTail++)] = ((byte)(0xE0 | ch >> 12));
/* 1655 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | ch >> 6 & 0x3F));
/* 1656 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | ch & 0x3F));
/* 1657 */     return inputOffset;
/*      */   }
/*      */   
/*      */   protected final void _outputSurrogates(int surr1, int surr2)
/*      */     throws IOException
/*      */   {
/* 1663 */     int c = _decodeSurrogate(surr1, surr2);
/* 1664 */     if (this._outputTail + 4 > this._outputEnd) {
/* 1665 */       _flushBuffer();
/*      */     }
/* 1667 */     byte[] bbuf = this._outputBuffer;
/* 1668 */     bbuf[(this._outputTail++)] = ((byte)(0xF0 | c >> 18));
/* 1669 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | c >> 12 & 0x3F));
/* 1670 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | c >> 6 & 0x3F));
/* 1671 */     bbuf[(this._outputTail++)] = ((byte)(0x80 | c & 0x3F));
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
/*      */   private final int _outputMultiByteChar(int ch, int outputPtr)
/*      */     throws IOException
/*      */   {
/* 1686 */     byte[] bbuf = this._outputBuffer;
/* 1687 */     if ((ch >= 55296) && (ch <= 57343)) {
/* 1688 */       bbuf[(outputPtr++)] = 92;
/* 1689 */       bbuf[(outputPtr++)] = 117;
/*      */       
/* 1691 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch >> 12 & 0xF)];
/* 1692 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch >> 8 & 0xF)];
/* 1693 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch >> 4 & 0xF)];
/* 1694 */       bbuf[(outputPtr++)] = HEX_CHARS[(ch & 0xF)];
/*      */     } else {
/* 1696 */       bbuf[(outputPtr++)] = ((byte)(0xE0 | ch >> 12));
/* 1697 */       bbuf[(outputPtr++)] = ((byte)(0x80 | ch >> 6 & 0x3F));
/* 1698 */       bbuf[(outputPtr++)] = ((byte)(0x80 | ch & 0x3F));
/*      */     }
/* 1700 */     return outputPtr;
/*      */   }
/*      */   
/*      */   protected final int _decodeSurrogate(int surr1, int surr2)
/*      */     throws IOException
/*      */   {
/* 1706 */     if ((surr2 < 56320) || (surr2 > 57343)) {
/* 1707 */       String msg = "Incomplete surrogate pair: first char 0x" + Integer.toHexString(surr1) + ", second 0x" + Integer.toHexString(surr2);
/* 1708 */       _reportError(msg);
/*      */     }
/* 1710 */     int c = 65536 + (surr1 - 55296 << 10) + (surr2 - 56320);
/* 1711 */     return c;
/*      */   }
/*      */   
/*      */   private final void _writeNull() throws IOException
/*      */   {
/* 1716 */     if (this._outputTail + 4 >= this._outputEnd) {
/* 1717 */       _flushBuffer();
/*      */     }
/* 1719 */     System.arraycopy(NULL_BYTES, 0, this._outputBuffer, this._outputTail, 4);
/* 1720 */     this._outputTail += 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int _writeGenericEscape(int charToEscape, int outputPtr)
/*      */     throws IOException
/*      */   {
/* 1731 */     byte[] bbuf = this._outputBuffer;
/* 1732 */     bbuf[(outputPtr++)] = 92;
/* 1733 */     bbuf[(outputPtr++)] = 117;
/* 1734 */     if (charToEscape > 255) {
/* 1735 */       int hi = charToEscape >> 8 & 0xFF;
/* 1736 */       bbuf[(outputPtr++)] = HEX_CHARS[(hi >> 4)];
/* 1737 */       bbuf[(outputPtr++)] = HEX_CHARS[(hi & 0xF)];
/* 1738 */       charToEscape &= 0xFF;
/*      */     } else {
/* 1740 */       bbuf[(outputPtr++)] = 48;
/* 1741 */       bbuf[(outputPtr++)] = 48;
/*      */     }
/*      */     
/* 1744 */     bbuf[(outputPtr++)] = HEX_CHARS[(charToEscape >> 4)];
/* 1745 */     bbuf[(outputPtr++)] = HEX_CHARS[(charToEscape & 0xF)];
/* 1746 */     return outputPtr;
/*      */   }
/*      */   
/*      */   protected final void _flushBuffer() throws IOException
/*      */   {
/* 1751 */     int len = this._outputTail;
/* 1752 */     if (len > 0) {
/* 1753 */       this._outputTail = 0;
/* 1754 */       this._outputStream.write(this._outputBuffer, 0, len);
/*      */     }
/*      */   }
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\Utf8Generator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */