package ppke.itk.xplang.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppke.itk.xplang.util.VersionInfo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


class Program {
    private final static Logger log = LoggerFactory.getLogger("Root.UI");
    private final static VersionInfo version = new VersionInfo();

    private final Deque<Action> actions = new ArrayDeque<>();

    Program() {
        // empty ctor
    }

    /**
     * Run the program.
     *
     * Starting from given initial Action, it executes the action chain as it unfolds.  If an action has consequences,
     * those consequcnes are executed right after the action.
     *
     * @param initial the initial Action to be taken.
     */
    void run(Action initial) {
        log.info("XPLanG starting");
        log.info("OS: {}", System.getProperty("os.name"));
        log.info("Java: {}", System.getProperty("java.version"));
        log.info("Version: {}", version.describe());

        actions.addFirst(initial);

        while(!actions.isEmpty()) {
            Action action = actions.pollFirst();
            List<Action> consequences = action.execute();
            for(int i=consequences.size(); i-->0;) {
                actions.addFirst(consequences.get(i));
            }
        }
    }
}
