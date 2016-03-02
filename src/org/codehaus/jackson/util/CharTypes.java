/*     */ package org.codehaus.jackson.util;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CharTypes
/*     */ {
/*   9 */   private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
/*     */   private static final byte[] HEX_BYTES;
/*     */   
/*  12 */   static { int len = HEX_CHARS.length;
/*  13 */     HEX_BYTES = new byte[len];
/*  14 */     for (int i = 0; i < len; i++) {
/*  15 */       HEX_BYTES[i] = ((byte)HEX_CHARS[i]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  30 */     int[] table = new int['Ā'];
/*     */     
/*  32 */     for (int i = 0; i < 32; i++) {
/*  33 */       table[i] = -1;
/*     */     }
/*     */     
/*  36 */     table[34] = 1;
/*  37 */     table[92] = 1;
/*  38 */     sInputCodes = table;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  47 */     int[] table = new int[sInputCodes.length];
/*  48 */     System.arraycopy(sInputCodes, 0, table, 0, sInputCodes.length);
/*  49 */     for (int c = 128; c < 256; c++)
/*     */     {
/*     */       int code;
/*     */       int code;
/*  53 */       if ((c & 0xE0) == 192) {
/*  54 */         code = 2; } else { int code;
/*  55 */         if ((c & 0xF0) == 224) {
/*  56 */           code = 3; } else { int code;
/*  57 */           if ((c & 0xF8) == 240)
/*     */           {
/*  59 */             code = 4;
/*     */           }
/*     */           else
/*  62 */             code = -1;
/*     */         } }
/*  64 */       table[c] = code;
/*     */     }
/*  66 */     sInputCodesUtf8 = table;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  79 */     int[] table = new int['Ā'];
/*     */     
/*  81 */     Arrays.fill(table, -1);
/*     */     
/*  83 */     for (int i = 33; i < 256; i++) {
/*  84 */       if (Character.isJavaIdentifierPart((char)i)) {
/*  85 */         table[i] = 0;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  91 */     table[64] = 0;
/*  92 */     table[35] = 0;
/*  93 */     table[42] = 0;
/*  94 */     table[45] = 0;
/*  95 */     table[43] = 0;
/*  96 */     sInputCodesJsNames = table;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */     int[] table = new int['Ā'];
/*     */     
/* 108 */     System.arraycopy(sInputCodesJsNames, 0, table, 0, sInputCodesJsNames.length);
/* 109 */     Arrays.fill(table, 128, 128, 0);
/* 110 */     sInputCodesUtf8JsNames = table;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */     sInputCodesComment = new int['Ā'];
/*     */     
/*     */ 
/* 120 */     System.arraycopy(sInputCodesUtf8, 128, sInputCodesComment, 128, 128);
/*     */     
/*     */ 
/* 123 */     Arrays.fill(sInputCodesComment, 0, 32, -1);
/* 124 */     sInputCodesComment[9] = 0;
/* 125 */     sInputCodesComment[10] = 10;
/* 126 */     sInputCodesComment[13] = 13;
/* 127 */     sInputCodesComment[42] = 42;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */     int[] table = new int[''];
/*     */     
/* 138 */     for (int i = 0; i < 32; i++)
/*     */     {
/* 140 */       table[i] = -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 145 */     table[34] = 34;
/* 146 */     table[92] = 92;
/*     */     
/* 148 */     table[8] = 98;
/* 149 */     table[9] = 116;
/* 150 */     table[12] = 102;
/* 151 */     table[10] = 110;
/* 152 */     table[13] = 114;
/* 153 */     sOutputEscapes128 = table;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 161 */     sHexValues = new int[''];
/*     */     
/* 163 */     Arrays.fill(sHexValues, -1);
/* 164 */     for (int i = 0; i < 10; i++) {
/* 165 */       sHexValues[(48 + i)] = i;
/*     */     }
/* 167 */     for (int i = 0; i < 6; i++) {
/* 168 */       sHexValues[(97 + i)] = (10 + i);
/* 169 */       sHexValues[(65 + i)] = (10 + i);
/*     */     } }
/*     */   
/*     */   static final int[] sInputCodes;
/* 173 */   public static final int[] getInputCodeLatin1() { return sInputCodes; }
/* 174 */   public static final int[] getInputCodeUtf8() { return sInputCodesUtf8; }
/*     */   
/* 176 */   public static final int[] getInputCodeLatin1JsNames() { return sInputCodesJsNames; }
/* 177 */   public static final int[] getInputCodeUtf8JsNames() { return sInputCodesUtf8JsNames; }
/*     */   
/* 179 */   public static final int[] getInputCodeComment() { return sInputCodesComment; }
/*     */   
/*     */   static final int[] sInputCodesUtf8;
/*     */   static final int[] sInputCodesJsNames;
/*     */   static final int[] sInputCodesUtf8JsNames;
/*     */   static final int[] sInputCodesComment;
/*     */   static final int[] sOutputEscapes128;
/*     */   static final int[] sHexValues;
/*     */   public static final int[] get7BitOutputEscapes() {
/* 188 */     return sOutputEscapes128;
/*     */   }
/*     */   
/*     */   public static int charToHex(int ch) {
/* 192 */     return ch > 127 ? -1 : sHexValues[ch];
/*     */   }
/*     */   
/*     */   public static void appendQuoted(StringBuilder sb, String content)
/*     */   {
/* 197 */     int[] escCodes = sOutputEscapes128;
/* 198 */     int escLen = escCodes.length;
/* 199 */     int i = 0; for (int len = content.length(); i < len; i++) {
/* 200 */       char c = content.charAt(i);
/* 201 */       if ((c >= escLen) || (escCodes[c] == 0)) {
/* 202 */         sb.append(c);
/*     */       }
/*     */       else {
/* 205 */         sb.append('\\');
/* 206 */         int escCode = escCodes[c];
/* 207 */         if (escCode < 0)
/*     */         {
/* 209 */           sb.append('u');
/* 210 */           sb.append('0');
/* 211 */           sb.append('0');
/* 212 */           int value = -(escCode + 1);
/* 213 */           sb.append(HEX_CHARS[(value >> 4)]);
/* 214 */           sb.append(HEX_CHARS[(value & 0xF)]);
/*     */         } else {
/* 216 */           sb.append((char)escCode);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static char[] copyHexChars()
/*     */   {
/* 226 */     return (char[])HEX_CHARS.clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] copyHexBytes()
/*     */   {
/* 234 */     return (byte[])HEX_BYTES.clone();
/*     */   }
/*     */ }


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\util\CharTypes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */