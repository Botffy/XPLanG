package ppke.itk.xplang.cli;

import java.util.List;


/**
 * A single action taken by the program.
 */
interface Action {
    /**
     * Do this action.
     *
     * @return The consequences of the action: further actions to be done.
     */
    List<Action> execute();
}
