/*     */ package org.codehaus.jackson.format;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collection;
/*     */ import org.codehaus.jackson.JsonFactory;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataFormatDetector
/*     */ {
/*     */   public static final int DEFAULT_MAX_INPUT_LOOKAHEAD = 64;
/*     */   protected final JsonFactory[] _detectors;
/*     */   protected final MatchStrength _optimalMatch;
/*     */   protected final MatchStrength _minimalMatch;
/*     */   protected final int _maxInputLookahead;
/*     */   
/*     */   public DataFormatDetector(JsonFactory... detectors)
/*     */   {
/*  60 */     this(detectors, MatchStrength.SOLID_MATCH, MatchStrength.WEAK_MATCH, 64);
/*     */   }
/*     */   
/*     */   public DataFormatDetector(Collection<JsonFactory> detectors)
/*     */   {
/*  65 */     this((JsonFactory[])detectors.toArray(new JsonFactory[detectors.size()]));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DataFormatDetector withOptimalMatch(MatchStrength optMatch)
/*     */   {
/*  74 */     if (optMatch == this._optimalMatch) {
/*  75 */       return this;
/*     */     }
/*  77 */     return new DataFormatDetector(this._detectors, optMatch, this._minimalMatch, this._maxInputLookahead);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DataFormatDetector withMinimalMatch(MatchStrength minMatch)
/*     */   {
/*  85 */     if (minMatch == this._minimalMatch) {
/*  86 */       return this;
/*     */     }
/*  88 */     return new DataFormatDetector(this._detectors, this._optimalMatch, minMatch, this._maxInputLookahead);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DataFormatDetector withMaxInputLookahead(int lookaheadBytes)
/*     */   {
/*  97 */     if (lookaheadBytes == this._maxInputLookahead) {
/*  98 */       return this;
/*     */     }
/* 100 */     return new DataFormatDetector(this._detectors, this._optimalMatch, this._minimalMatch, lookaheadBytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private DataFormatDetector(JsonFactory[] detectors, MatchStrength optMatch, MatchStrength minMatch, int maxInputLookahead)
/*     */   {
/* 107 */     this._detectors = detectors;
/* 108 */     this._optimalMatch = optMatch;
/* 109 */     this._minimalMatch = minMatch;
/* 110 */     this._maxInputLookahead = maxInputLookahead;
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
/*     */   public DataFormatMatcher findFormat(InputStream in)
/*     */     throws IOException
/*     */   {
/* 129 */     return _findFormat(new InputAccessor.Std(in, new byte[this._maxInputLookahead]));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DataFormatMatcher findFormat(byte[] fullInputData)
/*     */     throws IOException
/*     */   {
/* 141 */     return _findFormat(new InputAccessor.Std(fullInputData));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private DataFormatMatcher _findFormat(InputAccessor.Std acc)
/*     */     throws IOException
/*     */   {
/* 152 */     JsonFactory bestMatch = null;
/* 153 */     MatchStrength bestMatchStrength = null;
/* 154 */     for (JsonFactory f : this._detectors) {
/* 155 */       acc.reset();
/* 156 */       MatchStrength strength = f.hasFormat(acc);
/*     */       
/* 158 */       if ((strength != null) && (strength.ordinal() >= this._minimalMatch.ordinal()))
/*     */       {
/*     */ 
/*     */ 
/* 162 */         if ((bestMatch == null) || 
/* 163 */           (bestMatchStrength.ordinal() < strength.ordinal()))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 168 */           bestMatch = f;
/* 169 */           bestMatchStrength = strength;
/* 170 */           if (strength.ordinal() >= this._optimalMatch.ordinal())
/*     */             break;
/*     */         } }
/*     */     }
/* 174 */     return acc.createMatcher(bestMatch, bestMatchStrength);
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\format\DataFormatDetector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */