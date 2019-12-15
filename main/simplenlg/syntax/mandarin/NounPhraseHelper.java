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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simplenlg.features.*;
import simplenlg.framework.*;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise noun
 * phrases.
 * </p>
 * 
 * @author E. Reiter and D. Westwater, University of Aberdeen.
 * @version 4.0
 */
abstract class NounPhraseHelper {

    /** The localiser position for ordering premodifiers. */
    private static final int LOCALISER_POSITION = 1;

    /** The verb (phrase) position for ordering premodifiers. */
    private static final int VERB_POSITION = 2;

	/** The adjective position for ordering premodifiers. */
	private static final int ADJECTIVE_POSITION_DE = 3;

	private static final int NOUN_ASSOCIATION_POSITION = 3;

    private static final int ADJECTIVE_POSITION = 4;

    private static final int NONPREDICATE_POSITION = 5;

	/** The noun position for ordering premodifiers. */
	private static final int NOUN_POSITION = 6;

	/**
	 * The main method for realising noun phrases.
	 * 
	 * @param parent
	 *            the <code>SyntaxProcessor</code> that called this method.
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
		ListElement realisedElement = null;

		if (phrase != null
				&& !phrase.getFeatureAsBoolean(Feature.ELIDED)) {
			realisedElement = new ListElement();

			if (phrase.getFeatureAsBoolean(Feature.PRONOMINAL)) {
				realisedElement.addComponent(createPronoun(parent, phrase));
			} else {
				realiseSpecifier(phrase, parent, realisedElement);
				realisePreModifiers(phrase, parent, realisedElement);
				realiseHeadNoun(phrase, parent, realisedElement);
                realiseComplements(phrase, parent, realisedElement);
                realisePostModifiers(phrase, parent, realisedElement);
			}
		}

		return realisedElement;
	}

	/**
	 * Realises the head noun of the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseHeadNoun(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		NLGElement headElement = phrase.getHead();

		if (headElement != null) {
			headElement.setFeature(Feature.ELIDED, phrase
					.getFeature(Feature.ELIDED));
			headElement.setFeature(LexicalFeature.GENDER, phrase
					.getFeature(LexicalFeature.GENDER));
			headElement.setFeature(InternalFeature.ACRONYM, phrase
					.getFeature(InternalFeature.ACRONYM));
			headElement.setFeature(Feature.NUMBER, phrase
					.getFeature(Feature.NUMBER));
			headElement.setFeature(Feature.PERSON, phrase
					.getFeature(Feature.PERSON));
			headElement.setFeature(Feature.POSSESSIVE, phrase
					.getFeature(Feature.POSSESSIVE));
			headElement.setFeature(Feature.PASSIVE, phrase
					.getFeature(Feature.PASSIVE));
			NLGElement currentElement = parent.realise(headElement);
			currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					DiscourseFunction.SUBJECT);
			realisedElement.addComponent(currentElement);
		}
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
		if (phrase.getFeatureAsBoolean(Feature.ADJECTIVE_ORDERING)) {
			preModifiers = sortNPPreModifiers(preModifiers);
		}

		preModifiers = realisePostParticle(preModifiers);
		PhraseHelper.realiseList(parent, realisedElement, preModifiers,
				DiscourseFunction.PRE_MODIFIER);
	}

	private static List<NLGElement> realisePostParticle(List<NLGElement> preModifiers) {
        List<NLGElement> processedList = new ArrayList<NLGElement>();
        for (NLGElement eachElement: preModifiers) {
            processedList.add(eachElement);
            if (eachElement.getFeatureAsBoolean(Feature.ASSOCIATIVE)
                    && !eachElement.getFeatureAsBoolean(LexicalFeature.NO_DE)) {
                processedList.add(new StringElement("的"));
            }
        }
        return processedList;
    }

    private static void realisePostModifiers(PhraseElement phrase,
                                             SyntaxProcessor parent, ListElement realisedElement) {
	    List<NLGElement> postModifiers = phrase.getPostModifiers();
	    postModifiers = realisePreParticle(postModifiers);
        PhraseHelper.realiseList(parent, realisedElement, postModifiers, DiscourseFunction.POST_MODIFIER);
    }

    private static void realiseComplements(PhraseElement phrase,
                                             SyntaxProcessor parent, ListElement realisedElement) {
        List<NLGElement> complements = phrase.getFeatureAsElementList(InternalFeature.COMPLEMENTS);
        complements = realisePreParticle(complements);
        PhraseHelper.realiseList(parent, realisedElement, complements, DiscourseFunction.COMPLEMENT);
    }

    private static List<NLGElement> realisePreParticle(List<NLGElement> elements) {
        List<NLGElement> processedList = new ArrayList<NLGElement>();
        for (NLGElement eachElement: elements) {
            if (eachElement.getFeatureAsBoolean(Feature.PREASSOCIATIVE)
                    && !eachElement.getFeatureAsBoolean(LexicalFeature.NO_DE)) {
                processedList.add(new StringElement("的"));
            }
            processedList.add(eachElement);
        }
        return processedList;
    }

	/**
	 * Realises the specifier of the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseSpecifier(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		List<NLGElement> specifiers = phrase
				.getFeatureAsElementList(InternalFeature.SPECIFIERS);
		if (specifiers != null && !phrase.getFeatureAsBoolean(InternalFeature.RAISED)
				&& ! phrase.getFeatureAsBoolean(Feature.ELIDED)) {
			if (phrase.getFeatureAsBoolean(Feature.SPECIFIER_ORDERING)) {
				specifiers = sortNPSpecifiers(specifiers, phrase);
			}
			PhraseHelper.realiseList(parent, realisedElement, specifiers, DiscourseFunction.SPECIFIER);
		}
	}

    /**
     *
     * @param originalSpecifiers
     *            the original listing of the specifiers
     */
	private static List<NLGElement> sortNPSpecifiers(
	        List<NLGElement> originalSpecifiers, PhraseElement phraseElement){

	    List<NLGElement> orderedSpecifiers = null;
        NLGElement properName = null;
	    if (originalSpecifiers == null) {
	        orderedSpecifiers = originalSpecifiers;
        } else {
	        orderedSpecifiers = new ArrayList<NLGElement>();
	        Map<ElementCategory, NLGElement> specMap = new HashMap<ElementCategory, NLGElement>();
	        for (NLGElement specifier: originalSpecifiers) {
	            if (specifier.getFeatureAsBoolean(LexicalFeature.PROPER)) {
	                properName = specifier;
                } else {
                    specMap.put(specifier.getCategory(), specifier);
                }

            }



            if (specMap.containsKey(PhraseCategory.NOUN_PHRASE)
                    && specMap.get(PhraseCategory.NOUN_PHRASE).getFeatureAsBoolean(Feature.POSSESSIVE)) {
                orderedSpecifiers.add(specMap.get(PhraseCategory.NOUN_PHRASE));
            } else if (specMap.containsKey(LexicalCategory.PRONOUN)
                    && ((WordElement) specMap.get(LexicalCategory.PRONOUN)).getBaseForm().equals("多少")) {
                orderedSpecifiers.add(specMap.get(LexicalCategory.PRONOUN));
                specMap.remove(LexicalCategory.PRONOUN);
            } else if ((specMap.containsKey(LexicalCategory.PRONOUN)
                    || specMap.containsKey(LexicalCategory.DEMONSTRATIVE))
                    && (specMap.containsKey(LexicalCategory.NUMERAL)
                    || specMap.containsKey(LexicalCategory.DEMONSTRATIVE))
                    && (specMap.containsKey(LexicalCategory.CLASSIFIER))) {
                if (specMap.containsKey(PhraseCategory.NOUN_PHRASE)){
                    // Noun phrase as proper name
                    orderedSpecifiers.add(specMap.get(PhraseCategory.NOUN_PHRASE));
                } else if (properName != null) {
                    // proper name appears in the very beginning
                    if (NumberAgreement.PLURAL.equals(phraseElement.getFeature(Feature.NUMBER))) {
                        properName.setPlural(true);
                    }
                    orderedSpecifiers.add(properName);
                }
            } else if (properName != null) {
	            properName.setFeature(Feature.ELIDED, true);
                orderedSpecifiers.add(properName);
            }


            if (NumberAgreement.PLURAL.equals(phraseElement.getFeature(Feature.NUMBER))
                    && specMap.containsKey(LexicalCategory.PRONOUN)) {
                specMap.get(LexicalCategory.PRONOUN).setPlural(true);
	            orderedSpecifiers.add(specMap.get(LexicalCategory.PRONOUN));
            } else {
	            if (specMap.containsKey(LexicalCategory.CLASSIFIER) && specMap.containsKey(LexicalCategory.PRONOUN)
                        && (specMap.containsKey(LexicalCategory.DEMONSTRATIVE)
                        || specMap.containsKey(LexicalCategory.NUMERAL))){
	                orderedSpecifiers.add(specMap.get(LexicalCategory.PRONOUN));
                }
            }

            if (specMap.containsKey(LexicalCategory.CLASSIFIER) && specMap.containsKey(LexicalCategory.DEMONSTRATIVE)) {
	            orderedSpecifiers.add(specMap.get(LexicalCategory.DEMONSTRATIVE));
            }

            if (orderedSpecifiers.size() == 0 &&
                    (DiscourseFunction.SUBJECT.equals(phraseElement.getFeature(InternalFeature.DISCOURSE_FUNCTION))
                    || phraseElement.getFeatureAsBoolean(Feature.TOPIC))) {
	            if (specMap.containsKey(LexicalCategory.CLASSIFIER)) {
	                specMap.get(LexicalCategory.CLASSIFIER).setFeature(Feature.ELIDED, true);
	                orderedSpecifiers.add(specMap.get(LexicalCategory.CLASSIFIER));
                }

                if (specMap.containsKey(LexicalCategory.NUMERAL)) {
	                specMap.get(LexicalCategory.NUMERAL).setFeature(Feature.ELIDED, true);
                    orderedSpecifiers.add(specMap.get(LexicalCategory.NUMERAL));
                }

            } else if ((specMap.containsKey(LexicalCategory.DEMONSTRATIVE)
                    || specMap.containsKey(LexicalCategory.NUMERAL))
                    && specMap.containsKey(LexicalCategory.CLASSIFIER)) {
	            if (specMap.containsKey(LexicalCategory.NUMERAL)) {
	                orderedSpecifiers.add(specMap.get(LexicalCategory.NUMERAL));
	                phraseElement.getHead().setMEN(false);
                }
                orderedSpecifiers.add(specMap.get(LexicalCategory.CLASSIFIER));
            }

            if (specMap.containsKey(LexicalCategory.NOUN)
                    && specMap.get(LexicalCategory.NOUN).getFeatureAsBoolean(Feature.POSSESSIVE)) {
                orderedSpecifiers.add(specMap.get(LexicalCategory.NOUN));
            }

            // Just easy for debugging
            for (ElementCategory c: specMap.keySet()) {
	            if (!orderedSpecifiers.contains(specMap.get(c))) {
	                specMap.get(c).setFeature(Feature.ELIDED, true);
	                orderedSpecifiers.add(specMap.get(c));
                }
            }
        }

        return orderedSpecifiers;

    }

	/**
	 * Sort the list of premodifiers for this noun phrase using adjective
	 * ordering (ie, "big" comes before "red")
	 * 
	 * @param originalModifiers
	 *            the original listing of the premodifiers.
	 * @return the sorted <code>List</code> of premodifiers.
	 */
	private static List<NLGElement> sortNPPreModifiers(
			List<NLGElement> originalModifiers) {

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
		int position = LOCALISER_POSITION;

		if (modifier.isA(LexicalCategory.VERB)
                || modifier.isA(PhraseCategory.VERB_PHRASE) || modifier.isA(PhraseCategory.CLAUSE)) {
            position = VERB_POSITION;
        } else if (modifier.isA(LexicalCategory.ADJECTIVE)
                || modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
		    if (!modifier.getFeatureAsBoolean(LexicalFeature.NO_DE)
                    && !modifier.getFeatureAsBoolean(LexicalFeature.NONPREDICATE)) {
		        position = ADJECTIVE_POSITION_DE;
            } else if (!modifier.getFeatureAsBoolean(LexicalFeature.NONPREDICATE)) {
                position = ADJECTIVE_POSITION;
            } else {
		        position = NONPREDICATE_POSITION;
            }
        } else if (modifier.isA(LexicalCategory.NOUN)
				|| (modifier.isA(PhraseCategory.NOUN_PHRASE) && !modifier.getFeatureAsBoolean(InternalFeature.LOCATIVE))) {
			position = NOUN_POSITION;

			if (modifier.getFeatureAsBoolean(Feature.ASSOCIATIVE)) {
			    position = NOUN_ASSOCIATION_POSITION;
            }
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
		int position = NOUN_POSITION;

		if ((modifier.isA(LexicalCategory.NOUN) || modifier.isA(PhraseCategory.NOUN_PHRASE))
                && modifier.getFeatureAsBoolean(Feature.ASSOCIATIVE)) {
            position = NOUN_ASSOCIATION_POSITION;
        }

		if (modifier.isA(LexicalCategory.ADJECTIVE)
                || modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
		    if (modifier.getFeatureAsBoolean(LexicalFeature.NONPREDICATE)) {
		        position = NONPREDICATE_POSITION;
            } else if (modifier.getFeatureAsBoolean(LexicalFeature.NO_DE)
                    || !modifier.getFeatureAsBoolean(Feature.ASSOCIATIVE)) {
                position = ADJECTIVE_POSITION;
            } else {
                position = ADJECTIVE_POSITION_DE;
            }
        } else if (modifier.isA(LexicalCategory.VERB)
                || modifier.isA(PhraseCategory.VERB_PHRASE) || modifier.isA(PhraseCategory.CLAUSE)) {
            position = VERB_POSITION;
        } else if (modifier.isA(PhraseCategory.NOUN_PHRASE) && modifier.getFeatureAsBoolean(InternalFeature.LOCATIVE)) {
            position = LOCALISER_POSITION;
        }

        return position;
	}

	/**
	 * Retrieves the correct representation of the word from the element. This
	 * method will find the <code>WordElement</code>, if it exists, for the
	 * given phrase or inflected word.
	 * 
	 * @param element
	 *            the <code>NLGElement</code> from which the head is required.
	 * @return the <code>WordElement</code>
	 */
	private static WordElement getHeadWordElement(NLGElement element) {
		WordElement head = null;

		if (element instanceof WordElement)
			head = (WordElement) element;
		else if (element instanceof InflectedWordElement) {
			head = (WordElement) element.getFeature(InternalFeature.BASE_WORD);
		} else if (element instanceof PhraseElement) {
			head = getHeadWordElement(((PhraseElement) element).getHead());
		}
				
		return head;
	}

	/**
	 * Creates the appropriate pronoun if the subject of the noun phrase is
	 * pronominal.
     * Adaptation for Chinese has been done
     * Changes:
     *  1. add chinese words
     *  2. add supporting for the mixed gender cases
	 * 
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the <code>NLGElement</code> representing the pronominal.
	 */
	private static NLGElement createPronoun(SyntaxProcessor parent,
			PhraseElement phrase) {

		String pronoun = "它";
		NLGFactory phraseFactory = phrase.getFactory();
		Object personValue = phrase.getFeature(Feature.PERSON);
		Boolean mixedFlag = false;

		if (Person.FIRST.equals(personValue)) {
			pronoun = "我"; //$NON-NLS-1$
		} else if (Person.SECOND.equals(personValue)) {
			pronoun = "你"; //$NON-NLS-1$
		} else {
			Object genderValue = phrase.getFeature(LexicalFeature.GENDER);
			if (Gender.FEMININE.equals(genderValue)) {
				pronoun = "她"; //$NON-NLS-1$
			} else if (Gender.MASCULINE.equals(genderValue)) {
				pronoun = "他"; //$NON-NLS-1$
			} else if (Gender.MIXED.equals(genderValue)) {
			    pronoun = "他们";
			    mixedFlag = true;
            }
		}
		NLGElement element;
		NLGElement proElement = phraseFactory.createWord(pronoun,
				LexicalCategory.PRONOUN);
		
		if (proElement instanceof WordElement) {
			element = new InflectedWordElement((WordElement) proElement);
			if(mixedFlag) {
                element.setFeature(LexicalFeature.GENDER, Gender.MIXED);
            } else {
                element.setFeature(LexicalFeature.GENDER, ((WordElement) proElement).getFeature(LexicalFeature.GENDER));
            }
			element.setFeature(Feature.PERSON, ((WordElement) proElement).getFeature(Feature.PERSON));	
		} else {
			element = proElement;
		}
		
		element.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.SPECIFIER);
		element.setFeature(Feature.POSSESSIVE, phrase
				.getFeature(Feature.POSSESSIVE));
		element
				.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));

		
		if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
			element.setFeature(InternalFeature.DISCOURSE_FUNCTION, phrase
					.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		}		

		return element;
	}
}
