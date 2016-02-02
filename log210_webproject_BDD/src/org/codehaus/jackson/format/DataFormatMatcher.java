/*     */ package org.codehaus.jackson.format;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.codehaus.jackson.JsonFactory;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ import org.codehaus.jackson.io.MergedStream;
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
/*     */ public class DataFormatMatcher
/*     */ {
/*     */   protected final InputStream _originalStream;
/*     */   protected final byte[] _bufferedData;
/*     */   protected final int _bufferedLength;
/*     */   protected final JsonFactory _match;
/*     */   protected final MatchStrength _matchStrength;
/*     */   
/*     */   protected DataFormatMatcher(InputStream in, byte[] buffered, int bufferedLength, JsonFactory match, MatchStrength strength)
/*     */   {
/*  40 */     this._originalStream = in;
/*  41 */     this._bufferedData = buffered;
/*  42 */     this._bufferedLength = bufferedLength;
/*  43 */     this._match = match;
/*  44 */     this._matchStrength = strength;
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
/*     */   public boolean hasMatch()
/*     */   {
/*  57 */     return this._match != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MatchStrength getMatchStrength()
/*     */   {
/*  64 */     return this._matchStrength == null ? MatchStrength.INCONCLUSIVE : this._matchStrength;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonFactory getMatch()
/*     */   {
/*  70 */     return this._match;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMatchedFormatName()
/*     */   {
/*  80 */     return this._match.getFormatName();
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
/*     */   public JsonParser createParserWithMatch()
/*     */     throws IOException
/*     */   {
/*  95 */     if (this._match == null) {
/*  96 */       return null;
/*     */     }
/*  98 */     if (this._originalStream == null) {
/*  99 */       return this._match.createJsonParser(this._bufferedData, 0, this._bufferedLength);
/*     */     }
/* 101 */     return this._match.createJsonParser(getDataStream());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream getDataStream()
/*     */   {
/* 112 */     if (this._originalStream == null) {
/* 113 */       return new ByteArrayInputStream(this._bufferedData, 0, this._bufferedLength);
/*     */     }
/* 115 */     return new MergedStream(null, this._originalStream, this._bufferedData, 0, this._bufferedLength);
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\format\DataFormatMatcher.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */