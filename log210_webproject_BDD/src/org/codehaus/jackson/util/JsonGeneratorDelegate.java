/*     */ package org.codehaus.jackson.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.codehaus.jackson.FormatSchema;
/*     */ import org.codehaus.jackson.JsonGenerationException;
/*     */ import org.codehaus.jackson.JsonGenerator;
/*     */ import org.codehaus.jackson.JsonGenerator.Feature;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ import org.codehaus.jackson.JsonProcessingException;
/*     */ import org.codehaus.jackson.SerializableString;
/*     */ 
/*     */ public class JsonGeneratorDelegate extends JsonGenerator
/*     */ {
/*     */   protected JsonGenerator delegate;
/*     */   
/*     */   public JsonGeneratorDelegate(JsonGenerator d)
/*     */   {
/*  18 */     this.delegate = d;
/*     */   }
/*     */   
/*     */   public void close() throws IOException
/*     */   {
/*  23 */     this.delegate.close();
/*     */   }
/*     */   
/*     */   public void copyCurrentEvent(JsonParser jp) throws IOException, JsonProcessingException
/*     */   {
/*  28 */     this.delegate.copyCurrentEvent(jp);
/*     */   }
/*     */   
/*     */   public void copyCurrentStructure(JsonParser jp) throws IOException, JsonProcessingException
/*     */   {
/*  33 */     this.delegate.copyCurrentStructure(jp);
/*     */   }
/*     */   
/*     */   public JsonGenerator disable(JsonGenerator.Feature f)
/*     */   {
/*  38 */     return this.delegate.disable(f);
/*     */   }
/*     */   
/*     */   public JsonGenerator enable(JsonGenerator.Feature f)
/*     */   {
/*  43 */     return this.delegate.enable(f);
/*     */   }
/*     */   
/*     */   public void flush() throws IOException
/*     */   {
/*  48 */     this.delegate.flush();
/*     */   }
/*     */   
/*     */   public org.codehaus.jackson.ObjectCodec getCodec()
/*     */   {
/*  53 */     return this.delegate.getCodec();
/*     */   }
/*     */   
/*     */   public org.codehaus.jackson.JsonStreamContext getOutputContext()
/*     */   {
/*  58 */     return this.delegate.getOutputContext();
/*     */   }
/*     */   
/*     */   public void setSchema(FormatSchema schema)
/*     */   {
/*  63 */     this.delegate.setSchema(schema);
/*     */   }
/*     */   
/*     */   public boolean canUseSchema(FormatSchema schema)
/*     */   {
/*  68 */     return this.delegate.canUseSchema(schema);
/*     */   }
/*     */   
/*     */   public org.codehaus.jackson.Version version()
/*     */   {
/*  73 */     return this.delegate.version();
/*     */   }
/*     */   
/*     */   public Object getOutputTarget()
/*     */   {
/*  78 */     return this.delegate.getOutputTarget();
/*     */   }
/*     */   
/*     */   public boolean isClosed()
/*     */   {
/*  83 */     return this.delegate.isClosed();
/*     */   }
/*     */   
/*     */   public boolean isEnabled(JsonGenerator.Feature f)
/*     */   {
/*  88 */     return this.delegate.isEnabled(f);
/*     */   }
/*     */   
/*     */   public JsonGenerator setCodec(org.codehaus.jackson.ObjectCodec oc)
/*     */   {
/*  93 */     this.delegate.setCodec(oc);
/*  94 */     return this;
/*     */   }
/*     */   
/*     */   public JsonGenerator useDefaultPrettyPrinter()
/*     */   {
/*  99 */     this.delegate.useDefaultPrettyPrinter();
/* 100 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBinary(org.codehaus.jackson.Base64Variant b64variant, byte[] data, int offset, int len)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 107 */     this.delegate.writeBinary(b64variant, data, offset, len);
/*     */   }
/*     */   
/*     */   public void writeBoolean(boolean state) throws IOException, JsonGenerationException
/*     */   {
/* 112 */     this.delegate.writeBoolean(state);
/*     */   }
/*     */   
/*     */   public void writeEndArray() throws IOException, JsonGenerationException
/*     */   {
/* 117 */     this.delegate.writeEndArray();
/*     */   }
/*     */   
/*     */   public void writeEndObject() throws IOException, JsonGenerationException
/*     */   {
/* 122 */     this.delegate.writeEndObject();
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeFieldName(String name)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 129 */     this.delegate.writeFieldName(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeFieldName(org.codehaus.jackson.io.SerializedString name)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 136 */     this.delegate.writeFieldName(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeFieldName(SerializableString name)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 143 */     this.delegate.writeFieldName(name);
/*     */   }
/*     */   
/*     */   public void writeNull() throws IOException, JsonGenerationException
/*     */   {
/* 148 */     this.delegate.writeNull();
/*     */   }
/*     */   
/*     */   public void writeNumber(int v) throws IOException, JsonGenerationException
/*     */   {
/* 153 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(long v) throws IOException, JsonGenerationException
/*     */   {
/* 158 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(java.math.BigInteger v)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 164 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(double v)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 170 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(float v)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 176 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(java.math.BigDecimal v)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 182 */     this.delegate.writeNumber(v);
/*     */   }
/*     */   
/*     */   public void writeNumber(String encodedValue) throws IOException, JsonGenerationException, UnsupportedOperationException
/*     */   {
/* 187 */     this.delegate.writeNumber(encodedValue);
/*     */   }
/*     */   
/*     */   public void writeObject(Object pojo) throws IOException, JsonProcessingException
/*     */   {
/* 192 */     this.delegate.writeObject(pojo);
/*     */   }
/*     */   
/*     */   public void writeRaw(String text) throws IOException, JsonGenerationException
/*     */   {
/* 197 */     this.delegate.writeRaw(text);
/*     */   }
/*     */   
/*     */   public void writeRaw(String text, int offset, int len) throws IOException, JsonGenerationException
/*     */   {
/* 202 */     this.delegate.writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRaw(char[] text, int offset, int len) throws IOException, JsonGenerationException
/*     */   {
/* 207 */     this.delegate.writeRaw(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRaw(char c) throws IOException, JsonGenerationException
/*     */   {
/* 212 */     this.delegate.writeRaw(c);
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text) throws IOException, JsonGenerationException
/*     */   {
/* 217 */     this.delegate.writeRawValue(text);
/*     */   }
/*     */   
/*     */   public void writeRawValue(String text, int offset, int len) throws IOException, JsonGenerationException
/*     */   {
/* 222 */     this.delegate.writeRawValue(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeRawValue(char[] text, int offset, int len) throws IOException, JsonGenerationException
/*     */   {
/* 227 */     this.delegate.writeRawValue(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeStartArray() throws IOException, JsonGenerationException
/*     */   {
/* 232 */     this.delegate.writeStartArray();
/*     */   }
/*     */   
/*     */   public void writeStartObject() throws IOException, JsonGenerationException
/*     */   {
/* 237 */     this.delegate.writeStartObject();
/*     */   }
/*     */   
/*     */   public void writeString(String text) throws IOException, JsonGenerationException
/*     */   {
/* 242 */     this.delegate.writeString(text);
/*     */   }
/*     */   
/*     */   public void writeString(char[] text, int offset, int len) throws IOException, JsonGenerationException
/*     */   {
/* 247 */     this.delegate.writeString(text, offset, len);
/*     */   }
/*     */   
/*     */   public void writeString(SerializableString text) throws IOException, JsonGenerationException
/*     */   {
/* 252 */     this.delegate.writeString(text);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeRawUTF8String(byte[] text, int offset, int length)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 259 */     this.delegate.writeRawUTF8String(text, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeUTF8String(byte[] text, int offset, int length)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 266 */     this.delegate.writeUTF8String(text, offset, length);
/*     */   }
/*     */   
/*     */   public void writeTree(org.codehaus.jackson.JsonNode rootNode) throws IOException, JsonProcessingException
/*     */   {
/* 271 */     this.delegate.writeTree(rootNode);
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\util\JsonGeneratorDelegate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */