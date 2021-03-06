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
package com.espertech.esper.event.vaevent;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.codegen.core.CodegenContext;
import com.espertech.esper.codegen.core.CodegenMember;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.event.EventPropertyGetterSPI;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;

public class VAERevisionEventPropertyGetterDeclaredGetVersioned implements EventPropertyGetterSPI {
    private final RevisionGetterParameters parameters;

    public VAERevisionEventPropertyGetterDeclaredGetVersioned(RevisionGetterParameters parameters) {
        this.parameters = parameters;
    }

    public Object get(EventBean eventBean) throws PropertyAccessException {
        RevisionEventBeanDeclared riv = (RevisionEventBeanDeclared) eventBean;
        return riv.getVersionedValue(parameters);
    }

    public boolean isExistsProperty(EventBean eventBean) {
        return true;
    }

    public Object getFragment(EventBean eventBean) {
        return null; // fragments no provided by revision events
    }

    public CodegenExpression codegenEventBeanGet(CodegenExpression beanExpression, CodegenContext context) {
        CodegenMember member = context.makeAddMember(RevisionGetterParameters.class, parameters);
        return exprDotMethod(cast(RevisionEventBeanDeclared.class, beanExpression), "getVersionedValue", ref(member.getMemberName()));
    }

    public CodegenExpression codegenEventBeanExists(CodegenExpression beanExpression, CodegenContext context) {
        return constantTrue();
    }

    public CodegenExpression codegenEventBeanFragment(CodegenExpression beanExpression, CodegenContext context) {
        return constantNull();
    }

    public CodegenExpression codegenUnderlyingGet(CodegenExpression underlyingExpression, CodegenContext context) {
        throw revisionImplementationNotProvided();
    }

    public CodegenExpression codegenUnderlyingExists(CodegenExpression underlyingExpression, CodegenContext context) {
        throw revisionImplementationNotProvided();
    }

    public CodegenExpression codegenUnderlyingFragment(CodegenExpression underlyingExpression, CodegenContext context) {
        throw revisionImplementationNotProvided();
    }

    protected static UnsupportedOperationException revisionImplementationNotProvided() {
        return new UnsupportedOperationException("Revision event type does not provide an implementation for underlying get");
    }
}
