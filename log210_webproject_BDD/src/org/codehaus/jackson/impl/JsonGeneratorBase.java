/*     */ package org.codehaus.jackson.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.codehaus.jackson.JsonGenerationException;
/*     */ import org.codehaus.jackson.JsonGenerator;
/*     */ import org.codehaus.jackson.JsonGenerator.Feature;
/*     */ import org.codehaus.jackson.JsonNode;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ import org.codehaus.jackson.JsonProcessingException;
/*     */ import org.codehaus.jackson.JsonToken;
/*     */ import org.codehaus.jackson.ObjectCodec;
/*     */ import org.codehaus.jackson.PrettyPrinter;
/*     */ import org.codehaus.jackson.Version;
/*     */ import org.codehaus.jackson.util.DefaultPrettyPrinter;
/*     */ import org.codehaus.jackson.util.VersionUtil;
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
/*     */ public abstract class JsonGeneratorBase
/*     */   extends JsonGenerator
/*     */ {
/*     */   protected ObjectCodec _objectCodec;
/*     */   protected int _features;
/*     */   protected boolean _cfgNumbersAsStrings;
/*     */   protected JsonWriteContext _writeContext;
/*     */   protected boolean _closed;
/*     */   
/*     */   protected JsonGeneratorBase(int features, ObjectCodec codec)
/*     */   {
/*  72 */     this._features = features;
/*  73 */     this._writeContext = JsonWriteContext.createRootContext();
/*  74 */     this._objectCodec = codec;
/*  75 */     this._cfgNumbersAsStrings = isEnabled(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
/*     */   }
/*     */   
/*     */   public Version version()
/*     */   {
/*  80 */     return VersionUtil.versionFor(getClass());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonGenerator enable(JsonGenerator.Feature f)
/*     */   {
/*  91 */     this._features |= f.getMask();
/*  92 */     if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/*  93 */       this._cfgNumbersAsStrings = true;
/*  94 */     } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/*  95 */       setHighestNonEscapedChar(127);
/*     */     }
/*  97 */     return this;
/*     */   }
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f)
/*     */   {
/* 102 */     this._features &= (f.getMask() ^ 0xFFFFFFFF);
/* 103 */     if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
/* 104 */       this._cfgNumbersAsStrings = false;
/* 105 */     } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
/* 106 */       setHighestNonEscapedChar(0);
/*     */     }
/* 108 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean isEnabled(JsonGenerator.Feature f)
/*     */   {
/* 115 */     return (this._features & f.getMask()) != 0;
/*     */   }
/*     */   
/*     */   public JsonGenerator useDefaultPrettyPrinter()
/*     */   {
/* 120 */     return setPrettyPrinter(new DefaultPrettyPrinter());
/*     */   }
/*     */   
/*     */   public JsonGenerator setCodec(ObjectCodec oc)
/*     */   {
/* 125 */     this._objectCodec = oc;
/* 126 */     return this;
/*     */   }
/*     */   
/*     */   public final ObjectCodec getCodec() {
/* 130 */     return this._objectCodec;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final JsonWriteContext getOutputContext()
/*     */   {
/* 142 */     return this._writeContext;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeStartArray()
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 154 */     _verifyValueWrite("start an array");
/* 155 */     this._writeContext = this._writeContext.createChildArrayContext();
/* 156 */     if (this._cfgPrettyPrinter != null) {
/* 157 */       this._cfgPrettyPrinter.writeStartArray(this);
/*     */     } else {
/* 159 */       _writeStartArray();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected void _writeStartArray()
/*     */     throws IOException, JsonGenerationException
/*     */   {}
/*     */   
/*     */ 
/*     */   public void writeEndArray()
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 174 */     if (!this._writeContext.inArray()) {
/* 175 */       _reportError("Current context not an ARRAY but " + this._writeContext.getTypeDesc());
/*     */     }
/* 177 */     if (this._cfgPrettyPrinter != null) {
/* 178 */       this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
/*     */     } else {
/* 180 */       _writeEndArray();
/*     */     }
/* 182 */     this._writeContext = this._writeContext.getParent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected void _writeEndArray()
/*     */     throws IOException, JsonGenerationException
/*     */   {}
/*     */   
/*     */ 
/*     */   public void writeStartObject()
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 196 */     _verifyValueWrite("start an object");
/* 197 */     this._writeContext = this._writeContext.createChildObjectContext();
/* 198 */     if (this._cfgPrettyPrinter != null) {
/* 199 */       this._cfgPrettyPrinter.writeStartObject(this);
/*     */     } else {
/* 201 */       _writeStartObject();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected void _writeStartObject()
/*     */     throws IOException, JsonGenerationException
/*     */   {}
/*     */   
/*     */ 
/*     */   public void writeEndObject()
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 216 */     if (!this._writeContext.inObject()) {
/* 217 */       _reportError("Current context not an object but " + this._writeContext.getTypeDesc());
/*     */     }
/* 219 */     this._writeContext = this._writeContext.getParent();
/* 220 */     if (this._cfgPrettyPrinter != null) {
/* 221 */       this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
/*     */     } else {
/* 223 */       _writeEndObject();
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
/*     */   @Deprecated
/*     */   protected void _writeEndObject()
/*     */     throws IOException, JsonGenerationException
/*     */   {}
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
/*     */   public void writeRawValue(String text)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 253 */     _verifyValueWrite("write raw value");
/* 254 */     writeRaw(text);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeRawValue(String text, int offset, int len)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 261 */     _verifyValueWrite("write raw value");
/* 262 */     writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeRawValue(char[] text, int offset, int len)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 269 */     _verifyValueWrite("write raw value");
/* 270 */     writeRaw(text, offset, len);
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
/*     */   public void writeObject(Object value)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 304 */     if (value == null)
/*     */     {
/* 306 */       writeNull();
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 313 */       if (this._objectCodec != null) {
/* 314 */         this._objectCodec.writeValue(this, value);
/* 315 */         return;
/*     */       }
/* 317 */       _writeSimpleObject(value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeTree(JsonNode rootNode)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 326 */     if (rootNode == null) {
/* 327 */       writeNull();
/*     */     } else {
/* 329 */       if (this._objectCodec == null) {
/* 330 */         throw new IllegalStateException("No ObjectCodec defined for the generator, can not serialize JsonNode-based trees");
/*     */       }
/* 332 */       this._objectCodec.writeTree(this, rootNode);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void flush()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 348 */     this._closed = true;
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 352 */     return this._closed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void copyCurrentEvent(JsonParser jp)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 364 */     JsonToken t = jp.getCurrentToken();
/*     */     
/* 366 */     if (t == null) {
/* 367 */       _reportError("No current event to copy");
/*     */     }
/* 369 */     switch (t) {
/*     */     case START_OBJECT: 
/* 371 */       writeStartObject();
/* 372 */       break;
/*     */     case END_OBJECT: 
/* 374 */       writeEndObject();
/* 375 */       break;
/*     */     case START_ARRAY: 
/* 377 */       writeStartArray();
/* 378 */       break;
/*     */     case END_ARRAY: 
/* 380 */       writeEndArray();
/* 381 */       break;
/*     */     case FIELD_NAME: 
/* 383 */       writeFieldName(jp.getCurrentName());
/* 384 */       break;
/*     */     case VALUE_STRING: 
/* 386 */       if (jp.hasTextCharacters()) {
/* 387 */         writeString(jp.getTextCharacters(), jp.getTextOffset(), jp.getTextLength());
/*     */       } else {
/* 389 */         writeString(jp.getText());
/*     */       }
/* 391 */       break;
/*     */     case VALUE_NUMBER_INT: 
/* 393 */       switch (jp.getNumberType()) {
/*     */       case INT: 
/* 395 */         writeNumber(jp.getIntValue());
/* 396 */         break;
/*     */       case BIG_INTEGER: 
/* 398 */         writeNumber(jp.getBigIntegerValue());
/* 399 */         break;
/*     */       default: 
/* 401 */         writeNumber(jp.getLongValue());
/*     */       }
/* 403 */       break;
/*     */     case VALUE_NUMBER_FLOAT: 
/* 405 */       switch (jp.getNumberType()) {
/*     */       case BIG_DECIMAL: 
/* 407 */         writeNumber(jp.getDecimalValue());
/* 408 */         break;
/*     */       case FLOAT: 
/* 410 */         writeNumber(jp.getFloatValue());
/* 411 */         break;
/*     */       default: 
/* 413 */         writeNumber(jp.getDoubleValue());
/*     */       }
/* 415 */       break;
/*     */     case VALUE_TRUE: 
/* 417 */       writeBoolean(true);
/* 418 */       break;
/*     */     case VALUE_FALSE: 
/* 420 */       writeBoolean(false);
/* 421 */       break;
/*     */     case VALUE_NULL: 
/* 423 */       writeNull();
/* 424 */       break;
/*     */     case VALUE_EMBEDDED_OBJECT: 
/* 426 */       writeObject(jp.getEmbeddedObject());
/* 427 */       break;
/*     */     default: 
/* 429 */       _cantHappen();
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */   public final void copyCurrentStructure(JsonParser jp)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 437 */     JsonToken t = jp.getCurrentToken();
/*     */     
/*     */ 
/* 440 */     if (t == JsonToken.FIELD_NAME) {
/* 441 */       writeFieldName(jp.getCurrentName());
/* 442 */       t = jp.nextToken();
/*     */     }
/*     */     
/*     */ 
/* 446 */     switch (t) {
/*     */     case START_ARRAY: 
/* 448 */       writeStartArray();
/* 449 */       while (jp.nextToken() != JsonToken.END_ARRAY) {
/* 450 */         copyCurrentStructure(jp);
/*     */       }
/* 452 */       writeEndArray();
/* 453 */       break;
/*     */     case START_OBJECT: 
/* 455 */       writeStartObject();
/* 456 */       while (jp.nextToken() != JsonToken.END_OBJECT) {
/* 457 */         copyCurrentStructure(jp);
/*     */       }
/* 459 */       writeEndObject();
/* 460 */       break;
/*     */     default: 
/* 462 */       copyCurrentEvent(jp);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void _releaseBuffers();
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void _verifyValueWrite(String paramString)
/*     */     throws IOException, JsonGenerationException;
/*     */   
/*     */ 
/*     */   protected void _reportError(String msg)
/*     */     throws JsonGenerationException
/*     */   {
/* 480 */     throw new JsonGenerationException(msg);
/*     */   }
/*     */   
/*     */   protected void _cantHappen()
/*     */   {
/* 485 */     throw new RuntimeException("Internal error: should never end up through this code path");
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
/*     */   protected void _writeSimpleObject(Object value)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 502 */     if (value == null) {
/* 503 */       writeNull();
/* 504 */       return;
/*     */     }
/* 506 */     if ((value instanceof String)) {
/* 507 */       writeString((String)value);
/* 508 */       return;
/*     */     }
/* 510 */     if ((value instanceof Number)) {
/* 511 */       Number n = (Number)value;
/* 512 */       if ((n instanceof Integer)) {
/* 513 */         writeNumber(n.intValue());
/* 514 */         return; }
/* 515 */       if ((n instanceof Long)) {
/* 516 */         writeNumber(n.longValue());
/* 517 */         return; }
/* 518 */       if ((n instanceof Double)) {
/* 519 */         writeNumber(n.doubleValue());
/* 520 */         return; }
/* 521 */       if ((n instanceof Float)) {
/* 522 */         writeNumber(n.floatValue());
/* 523 */         return; }
/* 524 */       if ((n instanceof Short)) {
/* 525 */         writeNumber(n.shortValue());
/* 526 */         return; }
/* 527 */       if ((n instanceof Byte)) {
/* 528 */         writeNumber(n.byteValue());
/* 529 */         return; }
/* 530 */       if ((n instanceof BigInteger)) {
/* 531 */         writeNumber((BigInteger)n);
/* 532 */         return; }
/* 533 */       if ((n instanceof BigDecimal)) {
/* 534 */         writeNumber((BigDecimal)n);
/* 535 */         return;
/*     */       }
/*     */       
/*     */ 
/* 539 */       if ((n instanceof AtomicInteger)) {
/* 540 */         writeNumber(((AtomicInteger)n).get());
/* 541 */         return; }
/* 542 */       if ((n instanceof AtomicLong)) {
/* 543 */         writeNumber(((AtomicLong)n).get());
/* 544 */         return;
/*     */       }
/* 546 */     } else { if ((value instanceof byte[])) {
/* 547 */         writeBinary((byte[])value);
/* 548 */         return; }
/* 549 */       if ((value instanceof Boolean)) {
/* 550 */         writeBoolean(((Boolean)value).booleanValue());
/* 551 */         return; }
/* 552 */       if ((value instanceof AtomicBoolean)) {
/* 553 */         writeBoolean(((AtomicBoolean)value).get());
/* 554 */         return;
/*     */       } }
/* 556 */     throw new IllegalStateException("No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed " + value.getClass().getName() + ")");
/*     */   }
/*     */   
/*     */   protected final void _throwInternal()
/*     */   {
/* 561 */     throw new RuntimeException("Internal error: this code path should never get executed");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _reportUnsupportedOperation()
/*     */   {
/* 568 */     throw new UnsupportedOperationException("Operation not supported by generator of type " + getClass().getName());
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\impl\JsonGeneratorBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */