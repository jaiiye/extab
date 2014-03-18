package geeksaint.point;

import extab.spike.AnnotationProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class RowDefinition {
  private final Class rowType;
  private final int columnsToSkip;
  private final int rowsToSkip;

  @Getter(AccessLevel.PRIVATE)
  private AnnotationProcessor annotationProcessor;

  public RowDefinition(Class rowType, AnnotationProcessor annotationProcessor){
    this.annotationProcessor = annotationProcessor;
    this.rowType = rowType;
    rowsToSkip = annotationProcessor.getSkipRows(rowType);
    columnsToSkip = annotationProcessor.getSkipColumns(rowType);
  }

  public ColumnDefinition[] getRowDefinition() {
    List<ColumnDefinition> columnDefinitions =  new ArrayList<ColumnDefinition>();
    columnDefinitions.addAll(getColumnFields());
    columnDefinitions.addAll(getColumnMethods());
    Collections.sort(columnDefinitions);
    return columnDefinitions.toArray(new ColumnDefinition[0]);
  }

  private List<ColumnDefinition> getColumnFields() {
    List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
    for(Field field : rowType.getDeclaredFields()){
      try {
        ExcelColumnType excelColumnType = annotationProcessor.getFieldColumnType(rowType, field.getName());
        Integer order = annotationProcessor.getFieldColumnOrder(rowType, field.getName());
        String format = annotationProcessor.getFieldColumnFormat(rowType, field.getName());
        if(excelColumnType != null && order != null){
          columnDefinitions.add(toColumnDefinition(order, excelColumnType, format));
        }
      } catch (NoSuchFieldException e) {/*Never happens, getFieldColumnType returns null if not annotated*/}
    }
    return columnDefinitions;
  }

  private List<ColumnDefinition> getColumnMethods() {
    List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
    for (Method method : rowType.getDeclaredMethods()) {
      ExcelColumnType excelColumnType = annotationProcessor.getMethodColumnType(rowType, method.getName());
      Integer order = annotationProcessor.getMethodColumnOrder(rowType, method.getName());
      String format = annotationProcessor.getMethodColumnFormat(rowType, method.getName());
      if (excelColumnType != null && order != null) {
        columnDefinitions.add(toColumnDefinition(order, excelColumnType, format));
      }
    }
    return columnDefinitions;
  }

  private ColumnDefinition toColumnDefinition(int order, ExcelColumnType columnType, String format){
    return new ColumnDefinition(order, columnType, format);
  }
}
