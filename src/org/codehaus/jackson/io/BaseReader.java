/*     */ package org.codehaus.jackson.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class BaseReader
/*     */   extends Reader
/*     */ {
/*     */   protected static final int LAST_VALID_UNICODE_CHAR = 1114111;
/*     */   protected static final char NULL_CHAR = '\000';
/*     */   protected static final char NULL_BYTE = '\000';
/*     */   protected final IOContext _context;
/*     */   protected InputStream _in;
/*     */   protected byte[] _buffer;
/*     */   protected int _ptr;
/*     */   protected int _length;
/*     */   
/*     */   protected BaseReader(IOContext context, InputStream in, byte[] buf, int ptr, int len)
/*     */   {
/*  42 */     this._context = context;
/*  43 */     this._in = in;
/*  44 */     this._buffer = buf;
/*  45 */     this._ptr = ptr;
/*  46 */     this._length = len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  58 */     InputStream in = this._in;
/*     */     
/*  60 */     if (in != null) {
/*  61 */       this._in = null;
/*  62 */       freeBuffers();
/*  63 */       in.close();
/*     */     }
/*     */   }
/*     */   
/*  67 */   protected char[] _tmpBuf = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  77 */     if (this._tmpBuf == null) {
/*  78 */       this._tmpBuf = new char[1];
/*     */     }
/*  80 */     if (read(this._tmpBuf, 0, 1) < 1) {
/*  81 */       return -1;
/*     */     }
/*  83 */     return this._tmpBuf[0];
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
/*     */   public final void freeBuffers()
/*     */   {
/*  99 */     byte[] buf = this._buffer;
/* 100 */     if (buf != null) {
/* 101 */       this._buffer = null;
/* 102 */       this._context.releaseReadIOBuffer(buf);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void reportBounds(char[] cbuf, int start, int len)
/*     */     throws IOException
/*     */   {
/* 109 */     throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
/*     */   }
/*     */   
/*     */   protected void reportStrangeStream()
/*     */     throws IOException
/*     */   {
/* 115 */     throw new IOException("Strange I/O stream, returned 0 bytes on read");
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\io\BaseReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */