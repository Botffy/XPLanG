package ppke.itk.xplang.common;

import java.util.*;

import static java.util.Arrays.asList;

public final class Translator {
    private final static Set<String> LANGUAGES = new HashSet<>(asList("", "hu", "en"));
    private final static String DEFAULT_LANGUAGE = "";

    private static final Map<String, Translator> instances = new HashMap<>();
    private static String language = DEFAULT_LANGUAGE;

    private ResourceBundle messages;
    private Translator(String namespace) {
        reset(namespace);
    }

    private void reset(String namespace) {
        Locale locale = new Locale(language);
        messages = ResourceBundle.getBundle(
            String.format("messages.%s", namespace),
            locale,
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)
        );
    }

    public String translate(String key) {
        return messages.getString(key);
    }

    public String translate(String key, Object... args) {
        return String.format(messages.getString(key), args);
    }

    public static Translator getInstance(String namespace) {
        namespace = namespace.toLowerCase();
        if(!instances.containsKey(namespace)) {
            instances.put(namespace, new Translator(namespace));
        }

        return instances.get(namespace);
    }

    public static void setLanguage(String newLanguage) {
        if(!LANGUAGES.contains(newLanguage)) {
            throw new IllegalStateException(String.format("Language '%s' is not supported", language));
        }
        language = newLanguage;

        for(Map.Entry<String, Translator> instance : instances.entrySet()) {
            instance.getValue().reset(instance.getKey());
        }
    }
}
