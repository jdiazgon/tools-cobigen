package utils.resources;

import java.util.List;
import java.util.Set;

import utils.resources.dataaccess.api.DeepEntity;

public class TestClass {
  private int primitive;

  private int[] primitiveArray;

  private Integer boxed;

  private String object;

  private String[] objectArray;

  private TestEntity entity;

  private List<TestEntity> entitys;
  
  private Set<TestEntity> setEntitys;

  private DeepEntity deepEntity;
}