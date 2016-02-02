/*     */ package org.codehaus.jackson.sym;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.codehaus.jackson.util.InternCache;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CharsToNameCanonicalizer
/*     */ {
/*     */   public static final int HASH_MULT = 33;
/*     */   protected static final int DEFAULT_TABLE_SIZE = 64;
/*     */   protected static final int MAX_TABLE_SIZE = 65536;
/*     */   static final int MAX_ENTRIES_FOR_REUSE = 12000;
/*     */   static final int MAX_COLL_CHAIN_LENGTH = 255;
/*     */   static final int MAX_COLL_CHAIN_FOR_REUSE = 63;
/*  97 */   static final CharsToNameCanonicalizer sBootstrapSymbolTable = new CharsToNameCanonicalizer();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CharsToNameCanonicalizer _parent;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int _hashSeed;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _intern;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _canonicalize;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String[] _symbols;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Bucket[] _buckets;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _size;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _sizeThreshold;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _indexMask;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int _longestCollisionList;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _dirty;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CharsToNameCanonicalizer createRoot()
/*     */   {
/* 217 */     long now = System.currentTimeMillis();
/*     */     
/* 219 */     int seed = (int)now + ((int)now >>> 32) | 0x1;
/* 220 */     return createRoot(seed);
/*     */   }
/*     */   
/*     */   protected static CharsToNameCanonicalizer createRoot(int hashSeed) {
/* 224 */     return sBootstrapSymbolTable.makeOrphan(hashSeed);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private CharsToNameCanonicalizer()
/*     */   {
/* 236 */     this._canonicalize = true;
/* 237 */     this._intern = true;
/*     */     
/* 239 */     this._dirty = true;
/* 240 */     this._hashSeed = 0;
/* 241 */     this._longestCollisionList = 0;
/* 242 */     initTables(64);
/*     */   }
/*     */   
/*     */   private void initTables(int initialSize)
/*     */   {
/* 247 */     this._symbols = new String[initialSize];
/* 248 */     this._buckets = new Bucket[initialSize >> 1];
/*     */     
/* 250 */     this._indexMask = (initialSize - 1);
/* 251 */     this._size = 0;
/* 252 */     this._longestCollisionList = 0;
/*     */     
/* 254 */     this._sizeThreshold = _thresholdSize(initialSize);
/*     */   }
/*     */   
/*     */   private static final int _thresholdSize(int hashAreaSize) {
/* 258 */     return hashAreaSize - (hashAreaSize >> 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, boolean canonicalize, boolean intern, String[] symbols, Bucket[] buckets, int size, int hashSeed, int longestColl)
/*     */   {
/* 269 */     this._parent = parent;
/* 270 */     this._canonicalize = canonicalize;
/* 271 */     this._intern = intern;
/*     */     
/* 273 */     this._symbols = symbols;
/* 274 */     this._buckets = buckets;
/* 275 */     this._size = size;
/* 276 */     this._hashSeed = hashSeed;
/*     */     
/* 278 */     int arrayLen = symbols.length;
/* 279 */     this._sizeThreshold = _thresholdSize(arrayLen);
/* 280 */     this._indexMask = (arrayLen - 1);
/* 281 */     this._longestCollisionList = longestColl;
/*     */     
/*     */ 
/* 284 */     this._dirty = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized CharsToNameCanonicalizer makeChild(boolean canonicalize, boolean intern)
/*     */   {
/*     */     String[] symbols;
/*     */     
/*     */ 
/*     */ 
/*     */     Bucket[] buckets;
/*     */     
/*     */ 
/*     */ 
/*     */     int size;
/*     */     
/*     */ 
/*     */ 
/*     */     int hashSeed;
/*     */     
/*     */ 
/*     */ 
/*     */     int longestCollisionList;
/*     */     
/*     */ 
/*     */ 
/* 312 */     synchronized (this) {
/* 313 */       symbols = this._symbols;
/* 314 */       buckets = this._buckets;
/* 315 */       size = this._size;
/* 316 */       hashSeed = this._hashSeed;
/* 317 */       longestCollisionList = this._longestCollisionList;
/*     */     }
/*     */     
/* 320 */     return new CharsToNameCanonicalizer(this, canonicalize, intern, symbols, buckets, size, hashSeed, longestCollisionList);
/*     */   }
/*     */   
/*     */ 
/*     */   private CharsToNameCanonicalizer makeOrphan(int seed)
/*     */   {
/* 326 */     return new CharsToNameCanonicalizer(null, true, true, this._symbols, this._buckets, this._size, seed, this._longestCollisionList);
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
/*     */   private void mergeChild(CharsToNameCanonicalizer child)
/*     */   {
/* 345 */     if ((child.size() > 12000) || (child._longestCollisionList > 63))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 350 */       synchronized (this) {
/* 351 */         initTables(64);
/*     */         
/*     */ 
/* 354 */         this._dirty = false;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 359 */       if (child.size() <= size()) {
/* 360 */         return;
/*     */       }
/*     */       
/* 363 */       synchronized (this) {
/* 364 */         this._symbols = child._symbols;
/* 365 */         this._buckets = child._buckets;
/* 366 */         this._size = child._size;
/* 367 */         this._sizeThreshold = child._sizeThreshold;
/* 368 */         this._indexMask = child._indexMask;
/* 369 */         this._longestCollisionList = child._longestCollisionList;
/*     */         
/*     */ 
/* 372 */         this._dirty = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void release()
/*     */   {
/* 380 */     if (!maybeDirty()) {
/* 381 */       return;
/*     */     }
/* 383 */     if (this._parent != null) {
/* 384 */       this._parent.mergeChild(this);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 389 */       this._dirty = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 399 */     return this._size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 408 */   public int bucketCount() { return this._symbols.length; }
/*     */   
/* 410 */   public boolean maybeDirty() { return this._dirty; }
/*     */   
/* 412 */   public int hashSeed() { return this._hashSeed; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int collisionCount()
/*     */   {
/* 423 */     int count = 0;
/*     */     
/* 425 */     for (Bucket bucket : this._buckets) {
/* 426 */       if (bucket != null) {
/* 427 */         count += bucket.length();
/*     */       }
/*     */     }
/* 430 */     return count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int maxCollisionLength()
/*     */   {
/* 442 */     return this._longestCollisionList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String findSymbol(char[] buffer, int start, int len, int h)
/*     */   {
/* 453 */     if (len < 1) {
/* 454 */       return "";
/*     */     }
/* 456 */     if (!this._canonicalize) {
/* 457 */       return new String(buffer, start, len);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 465 */     int index = _hashToIndex(h);
/* 466 */     String sym = this._symbols[index];
/*     */     
/*     */ 
/* 469 */     if (sym != null)
/*     */     {
/* 471 */       if (sym.length() == len) {
/* 472 */         int i = 0;
/*     */         do {
/* 474 */           if (sym.charAt(i) != buffer[(start + i)]) {
/*     */             break;
/*     */           }
/* 477 */           i++; } while (i < len);
/*     */         
/* 479 */         if (i == len) {
/* 480 */           return sym;
/*     */         }
/*     */       }
/*     */       
/* 484 */       Bucket b = this._buckets[(index >> 1)];
/* 485 */       if (b != null) {
/* 486 */         sym = b.find(buffer, start, len);
/* 487 */         if (sym != null) {
/* 488 */           return sym;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 493 */     if (!this._dirty) {
/* 494 */       copyArrays();
/* 495 */       this._dirty = true;
/* 496 */     } else if (this._size >= this._sizeThreshold) {
/* 497 */       rehash();
/*     */       
/*     */ 
/*     */ 
/* 501 */       index = _hashToIndex(calcHash(buffer, start, len));
/*     */     }
/*     */     
/* 504 */     String newSymbol = new String(buffer, start, len);
/* 505 */     if (this._intern) {
/* 506 */       newSymbol = InternCache.instance.intern(newSymbol);
/*     */     }
/* 508 */     this._size += 1;
/*     */     
/* 510 */     if (this._symbols[index] == null) {
/* 511 */       this._symbols[index] = newSymbol;
/*     */     } else {
/* 513 */       int bix = index >> 1;
/* 514 */       Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
/* 515 */       this._buckets[bix] = newB;
/* 516 */       this._longestCollisionList = Math.max(newB.length(), this._longestCollisionList);
/* 517 */       if (this._longestCollisionList > 255) {
/* 518 */         reportTooManyCollisions(255);
/*     */       }
/*     */     }
/*     */     
/* 522 */     return newSymbol;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final int _hashToIndex(int rawHash)
/*     */   {
/* 531 */     rawHash += (rawHash >>> 15);
/* 532 */     return rawHash & this._indexMask;
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
/*     */   public int calcHash(char[] buffer, int start, int len)
/*     */   {
/* 546 */     int hash = this._hashSeed;
/* 547 */     for (int i = 0; i < len; i++) {
/* 548 */       hash = hash * 33 + buffer[i];
/*     */     }
/*     */     
/* 551 */     return hash == 0 ? 1 : hash;
/*     */   }
/*     */   
/*     */   public int calcHash(String key)
/*     */   {
/* 556 */     int len = key.length();
/*     */     
/* 558 */     int hash = this._hashSeed;
/* 559 */     for (int i = 0; i < len; i++) {
/* 560 */       hash = hash * 33 + key.charAt(i);
/*     */     }
/*     */     
/* 563 */     return hash == 0 ? 1 : hash;
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
/*     */   private void copyArrays()
/*     */   {
/* 577 */     String[] oldSyms = this._symbols;
/* 578 */     int size = oldSyms.length;
/* 579 */     this._symbols = new String[size];
/* 580 */     System.arraycopy(oldSyms, 0, this._symbols, 0, size);
/* 581 */     Bucket[] oldBuckets = this._buckets;
/* 582 */     size = oldBuckets.length;
/* 583 */     this._buckets = new Bucket[size];
/* 584 */     System.arraycopy(oldBuckets, 0, this._buckets, 0, size);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 596 */     int size = this._symbols.length;
/* 597 */     int newSize = size + size;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 603 */     if (newSize > 65536)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 608 */       this._size = 0;
/* 609 */       Arrays.fill(this._symbols, null);
/* 610 */       Arrays.fill(this._buckets, null);
/* 611 */       this._dirty = true;
/* 612 */       return;
/*     */     }
/*     */     
/* 615 */     String[] oldSyms = this._symbols;
/* 616 */     Bucket[] oldBuckets = this._buckets;
/* 617 */     this._symbols = new String[newSize];
/* 618 */     this._buckets = new Bucket[newSize >> 1];
/*     */     
/* 620 */     this._indexMask = (newSize - 1);
/* 621 */     this._sizeThreshold = _thresholdSize(newSize);
/*     */     
/* 623 */     int count = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 628 */     int maxColl = 0;
/* 629 */     for (int i = 0; i < size; i++) {
/* 630 */       String symbol = oldSyms[i];
/* 631 */       if (symbol != null) {
/* 632 */         count++;
/* 633 */         int index = _hashToIndex(calcHash(symbol));
/* 634 */         if (this._symbols[index] == null) {
/* 635 */           this._symbols[index] = symbol;
/*     */         } else {
/* 637 */           int bix = index >> 1;
/* 638 */           Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 639 */           this._buckets[bix] = newB;
/* 640 */           maxColl = Math.max(maxColl, newB.length());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 645 */     size >>= 1;
/* 646 */     for (int i = 0; i < size; i++) {
/* 647 */       Bucket b = oldBuckets[i];
/* 648 */       while (b != null) {
/* 649 */         count++;
/* 650 */         String symbol = b.getSymbol();
/* 651 */         int index = _hashToIndex(calcHash(symbol));
/* 652 */         if (this._symbols[index] == null) {
/* 653 */           this._symbols[index] = symbol;
/*     */         } else {
/* 655 */           int bix = index >> 1;
/* 656 */           Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 657 */           this._buckets[bix] = newB;
/* 658 */           maxColl = Math.max(maxColl, newB.length());
/*     */         }
/* 660 */         b = b.getNext();
/*     */       }
/*     */     }
/* 663 */     this._longestCollisionList = maxColl;
/*     */     
/* 665 */     if (count != this._size) {
/* 666 */       throw new Error("Internal error on SymbolTable.rehash(): had " + this._size + " entries; now have " + count + ".");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void reportTooManyCollisions(int maxLen)
/*     */   {
/* 675 */     throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static final class Bucket
/*     */   {
/*     */     private final String _symbol;
/*     */     
/*     */ 
/*     */ 
/*     */     private final Bucket _next;
/*     */     
/*     */ 
/*     */     private final int _length;
/*     */     
/*     */ 
/*     */ 
/*     */     public Bucket(String symbol, Bucket next)
/*     */     {
/* 696 */       this._symbol = symbol;
/* 697 */       this._next = next;
/* 698 */       this._length = (next == null ? 1 : next._length + 1);
/*     */     }
/*     */     
/* 701 */     public String getSymbol() { return this._symbol; }
/* 702 */     public Bucket getNext() { return this._next; }
/* 703 */     public int length() { return this._length; }
/*     */     
/*     */     public String find(char[] buf, int start, int len) {
/* 706 */       String sym = this._symbol;
/* 707 */       Bucket b = this._next;
/*     */       for (;;)
/*     */       {
/* 710 */         if (sym.length() == len) {
/* 711 */           int i = 0;
/*     */           do {
/* 713 */             if (sym.charAt(i) != buf[(start + i)]) {
/*     */               break;
/*     */             }
/* 716 */             i++; } while (i < len);
/* 717 */           if (i == len) {
/* 718 */             return sym;
/*     */           }
/*     */         }
/* 721 */         if (b == null) {
/*     */           break;
/*     */         }
/* 724 */         sym = b.getSymbol();
/* 725 */         b = b.getNext();
/*     */       }
/* 727 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\sym\CharsToNameCanonicalizer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */