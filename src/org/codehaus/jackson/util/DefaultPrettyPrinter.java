/*     */ package org.codehaus.jackson.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.codehaus.jackson.JsonGenerationException;
/*     */ import org.codehaus.jackson.JsonGenerator;
/*     */ import org.codehaus.jackson.PrettyPrinter;
/*     */ import org.codehaus.jackson.impl.Indenter;
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
/*     */ public class DefaultPrettyPrinter
/*     */   implements PrettyPrinter
/*     */ {
/*  24 */   protected Indenter _arrayIndenter = new FixedSpaceIndenter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  32 */   protected Indenter _objectIndenter = new Lf2SpacesIndenter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  41 */   protected boolean _spacesInObjectEntries = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  49 */   protected int _nesting = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void indentArraysWith(Indenter i)
/*     */   {
/*  61 */     this._arrayIndenter = (i == null ? new NopIndenter() : i);
/*     */   }
/*     */   
/*     */   public void indentObjectsWith(Indenter i)
/*     */   {
/*  66 */     this._objectIndenter = (i == null ? new NopIndenter() : i);
/*     */   }
/*     */   
/*  69 */   public void spacesInObjectEntries(boolean b) { this._spacesInObjectEntries = b; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeRootValueSeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/*  81 */     jg.writeRaw(' ');
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeStartObject(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/*  88 */     jg.writeRaw('{');
/*  89 */     if (!this._objectIndenter.isInline()) {
/*  90 */       this._nesting += 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void beforeObjectEntries(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/*  98 */     this._objectIndenter.writeIndentation(jg, this._nesting);
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
/*     */   public void writeObjectFieldValueSeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 114 */     if (this._spacesInObjectEntries) {
/* 115 */       jg.writeRaw(" : ");
/*     */     } else {
/* 117 */       jg.writeRaw(':');
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
/*     */ 
/*     */   public void writeObjectEntrySeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 134 */     jg.writeRaw(',');
/* 135 */     this._objectIndenter.writeIndentation(jg, this._nesting);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeEndObject(JsonGenerator jg, int nrOfEntries)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 142 */     if (!this._objectIndenter.isInline()) {
/* 143 */       this._nesting -= 1;
/*     */     }
/* 145 */     if (nrOfEntries > 0) {
/* 146 */       this._objectIndenter.writeIndentation(jg, this._nesting);
/*     */     } else {
/* 148 */       jg.writeRaw(' ');
/*     */     }
/* 150 */     jg.writeRaw('}');
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeStartArray(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 157 */     if (!this._arrayIndenter.isInline()) {
/* 158 */       this._nesting += 1;
/*     */     }
/* 160 */     jg.writeRaw('[');
/*     */   }
/*     */   
/*     */ 
/*     */   public void beforeArrayValues(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 167 */     this._arrayIndenter.writeIndentation(jg, this._nesting);
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
/*     */   public void writeArrayValueSeparator(JsonGenerator jg)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 183 */     jg.writeRaw(',');
/* 184 */     this._arrayIndenter.writeIndentation(jg, this._nesting);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeEndArray(JsonGenerator jg, int nrOfValues)
/*     */     throws IOException, JsonGenerationException
/*     */   {
/* 191 */     if (!this._arrayIndenter.isInline()) {
/* 192 */       this._nesting -= 1;
/*     */     }
/* 194 */     if (nrOfValues > 0) {
/* 195 */       this._arrayIndenter.writeIndentation(jg, this._nesting);
/*     */     } else {
/* 197 */       jg.writeRaw(' ');
/*     */     }
/* 199 */     jg.writeRaw(']');
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class NopIndenter
/*     */     implements Indenter
/*     */   {
/*     */     public void writeIndentation(JsonGenerator jg, int level) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isInline()
/*     */     {
/* 218 */       return true;
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
/*     */   public static class FixedSpaceIndenter
/*     */     implements Indenter
/*     */   {
/*     */     public void writeIndentation(JsonGenerator jg, int level)
/*     */       throws IOException, JsonGenerationException
/*     */     {
/* 235 */       jg.writeRaw(' ');
/*     */     }
/*     */     
/*     */     public boolean isInline() {
/* 239 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Lf2SpacesIndenter implements Indenter
/*     */   {
/*     */     static final String SYSTEM_LINE_SEPARATOR;
/*     */     static final int SPACE_COUNT = 64;
/*     */     static final char[] SPACES;
/*     */     
/*     */     static
/*     */     {
/* 251 */       String lf = null;
/*     */       try {
/* 253 */         lf = System.getProperty("line.separator");
/*     */       } catch (Throwable t) {}
/* 255 */       SYSTEM_LINE_SEPARATOR = lf == null ? "\n" : lf;
/*     */       
/*     */ 
/*     */ 
/* 259 */       SPACES = new char[64];
/*     */       
/* 261 */       Arrays.fill(SPACES, ' ');
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isInline()
/*     */     {
/* 267 */       return false;
/*     */     }
/*     */     
/*     */     public void writeIndentation(JsonGenerator jg, int level)
/*     */       throws IOException, JsonGenerationException
/*     */     {
/* 273 */       jg.writeRaw(SYSTEM_LINE_SEPARATOR);
/* 274 */       if (level > 0) {
/* 275 */         level += level;
/* 276 */         while (level > 64) {
/* 277 */           jg.writeRaw(SPACES, 0, 64);
/* 278 */           level -= SPACES.length;
/*     */         }
/* 280 */         jg.writeRaw(SPACES, 0, level);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\util\DefaultPrettyPrinter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */