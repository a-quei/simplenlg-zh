/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is "Simplenlg".
 *
 * The Initial Developer of the Original Code is Ehud Reiter, Albert Gatt and Dave Westwater.
 * Portions created by Ehud Reiter, Albert Gatt and Dave Westwater are Copyright (C) 2010-11 The University of Aberdeen. All Rights Reserved.
 *
 * Contributor(s): Ehud Reiter, Albert Gatt, Dave Wewstwater, Roman Kutlak, Margaret Mitchell.
 */
package simplenlg.syntax.mandarin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import simplenlg.features.*;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise verb
 * phrases. It adds auxiliary verbs into the element tree as required.
 * </p>
 * 
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */
abstract class VerbPhraseHelper {

    private static String NEGATIVE_WORD = "不";

	/**
	 * The main method for realising verb phrases.
	 * 
	 * @param parent
	 *            the <code>SyntaxProcessor</code> that called this method.
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
		ListElement realisedElement = null;
		Stack<NLGElement> vgComponents = null;
		Stack<NLGElement> mainVerbRealisation = new Stack<NLGElement>();
		Stack<NLGElement> auxiliaryRealisation = new Stack<NLGElement>();

		if (phrase != null) {
			vgComponents = createVerbGroup(parent, phrase);
			Object particle = phrase.getFeature(Feature.PARTICLE);
			splitVerbGroup(vgComponents, mainVerbRealisation,
					auxiliaryRealisation, particle != null);


			realisedElement = new ListElement();

			if (!phrase.hasFeature(InternalFeature.REALISE_AUXILIARY)
					|| phrase.getFeatureAsBoolean(
							InternalFeature.REALISE_AUXILIARY)) {

                realiseAuxiliaries(parent, realisedElement,
                        auxiliaryRealisation);


                realisePreModifiers(phrase, parent, realisedElement);


                realiseMainVerb(parent, phrase, mainVerbRealisation,
                        realisedElement);

            } else {
                realisePreModifiers(phrase, parent, realisedElement);
				realiseMainVerb(parent, phrase, mainVerbRealisation,
						realisedElement);
			}
			realiseComplements(parent, phrase, realisedElement);
            realisePostModifiers(phrase, parent, realisedElement);
		}

		return realisedElement;
	}

	/**
	 * Realises the pre-modifiers of the noun phrase. Before being realised,
	 * pre-modifiers undergo some basic sorting based on adjective ordering.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realisePreModifiers(PhraseElement phrase,
											SyntaxProcessor parent, ListElement realisedElement) {

		List<NLGElement> preModifiers = phrase.getPreModifiers();

		preModifiers = realisePostPredicates(preModifiers);
		PhraseHelper.realiseList(parent, realisedElement, preModifiers,
				DiscourseFunction.PRE_MODIFIER);
	}

    private static List<NLGElement> realisePostPredicates(List<NLGElement> preModifiers) {
        List<NLGElement> processedList = new ArrayList<NLGElement>();
        for (NLGElement eachElement: preModifiers) {
            processedList.add(eachElement);
            if (eachElement.getFeatureAsBoolean(Feature.ASSOCIATIVE)
                    && !eachElement.getFeatureAsBoolean(LexicalFeature.NO_DE)) {
                processedList.add(new StringElement("地"));
            }
        }
        return processedList;
    }

    private static void realisePostModifiers(PhraseElement phrase,
                                             SyntaxProcessor parent, ListElement realisedElement) {
        List<NLGElement> postModifiers = phrase.getPostModifiers();
        postModifiers = realisePrePredicates(postModifiers);
        PhraseHelper.realiseList(parent, realisedElement, postModifiers, DiscourseFunction.POST_MODIFIER);
    }

    private static List<NLGElement> realisePrePredicates(List<NLGElement> elements) {
        List<NLGElement> processedList = new ArrayList<NLGElement>();
        for (NLGElement eachElement: elements) {
            if (eachElement.getFeatureAsBoolean(Feature.PREASSOCIATIVE)
                    && !eachElement.getFeatureAsBoolean(LexicalFeature.NO_DE)) {
                processedList.add(new StringElement("得"));
            }
            processedList.add(eachElement);
        }
        return processedList;
    }

	/**
	 * Realises the auxiliary verbs in the verb group.
	 * 
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 * @param auxiliaryRealisation
	 *            the stack of auxiliary verbs.
	 */
	private static void realiseAuxiliaries(SyntaxProcessor parent,
			ListElement realisedElement, Stack<NLGElement> auxiliaryRealisation) {

		NLGElement aux = null;
		NLGElement currentElement = null;

		while (!auxiliaryRealisation.isEmpty()) {
			aux = auxiliaryRealisation.pop();
			currentElement = parent.realise(aux);
			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.AUXILIARY);
			}
		}
	}

	/**
	 * Realises the main group of verbs in the phrase.
	 * 
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param mainVerbRealisation
	 *            the stack of the main verbs in the phrase.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseMainVerb(SyntaxProcessor parent,
			PhraseElement phrase, Stack<NLGElement> mainVerbRealisation,
			ListElement realisedElement) {

		NLGElement currentElement = null;
		NLGElement main = null;

		while (!mainVerbRealisation.isEmpty()) {
			main = mainVerbRealisation.pop();
			main.setFeature(Feature.INTERROGATIVE_TYPE, phrase
					.getFeature(Feature.INTERROGATIVE_TYPE));
			currentElement = parent.realise(main);

			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
			}
		}
	}

	/**
	 * Realises the complements of this phrase.
	 * 
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseComplements(SyntaxProcessor parent,
			PhraseElement phrase, ListElement realisedElement) {

		ListElement indirects = new ListElement();
		ListElement directs = new ListElement();
		ListElement unknowns = new ListElement();
		Object discourseValue = null;
		NLGElement currentElement = null;

		for (NLGElement complement : phrase
				.getFeatureAsElementList(InternalFeature.COMPLEMENTS)) {

			discourseValue = complement
					.getFeature(InternalFeature.DISCOURSE_FUNCTION);
			currentElement = parent.realise(complement);
			if (currentElement != null) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.COMPLEMENT);

				if (DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {
					indirects.addComponent(currentElement);
				} else if (DiscourseFunction.OBJECT.equals(discourseValue)) {
					directs.addComponent(currentElement);
				} else {
					unknowns.addComponent(currentElement);
				}
			}
		}
		realisedElement.addComponents(indirects.getChildren());

		if (!phrase.getFeatureAsBoolean(Feature.PASSIVE)
                && !phrase.getFeatureAsBoolean(Feature.BA)) {
            realisedElement.addComponents(directs.getChildren());
			realisedElement.addComponents(unknowns.getChildren());
		}
	}

	/**
	 * Splits the stack of verb components into two sections. One being the verb
	 * associated with the main verb group, the other being associated with the
	 * auxiliary verb group.
	 * 
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param mainVerbRealisation
	 *            the main group of verbs.
	 * @param auxiliaryRealisation
	 *            the auxiliary group of verbs.
	 */
	private static void splitVerbGroup(Stack<NLGElement> vgComponents,
			Stack<NLGElement> mainVerbRealisation,
			Stack<NLGElement> auxiliaryRealisation, Boolean hasParticle) {

		boolean mainVerbSeen = false;

		for (NLGElement word : vgComponents) {
			if (!mainVerbSeen) {
			    if (hasParticle) {
			        mainVerbRealisation.push(word);
			        continue;
                }
				mainVerbRealisation.push(word);
				if (!word.equals(NEGATIVE_WORD)) { //$NON-NLS-1$
					mainVerbSeen = true;
				}
			} else {
				auxiliaryRealisation.push(word);
			}
		}

	}

	/**
	 * Creates a stack of verbs for the verb phrase. Additional auxiliary verbs
	 * are added as required based on the features of the verb phrase.
	 * 
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the verb group as a <code>Stack</code> of <code>NLGElement</code>
	 *         s.
	 */
	static final private Stack<NLGElement> createVerbGroup(
			SyntaxProcessor parent, PhraseElement phrase) {

		String actualModal = null;
		Object formValue = phrase.getFeature(Feature.FORM);
		Tense tenseValue = (Tense) phrase.getFeature(Feature.TENSE);
		String modal = phrase.getFeatureAsString(Feature.MODAL);

		if (modal == null && phrase instanceof VPPhraseSpec && ((VPPhraseSpec) phrase).getObject() != null) {
		    modal = ((VPPhraseSpec) phrase).getObject().getFeatureAsString(Feature.MODAL);
        }

        boolean modalPast = false;
		Stack<NLGElement> vgComponents = new Stack<NLGElement>();
		boolean interrogative = phrase.hasFeature(Feature.INTERROGATIVE_TYPE);

		if (Form.GERUND.equals(formValue) || Form.INFINITIVE.equals(formValue)) {
			tenseValue = Tense.PRESENT;
		}

		if (Form.INFINITIVE.equals(formValue)) {
			actualModal = "to"; //$NON-NLS-1$

		} else if (formValue == null || Form.NORMAL.equals(formValue)) {
			if (Tense.FUTURE.equals(tenseValue)
					&& modal == null
					&& ((!(phrase.getHead() instanceof CoordinatedPhraseElement)) || (phrase
							.getHead() instanceof CoordinatedPhraseElement && interrogative))) {

				actualModal = "will"; //$NON-NLS-1$

			} else if (modal != null) {
				actualModal = modal;

				if (Tense.PAST.equals(tenseValue)) {
					modalPast = true;
				}
			}
		}

		pushParticles(phrase, parent, vgComponents);
		NLGElement frontVG = grabHeadVerb(phrase, tenseValue, modal != null);
		checkImperativeInfinitive(formValue, frontVG);


		if (phrase.getFeatureAsBoolean(Feature.PERFECT) || modalPast) {
			frontVG = addHave(frontVG, vgComponents, modal, tenseValue);
		}

		frontVG = pushIfModal(actualModal != null, phrase, frontVG,
				vgComponents);


        frontVG = createNot(phrase, vgComponents, frontVG, modal != null);

		if (frontVG != null) {
			pushFrontVerb(phrase, vgComponents, frontVG, formValue,
					interrogative);
		}

        if (!phrase.getFeatureAsBoolean(Feature.PASSIVE)
                && !phrase.getFeatureAsBoolean(Feature.BA)) {
            pushModal(actualModal, phrase, vgComponents);
        } else {
		    phrase.setFeature(Feature.MODAL, actualModal);
        }

		return vgComponents;
	}

	/**
	 * Pushes the modal onto the stack of verb components.
	 * 
	 * @param actualModal
	 *            the modal to be used.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	private static void pushModal(String actualModal, PhraseElement phrase,
			Stack<NLGElement> vgComponents) {
		if (actualModal != null
				&& !phrase.getFeatureAsBoolean(InternalFeature.IGNORE_MODAL)) {
			vgComponents.push(new InflectedWordElement(actualModal,
					LexicalCategory.MODAL));
		}
	}

	/**
	 * Pushes the front verb onto the stack of verb components.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param formValue
	 *            the <code>Form</code> of the phrase.
	 * @param interrogative
	 *            <code>true</code> if the phrase is interrogative.
	 */
	private static void pushFrontVerb(PhraseElement phrase,
			Stack<NLGElement> vgComponents, NLGElement frontVG,
			Object formValue, boolean interrogative) {
		Object interrogType = phrase.getFeature(Feature.INTERROGATIVE_TYPE);
		
		if (Form.GERUND.equals(formValue)) {
			frontVG.setFeature(Feature.FORM, Form.PRESENT_PARTICIPLE);
			vgComponents.push(frontVG);

		} else if (Form.PAST_PARTICIPLE.equals(formValue)) {
			frontVG.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			vgComponents.push(frontVG);

		} else if (Form.PRESENT_PARTICIPLE.equals(formValue)) {
			frontVG.setFeature(Feature.FORM, Form.PRESENT_PARTICIPLE);
			vgComponents.push(frontVG);

		} else if ((!(formValue == null || Form.NORMAL.equals(formValue)) || interrogative)
                && vgComponents.isEmpty()) {


			vgComponents.push(frontVG);

		} else {
			NumberAgreement numToUse = determineNumber(phrase.getParent(),
					phrase);
			frontVG.setFeature(Feature.TENSE, phrase.getFeature(Feature.TENSE));
			frontVG.setFeature(Feature.PERSON, phrase
					.getFeature(Feature.PERSON));
			frontVG.setFeature(Feature.NUMBER, numToUse);
			
			//don't push the front VG if it's a negated interrogative WH object question
			if (!(phrase.getFeatureAsBoolean(Feature.NEGATED) && (InterrogativeType.WHO_OBJECT
					.equals(interrogType) || InterrogativeType.WHAT_OBJECT
					.equals(interrogType)))) {
				vgComponents.push(frontVG);
			}
		}
	}

	/**
	 * Adds <em>not</em> to the stack if the phrase is negated.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param hasModal
	 *            the phrase has a modal
	 * @return the new element for the front of the group.
	 */
	private static NLGElement createNot(PhraseElement phrase,
			Stack<NLGElement> vgComponents, NLGElement frontVG, boolean hasModal) {
		NLGElement newFront = frontVG;

		if (phrase.getFeatureAsBoolean(Feature.NEGATED) || (phrase instanceof VPPhraseSpec
                && ((VPPhraseSpec) phrase).getObject() != null && ((VPPhraseSpec) phrase).getObject().getFeatureAsBoolean(Feature.NEGATED))) {

			String alterNegWord = phrase.getFeatureAsString(Feature.NEGATIVE_WORD);
			if ( alterNegWord != null) {
			    NEGATIVE_WORD = alterNegWord;
            }

			if (!vgComponents.empty()) {
                if (!phrase.getFeatureAsBoolean(Feature.PASSIVE)
                        && !phrase.getFeatureAsBoolean(Feature.BA)) {
				    vgComponents.push(new InflectedWordElement(
                            NEGATIVE_WORD, LexicalCategory.ADVERB));
                } else {
                    phrase.setFeature(Feature.NEGATIVE_WORD, NEGATIVE_WORD);
                }
			} else {
				if (frontVG != null && !hasModal) {
					frontVG.setFeature(Feature.NEGATED, true);
					vgComponents.push(frontVG);
				}

                if (!phrase.getFeatureAsBoolean(Feature.PASSIVE)
                        && !phrase.getFeatureAsBoolean(Feature.BA)) {
                    vgComponents.push(new InflectedWordElement(
                            NEGATIVE_WORD, LexicalCategory.ADVERB));
                } else {
                    phrase.setFeature(Feature.NEGATIVE_WORD, NEGATIVE_WORD);
                }

                newFront = null;
			}
		}

		return newFront;
	}

	/**
	 * Pushes the front verb on to the stack if the phrase has a modal.
	 * 
	 * @param hasModal
	 *            the phrase has a modal
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @return the new element for the front of the group.
	 */
	private static NLGElement pushIfModal(boolean hasModal,
			PhraseElement phrase, NLGElement frontVG,
			Stack<NLGElement> vgComponents) {

		NLGElement newFront = frontVG;
		if (hasModal
				&& !phrase.getFeatureAsBoolean(InternalFeature.IGNORE_MODAL)) {
			if (frontVG != null) {
				frontVG.setFeature(InternalFeature.NON_MORPH, true);
				vgComponents.push(frontVG);
			}
			newFront = null;
		}
		return newFront;
	}

	/**
	 * Adds <em>have</em> to the stack.
	 * 
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param modal
	 *            the modal to be used.
	 * @param tenseValue
	 *            the <code>Tense</code> of the phrase.
	 * @return the new element for the front of the group.
	 */
	private static NLGElement addHave(NLGElement frontVG,
			Stack<NLGElement> vgComponents, String modal, Tense tenseValue) {
		NLGElement newFront = frontVG;

		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			vgComponents.push(frontVG);
		}
		newFront = new InflectedWordElement("have", LexicalCategory.VERB); //$NON-NLS-1$
		newFront.setFeature(Feature.TENSE, tenseValue);
		if (modal != null) {
			newFront.setFeature(InternalFeature.NON_MORPH, true);
		}
		return newFront;
	}

	/**
	 * Checks to see if the phrase is in imperative, infinitive or bare
	 * infinitive form. If it is then no morphology is done on the main verb.
	 * 
	 * @param formValue
	 *            the <code>Form</code> of the phrase.
	 * @param frontVG
	 *            the first verb in the verb group.
	 */
	private static void checkImperativeInfinitive(Object formValue,
			NLGElement frontVG) {

		if ((Form.IMPERATIVE.equals(formValue)
				|| Form.INFINITIVE.equals(formValue) || Form.BARE_INFINITIVE
				.equals(formValue))
				&& frontVG != null) {
			frontVG.setFeature(InternalFeature.NON_MORPH, true);
		}
	}

	/**
	 * Grabs the head verb of the verb phrase and sets it to future tense if the
	 * phrase is future tense. It also turns off negation if the group has a
	 * modal.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param tenseValue
	 *            the <code>Tense</code> of the phrase.
	 * @param hasModal
	 *            <code>true</code> if the verb phrase has a modal.
	 * @return the modified head element
	 */
	private static NLGElement grabHeadVerb(PhraseElement phrase,
			Tense tenseValue, boolean hasModal) {
		NLGElement frontVG = phrase.getHead();

		if (frontVG != null) {
			if (frontVG instanceof WordElement) {
				frontVG = new InflectedWordElement((WordElement) frontVG);
			}

			// AG: tense value should always be set on frontVG
			if (tenseValue != null) {
				frontVG.setFeature(Feature.TENSE, tenseValue);
			}

			// if (Tense.FUTURE.equals(tenseValue) && frontVG != null) {
			// frontVG.setFeature(Feature.TENSE, Tense.FUTURE);
			// }

			if (hasModal) {
				frontVG.setFeature(Feature.NEGATED, false);
			}
		}

		return frontVG;
	}

	/**
	 * Pushes the particles of the main verb onto the verb group stack.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	private static void pushParticles(PhraseElement phrase,
			SyntaxProcessor parent, Stack<NLGElement> vgComponents) {
		Object particle = phrase.getFeature(Feature.PARTICLE);

		if (particle instanceof String) {
			vgComponents.push(new StringElement((String) particle));

		} else if (particle instanceof NLGElement) {
			vgComponents.push(parent.realise((NLGElement) particle));
		}
	}

	/**
	 * Determines the number agreement for the phrase ensuring that any number
	 * agreement on the parent element is inherited by the phrase.
	 * 
	 * @param parent
	 *            the parent element of the phrase.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the <code>NumberAgreement</code> to be used for the phrase.
	 */
	private static NumberAgreement determineNumber(NLGElement parent,
			PhraseElement phrase) {
		Object numberValue = phrase.getFeature(Feature.NUMBER);
		NumberAgreement number = null;
		if (numberValue != null && numberValue instanceof NumberAgreement) {
			number = (NumberAgreement) numberValue;
		} else {
			number = NumberAgreement.SINGULAR;
		}

		return number;
	}

	/**
	 * Checks to see if any of the complements to the phrase are plural.
	 * 
	 * @param complements
	 *            the list of complements of the phrase.
	 * @return <code>true</code> if any of the complements are plural.
	 */
	private static boolean hasPluralComplement(List<NLGElement> complements) {
		boolean plural = false;
		Iterator<NLGElement> complementIterator = complements.iterator();
		NLGElement eachComplement = null;
		Object numberValue = null;

		while (complementIterator.hasNext() && !plural) {
			eachComplement = complementIterator.next();

			if (eachComplement != null
					&& eachComplement.isA(PhraseCategory.NOUN_PHRASE)) {

				numberValue = eachComplement.getFeature(Feature.NUMBER);
				if (numberValue != null
						&& NumberAgreement.PLURAL.equals(numberValue)) {
					plural = true;
				}
			}
		}
		return plural;
	}
}
