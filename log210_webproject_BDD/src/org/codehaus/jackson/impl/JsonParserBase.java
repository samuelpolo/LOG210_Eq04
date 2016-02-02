/*      */ package org.codehaus.jackson.impl;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import org.codehaus.jackson.Base64Variant;
/*      */ import org.codehaus.jackson.JsonLocation;
/*      */ import org.codehaus.jackson.JsonParseException;
/*      */ import org.codehaus.jackson.JsonParser.NumberType;
/*      */ import org.codehaus.jackson.JsonToken;
/*      */ import org.codehaus.jackson.Version;
/*      */ import org.codehaus.jackson.io.IOContext;
/*      */ import org.codehaus.jackson.io.NumberInput;
/*      */ import org.codehaus.jackson.util.ByteArrayBuilder;
/*      */ import org.codehaus.jackson.util.TextBuffer;
/*      */ import org.codehaus.jackson.util.VersionUtil;
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
/*      */ public abstract class JsonParserBase
/*      */   extends JsonParserMinimalBase
/*      */ {
/*      */   protected final IOContext _ioContext;
/*      */   protected boolean _closed;
/*   54 */   protected int _inputPtr = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   59 */   protected int _inputEnd = 0;
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
/*   71 */   protected long _currInputProcessed = 0L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   77 */   protected int _currInputRow = 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   85 */   protected int _currInputRowStart = 0;
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
/*  101 */   protected long _tokenInputTotal = 0L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  106 */   protected int _tokenInputRow = 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  112 */   protected int _tokenInputCol = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonReadContext _parsingContext;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonToken _nextToken;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final TextBuffer _textBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  151 */   protected char[] _nameCopyBuffer = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  158 */   protected boolean _nameCopied = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  164 */   protected ByteArrayBuilder _byteArrayBuilder = null;
/*      */   
/*      */ 
/*      */ 
/*      */   protected byte[] _binaryValue;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int NR_UNKNOWN = 0;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int NR_INT = 1;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int NR_LONG = 2;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int NR_BIGINT = 4;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int NR_DOUBLE = 8;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int NR_BIGDECIMAL = 16;
/*      */   
/*      */ 
/*  195 */   static final BigInteger BI_MIN_INT = BigInteger.valueOf(-2147483648L);
/*  196 */   static final BigInteger BI_MAX_INT = BigInteger.valueOf(2147483647L);
/*      */   
/*  198 */   static final BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
/*  199 */   static final BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
/*      */   
/*  201 */   static final BigDecimal BD_MIN_LONG = new BigDecimal(BI_MIN_LONG);
/*  202 */   static final BigDecimal BD_MAX_LONG = new BigDecimal(BI_MAX_LONG);
/*      */   
/*  204 */   static final BigDecimal BD_MIN_INT = new BigDecimal(BI_MIN_INT);
/*  205 */   static final BigDecimal BD_MAX_INT = new BigDecimal(BI_MAX_INT);
/*      */   
/*      */   static final long MIN_INT_L = -2147483648L;
/*      */   
/*      */   static final long MAX_INT_L = 2147483647L;
/*      */   
/*      */   static final double MIN_LONG_D = -9.223372036854776E18D;
/*      */   
/*      */   static final double MAX_LONG_D = 9.223372036854776E18D;
/*      */   
/*      */   static final double MIN_INT_D = -2.147483648E9D;
/*      */   
/*      */   static final double MAX_INT_D = 2.147483647E9D;
/*      */   
/*      */   protected static final int INT_0 = 48;
/*      */   
/*      */   protected static final int INT_1 = 49;
/*      */   
/*      */   protected static final int INT_2 = 50;
/*      */   
/*      */   protected static final int INT_3 = 51;
/*      */   
/*      */   protected static final int INT_4 = 52;
/*      */   
/*      */   protected static final int INT_5 = 53;
/*      */   
/*      */   protected static final int INT_6 = 54;
/*      */   
/*      */   protected static final int INT_7 = 55;
/*      */   
/*      */   protected static final int INT_8 = 56;
/*      */   
/*      */   protected static final int INT_9 = 57;
/*      */   
/*      */   protected static final int INT_MINUS = 45;
/*      */   
/*      */   protected static final int INT_PLUS = 43;
/*      */   
/*      */   protected static final int INT_DECIMAL_POINT = 46;
/*      */   protected static final int INT_e = 101;
/*      */   protected static final int INT_E = 69;
/*      */   protected static final char CHAR_NULL = '\000';
/*  247 */   protected int _numTypesValid = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _numberInt;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long _numberLong;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected double _numberDouble;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BigInteger _numberBigInt;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BigDecimal _numberBigDecimal;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _numberNegative;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _intLength;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _fractLength;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _expLength;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonParserBase(IOContext ctxt, int features)
/*      */   {
/*  300 */     this._features = features;
/*  301 */     this._ioContext = ctxt;
/*  302 */     this._textBuffer = ctxt.constructTextBuffer();
/*  303 */     this._parsingContext = JsonReadContext.createRootContext();
/*      */   }
/*      */   
/*      */   public Version version()
/*      */   {
/*  308 */     return VersionUtil.versionFor(getClass());
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
/*      */   public String getCurrentName()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  326 */     if ((this._currToken == JsonToken.START_OBJECT) || (this._currToken == JsonToken.START_ARRAY)) {
/*  327 */       JsonReadContext parent = this._parsingContext.getParent();
/*  328 */       return parent.getCurrentName();
/*      */     }
/*  330 */     return this._parsingContext.getCurrentName();
/*      */   }
/*      */   
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  336 */     if (!this._closed) {
/*  337 */       this._closed = true;
/*      */       try {
/*  339 */         _closeInput();
/*      */       }
/*      */       finally
/*      */       {
/*  343 */         _releaseBuffers();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isClosed() {
/*  349 */     return this._closed;
/*      */   }
/*      */   
/*      */   public JsonReadContext getParsingContext()
/*      */   {
/*  354 */     return this._parsingContext;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonLocation getTokenLocation()
/*      */   {
/*  365 */     return new JsonLocation(this._ioContext.getSourceReference(), getTokenCharacterOffset(), getTokenLineNr(), getTokenColumnNr());
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
/*      */   public JsonLocation getCurrentLocation()
/*      */   {
/*  378 */     int col = this._inputPtr - this._currInputRowStart + 1;
/*  379 */     return new JsonLocation(this._ioContext.getSourceReference(), this._currInputProcessed + this._inputPtr - 1L, this._currInputRow, col);
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
/*      */   public boolean hasTextCharacters()
/*      */   {
/*  393 */     if (this._currToken == JsonToken.VALUE_STRING) {
/*  394 */       return true;
/*      */     }
/*  396 */     if (this._currToken == JsonToken.FIELD_NAME) {
/*  397 */       return this._nameCopied;
/*      */     }
/*  399 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  408 */   public final long getTokenCharacterOffset() { return this._tokenInputTotal; }
/*  409 */   public final int getTokenLineNr() { return this._tokenInputRow; }
/*      */   
/*      */   public final int getTokenColumnNr() {
/*  412 */     int col = this._tokenInputCol;
/*  413 */     return col < 0 ? col : col + 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void loadMoreGuaranteed()
/*      */     throws IOException
/*      */   {
/*  425 */     if (!loadMore()) {
/*  426 */       _reportInvalidEOF();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected abstract boolean loadMore()
/*      */     throws IOException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected abstract void _finishString()
/*      */     throws IOException, JsonParseException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected abstract void _closeInput()
/*      */     throws IOException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _releaseBuffers()
/*      */     throws IOException
/*      */   {
/*  456 */     this._textBuffer.releaseBuffers();
/*  457 */     char[] buf = this._nameCopyBuffer;
/*  458 */     if (buf != null) {
/*  459 */       this._nameCopyBuffer = null;
/*  460 */       this._ioContext.releaseNameCopyBuffer(buf);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _handleEOF()
/*      */     throws JsonParseException
/*      */   {
/*  472 */     if (!this._parsingContext.inRoot()) {
/*  473 */       _reportInvalidEOF(": expected close marker for " + this._parsingContext.getTypeDesc() + " (from " + this._parsingContext.getStartLocation(this._ioContext.getSourceReference()) + ")");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _reportMismatchedEndMarker(int actCh, char expCh)
/*      */     throws JsonParseException
/*      */   {
/*  486 */     String startDesc = "" + this._parsingContext.getStartLocation(this._ioContext.getSourceReference());
/*  487 */     _reportError("Unexpected close marker '" + (char)actCh + "': expected '" + expCh + "' (for " + this._parsingContext.getTypeDesc() + " starting at " + startDesc + ")");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ByteArrayBuilder _getByteArrayBuilder()
/*      */   {
/*  498 */     if (this._byteArrayBuilder == null) {
/*  499 */       this._byteArrayBuilder = new ByteArrayBuilder();
/*      */     } else {
/*  501 */       this._byteArrayBuilder.reset();
/*      */     }
/*  503 */     return this._byteArrayBuilder;
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
/*      */   protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen)
/*      */   {
/*  516 */     if ((fractLen < 1) && (expLen < 1)) {
/*  517 */       return resetInt(negative, intLen);
/*      */     }
/*  519 */     return resetFloat(negative, intLen, fractLen, expLen);
/*      */   }
/*      */   
/*      */   protected final JsonToken resetInt(boolean negative, int intLen)
/*      */   {
/*  524 */     this._numberNegative = negative;
/*  525 */     this._intLength = intLen;
/*  526 */     this._fractLength = 0;
/*  527 */     this._expLength = 0;
/*  528 */     this._numTypesValid = 0;
/*  529 */     return JsonToken.VALUE_NUMBER_INT;
/*      */   }
/*      */   
/*      */   protected final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen)
/*      */   {
/*  534 */     this._numberNegative = negative;
/*  535 */     this._intLength = intLen;
/*  536 */     this._fractLength = fractLen;
/*  537 */     this._expLength = expLen;
/*  538 */     this._numTypesValid = 0;
/*  539 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*      */   }
/*      */   
/*      */   protected final JsonToken resetAsNaN(String valueStr, double value)
/*      */   {
/*  544 */     this._textBuffer.resetWithString(valueStr);
/*  545 */     this._numberDouble = value;
/*  546 */     this._numTypesValid = 8;
/*  547 */     return JsonToken.VALUE_NUMBER_FLOAT;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Number getNumberValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  559 */     if (this._numTypesValid == 0) {
/*  560 */       _parseNumericValue(0);
/*      */     }
/*      */     
/*  563 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  564 */       if ((this._numTypesValid & 0x1) != 0) {
/*  565 */         return Integer.valueOf(this._numberInt);
/*      */       }
/*  567 */       if ((this._numTypesValid & 0x2) != 0) {
/*  568 */         return Long.valueOf(this._numberLong);
/*      */       }
/*  570 */       if ((this._numTypesValid & 0x4) != 0) {
/*  571 */         return this._numberBigInt;
/*      */       }
/*      */       
/*  574 */       return this._numberBigDecimal;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  580 */     if ((this._numTypesValid & 0x10) != 0) {
/*  581 */       return this._numberBigDecimal;
/*      */     }
/*  583 */     if ((this._numTypesValid & 0x8) == 0) {
/*  584 */       _throwInternal();
/*      */     }
/*  586 */     return Double.valueOf(this._numberDouble);
/*      */   }
/*      */   
/*      */   public JsonParser.NumberType getNumberType()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  592 */     if (this._numTypesValid == 0) {
/*  593 */       _parseNumericValue(0);
/*      */     }
/*  595 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  596 */       if ((this._numTypesValid & 0x1) != 0) {
/*  597 */         return JsonParser.NumberType.INT;
/*      */       }
/*  599 */       if ((this._numTypesValid & 0x2) != 0) {
/*  600 */         return JsonParser.NumberType.LONG;
/*      */       }
/*  602 */       return JsonParser.NumberType.BIG_INTEGER;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  611 */     if ((this._numTypesValid & 0x10) != 0) {
/*  612 */       return JsonParser.NumberType.BIG_DECIMAL;
/*      */     }
/*  614 */     return JsonParser.NumberType.DOUBLE;
/*      */   }
/*      */   
/*      */   public int getIntValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  620 */     if ((this._numTypesValid & 0x1) == 0) {
/*  621 */       if (this._numTypesValid == 0) {
/*  622 */         _parseNumericValue(1);
/*      */       }
/*  624 */       if ((this._numTypesValid & 0x1) == 0) {
/*  625 */         convertNumberToInt();
/*      */       }
/*      */     }
/*  628 */     return this._numberInt;
/*      */   }
/*      */   
/*      */   public long getLongValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  634 */     if ((this._numTypesValid & 0x2) == 0) {
/*  635 */       if (this._numTypesValid == 0) {
/*  636 */         _parseNumericValue(2);
/*      */       }
/*  638 */       if ((this._numTypesValid & 0x2) == 0) {
/*  639 */         convertNumberToLong();
/*      */       }
/*      */     }
/*  642 */     return this._numberLong;
/*      */   }
/*      */   
/*      */   public BigInteger getBigIntegerValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  648 */     if ((this._numTypesValid & 0x4) == 0) {
/*  649 */       if (this._numTypesValid == 0) {
/*  650 */         _parseNumericValue(4);
/*      */       }
/*  652 */       if ((this._numTypesValid & 0x4) == 0) {
/*  653 */         convertNumberToBigInteger();
/*      */       }
/*      */     }
/*  656 */     return this._numberBigInt;
/*      */   }
/*      */   
/*      */   public float getFloatValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  662 */     double value = getDoubleValue();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  671 */     return (float)value;
/*      */   }
/*      */   
/*      */   public double getDoubleValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  677 */     if ((this._numTypesValid & 0x8) == 0) {
/*  678 */       if (this._numTypesValid == 0) {
/*  679 */         _parseNumericValue(8);
/*      */       }
/*  681 */       if ((this._numTypesValid & 0x8) == 0) {
/*  682 */         convertNumberToDouble();
/*      */       }
/*      */     }
/*  685 */     return this._numberDouble;
/*      */   }
/*      */   
/*      */   public BigDecimal getDecimalValue()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  691 */     if ((this._numTypesValid & 0x10) == 0) {
/*  692 */       if (this._numTypesValid == 0) {
/*  693 */         _parseNumericValue(16);
/*      */       }
/*  695 */       if ((this._numTypesValid & 0x10) == 0) {
/*  696 */         convertNumberToBigDecimal();
/*      */       }
/*      */     }
/*  699 */     return this._numberBigDecimal;
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
/*      */   protected void _parseNumericValue(int expType)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  721 */     if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/*  722 */       char[] buf = this._textBuffer.getTextBuffer();
/*  723 */       int offset = this._textBuffer.getTextOffset();
/*  724 */       int len = this._intLength;
/*  725 */       if (this._numberNegative) {
/*  726 */         offset++;
/*      */       }
/*  728 */       if (len <= 9) {
/*  729 */         int i = NumberInput.parseInt(buf, offset, len);
/*  730 */         this._numberInt = (this._numberNegative ? -i : i);
/*  731 */         this._numTypesValid = 1;
/*  732 */         return;
/*      */       }
/*  734 */       if (len <= 18) {
/*  735 */         long l = NumberInput.parseLong(buf, offset, len);
/*  736 */         if (this._numberNegative) {
/*  737 */           l = -l;
/*      */         }
/*      */         
/*  740 */         if (len == 10) {
/*  741 */           if (this._numberNegative) {
/*  742 */             if (l >= -2147483648L) {
/*  743 */               this._numberInt = ((int)l);
/*  744 */               this._numTypesValid = 1;
/*      */             }
/*      */             
/*      */           }
/*  748 */           else if (l <= 2147483647L) {
/*  749 */             this._numberInt = ((int)l);
/*  750 */             this._numTypesValid = 1;
/*  751 */             return;
/*      */           }
/*      */         }
/*      */         
/*  755 */         this._numberLong = l;
/*  756 */         this._numTypesValid = 2;
/*  757 */         return;
/*      */       }
/*  759 */       _parseSlowIntValue(expType, buf, offset, len);
/*  760 */       return;
/*      */     }
/*  762 */     if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
/*  763 */       _parseSlowFloatValue(expType);
/*  764 */       return;
/*      */     }
/*  766 */     _reportError("Current token (" + this._currToken + ") not numeric, can not use numeric value accessors");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _parseSlowFloatValue(int expType)
/*      */     throws IOException, JsonParseException
/*      */   {
/*      */     try
/*      */     {
/*  780 */       if (expType == 16) {
/*  781 */         this._numberBigDecimal = this._textBuffer.contentsAsDecimal();
/*  782 */         this._numTypesValid = 16;
/*      */       }
/*      */       else {
/*  785 */         this._numberDouble = this._textBuffer.contentsAsDouble();
/*  786 */         this._numTypesValid = 8;
/*      */       }
/*      */     }
/*      */     catch (NumberFormatException nex) {
/*  790 */       _wrapError("Malformed numeric value '" + this._textBuffer.contentsAsString() + "'", nex);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void _parseSlowIntValue(int expType, char[] buf, int offset, int len)
/*      */     throws IOException, JsonParseException
/*      */   {
/*  797 */     String numStr = this._textBuffer.contentsAsString();
/*      */     try
/*      */     {
/*  800 */       if (NumberInput.inLongRange(buf, offset, len, this._numberNegative))
/*      */       {
/*  802 */         this._numberLong = Long.parseLong(numStr);
/*  803 */         this._numTypesValid = 2;
/*      */       }
/*      */       else {
/*  806 */         this._numberBigInt = new BigInteger(numStr);
/*  807 */         this._numTypesValid = 4;
/*      */       }
/*      */     }
/*      */     catch (NumberFormatException nex) {
/*  811 */       _wrapError("Malformed numeric value '" + numStr + "'", nex);
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
/*      */   protected void convertNumberToInt()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  825 */     if ((this._numTypesValid & 0x2) != 0)
/*      */     {
/*  827 */       int result = (int)this._numberLong;
/*  828 */       if (result != this._numberLong) {
/*  829 */         _reportError("Numeric value (" + getText() + ") out of range of int");
/*      */       }
/*  831 */       this._numberInt = result;
/*  832 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  833 */       if ((BI_MIN_INT.compareTo(this._numberBigInt) > 0) || (BI_MAX_INT.compareTo(this._numberBigInt) < 0))
/*      */       {
/*  835 */         reportOverflowInt();
/*      */       }
/*  837 */       this._numberInt = this._numberBigInt.intValue();
/*  838 */     } else if ((this._numTypesValid & 0x8) != 0)
/*      */     {
/*  840 */       if ((this._numberDouble < -2.147483648E9D) || (this._numberDouble > 2.147483647E9D)) {
/*  841 */         reportOverflowInt();
/*      */       }
/*  843 */       this._numberInt = ((int)this._numberDouble);
/*  844 */     } else if ((this._numTypesValid & 0x10) != 0) {
/*  845 */       if ((BD_MIN_INT.compareTo(this._numberBigDecimal) > 0) || (BD_MAX_INT.compareTo(this._numberBigDecimal) < 0))
/*      */       {
/*  847 */         reportOverflowInt();
/*      */       }
/*  849 */       this._numberInt = this._numberBigDecimal.intValue();
/*      */     } else {
/*  851 */       _throwInternal();
/*      */     }
/*      */     
/*  854 */     this._numTypesValid |= 0x1;
/*      */   }
/*      */   
/*      */   protected void convertNumberToLong()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  860 */     if ((this._numTypesValid & 0x1) != 0) {
/*  861 */       this._numberLong = this._numberInt;
/*  862 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  863 */       if ((BI_MIN_LONG.compareTo(this._numberBigInt) > 0) || (BI_MAX_LONG.compareTo(this._numberBigInt) < 0))
/*      */       {
/*  865 */         reportOverflowLong();
/*      */       }
/*  867 */       this._numberLong = this._numberBigInt.longValue();
/*  868 */     } else if ((this._numTypesValid & 0x8) != 0)
/*      */     {
/*  870 */       if ((this._numberDouble < -9.223372036854776E18D) || (this._numberDouble > 9.223372036854776E18D)) {
/*  871 */         reportOverflowLong();
/*      */       }
/*  873 */       this._numberLong = (this._numberDouble);
/*  874 */     } else if ((this._numTypesValid & 0x10) != 0) {
/*  875 */       if ((BD_MIN_LONG.compareTo(this._numberBigDecimal) > 0) || (BD_MAX_LONG.compareTo(this._numberBigDecimal) < 0))
/*      */       {
/*  877 */         reportOverflowLong();
/*      */       }
/*  879 */       this._numberLong = this._numberBigDecimal.longValue();
/*      */     } else {
/*  881 */       _throwInternal();
/*      */     }
/*      */     
/*  884 */     this._numTypesValid |= 0x2;
/*      */   }
/*      */   
/*      */   protected void convertNumberToBigInteger()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  890 */     if ((this._numTypesValid & 0x10) != 0)
/*      */     {
/*  892 */       this._numberBigInt = this._numberBigDecimal.toBigInteger();
/*  893 */     } else if ((this._numTypesValid & 0x2) != 0) {
/*  894 */       this._numberBigInt = BigInteger.valueOf(this._numberLong);
/*  895 */     } else if ((this._numTypesValid & 0x1) != 0) {
/*  896 */       this._numberBigInt = BigInteger.valueOf(this._numberInt);
/*  897 */     } else if ((this._numTypesValid & 0x8) != 0) {
/*  898 */       this._numberBigInt = BigDecimal.valueOf(this._numberDouble).toBigInteger();
/*      */     } else {
/*  900 */       _throwInternal();
/*      */     }
/*  902 */     this._numTypesValid |= 0x4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void convertNumberToDouble()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  914 */     if ((this._numTypesValid & 0x10) != 0) {
/*  915 */       this._numberDouble = this._numberBigDecimal.doubleValue();
/*  916 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  917 */       this._numberDouble = this._numberBigInt.doubleValue();
/*  918 */     } else if ((this._numTypesValid & 0x2) != 0) {
/*  919 */       this._numberDouble = this._numberLong;
/*  920 */     } else if ((this._numTypesValid & 0x1) != 0) {
/*  921 */       this._numberDouble = this._numberInt;
/*      */     } else {
/*  923 */       _throwInternal();
/*      */     }
/*      */     
/*  926 */     this._numTypesValid |= 0x8;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void convertNumberToBigDecimal()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  938 */     if ((this._numTypesValid & 0x8) != 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  943 */       this._numberBigDecimal = new BigDecimal(getText());
/*  944 */     } else if ((this._numTypesValid & 0x4) != 0) {
/*  945 */       this._numberBigDecimal = new BigDecimal(this._numberBigInt);
/*  946 */     } else if ((this._numTypesValid & 0x2) != 0) {
/*  947 */       this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
/*  948 */     } else if ((this._numTypesValid & 0x1) != 0) {
/*  949 */       this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
/*      */     } else {
/*  951 */       _throwInternal();
/*      */     }
/*  953 */     this._numTypesValid |= 0x10;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void reportUnexpectedNumberChar(int ch, String comment)
/*      */     throws JsonParseException
/*      */   {
/*  965 */     String msg = "Unexpected character (" + _getCharDesc(ch) + ") in numeric value";
/*  966 */     if (comment != null) {
/*  967 */       msg = msg + ": " + comment;
/*      */     }
/*  969 */     _reportError(msg);
/*      */   }
/*      */   
/*      */   protected void reportInvalidNumber(String msg)
/*      */     throws JsonParseException
/*      */   {
/*  975 */     _reportError("Invalid numeric value: " + msg);
/*      */   }
/*      */   
/*      */   protected void reportOverflowInt()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  981 */     _reportError("Numeric value (" + getText() + ") out of range of int (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")");
/*      */   }
/*      */   
/*      */   protected void reportOverflowLong()
/*      */     throws IOException, JsonParseException
/*      */   {
/*  987 */     _reportError("Numeric value (" + getText() + ") out of range of long (" + Long.MIN_VALUE + " - " + Long.MAX_VALUE + ")");
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
/*      */   protected char _decodeEscaped()
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1003 */     throw new UnsupportedOperationException();
/*      */   }
/*      */   
/*      */ 
/*      */   protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1010 */     if (ch != 92) {
/* 1011 */       throw reportInvalidBase64Char(b64variant, ch, index);
/*      */     }
/* 1013 */     int unescaped = _decodeEscaped();
/*      */     
/* 1015 */     if ((unescaped <= 32) && 
/* 1016 */       (index == 0)) {
/* 1017 */       return -1;
/*      */     }
/*      */     
/*      */ 
/* 1021 */     int bits = b64variant.decodeBase64Char(unescaped);
/* 1022 */     if (bits < 0) {
/* 1023 */       throw reportInvalidBase64Char(b64variant, unescaped, index);
/*      */     }
/* 1025 */     return bits;
/*      */   }
/*      */   
/*      */ 
/*      */   protected final int _decodeBase64Escape(Base64Variant b64variant, char ch, int index)
/*      */     throws IOException, JsonParseException
/*      */   {
/* 1032 */     if (ch != '\\') {
/* 1033 */       throw reportInvalidBase64Char(b64variant, ch, index);
/*      */     }
/* 1035 */     char unescaped = _decodeEscaped();
/*      */     
/* 1037 */     if ((unescaped <= ' ') && 
/* 1038 */       (index == 0)) {
/* 1039 */       return -1;
/*      */     }
/*      */     
/*      */ 
/* 1043 */     int bits = b64variant.decodeBase64Char(unescaped);
/* 1044 */     if (bits < 0) {
/* 1045 */       throw reportInvalidBase64Char(b64variant, unescaped, index);
/*      */     }
/* 1047 */     return bits;
/*      */   }
/*      */   
/*      */   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex)
/*      */     throws IllegalArgumentException
/*      */   {
/* 1053 */     return reportInvalidBase64Char(b64variant, ch, bindex, null);
/*      */   }
/*      */   
/*      */ 
/*      */   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex, String msg)
/*      */     throws IllegalArgumentException
/*      */   {
/*      */     String base;
/*      */     
/*      */     String base;
/*      */     
/* 1064 */     if (ch <= 32) {
/* 1065 */       base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units"; } else { String base;
/* 1066 */       if (b64variant.usesPaddingChar(ch)) {
/* 1067 */         base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character"; } else { String base;
/* 1068 */         if ((!Character.isDefined(ch)) || (Character.isISOControl(ch)))
/*      */         {
/* 1070 */           base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*      */         } else
/* 1072 */           base = "Illegal character '" + (char)ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*      */       } }
/* 1074 */     if (msg != null) {
/* 1075 */       base = base + ": " + msg;
/*      */     }
/* 1077 */     return new IllegalArgumentException(base);
/*      */   }
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\JsonParserBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */