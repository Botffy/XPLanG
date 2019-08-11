package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.type.Type;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Access the actual lexical properties of the language.
 */
final class LexicalProperties {
    private static final Logger log = LoggerFactory.getLogger("Root.Parser.Lang");

    private final Properties props = new Properties();

    LexicalProperties() {
        try(InputStreamReader stream = new InputStreamReader(
            LexicalProperties.class.getResourceAsStream("/language/plang/lexical.properties"), StandardCharsets.UTF_8)
        ) {
            props.load(stream);
            log.info("Loaded PLanG language strings.");
        } catch(IOException | NullPointerException e) {
            log.error("Failed to load '/language/plang/lexical.properties'", e);
        }
    }

    String get(String key) {
        if(!props.containsKey(key)) {
            log.error("No key named '{}'", key);
            throw new IllegalStateException(String.format("Unknown key '%s'", key));
        }
        return props.getProperty(key);
    }

    /**
     * Get the regular expression matching the symbol.
     */
    String getSymbolPattern(PlangSymbol symbol) {
        return get(String.format("symbol.pattern.%s", symbol.name()));
    }

    String getTypeName(Type type) {
        return get(String.format("type.name.%s", type.getLabel()));
    }

    String getFunctionName(String key) {
        return get(String.format("function.name.%s", key));
    }
}
