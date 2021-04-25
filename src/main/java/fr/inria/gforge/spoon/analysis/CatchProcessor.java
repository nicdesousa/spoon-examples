package fr.inria.gforge.spoon.analysis;


import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCatch;
import spoon.support.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Reports warnings when empty catch blocks are found.
 */
public class CatchProcessor extends AbstractProcessor<CtCatch> {
    public final List<CtCatch> emptyCatchs = new ArrayList<>();

    @Override
    public boolean isToBeProcessed(final CtCatch candidate) {
        return candidate.getBody().getStatements().isEmpty();
    }

    @Override
    public void process(final CtCatch element) {
        getEnvironment().report(this, Level.WARN, element, "empty catch clause " + element.getPosition().toString());
        emptyCatchs.add(element);
    }
}