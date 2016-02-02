/*      */ package org.codehaus.jackson.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import org.codehaus.jackson.Base64Variant;
/*      */ import org.codehaus.jackson.JsonGenerationException;
/*      */ import org.codehaus.jackson.JsonGenerator;
/*      */ import org.codehaus.jackson.JsonGenerator.Feature;
/*      */ import org.codehaus.jackson.JsonLocation;
/*      */ import org.codehaus.jackson.JsonNode;
/*      */ import org.codehaus.jackson.JsonParseException;
/*      */ import org.codehaus.jackson.JsonParser;
/*      */ import org.codehaus.jackson.JsonParser.Feature;
/*      */ import org.codehaus.jackson.JsonParser.NumberType;
/*      */ import org.codehaus.jackson.JsonProcessingException;
/*      */ import org.codehaus.jackson.JsonStreamContext;
/*      */ import org.codehaus.jackson.JsonToken;
/*      */ import org.codehaus.jackson.ObjectCodec;
/*      */ import org.codehaus.jackson.SerializableString;
/*      */ import org.codehaus.jackson.impl.JsonParserMinimalBase;
/*      */ import org.codehaus.jackson.impl.JsonReadContext;
/*      */ import org.codehaus.jackson.impl.JsonWriteContext;
/*      */ import org.codehaus.jackson.io.SerializedString;
/*      */ 
/*      */ public class TokenBuffer
/*      */   extends JsonGenerator
/*      */ {
/*   29 */   protected static final int DEFAULT_PARSER_FEATURES = ;
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
/*      */   protected int _generatorFeatures;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _closed;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Segment _first;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Segment _last;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int _appendOffset;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonWriteContext _writeContext;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TokenBuffer(ObjectCodec codec)
/*      */   {
/*   98 */     this._objectCodec = codec;
/*   99 */     this._generatorFeatures = DEFAULT_PARSER_FEATURES;
/*  100 */     this._writeContext = JsonWriteContext.createRootContext();
/*      */     
/*  102 */     this._first = (this._last = new Segment());
/*  103 */     this._appendOffset = 0;
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
/*      */   public JsonParser asParser()
/*      */   {
/*  118 */     return asParser(this._objectCodec);
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
/*      */   public JsonParser asParser(ObjectCodec codec)
/*      */   {
/*  136 */     return new Parser(this._first, codec);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonParser asParser(JsonParser src)
/*      */   {
/*  145 */     Parser p = new Parser(this._first, src.getCodec());
/*  146 */     p.setLocation(src.getTokenLocation());
/*  147 */     return p;
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
/*      */   public void serialize(JsonGenerator jgen)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  169 */     Segment segment = this._first;
/*  170 */     int ptr = -1;
/*      */     for (;;)
/*      */     {
/*  173 */       ptr++; if (ptr >= 16) {
/*  174 */         ptr = 0;
/*  175 */         segment = segment.next();
/*  176 */         if (segment == null) break;
/*      */       }
/*  178 */       JsonToken t = segment.type(ptr);
/*  179 */       if (t == null) {
/*      */         break;
/*      */       }
/*  182 */       switch (t) {
/*      */       case START_OBJECT: 
/*  184 */         jgen.writeStartObject();
/*  185 */         break;
/*      */       case END_OBJECT: 
/*  187 */         jgen.writeEndObject();
/*  188 */         break;
/*      */       case START_ARRAY: 
/*  190 */         jgen.writeStartArray();
/*  191 */         break;
/*      */       case END_ARRAY: 
/*  193 */         jgen.writeEndArray();
/*  194 */         break;
/*      */       
/*      */ 
/*      */       case FIELD_NAME: 
/*  198 */         Object ob = segment.get(ptr);
/*  199 */         if ((ob instanceof SerializableString)) {
/*  200 */           jgen.writeFieldName((SerializableString)ob);
/*      */         } else {
/*  202 */           jgen.writeFieldName((String)ob);
/*      */         }
/*      */         
/*  205 */         break;
/*      */       
/*      */       case VALUE_STRING: 
/*  208 */         Object ob = segment.get(ptr);
/*  209 */         if ((ob instanceof SerializableString)) {
/*  210 */           jgen.writeString((SerializableString)ob);
/*      */         } else {
/*  212 */           jgen.writeString((String)ob);
/*      */         }
/*      */         
/*  215 */         break;
/*      */       
/*      */       case VALUE_NUMBER_INT: 
/*  218 */         Number n = (Number)segment.get(ptr);
/*  219 */         if ((n instanceof BigInteger)) {
/*  220 */           jgen.writeNumber((BigInteger)n);
/*  221 */         } else if ((n instanceof Long)) {
/*  222 */           jgen.writeNumber(n.longValue());
/*      */         } else {
/*  224 */           jgen.writeNumber(n.intValue());
/*      */         }
/*      */         
/*  227 */         break;
/*      */       
/*      */       case VALUE_NUMBER_FLOAT: 
/*  230 */         Object n = segment.get(ptr);
/*  231 */         if ((n instanceof BigDecimal)) {
/*  232 */           jgen.writeNumber((BigDecimal)n);
/*  233 */         } else if ((n instanceof Float)) {
/*  234 */           jgen.writeNumber(((Float)n).floatValue());
/*  235 */         } else if ((n instanceof Double)) {
/*  236 */           jgen.writeNumber(((Double)n).doubleValue());
/*  237 */         } else if (n == null) {
/*  238 */           jgen.writeNull();
/*  239 */         } else if ((n instanceof String)) {
/*  240 */           jgen.writeNumber((String)n);
/*      */         } else {
/*  242 */           throw new JsonGenerationException("Unrecognized value type for VALUE_NUMBER_FLOAT: " + n.getClass().getName() + ", can not serialize");
/*      */         }
/*      */         
/*  245 */         break;
/*      */       case VALUE_TRUE: 
/*  247 */         jgen.writeBoolean(true);
/*  248 */         break;
/*      */       case VALUE_FALSE: 
/*  250 */         jgen.writeBoolean(false);
/*  251 */         break;
/*      */       case VALUE_NULL: 
/*  253 */         jgen.writeNull();
/*  254 */         break;
/*      */       case VALUE_EMBEDDED_OBJECT: 
/*  256 */         jgen.writeObject(segment.get(ptr));
/*  257 */         break;
/*      */       default: 
/*  259 */         throw new RuntimeException("Internal error: should never end up through this code path");
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String toString()
/*      */   {
/*  268 */     int MAX_COUNT = 100;
/*      */     
/*  270 */     StringBuilder sb = new StringBuilder();
/*  271 */     sb.append("[TokenBuffer: ");
/*  272 */     JsonParser jp = asParser();
/*  273 */     int count = 0;
/*      */     for (;;)
/*      */     {
/*      */       JsonToken t;
/*      */       try {
/*  278 */         t = jp.nextToken();
/*      */       } catch (IOException ioe) {
/*  280 */         throw new IllegalStateException(ioe);
/*      */       }
/*  282 */       if (t == null) break;
/*  283 */       if (count < 100) {
/*  284 */         if (count > 0) {
/*  285 */           sb.append(", ");
/*      */         }
/*  287 */         sb.append(t.toString());
/*      */       }
/*  289 */       count++;
/*      */     }
/*      */     
/*  292 */     if (count >= 100) {
/*  293 */       sb.append(" ... (truncated ").append(count - 100).append(" entries)");
/*      */     }
/*  295 */     sb.append(']');
/*  296 */     return sb.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonGenerator enable(JsonGenerator.Feature f)
/*      */   {
/*  307 */     this._generatorFeatures |= f.getMask();
/*  308 */     return this;
/*      */   }
/*      */   
/*      */   public JsonGenerator disable(JsonGenerator.Feature f)
/*      */   {
/*  313 */     this._generatorFeatures &= (f.getMask() ^ 0xFFFFFFFF);
/*  314 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isEnabled(JsonGenerator.Feature f)
/*      */   {
/*  321 */     return (this._generatorFeatures & f.getMask()) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public JsonGenerator useDefaultPrettyPrinter()
/*      */   {
/*  327 */     return this;
/*      */   }
/*      */   
/*      */   public JsonGenerator setCodec(ObjectCodec oc)
/*      */   {
/*  332 */     this._objectCodec = oc;
/*  333 */     return this;
/*      */   }
/*      */   
/*      */   public ObjectCodec getCodec() {
/*  337 */     return this._objectCodec;
/*      */   }
/*      */   
/*  340 */   public final JsonWriteContext getOutputContext() { return this._writeContext; }
/*      */   
/*      */ 
/*      */ 
/*      */   public void flush()
/*      */     throws IOException
/*      */   {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  353 */     this._closed = true;
/*      */   }
/*      */   
/*      */   public boolean isClosed() {
/*  357 */     return this._closed;
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
/*  369 */     _append(JsonToken.START_ARRAY);
/*  370 */     this._writeContext = this._writeContext.createChildArrayContext();
/*      */   }
/*      */   
/*      */ 
/*      */   public final void writeEndArray()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  377 */     _append(JsonToken.END_ARRAY);
/*      */     
/*  379 */     JsonWriteContext c = this._writeContext.getParent();
/*  380 */     if (c != null) {
/*  381 */       this._writeContext = c;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final void writeStartObject()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  389 */     _append(JsonToken.START_OBJECT);
/*  390 */     this._writeContext = this._writeContext.createChildObjectContext();
/*      */   }
/*      */   
/*      */ 
/*      */   public final void writeEndObject()
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  397 */     _append(JsonToken.END_OBJECT);
/*      */     
/*  399 */     JsonWriteContext c = this._writeContext.getParent();
/*  400 */     if (c != null) {
/*  401 */       this._writeContext = c;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public final void writeFieldName(String name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  409 */     _append(JsonToken.FIELD_NAME, name);
/*  410 */     this._writeContext.writeFieldName(name);
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeFieldName(SerializableString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  417 */     _append(JsonToken.FIELD_NAME, name);
/*  418 */     this._writeContext.writeFieldName(name.getValue());
/*      */   }
/*      */   
/*      */ 
/*      */   public void writeFieldName(SerializedString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  425 */     _append(JsonToken.FIELD_NAME, name);
/*  426 */     this._writeContext.writeFieldName(name.getValue());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeString(String text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  437 */     if (text == null) {
/*  438 */       writeNull();
/*      */     } else {
/*  440 */       _append(JsonToken.VALUE_STRING, text);
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException
/*      */   {
/*  446 */     writeString(new String(text, offset, len));
/*      */   }
/*      */   
/*      */   public void writeString(SerializableString text) throws IOException, JsonGenerationException
/*      */   {
/*  451 */     if (text == null) {
/*  452 */       writeNull();
/*      */     } else {
/*  454 */       _append(JsonToken.VALUE_STRING, text);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  463 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeUTF8String(byte[] text, int offset, int length)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  471 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(String text) throws IOException, JsonGenerationException
/*      */   {
/*  476 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException
/*      */   {
/*  481 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException
/*      */   {
/*  486 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRaw(char c) throws IOException, JsonGenerationException
/*      */   {
/*  491 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRawValue(String text) throws IOException, JsonGenerationException
/*      */   {
/*  496 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRawValue(String text, int offset, int len) throws IOException, JsonGenerationException
/*      */   {
/*  501 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */   public void writeRawValue(char[] text, int offset, int len) throws IOException, JsonGenerationException
/*      */   {
/*  506 */     _reportUnsupportedOperation();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeNumber(int i)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  517 */     _append(JsonToken.VALUE_NUMBER_INT, Integer.valueOf(i));
/*      */   }
/*      */   
/*      */   public void writeNumber(long l) throws IOException, JsonGenerationException
/*      */   {
/*  522 */     _append(JsonToken.VALUE_NUMBER_INT, Long.valueOf(l));
/*      */   }
/*      */   
/*      */   public void writeNumber(double d) throws IOException, JsonGenerationException
/*      */   {
/*  527 */     _append(JsonToken.VALUE_NUMBER_FLOAT, Double.valueOf(d));
/*      */   }
/*      */   
/*      */   public void writeNumber(float f) throws IOException, JsonGenerationException
/*      */   {
/*  532 */     _append(JsonToken.VALUE_NUMBER_FLOAT, Float.valueOf(f));
/*      */   }
/*      */   
/*      */   public void writeNumber(BigDecimal dec) throws IOException, JsonGenerationException
/*      */   {
/*  537 */     if (dec == null) {
/*  538 */       writeNull();
/*      */     } else {
/*  540 */       _append(JsonToken.VALUE_NUMBER_FLOAT, dec);
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeNumber(BigInteger v) throws IOException, JsonGenerationException
/*      */   {
/*  546 */     if (v == null) {
/*  547 */       writeNull();
/*      */     } else {
/*  549 */       _append(JsonToken.VALUE_NUMBER_INT, v);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void writeNumber(String encodedValue)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  558 */     _append(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
/*      */   }
/*      */   
/*      */   public void writeBoolean(boolean state) throws IOException, JsonGenerationException
/*      */   {
/*  563 */     _append(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
/*      */   }
/*      */   
/*      */   public void writeNull() throws IOException, JsonGenerationException
/*      */   {
/*  568 */     _append(JsonToken.VALUE_NULL);
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
/*      */   public void writeObject(Object value)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/*  582 */     _append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeTree(JsonNode rootNode)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/*  592 */     _append(JsonToken.VALUE_EMBEDDED_OBJECT, rootNode);
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
/*      */   public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  611 */     byte[] copy = new byte[len];
/*  612 */     System.arraycopy(data, offset, copy, 0, len);
/*  613 */     writeObject(copy);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyCurrentEvent(JsonParser jp)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/*  625 */     switch (jp.getCurrentToken()) {
/*      */     case START_OBJECT: 
/*  627 */       writeStartObject();
/*  628 */       break;
/*      */     case END_OBJECT: 
/*  630 */       writeEndObject();
/*  631 */       break;
/*      */     case START_ARRAY: 
/*  633 */       writeStartArray();
/*  634 */       break;
/*      */     case END_ARRAY: 
/*  636 */       writeEndArray();
/*  637 */       break;
/*      */     case FIELD_NAME: 
/*  639 */       writeFieldName(jp.getCurrentName());
/*  640 */       break;
/*      */     case VALUE_STRING: 
/*  642 */       if (jp.hasTextCharacters()) {
/*  643 */         writeString(jp.getTextCharacters(), jp.getTextOffset(), jp.getTextLength());
/*      */       } else {
/*  645 */         writeString(jp.getText());
/*      */       }
/*  647 */       break;
/*      */     case VALUE_NUMBER_INT: 
/*  649 */       switch (jp.getNumberType()) {
/*      */       case INT: 
/*  651 */         writeNumber(jp.getIntValue());
/*  652 */         break;
/*      */       case BIG_INTEGER: 
/*  654 */         writeNumber(jp.getBigIntegerValue());
/*  655 */         break;
/*      */       default: 
/*  657 */         writeNumber(jp.getLongValue());
/*      */       }
/*  659 */       break;
/*      */     case VALUE_NUMBER_FLOAT: 
/*  661 */       switch (jp.getNumberType()) {
/*      */       case BIG_DECIMAL: 
/*  663 */         writeNumber(jp.getDecimalValue());
/*  664 */         break;
/*      */       case FLOAT: 
/*  666 */         writeNumber(jp.getFloatValue());
/*  667 */         break;
/*      */       default: 
/*  669 */         writeNumber(jp.getDoubleValue());
/*      */       }
/*  671 */       break;
/*      */     case VALUE_TRUE: 
/*  673 */       writeBoolean(true);
/*  674 */       break;
/*      */     case VALUE_FALSE: 
/*  676 */       writeBoolean(false);
/*  677 */       break;
/*      */     case VALUE_NULL: 
/*  679 */       writeNull();
/*  680 */       break;
/*      */     case VALUE_EMBEDDED_OBJECT: 
/*  682 */       writeObject(jp.getEmbeddedObject());
/*  683 */       break;
/*      */     default: 
/*  685 */       throw new RuntimeException("Internal error: should never end up through this code path");
/*      */     }
/*      */   }
/*      */   
/*      */   public void copyCurrentStructure(JsonParser jp) throws IOException, JsonProcessingException
/*      */   {
/*  691 */     JsonToken t = jp.getCurrentToken();
/*      */     
/*      */ 
/*  694 */     if (t == JsonToken.FIELD_NAME) {
/*  695 */       writeFieldName(jp.getCurrentName());
/*  696 */       t = jp.nextToken();
/*      */     }
/*      */     
/*      */ 
/*  700 */     switch (t) {
/*      */     case START_ARRAY: 
/*  702 */       writeStartArray();
/*  703 */       while (jp.nextToken() != JsonToken.END_ARRAY) {
/*  704 */         copyCurrentStructure(jp);
/*      */       }
/*  706 */       writeEndArray();
/*  707 */       break;
/*      */     case START_OBJECT: 
/*  709 */       writeStartObject();
/*  710 */       while (jp.nextToken() != JsonToken.END_OBJECT) {
/*  711 */         copyCurrentStructure(jp);
/*      */       }
/*  713 */       writeEndObject();
/*  714 */       break;
/*      */     default: 
/*  716 */       copyCurrentEvent(jp);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _append(JsonToken type)
/*      */   {
/*  726 */     Segment next = this._last.append(this._appendOffset, type);
/*  727 */     if (next == null) {
/*  728 */       this._appendOffset += 1;
/*      */     } else {
/*  730 */       this._last = next;
/*  731 */       this._appendOffset = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void _append(JsonToken type, Object value) {
/*  736 */     Segment next = this._last.append(this._appendOffset, type, value);
/*  737 */     if (next == null) {
/*  738 */       this._appendOffset += 1;
/*      */     } else {
/*  740 */       this._last = next;
/*  741 */       this._appendOffset = 1;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _reportUnsupportedOperation() {
/*  746 */     throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static final class Parser
/*      */     extends JsonParserMinimalBase
/*      */   {
/*      */     protected ObjectCodec _codec;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected TokenBuffer.Segment _segment;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected int _segmentPtr;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected JsonReadContext _parsingContext;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected boolean _closed;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected transient ByteArrayBuilder _byteBuilder;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  786 */     protected JsonLocation _location = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Parser(TokenBuffer.Segment firstSeg, ObjectCodec codec)
/*      */     {
/*  796 */       super();
/*  797 */       this._segment = firstSeg;
/*  798 */       this._segmentPtr = -1;
/*  799 */       this._codec = codec;
/*  800 */       this._parsingContext = JsonReadContext.createRootContext(-1, -1);
/*      */     }
/*      */     
/*      */     public void setLocation(JsonLocation l) {
/*  804 */       this._location = l;
/*      */     }
/*      */     
/*      */     public ObjectCodec getCodec() {
/*  808 */       return this._codec;
/*      */     }
/*      */     
/*  811 */     public void setCodec(ObjectCodec c) { this._codec = c; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonToken peekNextToken()
/*      */       throws IOException, JsonParseException
/*      */     {
/*  823 */       if (this._closed) return null;
/*  824 */       TokenBuffer.Segment seg = this._segment;
/*  825 */       int ptr = this._segmentPtr + 1;
/*  826 */       if (ptr >= 16) {
/*  827 */         ptr = 0;
/*  828 */         seg = seg == null ? null : seg.next();
/*      */       }
/*  830 */       return seg == null ? null : seg.type(ptr);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void close()
/*      */       throws IOException
/*      */     {
/*  841 */       if (!this._closed) {
/*  842 */         this._closed = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonToken nextToken()
/*      */       throws IOException, JsonParseException
/*      */     {
/*  856 */       if ((this._closed) || (this._segment == null)) { return null;
/*      */       }
/*      */       
/*  859 */       if (++this._segmentPtr >= 16) {
/*  860 */         this._segmentPtr = 0;
/*  861 */         this._segment = this._segment.next();
/*  862 */         if (this._segment == null) {
/*  863 */           return null;
/*      */         }
/*      */       }
/*  866 */       this._currToken = this._segment.type(this._segmentPtr);
/*      */       
/*  868 */       if (this._currToken == JsonToken.FIELD_NAME) {
/*  869 */         Object ob = _currentObject();
/*  870 */         String name = (ob instanceof String) ? (String)ob : ob.toString();
/*  871 */         this._parsingContext.setCurrentName(name);
/*  872 */       } else if (this._currToken == JsonToken.START_OBJECT) {
/*  873 */         this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
/*  874 */       } else if (this._currToken == JsonToken.START_ARRAY) {
/*  875 */         this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
/*  876 */       } else if ((this._currToken == JsonToken.END_OBJECT) || (this._currToken == JsonToken.END_ARRAY))
/*      */       {
/*      */ 
/*  879 */         this._parsingContext = this._parsingContext.getParent();
/*      */         
/*  881 */         if (this._parsingContext == null) {
/*  882 */           this._parsingContext = JsonReadContext.createRootContext(-1, -1);
/*      */         }
/*      */       }
/*  885 */       return this._currToken;
/*      */     }
/*      */     
/*      */     public boolean isClosed() {
/*  889 */       return this._closed;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonStreamContext getParsingContext()
/*      */     {
/*  898 */       return this._parsingContext;
/*      */     }
/*      */     
/*  901 */     public JsonLocation getTokenLocation() { return getCurrentLocation(); }
/*      */     
/*      */     public JsonLocation getCurrentLocation()
/*      */     {
/*  905 */       return this._location == null ? JsonLocation.NA : this._location;
/*      */     }
/*      */     
/*      */     public String getCurrentName() {
/*  909 */       return this._parsingContext.getCurrentName();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getText()
/*      */     {
/*  921 */       if ((this._currToken == JsonToken.VALUE_STRING) || (this._currToken == JsonToken.FIELD_NAME))
/*      */       {
/*  923 */         Object ob = _currentObject();
/*  924 */         if ((ob instanceof String)) {
/*  925 */           return (String)ob;
/*      */         }
/*  927 */         return ob == null ? null : ob.toString();
/*      */       }
/*  929 */       if (this._currToken == null) {
/*  930 */         return null;
/*      */       }
/*  932 */       switch (TokenBuffer.1.$SwitchMap$org$codehaus$jackson$JsonToken[this._currToken.ordinal()]) {
/*      */       case 7: 
/*      */       case 8: 
/*  935 */         Object ob = _currentObject();
/*  936 */         return ob == null ? null : ob.toString();
/*      */       }
/*  938 */       return this._currToken.asString();
/*      */     }
/*      */     
/*      */     public char[] getTextCharacters()
/*      */     {
/*  943 */       String str = getText();
/*  944 */       return str == null ? null : str.toCharArray();
/*      */     }
/*      */     
/*      */     public int getTextLength()
/*      */     {
/*  949 */       String str = getText();
/*  950 */       return str == null ? 0 : str.length();
/*      */     }
/*      */     
/*      */     public int getTextOffset() {
/*  954 */       return 0;
/*      */     }
/*      */     
/*      */     public boolean hasTextCharacters()
/*      */     {
/*  959 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public BigInteger getBigIntegerValue()
/*      */       throws IOException, JsonParseException
/*      */     {
/*  971 */       Number n = getNumberValue();
/*  972 */       if ((n instanceof BigInteger)) {
/*  973 */         return (BigInteger)n;
/*      */       }
/*  975 */       switch (TokenBuffer.1.$SwitchMap$org$codehaus$jackson$JsonParser$NumberType[getNumberType().ordinal()]) {
/*      */       case 3: 
/*  977 */         return ((BigDecimal)n).toBigInteger();
/*      */       }
/*      */       
/*  980 */       return BigInteger.valueOf(n.longValue());
/*      */     }
/*      */     
/*      */     public BigDecimal getDecimalValue()
/*      */       throws IOException, JsonParseException
/*      */     {
/*  986 */       Number n = getNumberValue();
/*  987 */       if ((n instanceof BigDecimal)) {
/*  988 */         return (BigDecimal)n;
/*      */       }
/*  990 */       switch (TokenBuffer.1.$SwitchMap$org$codehaus$jackson$JsonParser$NumberType[getNumberType().ordinal()]) {
/*      */       case 1: 
/*      */       case 5: 
/*  993 */         return BigDecimal.valueOf(n.longValue());
/*      */       case 2: 
/*  995 */         return new BigDecimal((BigInteger)n);
/*      */       }
/*      */       
/*  998 */       return BigDecimal.valueOf(n.doubleValue());
/*      */     }
/*      */     
/*      */     public double getDoubleValue() throws IOException, JsonParseException
/*      */     {
/* 1003 */       return getNumberValue().doubleValue();
/*      */     }
/*      */     
/*      */     public float getFloatValue() throws IOException, JsonParseException
/*      */     {
/* 1008 */       return getNumberValue().floatValue();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getIntValue()
/*      */       throws IOException, JsonParseException
/*      */     {
/* 1015 */       if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
/* 1016 */         return ((Number)_currentObject()).intValue();
/*      */       }
/* 1018 */       return getNumberValue().intValue();
/*      */     }
/*      */     
/*      */     public long getLongValue() throws IOException, JsonParseException
/*      */     {
/* 1023 */       return getNumberValue().longValue();
/*      */     }
/*      */     
/*      */     public JsonParser.NumberType getNumberType()
/*      */       throws IOException, JsonParseException
/*      */     {
/* 1029 */       Number n = getNumberValue();
/* 1030 */       if ((n instanceof Integer)) return JsonParser.NumberType.INT;
/* 1031 */       if ((n instanceof Long)) return JsonParser.NumberType.LONG;
/* 1032 */       if ((n instanceof Double)) return JsonParser.NumberType.DOUBLE;
/* 1033 */       if ((n instanceof BigDecimal)) return JsonParser.NumberType.BIG_DECIMAL;
/* 1034 */       if ((n instanceof Float)) return JsonParser.NumberType.FLOAT;
/* 1035 */       if ((n instanceof BigInteger)) return JsonParser.NumberType.BIG_INTEGER;
/* 1036 */       return null;
/*      */     }
/*      */     
/*      */     public final Number getNumberValue() throws IOException, JsonParseException
/*      */     {
/* 1041 */       _checkIsNumber();
/* 1042 */       return (Number)_currentObject();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Object getEmbeddedObject()
/*      */     {
/* 1054 */       if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 1055 */         return _currentObject();
/*      */       }
/* 1057 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getBinaryValue(Base64Variant b64variant)
/*      */       throws IOException, JsonParseException
/*      */     {
/* 1064 */       if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT)
/*      */       {
/* 1066 */         Object ob = _currentObject();
/* 1067 */         if ((ob instanceof byte[])) {
/* 1068 */           return (byte[])ob;
/*      */         }
/*      */       }
/*      */       
/* 1072 */       if (this._currToken != JsonToken.VALUE_STRING) {
/* 1073 */         throw _constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), can not access as binary");
/*      */       }
/* 1075 */       String str = getText();
/* 1076 */       if (str == null) {
/* 1077 */         return null;
/*      */       }
/* 1079 */       ByteArrayBuilder builder = this._byteBuilder;
/* 1080 */       if (builder == null) {
/* 1081 */         this._byteBuilder = (builder = new ByteArrayBuilder(100));
/*      */       } else {
/* 1083 */         this._byteBuilder.reset();
/*      */       }
/* 1085 */       _decodeBase64(str, builder, b64variant);
/* 1086 */       return builder.toByteArray();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected final Object _currentObject()
/*      */     {
/* 1096 */       return this._segment.get(this._segmentPtr);
/*      */     }
/*      */     
/*      */     protected final void _checkIsNumber() throws JsonParseException
/*      */     {
/* 1101 */       if ((this._currToken == null) || (!this._currToken.isNumeric())) {
/* 1102 */         throw _constructError("Current token (" + this._currToken + ") not numeric, can not use numeric value accessors");
/*      */       }
/*      */     }
/*      */     
/*      */     protected void _handleEOF() throws JsonParseException
/*      */     {
/* 1108 */       _throwInternal();
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
/*      */   protected static final class Segment
/*      */   {
/*      */     public static final int TOKENS_PER_SEGMENT = 16;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1130 */     private static final JsonToken[] TOKEN_TYPES_BY_INDEX = new JsonToken[16];
/* 1131 */     static { JsonToken[] t = JsonToken.values();
/* 1132 */       System.arraycopy(t, 1, TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Segment _next;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected long _tokenTypes;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1150 */     protected final Object[] _tokens = new Object[16];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public JsonToken type(int index)
/*      */     {
/* 1158 */       long l = this._tokenTypes;
/* 1159 */       if (index > 0) {
/* 1160 */         l >>= index << 2;
/*      */       }
/* 1162 */       int ix = (int)l & 0xF;
/* 1163 */       return TOKEN_TYPES_BY_INDEX[ix];
/*      */     }
/*      */     
/*      */     public Object get(int index) {
/* 1167 */       return this._tokens[index];
/*      */     }
/*      */     
/* 1170 */     public Segment next() { return this._next; }
/*      */     
/*      */ 
/*      */ 
/*      */     public Segment append(int index, JsonToken tokenType)
/*      */     {
/* 1176 */       if (index < 16) {
/* 1177 */         set(index, tokenType);
/* 1178 */         return null;
/*      */       }
/* 1180 */       this._next = new Segment();
/* 1181 */       this._next.set(0, tokenType);
/* 1182 */       return this._next;
/*      */     }
/*      */     
/*      */     public Segment append(int index, JsonToken tokenType, Object value)
/*      */     {
/* 1187 */       if (index < 16) {
/* 1188 */         set(index, tokenType, value);
/* 1189 */         return null;
/*      */       }
/* 1191 */       this._next = new Segment();
/* 1192 */       this._next.set(0, tokenType, value);
/* 1193 */       return this._next;
/*      */     }
/*      */     
/*      */     public void set(int index, JsonToken tokenType)
/*      */     {
/* 1198 */       long typeCode = tokenType.ordinal();
/*      */       
/*      */ 
/*      */ 
/* 1202 */       if (index > 0) {
/* 1203 */         typeCode <<= index << 2;
/*      */       }
/* 1205 */       this._tokenTypes |= typeCode;
/*      */     }
/*      */     
/*      */     public void set(int index, JsonToken tokenType, Object value)
/*      */     {
/* 1210 */       this._tokens[index] = value;
/* 1211 */       long typeCode = tokenType.ordinal();
/*      */       
/*      */ 
/*      */ 
/* 1215 */       if (index > 0) {
/* 1216 */         typeCode <<= index << 2;
/*      */       }
/* 1218 */       this._tokenTypes |= typeCode;
/*      */     }
/*      */   }
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\util\TokenBuffer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */