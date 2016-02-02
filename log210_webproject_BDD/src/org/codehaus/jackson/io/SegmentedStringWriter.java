/*     */ package org.codehaus.jackson.io;
/*     */ 
/*     */ import java.io.Writer;
/*     */ import org.codehaus.jackson.util.BufferRecycler;
/*     */ import org.codehaus.jackson.util.TextBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SegmentedStringWriter
/*     */   extends Writer
/*     */ {
/*     */   protected final TextBuffer _buffer;
/*     */   
/*     */   public SegmentedStringWriter(BufferRecycler br)
/*     */   {
/*  26 */     this._buffer = new TextBuffer(br);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Writer append(char c)
/*     */   {
/*  38 */     write(c);
/*  39 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Writer append(CharSequence csq)
/*     */   {
/*  45 */     String str = csq.toString();
/*  46 */     this._buffer.append(str, 0, str.length());
/*  47 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Writer append(CharSequence csq, int start, int end)
/*     */   {
/*  53 */     String str = csq.subSequence(start, end).toString();
/*  54 */     this._buffer.append(str, 0, str.length());
/*  55 */     return this;
/*     */   }
/*     */   
/*     */   public void close() {}
/*     */   
/*     */   public void flush() {}
/*     */   
/*     */   public void write(char[] cbuf)
/*     */   {
/*  64 */     this._buffer.append(cbuf, 0, cbuf.length);
/*     */   }
/*     */   
/*     */   public void write(char[] cbuf, int off, int len)
/*     */   {
/*  69 */     this._buffer.append(cbuf, off, len);
/*     */   }
/*     */   
/*     */   public void write(int c)
/*     */   {
/*  74 */     this._buffer.append((char)c);
/*     */   }
/*     */   
/*     */   public void write(String str) {
/*  78 */     this._buffer.append(str, 0, str.length());
/*     */   }
/*     */   
/*     */   public void write(String str, int off, int len) {
/*  82 */     this._buffer.append(str, off, len);
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
/*     */   public String getAndClear()
/*     */   {
/* 100 */     String result = this._buffer.contentsAsString();
/* 101 */     this._buffer.releaseBuffers();
/* 102 */     return result;
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\io\SegmentedStringWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */