package simplenlg.syntax.mandarin;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.*;

import java.util.ArrayList;
import java.util.List;

abstract class AdjectivePhraseHelper {

    private static final int PP_POSITION = 1;

    private static final int ADVERB_POSITION = 2;

    static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
        ListElement realisedElement = null;

        if (phrase != null
                && !phrase.getFeatureAsBoolean(Feature.ELIDED)) {
            realisedElement = new ListElement();

            realiseSpecifier(phrase, parent, realisedElement);
            realiseHeadAdjective(phrase, parent, realisedElement);
        }

        return realisedElement;
    }

    private static void realiseSpecifier(PhraseElement phrase,
                                         SyntaxProcessor parent, ListElement realisedElement) {
        List<NLGElement> preModifiers = phrase.getPreModifiers();
        preModifiers = sortAdjPPreModifiers(preModifiers);
        PhraseHelper.realiseList(parent, realisedElement, preModifiers,
                DiscourseFunction.PRE_MODIFIER);
    }

    private static List<NLGElement> sortAdjPPreModifiers(List<NLGElement> originalModifiers) {
        List<NLGElement> orderedModifiers = null;

        if (originalModifiers == null || originalModifiers.size() <= 1) {
            orderedModifiers = originalModifiers;
        } else {
            orderedModifiers = new ArrayList<NLGElement>(originalModifiers);
            boolean changesMade = false;
            do {
                changesMade = false;
                for (int i = 0; i < orderedModifiers.size() - 1; i++) {
                    if (getMinPos(orderedModifiers.get(i)) > getMaxPos(orderedModifiers
                            .get(i + 1))) {
                        NLGElement temp = orderedModifiers.get(i);
                        orderedModifiers.set(i, orderedModifiers.get(i + 1));
                        orderedModifiers.set(i + 1, temp);
                        changesMade = true;
                    }
                }
            } while (changesMade);
        }
        return orderedModifiers;
    }

    /**
     * Determines the minimim position at which this modifier can occur.
     * plural
     * @param modifier
     *            the modifier to be checked.
     * @return the minimum position for this modifier.
     */
    private static int getMinPos(NLGElement modifier) {
        int position = PP_POSITION;

        if (modifier.isA(LexicalCategory.ADVERB)
                || modifier.isA(PhraseCategory.ADVERB_PHRASE)) {
                position = ADVERB_POSITION;
        }

        return position;
    }

    /**
     * Determines the maximim position at which this modifier can occur.
     *
     * @param modifier
     *            the modifier to be checked.
     * @return the maximum position for this modifier.
     */
    private static int getMaxPos(NLGElement modifier) {
        int position = ADVERB_POSITION;

        if (modifier.isA(LexicalCategory.PREPOSITION)
                || modifier.isA(PhraseCategory.PREPOSITIONAL_PHRASE)) {
            position = PP_POSITION;
        }

        return position;
    }

    private static void realiseHeadAdjective(PhraseElement phrase,
                                             SyntaxProcessor parent, ListElement realisedElement) {
        NLGElement headElement = phrase.getHead();

        if (headElement != null) {
            headElement.setFeature(Feature.ELIDED, phrase
                    .getFeature(Feature.ELIDED));
            NLGElement currentElement = parent.realise(headElement);
            realisedElement.addComponent(currentElement);
        }
    }

}
