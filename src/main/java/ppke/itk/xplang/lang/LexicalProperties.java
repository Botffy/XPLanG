package ppke.itk.xplang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

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

    private String get(String key) {
        if(!props.containsKey(key)) {
            log.error("No key named '{}'", key);
            throw new IllegalStateException(String.format("Unknown key '%s'", key));
        }
        return props.getProperty(key);
    }

    String getSymbolPattern(PlangSymbol symbol) {
        return get(String.format("symbol.pattern.%s", symbol.name()));
    }
}
