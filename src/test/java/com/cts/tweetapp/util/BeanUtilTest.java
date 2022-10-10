package com.cts.tweetapp.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cts.tweetapp.beans.Message;
import com.cts.tweetapp.beans.ReplyTweet;
import com.cts.tweetapp.beans.Tweet;
import com.cts.tweetapp.beans.User;
import com.cts.tweetapp.beans.dto.LikeCountDto;
import com.cts.tweetapp.beans.dto.MessageDto;
import com.cts.tweetapp.beans.dto.TweetDto;
import com.cts.tweetapp.beans.dto.UserDto;

public class BeanUtilTest {
	public static final Logger logger = LoggerFactory.getLogger(BeanUtilTest.class);

	private static final Class<?>[] beanClasses;

	static {
		beanClasses = new Class[] { Message.class, ReplyTweet.class, Tweet.class, User.class,
				LikeCountDto.class, MessageDto.class, TweetDto.class, UserDto.class};
	}

	@Test
	public void toStringTest()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		for (Class<?> beanClass : beanClasses) {
			Object instance = beanClass.getConstructor().newInstance();
			String toString = instance.toString();
			Assertions.assertThat(toString).isNotNull().isNotEmpty();
		}
	}

	@Test
	public void setterGetterTest()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		for (Class<?> beanClass : beanClasses) {
			try {
				Object instance = beanClass.getConstructor().newInstance();
				for (Field field : beanClass.getDeclaredFields()) {
					if (!field.isSynthetic()) {
						field.setAccessible(true);
						Object value = createValue(field.getType());
						String setterName = constructSetterName(field);
						Method setter = beanClass.getDeclaredMethod(setterName, field.getType());
						setter.setAccessible(true);
						setter.invoke(instance, value);
						String getterName = constructGetterName(field);
						Object actual = beanClass.getDeclaredMethod(getterName).invoke(instance);
						// check results
						Assertions.assertThat(actual).isNotNull().isEqualTo(value);
					}
				}
			} catch (Exception e) {
				if (e instanceof NoSuchMethodException) {
					logger.error("NoSuchMethodException for {}. {}", beanClass, e.getMessage());
					continue;
				}
				logger.error("Unit test failing for {} class: {}", beanClass, e.getMessage());
				throw e;
			}
		}
	}

	/**
	 * Provides a field value to set
	 */
	private Object createValue(Class<?> clazz)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Object returnObject;
		if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) || Boolean.class.equals(clazz)
				|| BigDecimal.class.equals(clazz)) {
			String typeName = clazz.getTypeName();
			if ("boolean".equalsIgnoreCase(typeName) || Boolean.class.equals(clazz)) {
				returnObject = true;
			} else {
				if (Short.class.equals(clazz) || short.class.equals(clazz)) {
					returnObject = (short) 10;
				} else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
					returnObject = 10L;
				} else if (Double.class.equals(clazz) || double.class.equals(clazz) || Float.class.equals(clazz)
						|| float.class.equals(clazz)) {
					returnObject = 10.1;
				} else if (BigDecimal.class.equals(clazz)) {
					returnObject = new BigDecimal("-12345.6789");
				} else {
					returnObject = 10;
				}
			}
		} else if (clazz.isAssignableFrom(String.class)) {
			returnObject = "reflected-field-" + clazz.getName();
		} else if (clazz.isAssignableFrom(List.class)) {
			returnObject = Collections.emptyList();
		} else if (clazz.isAssignableFrom(Map.class)) {
			returnObject = Collections.emptyMap();
		} else if (LocalDateTime.class.equals(clazz)) {
			returnObject = LocalDateTime.now();
		} else if (Date.class.equals(clazz)) {
			returnObject = new Date();
		} else {
			returnObject = clazz.getConstructor().newInstance();
		}

		return returnObject;
	}

	/**
	 * Constructs boolean getter name. Example:
	 *
	 * <pre>
	 * isValid -> isValid
	 * valid -> isValid
	 * </pre>
	 */
	private String constructGetterName(Field field) {
		if (boolean.class == field.getType()) {
			return field.getName().startsWith("is") ? field.getName() : "is" + StringUtils.capitalize(field.getName());
		}
		return "get" + StringUtils.capitalize(field.getName());
	}

	private String constructSetterName(Field field) {
		if (boolean.class == field.getType()) {
			return "set" + (field.getName().startsWith("is") ? StringUtils.capitalize(field.getName().substring(2))
					: StringUtils.capitalize(field.getName()));
		}
		return "set" + StringUtils.capitalize(field.getName());
	}
}
