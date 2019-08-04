package com.naxanria.mappy;

public class Logger
{
  public String prefix = "";
  
  public Logger(String prefix)
  {
    this.prefix = prefix;
  }
  
  public void info(String s)
  {
    print("[INFO]" + s);
  }
  
  public  void info(Object o)
  {
    info(o == null ? "NULL" : o.toString());
  }
  
  public void warn(String s)
  {
    print("[WARN] " + s);
  }
  
  public void warn(Object o)
  {
    warn(o == null ? "NULL" : o.toString());
  }
  
  public void error(String s)
  {
    printError(s);
  }
  
  public void error(Object o)
  {
    error(o == null ? "NULL" : o.toString());
  }
  
  private void printError(String s)
  {
    System.err.println(prefix + s);
  }
  
  private void print(String s)
  {
    System.out.println(prefix + s);
  }
}
