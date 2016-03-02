/*     */ package org.codehaus.jackson.util;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import org.codehaus.jackson.io.NumberInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TextBuffer
/*     */ {
/*  28 */   static final char[] NO_CHARS = new char[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final int MIN_SEGMENT_LEN = 1000;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final int MAX_SEGMENT_LEN = 262144;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final BufferRecycler _allocator;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private char[] _inputBuffer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int _inputStart;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int _inputLen;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ArrayList<char[]> _segments;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  84 */   private boolean _hasSegments = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int _segmentSize;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private char[] _currentSegment;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int _currentSize;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String _resultString;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private char[] _resultArray;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TextBuffer(BufferRecycler allocator)
/*     */   {
/* 122 */     this._allocator = allocator;
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
/*     */   public void releaseBuffers()
/*     */   {
/* 136 */     if (this._allocator == null) {
/* 137 */       resetWithEmpty();
/*     */     }
/* 139 */     else if (this._currentSegment != null)
/*     */     {
/* 141 */       resetWithEmpty();
/*     */       
/* 143 */       char[] buf = this._currentSegment;
/* 144 */       this._currentSegment = null;
/* 145 */       this._allocator.releaseCharBuffer(BufferRecycler.CharBufferType.TEXT_BUFFER, buf);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetWithEmpty()
/*     */   {
/* 156 */     this._inputStart = -1;
/* 157 */     this._currentSize = 0;
/* 158 */     this._inputLen = 0;
/*     */     
/* 160 */     this._inputBuffer = null;
/* 161 */     this._resultString = null;
/* 162 */     this._resultArray = null;
/*     */     
/*     */ 
/* 165 */     if (this._hasSegments) {
/* 166 */       clearSegments();
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
/*     */   public void resetWithShared(char[] buf, int start, int len)
/*     */   {
/* 179 */     this._resultString = null;
/* 180 */     this._resultArray = null;
/*     */     
/*     */ 
/* 183 */     this._inputBuffer = buf;
/* 184 */     this._inputStart = start;
/* 185 */     this._inputLen = len;
/*     */     
/*     */ 
/* 188 */     if (this._hasSegments) {
/* 189 */       clearSegments();
/*     */     }
/*     */   }
/*     */   
/*     */   public void resetWithCopy(char[] buf, int start, int len)
/*     */   {
/* 195 */     this._inputBuffer = null;
/* 196 */     this._inputStart = -1;
/* 197 */     this._inputLen = 0;
/*     */     
/* 199 */     this._resultString = null;
/* 200 */     this._resultArray = null;
/*     */     
/*     */ 
/* 203 */     if (this._hasSegments) {
/* 204 */       clearSegments();
/* 205 */     } else if (this._currentSegment == null) {
/* 206 */       this._currentSegment = findBuffer(len);
/*     */     }
/* 208 */     this._currentSize = (this._segmentSize = 0);
/* 209 */     append(buf, start, len);
/*     */   }
/*     */   
/*     */   public void resetWithString(String value)
/*     */   {
/* 214 */     this._inputBuffer = null;
/* 215 */     this._inputStart = -1;
/* 216 */     this._inputLen = 0;
/*     */     
/* 218 */     this._resultString = value;
/* 219 */     this._resultArray = null;
/*     */     
/* 221 */     if (this._hasSegments) {
/* 222 */       clearSegments();
/*     */     }
/* 224 */     this._currentSize = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final char[] findBuffer(int needed)
/*     */   {
/* 234 */     if (this._allocator != null) {
/* 235 */       return this._allocator.allocCharBuffer(BufferRecycler.CharBufferType.TEXT_BUFFER, needed);
/*     */     }
/* 237 */     return new char[Math.max(needed, 1000)];
/*     */   }
/*     */   
/*     */   private final void clearSegments()
/*     */   {
/* 242 */     this._hasSegments = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 250 */     this._segments.clear();
/* 251 */     this._currentSize = (this._segmentSize = 0);
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
/*     */   public int size()
/*     */   {
/* 264 */     if (this._inputStart >= 0) {
/* 265 */       return this._inputLen;
/*     */     }
/* 267 */     if (this._resultArray != null) {
/* 268 */       return this._resultArray.length;
/*     */     }
/* 270 */     if (this._resultString != null) {
/* 271 */       return this._resultString.length();
/*     */     }
/*     */     
/* 274 */     return this._segmentSize + this._currentSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getTextOffset()
/*     */   {
/* 283 */     return this._inputStart >= 0 ? this._inputStart : 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasTextAsCharacters()
/*     */   {
/* 295 */     if ((this._inputStart >= 0) || (this._resultArray != null)) {
/* 296 */       return true;
/*     */     }
/*     */     
/* 299 */     if (this._resultString != null) {
/* 300 */       return false;
/*     */     }
/* 302 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public char[] getTextBuffer()
/*     */   {
/* 308 */     if (this._inputStart >= 0) {
/* 309 */       return this._inputBuffer;
/*     */     }
/* 311 */     if (this._resultArray != null) {
/* 312 */       return this._resultArray;
/*     */     }
/* 314 */     if (this._resultString != null) {
/* 315 */       return this._resultArray = this._resultString.toCharArray();
/*     */     }
/*     */     
/* 318 */     if (!this._hasSegments) {
/* 319 */       return this._currentSegment;
/*     */     }
/*     */     
/* 322 */     return contentsAsArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String contentsAsString()
/*     */   {
/* 333 */     if (this._resultString == null)
/*     */     {
/* 335 */       if (this._resultArray != null) {
/* 336 */         this._resultString = new String(this._resultArray);
/*     */ 
/*     */       }
/* 339 */       else if (this._inputStart >= 0) {
/* 340 */         if (this._inputLen < 1) {
/* 341 */           return this._resultString = "";
/*     */         }
/* 343 */         this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
/*     */       }
/*     */       else {
/* 346 */         int segLen = this._segmentSize;
/* 347 */         int currLen = this._currentSize;
/*     */         
/* 349 */         if (segLen == 0) {
/* 350 */           this._resultString = (currLen == 0 ? "" : new String(this._currentSegment, 0, currLen));
/*     */         } else {
/* 352 */           StringBuilder sb = new StringBuilder(segLen + currLen);
/*     */           
/* 354 */           if (this._segments != null) {
/* 355 */             int i = 0; for (int len = this._segments.size(); i < len; i++) {
/* 356 */               char[] curr = (char[])this._segments.get(i);
/* 357 */               sb.append(curr, 0, curr.length);
/*     */             }
/*     */           }
/*     */           
/* 361 */           sb.append(this._currentSegment, 0, this._currentSize);
/* 362 */           this._resultString = sb.toString();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 367 */     return this._resultString;
/*     */   }
/*     */   
/*     */   public char[] contentsAsArray()
/*     */   {
/* 372 */     char[] result = this._resultArray;
/* 373 */     if (result == null) {
/* 374 */       this._resultArray = (result = buildResultArray());
/*     */     }
/* 376 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BigDecimal contentsAsDecimal()
/*     */     throws NumberFormatException
/*     */   {
/* 387 */     if (this._resultArray != null) {
/* 388 */       return new BigDecimal(this._resultArray);
/*     */     }
/*     */     
/* 391 */     if (this._inputStart >= 0) {
/* 392 */       return new BigDecimal(this._inputBuffer, this._inputStart, this._inputLen);
/*     */     }
/*     */     
/* 395 */     if (this._segmentSize == 0) {
/* 396 */       return new BigDecimal(this._currentSegment, 0, this._currentSize);
/*     */     }
/*     */     
/* 399 */     return new BigDecimal(contentsAsArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double contentsAsDouble()
/*     */     throws NumberFormatException
/*     */   {
/* 409 */     return NumberInput.parseDouble(contentsAsString());
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
/*     */   public void ensureNotShared()
/*     */   {
/* 423 */     if (this._inputStart >= 0) {
/* 424 */       unshare(16);
/*     */     }
/*     */   }
/*     */   
/*     */   public void append(char c)
/*     */   {
/* 430 */     if (this._inputStart >= 0) {
/* 431 */       unshare(16);
/*     */     }
/* 433 */     this._resultString = null;
/* 434 */     this._resultArray = null;
/*     */     
/* 436 */     char[] curr = this._currentSegment;
/* 437 */     if (this._currentSize >= curr.length) {
/* 438 */       expand(1);
/* 439 */       curr = this._currentSegment;
/*     */     }
/* 441 */     curr[(this._currentSize++)] = c;
/*     */   }
/*     */   
/*     */ 
/*     */   public void append(char[] c, int start, int len)
/*     */   {
/* 447 */     if (this._inputStart >= 0) {
/* 448 */       unshare(len);
/*     */     }
/* 450 */     this._resultString = null;
/* 451 */     this._resultArray = null;
/*     */     
/*     */ 
/* 454 */     char[] curr = this._currentSegment;
/* 455 */     int max = curr.length - this._currentSize;
/*     */     
/* 457 */     if (max >= len) {
/* 458 */       System.arraycopy(c, start, curr, this._currentSize, len);
/* 459 */       this._currentSize += len;
/* 460 */       return;
/*     */     }
/*     */     
/* 463 */     if (max > 0) {
/* 464 */       System.arraycopy(c, start, curr, this._currentSize, max);
/* 465 */       start += max;
/* 466 */       len -= max;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     do
/*     */     {
/* 473 */       expand(len);
/* 474 */       int amount = Math.min(this._currentSegment.length, len);
/* 475 */       System.arraycopy(c, start, this._currentSegment, 0, amount);
/* 476 */       this._currentSize += amount;
/* 477 */       start += amount;
/* 478 */       len -= amount;
/* 479 */     } while (len > 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public void append(String str, int offset, int len)
/*     */   {
/* 485 */     if (this._inputStart >= 0) {
/* 486 */       unshare(len);
/*     */     }
/* 488 */     this._resultString = null;
/* 489 */     this._resultArray = null;
/*     */     
/*     */ 
/* 492 */     char[] curr = this._currentSegment;
/* 493 */     int max = curr.length - this._currentSize;
/* 494 */     if (max >= len) {
/* 495 */       str.getChars(offset, offset + len, curr, this._currentSize);
/* 496 */       this._currentSize += len;
/* 497 */       return;
/*     */     }
/*     */     
/* 500 */     if (max > 0) {
/* 501 */       str.getChars(offset, offset + max, curr, this._currentSize);
/* 502 */       len -= max;
/* 503 */       offset += max;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     do
/*     */     {
/* 510 */       expand(len);
/* 511 */       int amount = Math.min(this._currentSegment.length, len);
/* 512 */       str.getChars(offset, offset + amount, this._currentSegment, 0);
/* 513 */       this._currentSize += amount;
/* 514 */       offset += amount;
/* 515 */       len -= amount;
/* 516 */     } while (len > 0);
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
/*     */   public char[] getCurrentSegment()
/*     */   {
/* 531 */     if (this._inputStart >= 0) {
/* 532 */       unshare(1);
/*     */     } else {
/* 534 */       char[] curr = this._currentSegment;
/* 535 */       if (curr == null) {
/* 536 */         this._currentSegment = findBuffer(0);
/* 537 */       } else if (this._currentSize >= curr.length)
/*     */       {
/* 539 */         expand(1);
/*     */       }
/*     */     }
/* 542 */     return this._currentSegment;
/*     */   }
/*     */   
/*     */ 
/*     */   public final char[] emptyAndGetCurrentSegment()
/*     */   {
/* 548 */     this._inputStart = -1;
/* 549 */     this._currentSize = 0;
/* 550 */     this._inputLen = 0;
/*     */     
/* 552 */     this._inputBuffer = null;
/* 553 */     this._resultString = null;
/* 554 */     this._resultArray = null;
/*     */     
/*     */ 
/* 557 */     if (this._hasSegments) {
/* 558 */       clearSegments();
/*     */     }
/* 560 */     char[] curr = this._currentSegment;
/* 561 */     if (curr == null) {
/* 562 */       this._currentSegment = (curr = findBuffer(0));
/*     */     }
/* 564 */     return curr;
/*     */   }
/*     */   
/*     */   public int getCurrentSegmentSize() {
/* 568 */     return this._currentSize;
/*     */   }
/*     */   
/*     */   public void setCurrentLength(int len) {
/* 572 */     this._currentSize = len;
/*     */   }
/*     */   
/*     */   public char[] finishCurrentSegment()
/*     */   {
/* 577 */     if (this._segments == null) {
/* 578 */       this._segments = new ArrayList();
/*     */     }
/* 580 */     this._hasSegments = true;
/* 581 */     this._segments.add(this._currentSegment);
/* 582 */     int oldLen = this._currentSegment.length;
/* 583 */     this._segmentSize += oldLen;
/*     */     
/* 585 */     int newLen = Math.min(oldLen + (oldLen >> 1), 262144);
/* 586 */     char[] curr = _charArray(newLen);
/* 587 */     this._currentSize = 0;
/* 588 */     this._currentSegment = curr;
/* 589 */     return curr;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public char[] expandCurrentSegment()
/*     */   {
/* 599 */     char[] curr = this._currentSegment;
/*     */     
/* 601 */     int len = curr.length;
/*     */     
/* 603 */     int newLen = len == 262144 ? 262145 : Math.min(262144, len + (len >> 1));
/*     */     
/* 605 */     this._currentSegment = _charArray(newLen);
/* 606 */     System.arraycopy(curr, 0, this._currentSegment, 0, len);
/* 607 */     return this._currentSegment;
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
/*     */   public String toString()
/*     */   {
/* 623 */     return contentsAsString();
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
/*     */   private void unshare(int needExtra)
/*     */   {
/* 638 */     int sharedLen = this._inputLen;
/* 639 */     this._inputLen = 0;
/* 640 */     char[] inputBuf = this._inputBuffer;
/* 641 */     this._inputBuffer = null;
/* 642 */     int start = this._inputStart;
/* 643 */     this._inputStart = -1;
/*     */     
/*     */ 
/* 646 */     int needed = sharedLen + needExtra;
/* 647 */     if ((this._currentSegment == null) || (needed > this._currentSegment.length)) {
/* 648 */       this._currentSegment = findBuffer(needed);
/*     */     }
/* 650 */     if (sharedLen > 0) {
/* 651 */       System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen);
/*     */     }
/* 653 */     this._segmentSize = 0;
/* 654 */     this._currentSize = sharedLen;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void expand(int minNewSegmentSize)
/*     */   {
/* 664 */     if (this._segments == null) {
/* 665 */       this._segments = new ArrayList();
/*     */     }
/* 667 */     char[] curr = this._currentSegment;
/* 668 */     this._hasSegments = true;
/* 669 */     this._segments.add(curr);
/* 670 */     this._segmentSize += curr.length;
/* 671 */     int oldLen = curr.length;
/*     */     
/* 673 */     int sizeAddition = oldLen >> 1;
/* 674 */     if (sizeAddition < minNewSegmentSize) {
/* 675 */       sizeAddition = minNewSegmentSize;
/*     */     }
/* 677 */     curr = _charArray(Math.min(262144, oldLen + sizeAddition));
/* 678 */     this._currentSize = 0;
/* 679 */     this._currentSegment = curr;
/*     */   }
/*     */   
/*     */   private char[] buildResultArray()
/*     */   {
/* 684 */     if (this._resultString != null) {
/* 685 */       return this._resultString.toCharArray();
/*     */     }
/*     */     
/*     */     char[] result;
/*     */     
/* 690 */     if (this._inputStart >= 0) {
/* 691 */       if (this._inputLen < 1) {
/* 692 */         return NO_CHARS;
/*     */       }
/* 694 */       char[] result = _charArray(this._inputLen);
/* 695 */       System.arraycopy(this._inputBuffer, this._inputStart, result, 0, this._inputLen);
/*     */     }
/*     */     else {
/* 698 */       int size = size();
/* 699 */       if (size < 1) {
/* 700 */         return NO_CHARS;
/*     */       }
/* 702 */       int offset = 0;
/* 703 */       result = _charArray(size);
/* 704 */       if (this._segments != null) {
/* 705 */         int i = 0; for (int len = this._segments.size(); i < len; i++) {
/* 706 */           char[] curr = (char[])this._segments.get(i);
/* 707 */           int currLen = curr.length;
/* 708 */           System.arraycopy(curr, 0, result, offset, currLen);
/* 709 */           offset += currLen;
/*     */         }
/*     */       }
/* 712 */       System.arraycopy(this._currentSegment, 0, result, offset, this._currentSize);
/*     */     }
/* 714 */     return result;
/*     */   }
/*     */   
/*     */   private final char[] _charArray(int len) {
/* 718 */     return new char[len];
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\util\TextBuffer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */