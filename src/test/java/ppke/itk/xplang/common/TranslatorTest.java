package ppke.itk.xplang.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TranslatorTest {
    @Test public void translatorShouldParametrizeMessage() {
        Translator.setLanguage("");
        Translator translator = Translator.getInstance("test");
        assertEquals("TEST 1", translator.translate("parser.parametrized.message", 1));
    }

    @Test public void translatorShouldFallBackToDefault() {
        Translator.setLanguage("hu");
        Translator translator = Translator.getInstance("test");

        assertEquals("TEST_FALLBACK", translator.translate("parser.missing.message"));
    }

    @Test public void switchingLanguagesShouldChangeInstances() {
        Translator.setLanguage("");
        Translator translator = Translator.getInstance("test");

        Translator.setLanguage("hu");
        assertEquals("DIFO_HU", translator.translate("parser.simple.message"));
    }
}
