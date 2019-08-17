package ppke.itk.xplang.gui;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.junit.Assert.*;


public class EditorTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void fileLoading() throws Exception {
        String text = "hello wörld";
        File toLoad = testFolder.newFile("test.prog");
        FileUtils.writeStringToFile(toLoad, text, StandardCharsets.UTF_8);

        StateChangeListener listener = new StateChangeListener();
        Editor editor = new Editor(listener::onStateChange);
        editor.loadFile(toLoad);
        assertTrue(listener.stateChangeHappened);
        assertFalse(editor.isDirty());
        assertThat(editor.getLoadedFile(), isPresent());
        assertEquals(toLoad, editor.getLoadedFile().get());
        assertEquals(text, editor.getText());
    }

    @Test
    public void fileSaving() throws Exception {
        File toLoad = testFolder.newFile("test.prog");
        File toSave = testFolder.newFile("saved.prog");
        FileUtils.writeStringToFile(toLoad, "hello wörld", StandardCharsets.UTF_8);

        Editor editor = new Editor(x -> {});
        editor.loadFile(toLoad);
        editor.saveTo(toSave);
        assertFalse(editor.isDirty());
        assertThat(editor.getLoadedFile(), isPresent());
        assertEquals(toSave, editor.getLoadedFile().get());
        assertTrue("The editor should ensure the text ends with a newline", editor.getText().endsWith("\n"));
        assertEquals("hello wörld\n", IOUtils.toString(new FileInputStream(toSave), StandardCharsets.UTF_8));
    }

    private static class StateChangeListener {
        boolean stateChangeHappened = false;

        public void onStateChange(Editor editor) {
            stateChangeHappened = true;
        }
    }
}
