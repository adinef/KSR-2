package net.script.logic.summary;

public class Metadata {
    private Object[] data;

    public Metadata(Object... objs) {
        data = objs;
    }

    @Override
    public String toString() {
        String toRet = "[";
        if (data != null) {
            for (Object elem : data) {
                toRet += elem.toString() + ", ";
            }
        }
        return toRet + "]";
    }
}
