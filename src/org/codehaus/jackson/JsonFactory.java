/*     */ package org.codehaus.jackson;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.net.URL;
/*     */ import org.codehaus.jackson.format.InputAccessor;
/*     */ import org.codehaus.jackson.format.MatchStrength;
/*     */ import org.codehaus.jackson.impl.ByteSourceBootstrapper;
/*     */ import org.codehaus.jackson.impl.ReaderBasedParser;
/*     */ import org.codehaus.jackson.impl.Utf8Generator;
/*     */ import org.codehaus.jackson.impl.WriterBasedGenerator;
/*     */ import org.codehaus.jackson.io.CharacterEscapes;
/*     */ import org.codehaus.jackson.io.IOContext;
/*     */ import org.codehaus.jackson.io.InputDecorator;
/*     */ import org.codehaus.jackson.io.OutputDecorator;
/*     */ import org.codehaus.jackson.io.UTF8Writer;
/*     */ import org.codehaus.jackson.sym.BytesToNameCanonicalizer;
/*     */ import org.codehaus.jackson.sym.CharsToNameCanonicalizer;
/*     */ import org.codehaus.jackson.util.BufferRecycler;
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
/*     */ public class JsonFactory
/*     */   implements Versioned
/*     */ {
/*     */   public static final String FORMAT_NAME_JSON = "JSON";
/*  65 */   static final int DEFAULT_PARSER_FEATURE_FLAGS = ;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  71 */   static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
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
/*  84 */   protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef = new ThreadLocal();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  92 */   protected CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer.createRoot();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */   protected BytesToNameCanonicalizer _rootByteSymbols = BytesToNameCanonicalizer.createRoot();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ObjectCodec _objectCodec;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 121 */   protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 126 */   protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
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
/*     */   protected CharacterEscapes _characterEscapes;
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
/*     */   protected InputDecorator _inputDecorator;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected OutputDecorator _outputDecorator;
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
/* 169 */   public JsonFactory() { this(null); }
/*     */   
/* 171 */   public JsonFactory(ObjectCodec oc) { this._objectCodec = oc; }
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
/*     */   public String getFormatName()
/*     */   {
/* 194 */     if (getClass() == JsonFactory.class) {
/* 195 */       return "JSON";
/*     */     }
/* 197 */     return null;
/*     */   }
/*     */   
/*     */   public MatchStrength hasFormat(InputAccessor acc)
/*     */     throws IOException
/*     */   {
/* 203 */     if (getClass() == JsonFactory.class) {
/* 204 */       return hasJSONFormat(acc);
/*     */     }
/* 206 */     return null;
/*     */   }
/*     */   
/*     */   protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException
/*     */   {
/* 211 */     return ByteSourceBootstrapper.hasJSONFormat(acc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Version version()
/*     */   {
/* 223 */     return VersionUtil.versionFor(Utf8Generator.class);
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
/*     */   public final JsonFactory configure(JsonParser.Feature f, boolean state)
/*     */   {
/* 240 */     if (state) {
/* 241 */       enable(f);
/*     */     } else {
/* 243 */       disable(f);
/*     */     }
/* 245 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory enable(JsonParser.Feature f)
/*     */   {
/* 255 */     this._parserFeatures |= f.getMask();
/* 256 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory disable(JsonParser.Feature f)
/*     */   {
/* 266 */     this._parserFeatures &= (f.getMask() ^ 0xFFFFFFFF);
/* 267 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isEnabled(JsonParser.Feature f)
/*     */   {
/* 276 */     return (this._parserFeatures & f.getMask()) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public final void enableParserFeature(JsonParser.Feature f)
/*     */   {
/* 286 */     enable(f);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public final void disableParserFeature(JsonParser.Feature f)
/*     */   {
/* 294 */     disable(f);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public final void setParserFeature(JsonParser.Feature f, boolean state)
/*     */   {
/* 302 */     configure(f, state);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public final boolean isParserFeatureEnabled(JsonParser.Feature f)
/*     */   {
/* 310 */     return (this._parserFeatures & f.getMask()) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputDecorator getInputDecorator()
/*     */   {
/* 320 */     return this._inputDecorator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory setInputDecorator(InputDecorator d)
/*     */   {
/* 329 */     this._inputDecorator = d;
/* 330 */     return this;
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
/*     */   public final JsonFactory configure(JsonGenerator.Feature f, boolean state)
/*     */   {
/* 346 */     if (state) {
/* 347 */       enable(f);
/*     */     } else {
/* 349 */       disable(f);
/*     */     }
/* 351 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory enable(JsonGenerator.Feature f)
/*     */   {
/* 362 */     this._generatorFeatures |= f.getMask();
/* 363 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory disable(JsonGenerator.Feature f)
/*     */   {
/* 373 */     this._generatorFeatures &= (f.getMask() ^ 0xFFFFFFFF);
/* 374 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isEnabled(JsonGenerator.Feature f)
/*     */   {
/* 383 */     return (this._generatorFeatures & f.getMask()) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final void enableGeneratorFeature(JsonGenerator.Feature f)
/*     */   {
/* 393 */     enable(f);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final void disableGeneratorFeature(JsonGenerator.Feature f)
/*     */   {
/* 401 */     disable(f);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final void setGeneratorFeature(JsonGenerator.Feature f, boolean state)
/*     */   {
/* 409 */     configure(f, state);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final boolean isGeneratorFeatureEnabled(JsonGenerator.Feature f)
/*     */   {
/* 417 */     return isEnabled(f);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CharacterEscapes getCharacterEscapes()
/*     */   {
/* 427 */     return this._characterEscapes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory setCharacterEscapes(CharacterEscapes esc)
/*     */   {
/* 437 */     this._characterEscapes = esc;
/* 438 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OutputDecorator getOutputDecorator()
/*     */   {
/* 448 */     return this._outputDecorator;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonFactory setOutputDecorator(OutputDecorator d)
/*     */   {
/* 457 */     this._outputDecorator = d;
/* 458 */     return this;
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
/*     */   public JsonFactory setCodec(ObjectCodec oc)
/*     */   {
/* 475 */     this._objectCodec = oc;
/* 476 */     return this;
/*     */   }
/*     */   
/* 479 */   public ObjectCodec getCodec() { return this._objectCodec; }
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
/*     */   public JsonParser createJsonParser(File f)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 503 */     IOContext ctxt = _createContext(f, true);
/* 504 */     InputStream in = new FileInputStream(f);
/*     */     
/* 506 */     if (this._inputDecorator != null) {
/* 507 */       in = this._inputDecorator.decorate(ctxt, in);
/*     */     }
/* 509 */     return _createJsonParser(in, ctxt);
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
/*     */   public JsonParser createJsonParser(URL url)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 529 */     IOContext ctxt = _createContext(url, true);
/* 530 */     InputStream in = _optimizedStreamFromURL(url);
/*     */     
/* 532 */     if (this._inputDecorator != null) {
/* 533 */       in = this._inputDecorator.decorate(ctxt, in);
/*     */     }
/* 535 */     return _createJsonParser(in, ctxt);
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
/*     */   public JsonParser createJsonParser(InputStream in)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 556 */     IOContext ctxt = _createContext(in, false);
/*     */     
/* 558 */     if (this._inputDecorator != null) {
/* 559 */       in = this._inputDecorator.decorate(ctxt, in);
/*     */     }
/* 561 */     return _createJsonParser(in, ctxt);
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
/*     */   public JsonParser createJsonParser(Reader r)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 581 */     IOContext ctxt = _createContext(r, false);
/*     */     
/* 583 */     if (this._inputDecorator != null) {
/* 584 */       r = this._inputDecorator.decorate(ctxt, r);
/*     */     }
/* 586 */     return _createJsonParser(r, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonParser createJsonParser(byte[] data)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 596 */     IOContext ctxt = _createContext(data, true);
/*     */     
/* 598 */     if (this._inputDecorator != null) {
/* 599 */       InputStream in = this._inputDecorator.decorate(ctxt, data, 0, data.length);
/* 600 */       if (in != null) {
/* 601 */         return _createJsonParser(in, ctxt);
/*     */       }
/*     */     }
/* 604 */     return _createJsonParser(data, 0, data.length, ctxt);
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
/*     */   public JsonParser createJsonParser(byte[] data, int offset, int len)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 618 */     IOContext ctxt = _createContext(data, true);
/*     */     
/* 620 */     if (this._inputDecorator != null) {
/* 621 */       InputStream in = this._inputDecorator.decorate(ctxt, data, offset, len);
/* 622 */       if (in != null) {
/* 623 */         return _createJsonParser(in, ctxt);
/*     */       }
/*     */     }
/* 626 */     return _createJsonParser(data, offset, len, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonParser createJsonParser(String content)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 636 */     Reader r = new StringReader(content);
/*     */     
/* 638 */     IOContext ctxt = _createContext(r, true);
/*     */     
/* 640 */     if (this._inputDecorator != null) {
/* 641 */       r = this._inputDecorator.decorate(ctxt, r);
/*     */     }
/* 643 */     return _createJsonParser(r, ctxt);
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
/*     */   public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc)
/*     */     throws IOException
/*     */   {
/* 676 */     IOContext ctxt = _createContext(out, false);
/* 677 */     ctxt.setEncoding(enc);
/* 678 */     if (enc == JsonEncoding.UTF8)
/*     */     {
/* 680 */       if (this._outputDecorator != null) {
/* 681 */         out = this._outputDecorator.decorate(ctxt, out);
/*     */       }
/* 683 */       return _createUTF8JsonGenerator(out, ctxt);
/*     */     }
/* 685 */     Writer w = _createWriter(out, enc, ctxt);
/*     */     
/* 687 */     if (this._outputDecorator != null) {
/* 688 */       w = this._outputDecorator.decorate(ctxt, w);
/*     */     }
/* 690 */     return _createJsonGenerator(w, ctxt);
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
/*     */   public JsonGenerator createJsonGenerator(Writer out)
/*     */     throws IOException
/*     */   {
/* 709 */     IOContext ctxt = _createContext(out, false);
/*     */     
/* 711 */     if (this._outputDecorator != null) {
/* 712 */       out = this._outputDecorator.decorate(ctxt, out);
/*     */     }
/* 714 */     return _createJsonGenerator(out, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonGenerator createJsonGenerator(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 726 */     return createJsonGenerator(out, JsonEncoding.UTF8);
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
/*     */   public JsonGenerator createJsonGenerator(File f, JsonEncoding enc)
/*     */     throws IOException
/*     */   {
/* 746 */     OutputStream out = new FileOutputStream(f);
/*     */     
/* 748 */     IOContext ctxt = _createContext(out, true);
/* 749 */     ctxt.setEncoding(enc);
/* 750 */     if (enc == JsonEncoding.UTF8)
/*     */     {
/* 752 */       if (this._outputDecorator != null) {
/* 753 */         out = this._outputDecorator.decorate(ctxt, out);
/*     */       }
/* 755 */       return _createUTF8JsonGenerator(out, ctxt);
/*     */     }
/* 757 */     Writer w = _createWriter(out, enc, ctxt);
/*     */     
/* 759 */     if (this._outputDecorator != null) {
/* 760 */       w = this._outputDecorator.decorate(ctxt, w);
/*     */     }
/* 762 */     return _createJsonGenerator(w, ctxt);
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
/*     */   protected JsonParser _createJsonParser(InputStream in, IOContext ctxt)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 785 */     return new ByteSourceBootstrapper(ctxt, in).constructParser(this._parserFeatures, this._objectCodec, this._rootByteSymbols, this._rootCharSymbols);
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
/*     */   protected JsonParser _createJsonParser(Reader r, IOContext ctxt)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 802 */     return new ReaderBasedParser(ctxt, this._parserFeatures, r, this._objectCodec, this._rootCharSymbols.makeChild(isEnabled(JsonParser.Feature.CANONICALIZE_FIELD_NAMES), isEnabled(JsonParser.Feature.INTERN_FIELD_NAMES)));
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
/*     */   protected JsonParser _createJsonParser(byte[] data, int offset, int len, IOContext ctxt)
/*     */     throws IOException, JsonParseException
/*     */   {
/* 821 */     return new ByteSourceBootstrapper(ctxt, data, offset, len).constructParser(this._parserFeatures, this._objectCodec, this._rootByteSymbols, this._rootCharSymbols);
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
/*     */   protected JsonGenerator _createJsonGenerator(Writer out, IOContext ctxt)
/*     */     throws IOException
/*     */   {
/* 845 */     WriterBasedGenerator gen = new WriterBasedGenerator(ctxt, this._generatorFeatures, this._objectCodec, out);
/* 846 */     if (this._characterEscapes != null) {
/* 847 */       gen.setCharacterEscapes(this._characterEscapes);
/*     */     }
/* 849 */     return gen;
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
/*     */   protected JsonGenerator _createUTF8JsonGenerator(OutputStream out, IOContext ctxt)
/*     */     throws IOException
/*     */   {
/* 865 */     Utf8Generator gen = new Utf8Generator(ctxt, this._generatorFeatures, this._objectCodec, out);
/* 866 */     if (this._characterEscapes != null) {
/* 867 */       gen.setCharacterEscapes(this._characterEscapes);
/*     */     }
/* 869 */     return gen;
/*     */   }
/*     */   
/*     */   protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt)
/*     */     throws IOException
/*     */   {
/* 875 */     if (enc == JsonEncoding.UTF8) {
/* 876 */       return new UTF8Writer(ctxt, out);
/*     */     }
/*     */     
/* 879 */     return new OutputStreamWriter(out, enc.getJavaName());
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
/*     */   protected IOContext _createContext(Object srcRef, boolean resourceManaged)
/*     */   {
/* 894 */     return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BufferRecycler _getBufferRecycler()
/*     */   {
/* 905 */     SoftReference<BufferRecycler> ref = (SoftReference)_recyclerRef.get();
/* 906 */     BufferRecycler br = ref == null ? null : (BufferRecycler)ref.get();
/*     */     
/* 908 */     if (br == null) {
/* 909 */       br = new BufferRecycler();
/* 910 */       _recyclerRef.set(new SoftReference(br));
/*     */     }
/* 912 */     return br;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected InputStream _optimizedStreamFromURL(URL url)
/*     */     throws IOException
/*     */   {
/* 923 */     if ("file".equals(url.getProtocol()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 930 */       String host = url.getHost();
/* 931 */       if ((host == null) || (host.length() == 0)) {
/* 932 */         return new FileInputStream(url.getPath());
/*     */       }
/*     */     }
/* 935 */     return url.openStream();
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\JsonFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */