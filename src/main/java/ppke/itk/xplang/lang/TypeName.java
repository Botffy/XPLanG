package ppke.itk.xplang.lang;

import org.apache.commons.lang3.StringUtils;

class TypeName extends PlangName {
    TypeName(String str) {
        super(str);
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(super.toString());
    }

    static TypeName typeName(String string) {
        return new TypeName(string);
    }
}
