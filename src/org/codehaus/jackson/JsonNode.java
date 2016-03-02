/*     */ package org.codehaus.jackson;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
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
/*     */ public abstract class JsonNode
/*     */   implements Iterable<JsonNode>
/*     */ {
/*  32 */   protected static final List<JsonNode> NO_NODES = ;
/*  33 */   protected static final List<String> NO_STRINGS = Collections.emptyList();
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
/*     */   public boolean isValueNode()
/*     */   {
/*  55 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isContainerNode()
/*     */   {
/*  64 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isMissingNode()
/*     */   {
/*  75 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isArray()
/*     */   {
/*  83 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isObject()
/*     */   {
/*  88 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isPojo()
/*     */   {
/*  98 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNumber()
/*     */   {
/* 104 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIntegralNumber()
/*     */   {
/* 110 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFloatingPointNumber()
/*     */   {
/* 116 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInt()
/*     */   {
/* 122 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 129 */   public boolean isLong() { return false; }
/*     */   
/* 131 */   public boolean isDouble() { return false; }
/* 132 */   public boolean isBigDecimal() { return false; }
/* 133 */   public boolean isBigInteger() { return false; }
/*     */   
/* 135 */   public boolean isTextual() { return false; }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isBoolean()
/*     */   {
/* 141 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull()
/*     */   {
/* 147 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isBinary()
/*     */   {
/* 157 */     return false;
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
/*     */   public abstract JsonToken asToken();
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
/*     */   public abstract JsonParser.NumberType getNumberType();
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
/*     */   public String getTextValue()
/*     */   {
/* 193 */     return null;
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
/*     */   public byte[] getBinaryValue()
/*     */     throws IOException
/*     */   {
/* 207 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getBooleanValue()
/*     */   {
/* 218 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Number getNumberValue()
/*     */   {
/* 228 */     return null;
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
/* 240 */   public int getIntValue() { return 0; }
/*     */   
/* 242 */   public long getLongValue() { return 0L; }
/* 243 */   public double getDoubleValue() { return 0.0D; }
/* 244 */   public BigDecimal getDecimalValue() { return BigDecimal.ZERO; }
/* 245 */   public BigInteger getBigIntegerValue() { return BigInteger.ZERO; }
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
/*     */   public JsonNode get(int index)
/*     */   {
/* 263 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonNode get(String fieldName)
/*     */   {
/* 275 */     return null;
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
/*     */   public abstract String asText();
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
/*     */   public int asInt()
/*     */   {
/* 306 */     return asInt(0);
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
/*     */   public int asInt(int defaultValue)
/*     */   {
/* 322 */     return defaultValue;
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
/*     */   public long asLong()
/*     */   {
/* 338 */     return asLong(0L);
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
/*     */   public long asLong(long defaultValue)
/*     */   {
/* 354 */     return defaultValue;
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
/*     */   public double asDouble()
/*     */   {
/* 370 */     return asDouble(0.0D);
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
/*     */   public double asDouble(double defaultValue)
/*     */   {
/* 386 */     return defaultValue;
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
/*     */   public boolean asBoolean()
/*     */   {
/* 402 */     return asBoolean(false);
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
/*     */   public boolean asBoolean(boolean defaultValue)
/*     */   {
/* 418 */     return defaultValue;
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
/*     */   @Deprecated
/*     */   public String getValueAsText()
/*     */   {
/* 438 */     return asText();
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
/*     */   @Deprecated
/*     */   public int getValueAsInt()
/*     */   {
/* 455 */     return asInt(0);
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
/*     */   @Deprecated
/*     */   public int getValueAsInt(int defaultValue)
/*     */   {
/* 472 */     return asInt(defaultValue);
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
/*     */   @Deprecated
/*     */   public long getValueAsLong()
/*     */   {
/* 489 */     return asLong(0L);
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
/*     */   @Deprecated
/*     */   public long getValueAsLong(long defaultValue)
/*     */   {
/* 506 */     return asLong(defaultValue);
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
/*     */   @Deprecated
/*     */   public double getValueAsDouble()
/*     */   {
/* 523 */     return asDouble(0.0D);
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
/*     */   @Deprecated
/*     */   public double getValueAsDouble(double defaultValue)
/*     */   {
/* 540 */     return asDouble(defaultValue);
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
/*     */   @Deprecated
/*     */   public boolean getValueAsBoolean()
/*     */   {
/* 557 */     return asBoolean(false);
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
/*     */   @Deprecated
/*     */   public boolean getValueAsBoolean(boolean defaultValue)
/*     */   {
/* 574 */     return asBoolean(defaultValue);
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
/*     */   public boolean has(String fieldName)
/*     */   {
/* 602 */     return get(fieldName) != null;
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
/*     */   public boolean has(int index)
/*     */   {
/* 627 */     return get(index) != null;
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
/*     */   public abstract JsonNode findValue(String paramString);
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
/*     */   public final List<JsonNode> findValues(String fieldName)
/*     */   {
/* 656 */     List<JsonNode> result = findValues(fieldName, null);
/* 657 */     if (result == null) {
/* 658 */       return Collections.emptyList();
/*     */     }
/* 660 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final List<String> findValuesAsText(String fieldName)
/*     */   {
/* 671 */     List<String> result = findValuesAsText(fieldName, null);
/* 672 */     if (result == null) {
/* 673 */       return Collections.emptyList();
/*     */     }
/* 675 */     return result;
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
/*     */   public abstract JsonNode findPath(String paramString);
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
/*     */   public abstract JsonNode findParent(String paramString);
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
/*     */   public final List<JsonNode> findParents(String fieldName)
/*     */   {
/* 720 */     List<JsonNode> result = findParents(fieldName, null);
/* 721 */     if (result == null) {
/* 722 */       return Collections.emptyList();
/*     */     }
/* 724 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<JsonNode> findValues(String paramString, List<JsonNode> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<String> findValuesAsText(String paramString, List<String> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract List<JsonNode> findParents(String paramString, List<JsonNode> paramList);
/*     */   
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 745 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final Iterator<JsonNode> iterator()
/*     */   {
/* 753 */     return getElements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<JsonNode> getElements()
/*     */   {
/* 761 */     return NO_NODES.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<String> getFieldNames()
/*     */   {
/* 767 */     return NO_STRINGS.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<Map.Entry<String, JsonNode>> getFields()
/*     */   {
/* 776 */     Collection<Map.Entry<String, JsonNode>> coll = Collections.emptyList();
/* 777 */     return coll.iterator();
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
/*     */   public abstract JsonNode path(String paramString);
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
/*     */   public final JsonNode getPath(String fieldName)
/*     */   {
/* 803 */     return path(fieldName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonNode path(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final JsonNode getPath(int index)
/*     */   {
/* 821 */     return path(index);
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
/*     */   public JsonNode with(String propertyName)
/*     */   {
/* 834 */     throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + getClass().getName() + "), can not call with() on it");
/*     */   }
/*     */   
/*     */   public abstract JsonParser traverse();
/*     */   
/*     */   public abstract String toString();
/*     */   
/*     */   public abstract boolean equals(Object paramObject);
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\JsonNode.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */