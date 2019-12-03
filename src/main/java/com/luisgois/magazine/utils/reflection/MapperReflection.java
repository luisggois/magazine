/**
 * 
 */
package com.luisgois.magazine.utils.reflection;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author luisgois
 */
@Component
public class MapperReflection {

	/**
	 * 
	 * @param dtoObject (dto received from the user)
	 * @param modelObject (model we want to override with valid fields from dto)	  
	 * @return updated model
	 * 
	 */
	public final <S, T> T mapper (S dtoObject, T modelObject) throws IllegalArgumentException, IllegalAccessException{

		for (Field dtoField : dtoObject.getClass().getDeclaredFields()) {			
			dtoField.setAccessible(true);						
			Object dtoValue = dtoField.get(dtoObject); 	
			if (!ObjectUtils.isEmpty(dtoValue)) {					
				for (Field modelField : modelObject.getClass().getDeclaredFields()) {
					modelField.setAccessible(true);							
					if(modelField.getName().equals(dtoField.getName())) {
						modelField.set(modelObject,dtoValue);
					}
				}
			}
		}
		return modelObject;
	}

}
