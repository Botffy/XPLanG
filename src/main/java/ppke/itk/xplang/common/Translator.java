package ppke.itk.xplang.common;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * Localising messages using this big bad static singleton.
 *
 * A primitive thing, Translator looks for {@link ResourceBundle}s in the {@code /messages} folder in the classpath.
 * It holds a translator instance for every such ResourceBundle. Get an instance using the "almost fully qualified name
 * of the ResourceBundle (just omit the "/messages" bit.
 *
 * The translator interpolates variables in strings using the {@link String#format(String, Object...)} method. Why don't
 * we use {@link java.text.MessageFormat}? Because this is a compiler, and locale-specific formatting of dates, times
 * and numbers would be counterproductive. MAYBE we could get a MessageFormat-based implementation going, but YAGNI.
 */
public final class Translator {
    private final static Set<String> LANGUAGES = new HashSet<>(asList("", "hu", "en"));
    private final static String DEFAULT_LANGUAGE = "";

    private static Map<String, Translator> instances = new HashMap<>();
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

    /**
     * Get the localised message for a given identifier.
     */
    public String translate(String key) {
        return messages.getString(key);
    }

    /**
     * Get the localised message for a given identifier, and format it with {@link String#format(String, Object...)}.
     */
    public String translate(String key, Object... args) {
        return String.format(messages.getString(key), args);
    }

    /**
     * Get the translator instance associated with the given namespace.
     * @param namespace A string describing a "namespace", which is really just the almost fully qualified name of the
     *                  {@link ResourceBundle} the translator instance is going to use.
     */
    public static Translator getInstance(String namespace) {
        namespace = namespace.toLowerCase(Locale.ENGLISH);
        if(!instances.containsKey(namespace)) {
            instances.put(namespace, new Translator(namespace));
        }

        return instances.get(namespace);
    }

    /**
     * Set the language of the application's messages.
     * @param newLanguage The two or three letter code of the desired language (see {@link Locale#Locale(String)}, or
     *                    an empty string for the default language.
     * @throws IllegalStateException if the language is not supported.
     */
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
