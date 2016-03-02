package org.codehaus.jackson;

public abstract interface SerializableString
{
  public abstract String getValue();
  
  public abstract int charLength();
  
  public abstract char[] asQuotedChars();
  
  public abstract byte[] asUnquotedUTF8();
  
  public abstract byte[] asQuotedUTF8();
}


/* Location:              D:\EclipseWorkspace\jackson-core-asl-1.9.11.jar!\org\codehaus\jackson\SerializableString.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */