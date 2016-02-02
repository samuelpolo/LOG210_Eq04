/*      */ package org.codehaus.jackson.sym;
/*      */ 
/*      */ import java.util.Arrays;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import org.codehaus.jackson.util.InternCache;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class BytesToNameCanonicalizer
/*      */ {
/*      */   protected static final int DEFAULT_TABLE_SIZE = 64;
/*      */   protected static final int MAX_TABLE_SIZE = 65536;
/*      */   static final int MAX_ENTRIES_FOR_REUSE = 6000;
/*      */   static final int MAX_COLL_CHAIN_LENGTH = 255;
/*      */   static final int MAX_COLL_CHAIN_FOR_REUSE = 63;
/*      */   static final int MIN_HASH_SIZE = 16;
/*      */   static final int INITIAL_COLLISION_LEN = 32;
/*      */   static final int LAST_VALID_BUCKET = 254;
/*      */   protected final BytesToNameCanonicalizer _parent;
/*      */   protected final AtomicReference<TableInfo> _tableInfo;
/*      */   private final int _hashSeed;
/*      */   protected final boolean _intern;
/*      */   protected int _count;
/*      */   protected int _longestCollisionList;
/*      */   protected int _mainHashMask;
/*      */   protected int[] _mainHash;
/*      */   protected Name[] _mainNames;
/*      */   protected Bucket[] _collList;
/*      */   protected int _collCount;
/*      */   protected int _collEnd;
/*      */   private transient boolean _needRehash;
/*      */   private boolean _mainHashShared;
/*      */   private boolean _mainNamesShared;
/*      */   private boolean _collListShared;
/*      */   private static final int MULT = 33;
/*      */   private static final int MULT2 = 65599;
/*      */   private static final int MULT3 = 31;
/*      */   
/*      */   private BytesToNameCanonicalizer(int hashSize, boolean intern, int seed)
/*      */   {
/*  241 */     this._parent = null;
/*  242 */     this._hashSeed = seed;
/*  243 */     this._intern = intern;
/*      */     
/*  245 */     if (hashSize < 16) {
/*  246 */       hashSize = 16;
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*  251 */     else if ((hashSize & hashSize - 1) != 0) {
/*  252 */       int curr = 16;
/*  253 */       while (curr < hashSize) {
/*  254 */         curr += curr;
/*      */       }
/*  256 */       hashSize = curr;
/*      */     }
/*      */     
/*  259 */     this._tableInfo = new AtomicReference(initTableInfo(hashSize));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private BytesToNameCanonicalizer(BytesToNameCanonicalizer parent, boolean intern, int seed, TableInfo state)
/*      */   {
/*  268 */     this._parent = parent;
/*  269 */     this._hashSeed = seed;
/*  270 */     this._intern = intern;
/*  271 */     this._tableInfo = null;
/*      */     
/*      */ 
/*  274 */     this._count = state.count;
/*  275 */     this._mainHashMask = state.mainHashMask;
/*  276 */     this._mainHash = state.mainHash;
/*  277 */     this._mainNames = state.mainNames;
/*  278 */     this._collList = state.collList;
/*  279 */     this._collCount = state.collCount;
/*  280 */     this._collEnd = state.collEnd;
/*  281 */     this._longestCollisionList = state.longestCollisionList;
/*      */     
/*      */ 
/*  284 */     this._needRehash = false;
/*  285 */     this._mainHashShared = true;
/*  286 */     this._mainNamesShared = true;
/*  287 */     this._collListShared = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private TableInfo initTableInfo(int hashSize)
/*      */   {
/*  296 */     return new TableInfo(0, hashSize - 1, new int[hashSize], new Name[hashSize], null, 0, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static BytesToNameCanonicalizer createRoot()
/*      */   {
/*  322 */     long now = System.currentTimeMillis();
/*      */     
/*  324 */     int seed = (int)now + ((int)now >>> 32) | 0x1;
/*  325 */     return createRoot(seed);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static BytesToNameCanonicalizer createRoot(int hashSeed)
/*      */   {
/*  333 */     return new BytesToNameCanonicalizer(64, true, hashSeed);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BytesToNameCanonicalizer makeChild(boolean canonicalize, boolean intern)
/*      */   {
/*  346 */     return new BytesToNameCanonicalizer(this, intern, this._hashSeed, (TableInfo)this._tableInfo.get());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void release()
/*      */   {
/*  359 */     if ((this._parent != null) && (maybeDirty())) {
/*  360 */       this._parent.mergeChild(new TableInfo(this));
/*      */       
/*      */ 
/*      */ 
/*  364 */       this._mainHashShared = true;
/*  365 */       this._mainNamesShared = true;
/*  366 */       this._collListShared = true;
/*      */     }
/*      */   }
/*      */   
/*      */   private void mergeChild(TableInfo childState)
/*      */   {
/*  372 */     int childCount = childState.count;
/*  373 */     TableInfo currState = (TableInfo)this._tableInfo.get();
/*      */     
/*      */ 
/*  376 */     if (childCount <= currState.count) {
/*  377 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  386 */     if ((childCount > 6000) || (childState.longestCollisionList > 63))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  393 */       childState = initTableInfo(64);
/*      */     }
/*  395 */     this._tableInfo.compareAndSet(currState, childState);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int size()
/*      */   {
/*  406 */     if (this._tableInfo != null) {
/*  407 */       return ((TableInfo)this._tableInfo.get()).count;
/*      */     }
/*      */     
/*  410 */     return this._count;
/*      */   }
/*      */   
/*      */ 
/*      */   public int bucketCount()
/*      */   {
/*  416 */     return this._mainHash.length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean maybeDirty()
/*      */   {
/*  424 */     return !this._mainHashShared;
/*      */   }
/*      */   
/*      */ 
/*      */   public int hashSeed()
/*      */   {
/*  430 */     return this._hashSeed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int collisionCount()
/*      */   {
/*  440 */     return this._collCount;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int maxCollisionLength()
/*      */   {
/*  451 */     return this._longestCollisionList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Name getEmptyName()
/*      */   {
/*  462 */     return Name1.getEmptyName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Name findName(int firstQuad)
/*      */   {
/*  482 */     int hash = calcHash(firstQuad);
/*  483 */     int ix = hash & this._mainHashMask;
/*  484 */     int val = this._mainHash[ix];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  489 */     if ((val >> 8 ^ hash) << 8 == 0)
/*      */     {
/*  491 */       Name name = this._mainNames[ix];
/*  492 */       if (name == null) {
/*  493 */         return null;
/*      */       }
/*  495 */       if (name.equals(firstQuad)) {
/*  496 */         return name;
/*      */       }
/*  498 */     } else if (val == 0) {
/*  499 */       return null;
/*      */     }
/*      */     
/*  502 */     val &= 0xFF;
/*  503 */     if (val > 0) {
/*  504 */       val--;
/*  505 */       Bucket bucket = this._collList[val];
/*  506 */       if (bucket != null) {
/*  507 */         return bucket.find(hash, firstQuad, 0);
/*      */       }
/*      */     }
/*      */     
/*  511 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Name findName(int firstQuad, int secondQuad)
/*      */   {
/*  531 */     int hash = secondQuad == 0 ? calcHash(firstQuad) : calcHash(firstQuad, secondQuad);
/*  532 */     int ix = hash & this._mainHashMask;
/*  533 */     int val = this._mainHash[ix];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  538 */     if ((val >> 8 ^ hash) << 8 == 0)
/*      */     {
/*  540 */       Name name = this._mainNames[ix];
/*  541 */       if (name == null) {
/*  542 */         return null;
/*      */       }
/*  544 */       if (name.equals(firstQuad, secondQuad)) {
/*  545 */         return name;
/*      */       }
/*  547 */     } else if (val == 0) {
/*  548 */       return null;
/*      */     }
/*      */     
/*  551 */     val &= 0xFF;
/*  552 */     if (val > 0) {
/*  553 */       val--;
/*  554 */       Bucket bucket = this._collList[val];
/*  555 */       if (bucket != null) {
/*  556 */         return bucket.find(hash, firstQuad, secondQuad);
/*      */       }
/*      */     }
/*      */     
/*  560 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Name findName(int[] quads, int qlen)
/*      */   {
/*  582 */     if (qlen < 3) {
/*  583 */       return findName(quads[0], qlen < 2 ? 0 : quads[1]);
/*      */     }
/*  585 */     int hash = calcHash(quads, qlen);
/*      */     
/*  587 */     int ix = hash & this._mainHashMask;
/*  588 */     int val = this._mainHash[ix];
/*  589 */     if ((val >> 8 ^ hash) << 8 == 0) {
/*  590 */       Name name = this._mainNames[ix];
/*  591 */       if ((name == null) || (name.equals(quads, qlen)))
/*      */       {
/*  593 */         return name;
/*      */       }
/*  595 */     } else if (val == 0) {
/*  596 */       return null;
/*      */     }
/*  598 */     val &= 0xFF;
/*  599 */     if (val > 0) {
/*  600 */       val--;
/*  601 */       Bucket bucket = this._collList[val];
/*  602 */       if (bucket != null) {
/*  603 */         return bucket.find(hash, quads, qlen);
/*      */       }
/*      */     }
/*  606 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Name addName(String symbolStr, int q1, int q2)
/*      */   {
/*  617 */     if (this._intern) {
/*  618 */       symbolStr = InternCache.instance.intern(symbolStr);
/*      */     }
/*  620 */     int hash = q2 == 0 ? calcHash(q1) : calcHash(q1, q2);
/*  621 */     Name symbol = constructName(hash, symbolStr, q1, q2);
/*  622 */     _addSymbol(hash, symbol);
/*  623 */     return symbol;
/*      */   }
/*      */   
/*      */   public Name addName(String symbolStr, int[] quads, int qlen)
/*      */   {
/*  628 */     if (this._intern)
/*  629 */       symbolStr = InternCache.instance.intern(symbolStr);
/*      */     int hash;
/*      */     int hash;
/*  632 */     if (qlen < 3) {
/*  633 */       hash = qlen == 1 ? calcHash(quads[0]) : calcHash(quads[0], quads[1]);
/*      */     } else {
/*  635 */       hash = calcHash(quads, qlen);
/*      */     }
/*  637 */     Name symbol = constructName(hash, symbolStr, quads, qlen);
/*  638 */     _addSymbol(hash, symbol);
/*  639 */     return symbol;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final int calcHash(int firstQuad)
/*      */   {
/*  664 */     int hash = firstQuad ^ this._hashSeed;
/*  665 */     hash += (hash >>> 15);
/*  666 */     hash ^= hash >>> 9;
/*  667 */     return hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public final int calcHash(int firstQuad, int secondQuad)
/*      */   {
/*  675 */     int hash = firstQuad;
/*  676 */     hash ^= hash >>> 15;
/*  677 */     hash += secondQuad * 33;
/*  678 */     hash ^= this._hashSeed;
/*  679 */     hash += (hash >>> 7);
/*  680 */     return hash;
/*      */   }
/*      */   
/*      */ 
/*      */   public final int calcHash(int[] quads, int qlen)
/*      */   {
/*  686 */     if (qlen < 3) {
/*  687 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  695 */     int hash = quads[0] ^ this._hashSeed;
/*  696 */     hash += (hash >>> 9);
/*  697 */     hash *= 33;
/*  698 */     hash += quads[1];
/*  699 */     hash *= 65599;
/*  700 */     hash += (hash >>> 15);
/*  701 */     hash ^= quads[2];
/*  702 */     hash += (hash >>> 17);
/*      */     
/*  704 */     for (int i = 3; i < qlen; i++) {
/*  705 */       hash = hash * 31 ^ quads[i];
/*      */       
/*  707 */       hash += (hash >>> 3);
/*  708 */       hash ^= hash << 7;
/*      */     }
/*      */     
/*  711 */     hash += (hash >>> 15);
/*  712 */     hash ^= hash << 9;
/*  713 */     return hash;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int[] calcQuads(byte[] wordBytes)
/*      */   {
/*  719 */     int blen = wordBytes.length;
/*  720 */     int[] result = new int[(blen + 3) / 4];
/*  721 */     for (int i = 0; i < blen; i++) {
/*  722 */       int x = wordBytes[i] & 0xFF;
/*      */       
/*  724 */       i++; if (i < blen) {
/*  725 */         x = x << 8 | wordBytes[i] & 0xFF;
/*  726 */         i++; if (i < blen) {
/*  727 */           x = x << 8 | wordBytes[i] & 0xFF;
/*  728 */           i++; if (i < blen) {
/*  729 */             x = x << 8 | wordBytes[i] & 0xFF;
/*      */           }
/*      */         }
/*      */       }
/*  733 */       result[(i >> 2)] = x;
/*      */     }
/*  735 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _addSymbol(int hash, Name symbol)
/*      */   {
/*  791 */     if (this._mainHashShared) {
/*  792 */       unshareMain();
/*      */     }
/*      */     
/*  795 */     if (this._needRehash) {
/*  796 */       rehash();
/*      */     }
/*      */     
/*  799 */     this._count += 1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  804 */     int ix = hash & this._mainHashMask;
/*  805 */     if (this._mainNames[ix] == null) {
/*  806 */       this._mainHash[ix] = (hash << 8);
/*  807 */       if (this._mainNamesShared) {
/*  808 */         unshareNames();
/*      */       }
/*  810 */       this._mainNames[ix] = symbol;
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  815 */       if (this._collListShared) {
/*  816 */         unshareCollision();
/*      */       }
/*  818 */       this._collCount += 1;
/*  819 */       int entryValue = this._mainHash[ix];
/*  820 */       int bucket = entryValue & 0xFF;
/*  821 */       if (bucket == 0) {
/*  822 */         if (this._collEnd <= 254) {
/*  823 */           bucket = this._collEnd;
/*  824 */           this._collEnd += 1;
/*      */           
/*  826 */           if (bucket >= this._collList.length) {
/*  827 */             expandCollision();
/*      */           }
/*      */         } else {
/*  830 */           bucket = findBestBucket();
/*      */         }
/*      */         
/*  833 */         this._mainHash[ix] = (entryValue & 0xFF00 | bucket + 1);
/*      */       } else {
/*  835 */         bucket--;
/*      */       }
/*      */       
/*      */ 
/*  839 */       Bucket newB = new Bucket(symbol, this._collList[bucket]);
/*  840 */       this._collList[bucket] = newB;
/*      */       
/*  842 */       this._longestCollisionList = Math.max(newB.length(), this._longestCollisionList);
/*  843 */       if (this._longestCollisionList > 255) {
/*  844 */         reportTooManyCollisions(255);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  852 */     int hashSize = this._mainHash.length;
/*  853 */     if (this._count > hashSize >> 1) {
/*  854 */       int hashQuarter = hashSize >> 2;
/*      */       
/*      */ 
/*      */ 
/*  858 */       if (this._count > hashSize - hashQuarter) {
/*  859 */         this._needRehash = true;
/*  860 */       } else if (this._collCount >= hashQuarter) {
/*  861 */         this._needRehash = true;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void rehash()
/*      */   {
/*  869 */     this._needRehash = false;
/*      */     
/*  871 */     this._mainNamesShared = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  877 */     int[] oldMainHash = this._mainHash;
/*  878 */     int len = oldMainHash.length;
/*  879 */     int newLen = len + len;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  884 */     if (newLen > 65536) {
/*  885 */       nukeSymbols();
/*  886 */       return;
/*      */     }
/*      */     
/*  889 */     this._mainHash = new int[newLen];
/*  890 */     this._mainHashMask = (newLen - 1);
/*  891 */     Name[] oldNames = this._mainNames;
/*  892 */     this._mainNames = new Name[newLen];
/*  893 */     int symbolsSeen = 0;
/*  894 */     for (int i = 0; i < len; i++) {
/*  895 */       Name symbol = oldNames[i];
/*  896 */       if (symbol != null) {
/*  897 */         symbolsSeen++;
/*  898 */         int hash = symbol.hashCode();
/*  899 */         int ix = hash & this._mainHashMask;
/*  900 */         this._mainNames[ix] = symbol;
/*  901 */         this._mainHash[ix] = (hash << 8);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  909 */     int oldEnd = this._collEnd;
/*  910 */     if (oldEnd == 0) {
/*  911 */       this._longestCollisionList = 0;
/*  912 */       return;
/*      */     }
/*      */     
/*  915 */     this._collCount = 0;
/*  916 */     this._collEnd = 0;
/*  917 */     this._collListShared = false;
/*      */     
/*  919 */     int maxColl = 0;
/*      */     
/*  921 */     Bucket[] oldBuckets = this._collList;
/*  922 */     this._collList = new Bucket[oldBuckets.length];
/*  923 */     for (int i = 0; i < oldEnd; i++) {
/*  924 */       for (Bucket curr = oldBuckets[i]; curr != null; curr = curr._next) {
/*  925 */         symbolsSeen++;
/*  926 */         Name symbol = curr._name;
/*  927 */         int hash = symbol.hashCode();
/*  928 */         int ix = hash & this._mainHashMask;
/*  929 */         int val = this._mainHash[ix];
/*  930 */         if (this._mainNames[ix] == null) {
/*  931 */           this._mainHash[ix] = (hash << 8);
/*  932 */           this._mainNames[ix] = symbol;
/*      */         } else {
/*  934 */           this._collCount += 1;
/*  935 */           int bucket = val & 0xFF;
/*  936 */           if (bucket == 0) {
/*  937 */             if (this._collEnd <= 254) {
/*  938 */               bucket = this._collEnd;
/*  939 */               this._collEnd += 1;
/*      */               
/*  941 */               if (bucket >= this._collList.length) {
/*  942 */                 expandCollision();
/*      */               }
/*      */             } else {
/*  945 */               bucket = findBestBucket();
/*      */             }
/*      */             
/*  948 */             this._mainHash[ix] = (val & 0xFF00 | bucket + 1);
/*      */           } else {
/*  950 */             bucket--;
/*      */           }
/*      */           
/*  953 */           Bucket newB = new Bucket(symbol, this._collList[bucket]);
/*  954 */           this._collList[bucket] = newB;
/*  955 */           maxColl = Math.max(maxColl, newB.length());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  960 */     this._longestCollisionList = maxColl;
/*      */     
/*  962 */     if (symbolsSeen != this._count) {
/*  963 */       throw new RuntimeException("Internal error: count after rehash " + symbolsSeen + "; should be " + this._count);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void nukeSymbols()
/*      */   {
/*  973 */     this._count = 0;
/*  974 */     this._longestCollisionList = 0;
/*  975 */     Arrays.fill(this._mainHash, 0);
/*  976 */     Arrays.fill(this._mainNames, null);
/*  977 */     Arrays.fill(this._collList, null);
/*  978 */     this._collCount = 0;
/*  979 */     this._collEnd = 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int findBestBucket()
/*      */   {
/*  989 */     Bucket[] buckets = this._collList;
/*  990 */     int bestCount = Integer.MAX_VALUE;
/*  991 */     int bestIx = -1;
/*      */     
/*  993 */     int i = 0; for (int len = this._collEnd; i < len; i++) {
/*  994 */       int count = buckets[i].length();
/*  995 */       if (count < bestCount) {
/*  996 */         if (count == 1) {
/*  997 */           return i;
/*      */         }
/*  999 */         bestCount = count;
/* 1000 */         bestIx = i;
/*      */       }
/*      */     }
/* 1003 */     return bestIx;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void unshareMain()
/*      */   {
/* 1014 */     int[] old = this._mainHash;
/* 1015 */     int len = this._mainHash.length;
/*      */     
/* 1017 */     this._mainHash = new int[len];
/* 1018 */     System.arraycopy(old, 0, this._mainHash, 0, len);
/* 1019 */     this._mainHashShared = false;
/*      */   }
/*      */   
/*      */   private void unshareCollision()
/*      */   {
/* 1024 */     Bucket[] old = this._collList;
/* 1025 */     if (old == null) {
/* 1026 */       this._collList = new Bucket[32];
/*      */     } else {
/* 1028 */       int len = old.length;
/* 1029 */       this._collList = new Bucket[len];
/* 1030 */       System.arraycopy(old, 0, this._collList, 0, len);
/*      */     }
/* 1032 */     this._collListShared = false;
/*      */   }
/*      */   
/*      */   private void unshareNames()
/*      */   {
/* 1037 */     Name[] old = this._mainNames;
/* 1038 */     int len = old.length;
/* 1039 */     this._mainNames = new Name[len];
/* 1040 */     System.arraycopy(old, 0, this._mainNames, 0, len);
/* 1041 */     this._mainNamesShared = false;
/*      */   }
/*      */   
/*      */   private void expandCollision()
/*      */   {
/* 1046 */     Bucket[] old = this._collList;
/* 1047 */     int len = old.length;
/* 1048 */     this._collList = new Bucket[len + len];
/* 1049 */     System.arraycopy(old, 0, this._collList, 0, len);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Name constructName(int hash, String name, int q1, int q2)
/*      */   {
/* 1061 */     if (q2 == 0) {
/* 1062 */       return new Name1(name, hash, q1);
/*      */     }
/* 1064 */     return new Name2(name, hash, q1, q2);
/*      */   }
/*      */   
/*      */   private static Name constructName(int hash, String name, int[] quads, int qlen)
/*      */   {
/* 1069 */     if (qlen < 4) {
/* 1070 */       switch (qlen) {
/*      */       case 1: 
/* 1072 */         return new Name1(name, hash, quads[0]);
/*      */       case 2: 
/* 1074 */         return new Name2(name, hash, quads[0], quads[1]);
/*      */       case 3: 
/* 1076 */         return new Name3(name, hash, quads[0], quads[1], quads[2]);
/*      */       }
/*      */       
/*      */     }
/*      */     
/* 1081 */     int[] buf = new int[qlen];
/* 1082 */     for (int i = 0; i < qlen; i++) {
/* 1083 */       buf[i] = quads[i];
/*      */     }
/* 1085 */     return new NameN(name, hash, buf, qlen);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void reportTooManyCollisions(int maxLen)
/*      */   {
/* 1099 */     throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._count + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static final class TableInfo
/*      */   {
/*      */     public final int count;
/*      */     
/*      */ 
/*      */     public final int mainHashMask;
/*      */     
/*      */ 
/*      */     public final int[] mainHash;
/*      */     
/*      */ 
/*      */     public final Name[] mainNames;
/*      */     
/*      */ 
/*      */     public final BytesToNameCanonicalizer.Bucket[] collList;
/*      */     
/*      */ 
/*      */     public final int collCount;
/*      */     
/*      */     public final int collEnd;
/*      */     
/*      */     public final int longestCollisionList;
/*      */     
/*      */ 
/*      */     public TableInfo(int count, int mainHashMask, int[] mainHash, Name[] mainNames, BytesToNameCanonicalizer.Bucket[] collList, int collCount, int collEnd, int longestCollisionList)
/*      */     {
/* 1130 */       this.count = count;
/* 1131 */       this.mainHashMask = mainHashMask;
/* 1132 */       this.mainHash = mainHash;
/* 1133 */       this.mainNames = mainNames;
/* 1134 */       this.collList = collList;
/* 1135 */       this.collCount = collCount;
/* 1136 */       this.collEnd = collEnd;
/* 1137 */       this.longestCollisionList = longestCollisionList;
/*      */     }
/*      */     
/*      */     public TableInfo(BytesToNameCanonicalizer src)
/*      */     {
/* 1142 */       this.count = src._count;
/* 1143 */       this.mainHashMask = src._mainHashMask;
/* 1144 */       this.mainHash = src._mainHash;
/* 1145 */       this.mainNames = src._mainNames;
/* 1146 */       this.collList = src._collList;
/* 1147 */       this.collCount = src._collCount;
/* 1148 */       this.collEnd = src._collEnd;
/* 1149 */       this.longestCollisionList = src._longestCollisionList;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static final class Bucket
/*      */   {
/*      */     protected final Name _name;
/*      */     
/*      */     protected final Bucket _next;
/*      */     
/*      */     private final int _length;
/*      */     
/*      */ 
/*      */     Bucket(Name name, Bucket next)
/*      */     {
/* 1165 */       this._name = name;
/* 1166 */       this._next = next;
/* 1167 */       this._length = (next == null ? 1 : next._length + 1);
/*      */     }
/*      */     
/* 1170 */     public int length() { return this._length; }
/*      */     
/*      */     public Name find(int hash, int firstQuad, int secondQuad)
/*      */     {
/* 1174 */       if ((this._name.hashCode() == hash) && 
/* 1175 */         (this._name.equals(firstQuad, secondQuad))) {
/* 1176 */         return this._name;
/*      */       }
/*      */       
/* 1179 */       for (Bucket curr = this._next; curr != null; curr = curr._next) {
/* 1180 */         Name currName = curr._name;
/* 1181 */         if ((currName.hashCode() == hash) && 
/* 1182 */           (currName.equals(firstQuad, secondQuad))) {
/* 1183 */           return currName;
/*      */         }
/*      */       }
/*      */       
/* 1187 */       return null;
/*      */     }
/*      */     
/*      */     public Name find(int hash, int[] quads, int qlen)
/*      */     {
/* 1192 */       if ((this._name.hashCode() == hash) && 
/* 1193 */         (this._name.equals(quads, qlen))) {
/* 1194 */         return this._name;
/*      */       }
/*      */       
/* 1197 */       for (Bucket curr = this._next; curr != null; curr = curr._next) {
/* 1198 */         Name currName = curr._name;
/* 1199 */         if ((currName.hashCode() == hash) && 
/* 1200 */           (currName.equals(quads, qlen))) {
/* 1201 */           return currName;
/*      */         }
/*      */       }
/*      */       
/* 1205 */       return null;
/*      */     }
/*      */   }
/*      */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\sym\BytesToNameCanonicalizer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */