/*     */ package org.codehaus.jackson.io;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class NumberInput
/*     */ {
/*     */   public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
/*     */   
/*     */ 
/*     */ 
/*     */   static final long L_BILLION = 1000000000L;
/*     */   
/*     */ 
/*     */ 
/*  16 */   static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
/*  17 */   static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int parseInt(char[] digitChars, int offset, int len)
/*     */   {
/*  28 */     int num = digitChars[offset] - '0';
/*  29 */     len += offset;
/*     */     
/*  31 */     offset++; if (offset < len) {
/*  32 */       num = num * 10 + (digitChars[offset] - '0');
/*  33 */       offset++; if (offset < len) {
/*  34 */         num = num * 10 + (digitChars[offset] - '0');
/*  35 */         offset++; if (offset < len) {
/*  36 */           num = num * 10 + (digitChars[offset] - '0');
/*  37 */           offset++; if (offset < len) {
/*  38 */             num = num * 10 + (digitChars[offset] - '0');
/*  39 */             offset++; if (offset < len) {
/*  40 */               num = num * 10 + (digitChars[offset] - '0');
/*  41 */               offset++; if (offset < len) {
/*  42 */                 num = num * 10 + (digitChars[offset] - '0');
/*  43 */                 offset++; if (offset < len) {
/*  44 */                   num = num * 10 + (digitChars[offset] - '0');
/*  45 */                   offset++; if (offset < len) {
/*  46 */                     num = num * 10 + (digitChars[offset] - '0');
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  55 */     return num;
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
/*     */   public static final int parseInt(String str)
/*     */   {
/*  70 */     char c = str.charAt(0);
/*  71 */     int length = str.length();
/*  72 */     boolean negative = c == '-';
/*  73 */     int offset = 1;
/*     */     
/*     */ 
/*  76 */     if (negative) {
/*  77 */       if ((length == 1) || (length > 10)) {
/*  78 */         return Integer.parseInt(str);
/*     */       }
/*  80 */       c = str.charAt(offset++);
/*     */     }
/*  82 */     else if (length > 9) {
/*  83 */       return Integer.parseInt(str);
/*     */     }
/*     */     
/*  86 */     if ((c > '9') || (c < '0')) {
/*  87 */       return Integer.parseInt(str);
/*     */     }
/*  89 */     int num = c - '0';
/*  90 */     if (offset < length) {
/*  91 */       c = str.charAt(offset++);
/*  92 */       if ((c > '9') || (c < '0')) {
/*  93 */         return Integer.parseInt(str);
/*     */       }
/*  95 */       num = num * 10 + (c - '0');
/*  96 */       if (offset < length) {
/*  97 */         c = str.charAt(offset++);
/*  98 */         if ((c > '9') || (c < '0')) {
/*  99 */           return Integer.parseInt(str);
/*     */         }
/* 101 */         num = num * 10 + (c - '0');
/*     */         
/* 103 */         if (offset < length) {
/*     */           do {
/* 105 */             c = str.charAt(offset++);
/* 106 */             if ((c > '9') || (c < '0')) {
/* 107 */               return Integer.parseInt(str);
/*     */             }
/* 109 */             num = num * 10 + (c - '0');
/* 110 */           } while (offset < length);
/*     */         }
/*     */       }
/*     */     }
/* 114 */     return negative ? -num : num;
/*     */   }
/*     */   
/*     */ 
/*     */   public static final long parseLong(char[] digitChars, int offset, int len)
/*     */   {
/* 120 */     int len1 = len - 9;
/* 121 */     long val = parseInt(digitChars, offset, len1) * 1000000000L;
/* 122 */     return val + parseInt(digitChars, offset + len1, 9);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final long parseLong(String str)
/*     */   {
/* 130 */     int length = str.length();
/* 131 */     if (length <= 9) {
/* 132 */       return parseInt(str);
/*     */     }
/*     */     
/* 135 */     return Long.parseLong(str);
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
/*     */   public static final boolean inLongRange(char[] digitChars, int offset, int len, boolean negative)
/*     */   {
/* 150 */     String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
/* 151 */     int cmpLen = cmpStr.length();
/* 152 */     if (len < cmpLen) return true;
/* 153 */     if (len > cmpLen) { return false;
/*     */     }
/* 155 */     for (int i = 0; i < cmpLen; i++) {
/* 156 */       int diff = digitChars[(offset + i)] - cmpStr.charAt(i);
/* 157 */       if (diff != 0) {
/* 158 */         return diff < 0;
/*     */       }
/*     */     }
/* 161 */     return true;
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
/*     */   public static final boolean inLongRange(String numberStr, boolean negative)
/*     */   {
/* 175 */     String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
/* 176 */     int cmpLen = cmpStr.length();
/* 177 */     int actualLen = numberStr.length();
/* 178 */     if (actualLen < cmpLen) return true;
/* 179 */     if (actualLen > cmpLen) { return false;
/*     */     }
/*     */     
/* 182 */     for (int i = 0; i < cmpLen; i++) {
/* 183 */       int diff = numberStr.charAt(i) - cmpStr.charAt(i);
/* 184 */       if (diff != 0) {
/* 185 */         return diff < 0;
/*     */       }
/*     */     }
/* 188 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int parseAsInt(String input, int defaultValue)
/*     */   {
/* 196 */     if (input == null) {
/* 197 */       return defaultValue;
/*     */     }
/* 199 */     input = input.trim();
/* 200 */     int len = input.length();
/* 201 */     if (len == 0) {
/* 202 */       return defaultValue;
/*     */     }
/*     */     
/* 205 */     int i = 0;
/* 206 */     if (i < len) {
/* 207 */       char c = input.charAt(0);
/* 208 */       if (c == '+') {
/* 209 */         input = input.substring(1);
/* 210 */         len = input.length();
/* 211 */       } else if (c == '-') {
/* 212 */         i++;
/*     */       }
/*     */     }
/* 215 */     for (; i < len; i++) {
/* 216 */       char c = input.charAt(i);
/*     */       
/* 218 */       if ((c > '9') || (c < '0')) {
/*     */         try {
/* 220 */           return (int)parseDouble(input);
/*     */         } catch (NumberFormatException e) {
/* 222 */           return defaultValue;
/*     */         }
/*     */       }
/*     */     }
/*     */     try {
/* 227 */       return Integer.parseInt(input);
/*     */     } catch (NumberFormatException e) {}
/* 229 */     return defaultValue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long parseAsLong(String input, long defaultValue)
/*     */   {
/* 237 */     if (input == null) {
/* 238 */       return defaultValue;
/*     */     }
/* 240 */     input = input.trim();
/* 241 */     int len = input.length();
/* 242 */     if (len == 0) {
/* 243 */       return defaultValue;
/*     */     }
/*     */     
/* 246 */     int i = 0;
/* 247 */     if (i < len) {
/* 248 */       char c = input.charAt(0);
/* 249 */       if (c == '+') {
/* 250 */         input = input.substring(1);
/* 251 */         len = input.length();
/* 252 */       } else if (c == '-') {
/* 253 */         i++;
/*     */       }
/*     */     }
/* 256 */     for (; i < len; i++) {
/* 257 */       char c = input.charAt(i);
/*     */       
/* 259 */       if ((c > '9') || (c < '0')) {
/*     */         try {
/* 261 */           return parseDouble(input);
/*     */         } catch (NumberFormatException e) {
/* 263 */           return defaultValue;
/*     */         }
/*     */       }
/*     */     }
/*     */     try {
/* 268 */       return Long.parseLong(input);
/*     */     } catch (NumberFormatException e) {}
/* 270 */     return defaultValue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static double parseAsDouble(String input, double defaultValue)
/*     */   {
/* 278 */     if (input == null) {
/* 279 */       return defaultValue;
/*     */     }
/* 281 */     input = input.trim();
/* 282 */     int len = input.length();
/* 283 */     if (len == 0) {
/* 284 */       return defaultValue;
/*     */     }
/*     */     try {
/* 287 */       return parseDouble(input);
/*     */     } catch (NumberFormatException e) {}
/* 289 */     return defaultValue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final double parseDouble(String numStr)
/*     */     throws NumberFormatException
/*     */   {
/* 298 */     if ("2.2250738585072012e-308".equals(numStr)) {
/* 299 */       return 2.2250738585072014E-308D;
/*     */     }
/* 301 */     return Double.parseDouble(numStr);
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\io\NumberInput.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */