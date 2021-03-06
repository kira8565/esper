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
package com.espertech.esper.avro.getter;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.codegen.core.CodegenContext;
import com.espertech.esper.codegen.core.CodegenMember;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.EventPropertyGetterSPI;
import org.apache.avro.generic.GenericData;

import java.util.Collection;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;

public class AvroEventBeanGetterNestedIndexed implements EventPropertyGetterSPI {
    private final int top;
    private final int pos;
    private final int index;
    private final EventType fragmentEventType;
    private final EventAdapterService eventAdapterService;

    public AvroEventBeanGetterNestedIndexed(int top, int pos, int index, EventType fragmentEventType, EventAdapterService eventAdapterService) {
        this.top = top;
        this.pos = pos;
        this.index = index;
        this.fragmentEventType = fragmentEventType;
        this.eventAdapterService = eventAdapterService;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        GenericData.Record record = (GenericData.Record) eventBean.getUnderlying();
        GenericData.Record inner = (GenericData.Record) record.get(top);
        if (inner == null) {
            return null;
        }
        Collection collection = (Collection) inner.get(pos);
        return AvroEventBeanGetterIndexed.getAvroIndexedValue(collection, index);
    }

    private String getCodegen(CodegenContext context) {
        return context.addMethod(Object.class, GenericData.Record.class, "record", this.getClass())
                .declareVar(GenericData.Record.class, "inner", cast(GenericData.Record.class, exprDotMethod(ref("record"), "get", constant(top))))
                .ifRefNullReturnNull("inner")
                .declareVar(Collection.class, "collection", cast(Collection.class, exprDotMethod(ref("inner"), "get", constant(pos))))
                .methodReturn(staticMethod(AvroEventBeanGetterIndexed.class, "getAvroIndexedValue", ref("collection"), constant(index)));
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return true;
    }

    public Object getFragment(EventBean eventBean) throws PropertyAccessException {
        if (fragmentEventType == null) {
            return null;
        }
        Object value = get(eventBean);
        if (value == null) {
            return null;
        }
        return eventAdapterService.adapterForTypedAvro(value, fragmentEventType);
    }

    private String getFragmentCodegen(CodegenContext context) {
        CodegenMember mSvc = context.makeAddMember(EventAdapterService.class, eventAdapterService);
        CodegenMember mType = context.makeAddMember(EventType.class, fragmentEventType);

        return context.addMethod(Object.class, GenericData.Record.class, "record", this.getClass())
                .declareVar(Object.class, "value", codegenUnderlyingGet(ref("record"), context))
                .ifRefNullReturnNull("value")
                .methodReturn(exprDotMethod(ref(mSvc.getMemberName()), "adapterForTypedAvro", ref("value"), ref(mType.getMemberName())));
    }

    public CodegenExpression codegenEventBeanGet(CodegenExpression beanExpression, CodegenContext context) {
        return codegenUnderlyingGet(castUnderlying(GenericData.Record.class, beanExpression), context);
    }

    public CodegenExpression codegenEventBeanExists(CodegenExpression beanExpression, CodegenContext context) {
        return constantTrue();
    }

    public CodegenExpression codegenEventBeanFragment(CodegenExpression beanExpression, CodegenContext context) {
        return codegenUnderlyingFragment(castUnderlying(GenericData.Record.class, beanExpression), context);
    }

    public CodegenExpression codegenUnderlyingGet(CodegenExpression underlyingExpression, CodegenContext context) {
        return localMethod(getCodegen(context), underlyingExpression);
    }

    public CodegenExpression codegenUnderlyingExists(CodegenExpression underlyingExpression, CodegenContext context) {
        return constantTrue();
    }

    public CodegenExpression codegenUnderlyingFragment(CodegenExpression underlyingExpression, CodegenContext context) {
        if (fragmentEventType == null) {
            return constantNull();
        }
        return localMethod(getFragmentCodegen(context), underlyingExpression);
    }
}
