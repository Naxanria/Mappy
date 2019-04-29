package com.naxanria.mappy.config;

import com.naxanria.mappy.util.MathUtil;

public abstract class ConfigEntry<T>
{
  public final String name;
  protected final DataMap map;
  protected final T defaultValue;
  
  public ConfigEntry(String name, DataMap map, T defaultValue)
  {
    this.name = name;
    this.map = map;
    this.defaultValue = defaultValue;
  }
  
  public abstract T getValue();
  
  public T getDefaultValue()
  {
    return defaultValue;
  }
  
  public abstract void setValue(T value);
  
  public abstract void saveDefault();
  
  
  public static class StringEntry extends ConfigEntry<String>
  {
    public StringEntry(String name, DataMap map, String defaultValue)
    {
      super(name, map, defaultValue);
    }
  
    @Override
    public String getValue()
    {
      return map.get(name, defaultValue);
    }
  
    @Override
    public void setValue(String value)
    {
      map.set(name, value);
    }
  
    @Override
    public void saveDefault()
    {
      map.set(name, defaultValue);
    }
  }
  
  public static class IntegerEntry extends ConfigEntry<Integer>
  {
    public IntegerEntry(String name, DataMap map, Integer defaultValue)
    {
      super(name, map, defaultValue);
    }
  
    @Override
    public Integer getValue()
    {
      return map.getInt(name, defaultValue);
    }
  
    @Override
    public void setValue(Integer value)
    {
      map.set(name, value);
    }
  
    @Override
    public void saveDefault()
    {
      map.set(name, defaultValue);
    }
  }
  
  public static class IntegerRangeEntry extends IntegerEntry
  {
    protected int min, max;
  
    public IntegerRangeEntry(String name, DataMap map, Integer defaultValue, int max)
    {
      this(name, map, defaultValue, 0, max);
    }
    
    public IntegerRangeEntry(String name, DataMap map, Integer defaultValue, int min, int max)
    {
      super(name, map, defaultValue);
      this.min = min;
      this.max = max;
    }
    
    private int clamped(int val)
    {
      return MathUtil.clamp(val, min, max);
    }
  
    @Override
    public Integer getValue()
    {
      return clamped(super.getValue());
    }
  
    @Override
    public void setValue(Integer value)
    {
      super.setValue(clamped(value));
    }
  }
  
  public static class BooleanEntry extends ConfigEntry<Boolean>
  {
    public BooleanEntry(String name, DataMap map, Boolean defaultValue)
    {
      super(name, map, defaultValue);
    }
  
    @Override
    public Boolean getValue()
    {
      return map.getBoolean(name, defaultValue);
    }
  
    @Override
    public void setValue(Boolean value)
    {
      map.set(name, value);
    }
  
    @Override
    public void saveDefault()
    {
      map.set(name, defaultValue);
    }
  }
}
