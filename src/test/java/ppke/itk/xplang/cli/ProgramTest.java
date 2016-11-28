package ppke.itk.xplang.cli;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

public class ProgramTest {
    private static class TestAction implements Action {
        private boolean executed = false;
        private final List<Action> consequences;

        TestAction() {
            this.consequences = emptyList();
        }

        TestAction(Action... actions) {
            this.consequences = Arrays.asList(actions);
        }

        @Override
        public List<Action> execute() {
            executed = true;
            return consequences;
        }

        boolean isExecuted() {
            return executed;
        }
    }

    @Test
    public void executesInitialAction() {
        TestAction act = new TestAction();

        Program program = new Program();
        program.run(act);

        assertTrue("Program should execute the initial action", act.isExecuted());
    }

    @Test
    public void shouldExecuteAllConsequences() {
        TestAction c1 = new TestAction();
        TestAction c2 = new TestAction();
        TestAction root = new TestAction(c1, c2);

        Program program = new Program();
        program.run(root);

        assertTrue("Root should be executed", root.isExecuted());
        assertTrue("c1 should be executed", c1.isExecuted());
        assertTrue("c2 should be executed", c2.isExecuted());
    }

    @Test
    public void shouldExecuteConsequenceOfConsequence() {
        TestAction c2 = new TestAction();
        TestAction c1 = new TestAction(c2);
        TestAction root = new TestAction(c1);

        Program program = new Program();
        program.run(root);

        assertTrue("Root should be executed", root.isExecuted());
        assertTrue("c1 should be executed", c1.isExecuted());
        assertTrue("c2 should be executed", c2.isExecuted());
    }

    private static class OrderedAction implements Action {
        private final String name;
        private final List<Action> consequences;
        private final List<Object> executionOrder;

        OrderedAction(String name, List<Object> executionOrder) {
            this.name = name;
            consequences = new ArrayList<>();
            this.executionOrder = executionOrder;
        }

        OrderedAction(String name, List<Object> executionOrder, Action... actions) {
            this.name = name;
            this.consequences = Arrays.asList(actions);
            this.executionOrder = executionOrder;
        }

        @Override
        public List<Action> execute() {
            executionOrder.add(this);
            return consequences;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    public void consequencesShouldBeExecutedInOrder() {
        List<Object> executionOrderMap = new ArrayList<>();

        OrderedAction d1 = new OrderedAction("d1", executionOrderMap);
        OrderedAction d2 = new OrderedAction("d2", executionOrderMap);
        OrderedAction c1 = new OrderedAction("c1", executionOrderMap, d1, d2);
        OrderedAction c2 = new OrderedAction("c2", executionOrderMap);
        OrderedAction root = new OrderedAction("root", executionOrderMap, c1, c2);

        Program program = new Program();
        program.run(root);

        assertEquals(Arrays.asList(root, c1, d1, d2, c2), executionOrderMap);
    }
}
