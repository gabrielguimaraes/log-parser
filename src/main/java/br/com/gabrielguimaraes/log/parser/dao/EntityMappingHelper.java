package br.com.gabrielguimaraes.log.parser.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.gabrielguimaraes.log.parser.database.Column;
import br.com.gabrielguimaraes.log.parser.database.Id;

public class EntityMappingHelper {
    public static Map<Class<?>, Object> addObjectToMap(Field field, Object object) {
        Map<Class<?>, Object> map = new HashMap<>();
        try {
            Object invokedValue = getValueFromObject(field, object);
            if (isIdField(field)) {
                return map;
            }

            map.put(field.getType(), invokedValue);

        } catch (IllegalArgumentException e) {
            System.out.println("Error trying to get object values");
        } catch (SecurityException e) {
            System.out.println("Security error trying to get object values");
        }
        return map;
    }

    public static Object getValueFromObject(Field field, Object object) {
        Object invokedValue = null;
        try {
            String methodName = "get" + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1, field.getName().length());
            invokedValue = object.getClass().getDeclaredMethod(methodName).invoke(object);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            System.out.printf("Error trying to access attribute value from object: %s", e.getMessage());
        }
        return invokedValue;
    }

    public static Boolean isIdField(Field field) {
        Id idAnnotation = field.getAnnotation(Id.class);
        if (idAnnotation != null) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
    
    private static Field getIdField(Object entity) {
        Optional<Field> idField = Stream.of(entity.getClass().getDeclaredFields())
            .filter(EntityMappingHelper::isIdField)
            .findFirst();
        
        if (idField.isPresent()) {
            return idField.get();
        }
        
        return null;
    }

    public static String getSQLNameForField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return column.value();
        }

        return field.getName();
    }

    public static List<String> getSQLFieldsList(Class<?> clazz, Object entity) {
        List<String> fieldNamesList = Stream.of(entity.getClass().getDeclaredFields())
            .filter(f -> getValueFromObject(f, entity) != null && !isIdField(f))
            .map(f -> getSQLNameForField(f))
            .collect(Collectors.toList());
        
        if (fieldNamesList.isEmpty()) {
            return null;
        }
        
        return fieldNamesList;
    }
    
    public static String getSQLFieldsInString(List<String> fieldNamesList) {
        String sqlFieldNames = fieldNamesList
            .stream()
            .collect(Collectors.joining(", "));
       
        return sqlFieldNames;
    }

        

    public static String getValuesTerms(List<String> fieldNamesList, Object entity) {
        Field idField = getIdField(entity);
        String sqlIdField = getSQLNameForField(idField);
        Object idFieldValue = getValueFromObject(idField, entity);
        
        String idFieldSubstitute;
        if (idFieldValue == null) {
            idFieldSubstitute = "default";
        } else {
            idFieldSubstitute = "?";
        }
        
        StringBuilder valuesTerm = new StringBuilder(" ( ")
        .append(fieldNamesList
            .stream()
            .map(f -> f.equals(sqlIdField) ? idFieldSubstitute : "?")
            .collect(Collectors.joining(", ")))
        .append(" )");

        return valuesTerm.toString();
    }

    public static String getSingleEntityValuesTerms(Class<?> clazz, Object entity) {
        List<String> sqlFieldsList = getSQLFieldsList(clazz, entity);
        
        String valuesTerms = getValuesTerms(sqlFieldsList, entity);
        
        StringBuilder sqlFields = new StringBuilder("(")
                .append(getSQLFieldsInString(sqlFieldsList))
                .append(") VALUES ")
                .append(valuesTerms);
        
        return sqlFields.toString();
    }
    
//    public static String getEntityValuesTermsForBatch(Class<?> clazz, Object entity, int repetition) {
//        List<String> sqlFieldsList = getSQLFieldsList(clazz, entity);
//        
//        String valuesTerms = getValuesTerms(sqlFieldsList, entity);
//        
//        String valuesTermsForBatch = IntStream
//            .range(0, repetition)
//            .mapToObj(i -> valuesTerms)
//            .collect(Collectors.joining(","));
//            
//        StringBuilder sqlFields = new StringBuilder("(")
//                .append(getSQLFieldsInString(sqlFieldsList))
//                .append(") VALUES ")
//                .append(valuesTermsForBatch);
//        
//        return sqlFields.toString();
//    }
}
