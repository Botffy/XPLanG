package ppke.itk.xplang.common;

import java.util.Locale;
import java.util.ResourceBundle;

public class CompilerMessageTranslator {
    private final ResourceBundle messages;

    public CompilerMessageTranslator(Locale locale) {
        this.messages = ResourceBundle.getBundle(
            "messages.compiler_messages",
            locale,
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)
        );
    }

    public String translate(CompilerMessage message) {
        String baseString = messages.getString(message.getErrorCode().name());
        return String.format(baseString, message.getParams().toArray());
    }
}
