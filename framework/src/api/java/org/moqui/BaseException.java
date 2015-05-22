/*
 * This is free and unencumbered software released into the public domain.
 * For specific language governing permissions and limitations refer to
 * the LICENSE.md file or http://unlicense.org
 */
package org.moqui;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseException - the base/root exception for all exception classes in Moqui Framework.
 */
public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable nested) {
        super(message, nested);
    }

    @Override
    public void printStackTrace() {
        filterStackTrace(this);
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream printStream) {
        filterStackTrace(this);
        super.printStackTrace(printStream);
    }

    @Override
    public void printStackTrace(PrintWriter printWriter) {
        filterStackTrace(this);
        super.printStackTrace(printWriter);
    }

    /* Shouldn't be needed and with new parameterized getFilteredStackTrace method needed for filterStackTrace causes infinite recursion
    @Override
    public StackTraceElement[] getStackTrace() { return getFilteredStackTrace(this); }
    */

    public static void filterStackTrace(Throwable t) {
        t.setStackTrace(getFilteredStackTrace(t));
        if (t.getCause() != null) filterStackTrace(t.getCause());
    }

    public static StackTraceElement[] getFilteredStackTrace(Throwable t) {
        StackTraceElement[] orig = t.getStackTrace();
        List<StackTraceElement> newList = new ArrayList<StackTraceElement>(orig.length);
        for (StackTraceElement ste: orig) {
            String cn = ste.getClassName();
            if (cn.startsWith("freemarker.core.") || cn.startsWith("freemarker.ext.beans.") ||
                    cn.startsWith("java.lang.reflect.") || cn.startsWith("sun.reflect.") ||
                    cn.startsWith("org.codehaus.groovy.runtime.") || cn.startsWith("org.codehaus.groovy.reflection.") ||
                    cn.startsWith("groovy.lang.")) {
                continue;
            }
            // if ("renderSingle".equals(ste.getMethodName()) && cn.startsWith("org.moqui.impl.screen.ScreenSection")) continue;
            // if (("internalRender".equals(ste.getMethodName()) || "doActualRender".equals(ste.getMethodName())) && cn.startsWith("org.moqui.impl.screen.ScreenRenderImpl")) continue;
            if (("call".equals(ste.getMethodName()) || "callCurrent".equals(ste.getMethodName())) && ste.getLineNumber() == -1) continue;
            //System.out.println("Adding className: " + cn + ", line: " + ste.getLineNumber());
            newList.add(ste);
        }
        //System.out.println("Called getFilteredStackTrace, orig.length=" + orig.length + ", newList.size()=" + newList.size());
        return newList.toArray(new StackTraceElement[newList.size()]);
    }
}
