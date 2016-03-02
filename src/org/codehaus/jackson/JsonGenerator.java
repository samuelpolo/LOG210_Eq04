/*      */ package org.codehaus.jackson;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import org.codehaus.jackson.io.CharacterEscapes;
/*      */ import org.codehaus.jackson.io.SerializedString;
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
/*      */ public abstract class JsonGenerator
/*      */   implements Closeable, Versioned
/*      */ {
/*      */   protected PrettyPrinter _cfgPrettyPrinter;
/*      */   
/*      */   public static enum Feature
/*      */   {
/*   51 */     AUTO_CLOSE_TARGET(true), 
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
/*   63 */     AUTO_CLOSE_JSON_CONTENT(true), 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   72 */     QUOTE_FIELD_NAMES(true), 
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
/*   86 */     QUOTE_NON_NUMERIC_NUMBERS(true), 
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
/*  105 */     WRITE_NUMBERS_AS_STRINGS(false), 
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
/*  120 */     FLUSH_PASSED_TO_STREAM(true), 
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
/*  131 */     ESCAPE_NON_ASCII(false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     final boolean _defaultState;
/*      */     
/*      */ 
/*      */     final int _mask;
/*      */     
/*      */ 
/*      */ 
/*      */     public static int collectDefaults()
/*      */     {
/*  145 */       int flags = 0;
/*  146 */       for (Feature f : values()) {
/*  147 */         if (f.enabledByDefault()) {
/*  148 */           flags |= f.getMask();
/*      */         }
/*      */       }
/*  151 */       return flags;
/*      */     }
/*      */     
/*      */     private Feature(boolean defaultState) {
/*  155 */       this._defaultState = defaultState;
/*  156 */       this._mask = (1 << ordinal());
/*      */     }
/*      */     
/*  159 */     public boolean enabledByDefault() { return this._defaultState; }
/*      */     
/*  161 */     public int getMask() { return this._mask; }
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
/*      */   public void setSchema(FormatSchema schema)
/*      */   {
/*  204 */     throw new UnsupportedOperationException("Generator of type " + getClass().getName() + " does not support schema of type '" + schema.getSchemaType() + "'");
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
/*      */   public boolean canUseSchema(FormatSchema schema)
/*      */   {
/*  219 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Version version()
/*      */   {
/*  227 */     return Version.unknownVersion();
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
/*      */   public Object getOutputTarget()
/*      */   {
/*  248 */     return null;
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
/*      */   public abstract JsonGenerator enable(Feature paramFeature);
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
/*      */   public abstract JsonGenerator disable(Feature paramFeature);
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
/*      */   public JsonGenerator configure(Feature f, boolean state)
/*      */   {
/*  287 */     if (state) {
/*  288 */       enable(f);
/*      */     } else {
/*  290 */       disable(f);
/*      */     }
/*  292 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract boolean isEnabled(Feature paramFeature);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract JsonGenerator setCodec(ObjectCodec paramObjectCodec);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract ObjectCodec getCodec();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public void enableFeature(Feature f)
/*      */   {
/*  324 */     enable(f);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public void disableFeature(Feature f) {
/*  329 */     disable(f);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public void setFeature(Feature f, boolean state) {
/*  334 */     configure(f, state);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public boolean isFeatureEnabled(Feature f) {
/*  339 */     return isEnabled(f);
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
/*      */   public JsonGenerator setPrettyPrinter(PrettyPrinter pp)
/*      */   {
/*  359 */     this._cfgPrettyPrinter = pp;
/*  360 */     return this;
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
/*      */   public abstract JsonGenerator useDefaultPrettyPrinter();
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
/*      */   public JsonGenerator setHighestNonEscapedChar(int charCode)
/*      */   {
/*  396 */     return this;
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
/*      */   public int getHighestEscapedChar()
/*      */   {
/*  412 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public CharacterEscapes getCharacterEscapes()
/*      */   {
/*  421 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonGenerator setCharacterEscapes(CharacterEscapes esc)
/*      */   {
/*  431 */     return this;
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
/*      */   public abstract void writeStartArray()
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeEndArray()
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeStartObject()
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeEndObject()
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeFieldName(String paramString)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public void writeFieldName(SerializedString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  516 */     writeFieldName(name.getValue());
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
/*      */   public void writeFieldName(SerializableString name)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  535 */     writeFieldName(name.getValue());
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
/*      */   public abstract void writeString(String paramString)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeString(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public void writeString(SerializableString text)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  579 */     writeString(text.getValue());
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
/*      */   public abstract void writeRawUTF8String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeUTF8String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeRaw(String paramString)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeRaw(String paramString, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeRaw(char paramChar)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeRawValue(String paramString)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeRawValue(String paramString, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeRawValue(char[] paramArrayOfChar, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeBinary(Base64Variant paramBase64Variant, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public void writeBinary(byte[] data, int offset, int len)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  740 */     writeBinary(Base64Variants.getDefaultVariant(), data, offset, len);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeBinary(byte[] data)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  752 */     writeBinary(Base64Variants.getDefaultVariant(), data, 0, data.length);
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
/*      */   public abstract void writeNumber(int paramInt)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeNumber(long paramLong)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeNumber(BigInteger paramBigInteger)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeNumber(double paramDouble)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeNumber(float paramFloat)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeNumber(BigDecimal paramBigDecimal)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeNumber(String paramString)
/*      */     throws IOException, JsonGenerationException, UnsupportedOperationException;
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
/*      */   public abstract void writeBoolean(boolean paramBoolean)
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeNull()
/*      */     throws IOException, JsonGenerationException;
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
/*      */   public abstract void writeObject(Object paramObject)
/*      */     throws IOException, JsonProcessingException;
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
/*      */   public abstract void writeTree(JsonNode paramJsonNode)
/*      */     throws IOException, JsonProcessingException;
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
/*      */   public void writeStringField(String fieldName, String value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  912 */     writeFieldName(fieldName);
/*  913 */     writeString(value);
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
/*      */   public final void writeBooleanField(String fieldName, boolean value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  927 */     writeFieldName(fieldName);
/*  928 */     writeBoolean(value);
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
/*      */   public final void writeNullField(String fieldName)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  942 */     writeFieldName(fieldName);
/*  943 */     writeNull();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void writeNumberField(String fieldName, int value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  956 */     writeFieldName(fieldName);
/*  957 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, long value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  971 */     writeFieldName(fieldName);
/*  972 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, double value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/*  986 */     writeFieldName(fieldName);
/*  987 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, float value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1001 */     writeFieldName(fieldName);
/* 1002 */     writeNumber(value);
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
/*      */   public final void writeNumberField(String fieldName, BigDecimal value)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1017 */     writeFieldName(fieldName);
/* 1018 */     writeNumber(value);
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
/*      */   public final void writeBinaryField(String fieldName, byte[] data)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1033 */     writeFieldName(fieldName);
/* 1034 */     writeBinary(data);
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
/*      */   public final void writeArrayFieldStart(String fieldName)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1053 */     writeFieldName(fieldName);
/* 1054 */     writeStartArray();
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
/*      */   public final void writeObjectFieldStart(String fieldName)
/*      */     throws IOException, JsonGenerationException
/*      */   {
/* 1073 */     writeFieldName(fieldName);
/* 1074 */     writeStartObject();
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
/*      */   public final void writeObjectField(String fieldName, Object pojo)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1089 */     writeFieldName(fieldName);
/* 1090 */     writeObject(pojo);
/*      */   }
/*      */   
/*      */   public abstract void copyCurrentEvent(JsonParser paramJsonParser)
/*      */     throws IOException, JsonProcessingException;
/*      */   
/*      */   public abstract void copyCurrentStructure(JsonParser paramJsonParser)
/*      */     throws IOException, JsonProcessingException;
/*      */   
/*      */   public abstract JsonStreamContext getOutputContext();
/*      */   
/*      */   public abstract void flush()
/*      */     throws IOException;
/*      */   
/*      */   public abstract boolean isClosed();
/*      */   
/*      */   public abstract void close()
/*      */     throws IOException;
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\JsonGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */