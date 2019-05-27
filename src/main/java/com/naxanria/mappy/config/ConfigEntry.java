package com.naxanria.mappy.config;

import com.google.common.collect.ImmutableList;
import com.naxanria.mappy.util.MathUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ConfigEntry<T>
{
  public final String name;
  protected DataMap map;
  protected final T defaultValue;
  protected final Consumer<T> setter;
  protected final Supplier<T> getter;
  
  public ConfigEntry(String name, T defaultValue, Consumer<T> setter, Supplier<T> getter)
  {
    this.name = name;
    this.defaultValue = defaultValue;
    this.setter = setter;
    this.getter = getter;
  }
  
  public abstract T getValue();
  
  public T getDefaultValue()
  {
    return defaultValue;
  }
  
  public abstract void setValue(T value);
  
  public abstract void saveDefault();
  
  public abstract void save();
  
  public abstract void load();
  
  public ConfigEntry<T> setMap(DataMap map)
  {
    this.map = map;
    
    return this;
  }
  
  public static class StringEntry extends ConfigEntry<String>
  {
    public StringEntry(String name, String defaultValue, Consumer<String> setter, Supplier<String> getter)
    {
      super(name, defaultValue, setter, getter);
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
  
    @Override
    public void save()
    {
      map.set(name, getter.get());
    }
  
    @Override
    public void load()
    {
      setter.accept(map.get(name, defaultValue));
    }
  }
  
  public static class IntegerEntry extends ConfigEntry<Integer>
  {
    public IntegerEntry(String name, Integer defaultValue, Consumer<Integer> setter, Supplier<Integer> getter)
    {
      super(name, defaultValue, setter, getter);
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
  
    @Override
    public void save()
    {
      map.set(name, getter.get());
    }
  
    @Override
    public void load()
    {
      setter.accept(map.getInt(name, defaultValue));
    }
  }
  
  public static class IntegerRangeEntry extends IntegerEntry
  {
    protected int min, max;
  
    public IntegerRangeEntry(String name, Integer defaultValue, int max, Consumer<Integer> setter, Supplier<Integer> getter)
    {
      this(name, defaultValue, 0, max, setter, getter);
    }
    
    public IntegerRangeEntry(String name, Integer defaultValue, int min, int max, Consumer<Integer> setter, Supplier<Integer> getter)
    {
      super(name, defaultValue, setter, getter);
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
    public BooleanEntry(String name, Boolean defaultValue, Consumer<Boolean> setter, Supplier<Boolean> getter)
    {
      super(name, defaultValue, setter, getter);
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
  
    @Override
    public void save()
    {
      setValue(getter.get());
    }
  
    @Override
    public void load()
    {
      setter.accept(getValue());
    }
  }
  
  public static class EnumEntry<T extends Enum<?>> extends ConfigEntry<T>
  {
    private final ImmutableList<T> values;
    private final int defaultIndex;
  
    public EnumEntry(String name, Class<T> clazz, T defaultValue, Consumer<T> setter, Supplier<T> getter)
    {
      super(name, defaultValue, setter, getter);
      
      T[] enumValues = clazz.getEnumConstants();
      
      if (enumValues != null)
      {
        values = ImmutableList.copyOf(enumValues);
      }
      else
      {
        values = ImmutableList.of(defaultValue);
      }
      
      defaultIndex = values.indexOf(defaultValue);
    }
  
    @Override
    public T getValue()
    {
      int index = map.getInt(name, defaultIndex);
      if (index < 0 || index >= values.size())
      {
        index = defaultIndex;
        map.set(name, defaultIndex);
      }
      
      return values.get(index);
    }
  
    @Override
    public void setValue(T value)
    {
      int index = values.indexOf(value);
      map.set(name, index);
    }
  
    @Override
    public void saveDefault()
    {
      setValue(defaultValue);
    }
  
    @Override
    public void save()
    {
      setValue(getter.get());
    }
  
    @Override
    public void load()
    {
      setter.accept(getValue());
    }
  }
}
