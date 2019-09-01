package ppke.itk.xplang.interpreter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

class DecimalFormatFactory {
    private DecimalFormatFactory() {}

    public static DecimalFormat integerDecimalFormat() {
        DecimalFormat result = new DecimalFormat();
        result.setGroupingUsed(false);
        return result;
    }

    public static DecimalFormat realDecimalFormat() {
        DecimalFormat result = new DecimalFormat("0.0#########", new DecimalFormatSymbols(Locale.US));
        result.setGroupingUsed(false);
        return result;
    }
}
