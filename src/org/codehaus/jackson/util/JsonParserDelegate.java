/*     */ package org.codehaus.jackson.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import org.codehaus.jackson.Base64Variant;
/*     */ import org.codehaus.jackson.FormatSchema;
/*     */ import org.codehaus.jackson.JsonLocation;
/*     */ import org.codehaus.jackson.JsonParseException;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ import org.codehaus.jackson.JsonParser.Feature;
/*     */ import org.codehaus.jackson.JsonParser.NumberType;
/*     */ import org.codehaus.jackson.JsonStreamContext;
/*     */ import org.codehaus.jackson.JsonToken;
/*     */ import org.codehaus.jackson.ObjectCodec;
/*     */ import org.codehaus.jackson.Version;
/*     */ 
/*     */ 
/*     */ public class JsonParserDelegate
/*     */   extends JsonParser
/*     */ {
/*     */   protected JsonParser delegate;
/*     */   
/*     */   public JsonParserDelegate(JsonParser d)
/*     */   {
/*  26 */     this.delegate = d;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCodec(ObjectCodec c)
/*     */   {
/*  37 */     this.delegate.setCodec(c);
/*     */   }
/*     */   
/*     */   public ObjectCodec getCodec()
/*     */   {
/*  42 */     return this.delegate.getCodec();
/*     */   }
/*     */   
/*     */   public JsonParser enable(JsonParser.Feature f)
/*     */   {
/*  47 */     this.delegate.enable(f);
/*  48 */     return this;
/*     */   }
/*     */   
/*     */   public JsonParser disable(JsonParser.Feature f)
/*     */   {
/*  53 */     this.delegate.disable(f);
/*  54 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isEnabled(JsonParser.Feature f)
/*     */   {
/*  59 */     return this.delegate.isEnabled(f);
/*     */   }
/*     */   
/*     */   public void setSchema(FormatSchema schema)
/*     */   {
/*  64 */     this.delegate.setSchema(schema);
/*     */   }
/*     */   
/*     */   public boolean canUseSchema(FormatSchema schema)
/*     */   {
/*  69 */     return this.delegate.canUseSchema(schema);
/*     */   }
/*     */   
/*     */   public Version version()
/*     */   {
/*  74 */     return this.delegate.version();
/*     */   }
/*     */   
/*     */   public Object getInputSource()
/*     */   {
/*  79 */     return this.delegate.getInputSource();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  90 */     this.delegate.close();
/*     */   }
/*     */   
/*     */   public boolean isClosed()
/*     */   {
/*  95 */     return this.delegate.isClosed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonToken getCurrentToken()
/*     */   {
/* 106 */     return this.delegate.getCurrentToken();
/*     */   }
/*     */   
/*     */   public boolean hasCurrentToken()
/*     */   {
/* 111 */     return this.delegate.hasCurrentToken();
/*     */   }
/*     */   
/*     */   public void clearCurrentToken()
/*     */   {
/* 116 */     this.delegate.clearCurrentToken();
/*     */   }
/*     */   
/*     */   public String getCurrentName() throws IOException, JsonParseException
/*     */   {
/* 121 */     return this.delegate.getCurrentName();
/*     */   }
/*     */   
/*     */   public JsonLocation getCurrentLocation()
/*     */   {
/* 126 */     return this.delegate.getCurrentLocation();
/*     */   }
/*     */   
/*     */   public JsonToken getLastClearedToken()
/*     */   {
/* 131 */     return this.delegate.getLastClearedToken();
/*     */   }
/*     */   
/*     */   public JsonStreamContext getParsingContext()
/*     */   {
/* 136 */     return this.delegate.getParsingContext();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getText()
/*     */     throws IOException, JsonParseException
/*     */   {
/* 147 */     return this.delegate.getText();
/*     */   }
/*     */   
/*     */   public char[] getTextCharacters() throws IOException, JsonParseException
/*     */   {
/* 152 */     return this.delegate.getTextCharacters();
/*     */   }
/*     */   
/*     */   public int getTextLength() throws IOException, JsonParseException
/*     */   {
/* 157 */     return this.delegate.getTextLength();
/*     */   }
/*     */   
/*     */   public int getTextOffset() throws IOException, JsonParseException
/*     */   {
/* 162 */     return this.delegate.getTextOffset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getBooleanValue()
/*     */     throws IOException, JsonParseException
/*     */   {
/* 173 */     return this.delegate.getBooleanValue();
/*     */   }
/*     */   
/*     */   public BigInteger getBigIntegerValue() throws IOException, JsonParseException
/*     */   {
/* 178 */     return this.delegate.getBigIntegerValue();
/*     */   }
/*     */   
/*     */   public byte getByteValue() throws IOException, JsonParseException
/*     */   {
/* 183 */     return this.delegate.getByteValue();
/*     */   }
/*     */   
/*     */   public short getShortValue() throws IOException, JsonParseException
/*     */   {
/* 188 */     return this.delegate.getShortValue();
/*     */   }
/*     */   
/*     */   public BigDecimal getDecimalValue() throws IOException, JsonParseException
/*     */   {
/* 193 */     return this.delegate.getDecimalValue();
/*     */   }
/*     */   
/*     */   public double getDoubleValue() throws IOException, JsonParseException
/*     */   {
/* 198 */     return this.delegate.getDoubleValue();
/*     */   }
/*     */   
/*     */   public float getFloatValue() throws IOException, JsonParseException
/*     */   {
/* 203 */     return this.delegate.getFloatValue();
/*     */   }
/*     */   
/*     */   public int getIntValue() throws IOException, JsonParseException
/*     */   {
/* 208 */     return this.delegate.getIntValue();
/*     */   }
/*     */   
/*     */   public long getLongValue() throws IOException, JsonParseException
/*     */   {
/* 213 */     return this.delegate.getLongValue();
/*     */   }
/*     */   
/*     */   public JsonParser.NumberType getNumberType() throws IOException, JsonParseException
/*     */   {
/* 218 */     return this.delegate.getNumberType();
/*     */   }
/*     */   
/*     */   public Number getNumberValue() throws IOException, JsonParseException
/*     */   {
/* 223 */     return this.delegate.getNumberValue();
/*     */   }
/*     */   
/*     */   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException
/*     */   {
/* 228 */     return this.delegate.getBinaryValue(b64variant);
/*     */   }
/*     */   
/*     */   public Object getEmbeddedObject() throws IOException, JsonParseException
/*     */   {
/* 233 */     return this.delegate.getEmbeddedObject();
/*     */   }
/*     */   
/*     */   public JsonLocation getTokenLocation()
/*     */   {
/* 238 */     return this.delegate.getTokenLocation();
/*     */   }
/*     */   
/*     */   public JsonToken nextToken() throws IOException, JsonParseException
/*     */   {
/* 243 */     return this.delegate.nextToken();
/*     */   }
/*     */   
/*     */   public JsonParser skipChildren() throws IOException, JsonParseException
/*     */   {
/* 248 */     this.delegate.skipChildren();
/*     */     
/* 250 */     return this;
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\util\JsonParserDelegate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */