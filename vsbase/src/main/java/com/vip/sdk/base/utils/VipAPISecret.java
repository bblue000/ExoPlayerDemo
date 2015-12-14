package com.vip.sdk.base.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识注解
 *
 * <p/>
 *
 * 标识字段为API的特殊加密字段，如userSecret
 *
 * <p/>
 *
 * Created by yong01.yin on 2014/12/22.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VipAPISecret {
}
