/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.event.map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.codegen.core.CodegenContext;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.event.BaseNestableEventUtil;

import java.util.Map;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.castUnderlying;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethodTakingExprAndConst;

/**
 * Getter for a dynamic indexed property for maps.
 */
public class MapIndexedPropertyGetter implements MapEventPropertyGetter {
    private final int index;
    private final String fieldName;

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param map map
     * @param fieldName name
     * @param index index
     * @return value
     * @throws PropertyAccessException exception
     */
    public static Object getMapIndexedValue(Map<String, Object> map, String fieldName, int index) throws PropertyAccessException {
        Object value = map.get(fieldName);
        return BaseNestableEventUtil.getBNArrayValueAtIndexWithNullCheck(value, index);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param map map
     * @param fieldName name
     * @param index index
     * @return value
     * @throws PropertyAccessException exception
     */
    public static boolean getMapIndexedExists(Map<String, Object> map, String fieldName, int index) throws PropertyAccessException {
        Object value = map.get(fieldName);
        return BaseNestableEventUtil.isExistsIndexedValue(value, index);
    }

    /**
     * Ctor.
     *
     * @param fieldName property name
     * @param index     index to get the element at
     */
    public MapIndexedPropertyGetter(String fieldName, int index) {
        this.index = index;
        this.fieldName = fieldName;
    }

    public Object getMap(Map<String, Object> map) throws PropertyAccessException {
        return getMapIndexedValue(map, fieldName, index);
    }

    public boolean isMapExistsProperty(Map<String, Object> map) {
        return getMapIndexedExists(map, fieldName, index);
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        return getMap(BaseNestableEventUtil.checkedCastUnderlyingMap(eventBean));
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return isMapExistsProperty(BaseNestableEventUtil.checkedCastUnderlyingMap(eventBean));
    }

    public Object getFragment(EventBean eventBean) {
        return null;
    }

    public CodegenExpression codegenEventBeanGet(CodegenExpression beanExpression, CodegenContext context) {
        return codegenUnderlyingGet(castUnderlying(Map.class, beanExpression), context);
    }

    public CodegenExpression codegenEventBeanExists(CodegenExpression beanExpression, CodegenContext context) {
        return codegenUnderlyingExists(castUnderlying(Map.class, beanExpression), context);
    }

    public CodegenExpression codegenEventBeanFragment(CodegenExpression beanExpression, CodegenContext context) {
        return constantNull();
    }

    public CodegenExpression codegenUnderlyingGet(CodegenExpression underlyingExpression, CodegenContext context) {
        return staticMethodTakingExprAndConst(this.getClass(), "getMapIndexedValue", underlyingExpression, fieldName, index);
    }

    public CodegenExpression codegenUnderlyingExists(CodegenExpression underlyingExpression, CodegenContext context) {
        return staticMethodTakingExprAndConst(this.getClass(), "getMapIndexedExists", underlyingExpression, fieldName, index);
    }

    public CodegenExpression codegenUnderlyingFragment(CodegenExpression underlyingExpression, CodegenContext context) {
        return constantNull();
    }
}
