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
package simplenlg.morphology.mandarin;

import simplenlg.features.*;
import simplenlg.framework.*;

/**
 * <p>
 * This abstract class contains a number of rules for doing simple inflection.
 * </p>
 *
 * <p>
 * As a matter of course, the processor will first use any user-defined
 * inflection for the world. If no inflection is provided then the lexicon, if
 * it exists, will be examined for the correct inflection. Failing this a set of
 * very basic rules will be examined to inflect the word.
 * </p>
 *
 * <p>
 * All processing modules perform realisation on a tree of
 * <code>NLGElement</code>s. The modules can alter the tree in whichever way
 * they wish. For example, the syntax processor replaces phrase elements with
 * list elements consisting of inflected words while the morphology processor
 * replaces inflected words with string elements.
 * </p>
 *
 * <p>
 * <b>N.B.</b> the use of <em>module</em>, <em>processing module</em> and
 * <em>processor</em> is interchangeable. They all mean an instance of this
 * class.
 * </p>
 *
 *
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0 16-Mar-2011 modified to use correct base form (ER)
 */
public abstract class MorphologyRules extends NLGModule {

	/**
	 * A triple array of Pronouns organised by singular/plural,
	 * possessive/reflexive/subjective/objective and by gender/person.
	 */
    private static final String[][][] PRONOUNS = {{{"我", "你", "他", "她", "它"},
            {"我 自己", "你 自己", "他 自己", "她 自己", "它 自己"},
            {"我 的", "你 的", "他 的", "她 的", "它 的"}},
            {{"我们", "你们", "他们", "她们", "它们"},
                    {"我们 自己",
                            "你们 自己",
                            "他们 自己",
                            "她们 自己",
                            "它们 自己"},
                    {"我们 的", "你们 的", "他们 的", "她们 的", "它们 的"}}};
    /**
     * A list of Wh-pronouns in Chinese, note that this list only contains a limited number of Chinese wh-pronouns
     */
	private static final String[] WH_PRONOUNS = {"谁", "什么", "哪个", "哪里", "为什么", "怎么", "多少", "什么 时候"};

	/**
	 * This method performs the morphology for nouns.
	 *
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	protected static StringElement doNounMorphology(InflectedWordElement element, WordElement baseWord) {
		StringBuffer realised = new StringBuffer();

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);
		if(element.isPlural() && element.getFeatureAsBoolean(LexicalFeature.MEN)) {

			String pluralForm = buildRegularPluralNoun(baseForm);
			realised.append(pluralForm);

		} else {
			realised.append(baseForm);
		}

		checkPossessive(element, realised);
		StringElement realisedElement = new StringElement(realised.toString());
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
		                           element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * Checks to see if the noun is possessive. Add a "的" after the word.
	 * Adaptation for Chinese has been done.
	 *
	 * @param element
	 *            the <code>InflectedWordElement</code>
	 * @param realised
	 *            the realisation of the word.
	 */
	private static void checkPossessive(InflectedWordElement element, StringBuffer realised) {

		if(element.getFeatureAsBoolean(Feature.POSSESSIVE)) {
			realised.append(" 的"); //$NON-NLS-1$
		}
	}

	/**
	 * Builds a plural for regular nouns. The rules are performed in this order:
	 * <ul>
	 * <li>For nouns ending <em>-Cy</em>, where C is any consonant, the ending
	 * becomes <em>-ies</em>. For example, <em>fly</em> becomes <em>flies</em>.</li>
	 * <li>For nouns ending <em>-ch</em>, <em>-s</em>, <em>-sh</em>, <em>-x</em>
	 * or <em>-z</em> the ending becomes <em>-es</em>. For example, <em>box</em>
	 * becomes <em>boxes</em>.</li>
	 * <li>All other nouns have <em>-s</em> appended the other end. For example,
	 * <em>dog</em> becomes <em>dogs</em>.</li>
	 * </ul>
	 *
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildRegularPluralNoun(String baseForm) {
		String plural = null;
		if(baseForm != null) {
            plural = baseForm + "们"; //$NON-NLS-1$
		}
		return plural;
	}

	/**
	 * This method performs the morphology for verbs.
	 *
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	protected static NLGElement doVerbMorphology(InflectedWordElement element, WordElement baseWord) {

		String realised = null;

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

		realised = baseForm;


		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
		                           element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * return the base form of a word
	 *
	 * @param element
	 * @param baseWord
	 * @return
	 */
	private static String getBaseForm(InflectedWordElement element, WordElement baseWord) {
		if(element.getBaseForm() != null)
			return element.getBaseForm();
		else if(baseWord == null)
			return null;
		else
			return baseWord.getBaseForm();
	}


	/**
	 * Builds the third-person singular form for regular verbs. The rules are
	 * performed in this order:
	 * <ul>
	 * <li>If the verb is <em>be</em> the realised form is <em>is</em>.</li>
	 * <li>For verbs ending <em>-ch</em>, <em>-s</em>, <em>-sh</em>, <em>-x</em>
	 * or <em>-z</em> the ending becomes <em>-es</em>. For example,
	 * <em>preach</em> becomes <em>preaches</em>.</li>
	 * <li>For verbs ending <em>-y</em> the ending becomes <em>-ies</em>. For
	 * example, <em>fly</em> becomes <em>flies</em>.</li>
	 * <li>For every other verb, <em>-s</em> is added to the end of the word.</li>
	 * </ul>
	 *
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildPresent3SVerb(String baseForm) {
		String morphology = null;
		if(baseForm != null) {
			if(baseForm.equalsIgnoreCase("be")) { //$NON-NLS-1$
				morphology = "is"; //$NON-NLS-1$
			} else if(baseForm.matches(".*[szx(ch)(sh)]\\b")) { //$NON-NLS-1$
				morphology = baseForm + "es"; //$NON-NLS-1$
			} else if(baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
				morphology = baseForm.replaceAll("y\\b", "ies"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				morphology = baseForm + "s"; //$NON-NLS-1$
			}
		}
		return morphology;
	}

	/**
	 * This method performs the morphology for adjectives.
	 *
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public static NLGElement doAdjectiveMorphology(InflectedWordElement element, WordElement baseWord) {

		String realised = null;

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

//		if (element.getFeatureAsBoolean(Feature.IS_COMPARATIVE)) {
//			realised = element.getFeatureAsString(LexicalFeature.COMPARATIVE);
//
//			if (realised == null && baseWord != null) {
//				realised = baseWord.getFeatureAsString(LexicalFeature.COMPARATIVE);
//			}
//			if (realised == null) {
//				if (Inflection.REGULAR_DOUBLE.equals(patternValue)) {
//					realised = buildDoubleCompAdjective(baseForm);
//				} else {
//					realised = buildRegularComparative(baseForm);
//				}
//			}
//		} else if (element.getFeatureAsBoolean(Feature.IS_SUPERLATIVE)) {
//
//			realised = element.getFeatureAsString(LexicalFeature.SUPERLATIVE);
//
//			if (realised == null && baseWord != null) {
//				realised = baseWord.getFeatureAsString(LexicalFeature.SUPERLATIVE);
//			}
//			if (realised == null) {
//				if (Inflection.REGULAR_DOUBLE.equals(patternValue)) {
//					realised = buildDoubleSuperAdjective(baseForm);
//				} else {
//					realised = buildRegularSuperlative(baseForm);
//				}
//			}
//		} else {
			realised = baseForm;
//		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
		                           element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}


//	/**
//	 * Builds the comparative form for regular adjectives. The rules are
//	 * performed in this order:
//	 * <ul>
//	 * <li>For adjectives ending <em>-Cy</em>, where C is any consonant, the
//	 * ending becomes <em>-ier</em>. For example, <em>brainy</em> becomes
//	 * <em>brainier</em>.</li>
//	 * <li>For adjectives ending <em>-e</em> the ending becomes <em>-er</em>.
//	 * For example, <em>fine</em> becomes <em>finer</em>.</li>
//	 * <li>For all other adjectives, <em>-er</em> is added to the end. For
//	 * example, <em>clear</em> becomes <em>clearer</em>.</li>
//	 * </ul>
//	 *
//	 * @param baseForm
//	 *            the base form of the word.
//	 * @return the inflected word.
//	 */
//	private static String buildRegularComparative(String baseForm) {
//		String morphology = null;
//		if(baseForm != null) {
//			if(baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
//				morphology = baseForm.replaceAll("y\\b", "ier"); //$NON-NLS-1$ //$NON-NLS-2$
//			} else if(baseForm.endsWith("e")) { //$NON-NLS-1$
//				morphology = baseForm + "r"; //$NON-NLS-1$
//			} else {
//				morphology = baseForm + "er"; //$NON-NLS-1$
//			}
//		}
//		return morphology;
//	}

//	/**
//	 * Builds the superlative form for regular adjectives. The rules are
//	 * performed in this order:
//	 * <ul>
//	 * <li>For verbs ending <em>-Cy</em>, where C is any consonant, the ending
//	 * becomes <em>-iest</em>. For example, <em>brainy</em> becomes
//	 * <em>brainiest</em>.</li>
//	 * <li>For verbs ending <em>-e</em> the ending becomes <em>-est</em>. For
//	 * example, <em>fine</em> becomes <em>finest</em>.</li>
//	 * <li>For all other verbs, <em>-est</em> is added to the end. For example,
//	 * <em>clear</em> becomes <em>clearest</em>.</li>
//	 * </ul>
//	 *
//	 * @param baseForm
//	 *            the base form of the word.
//	 * @return the inflected word.
//	 */
//	private static String buildRegularSuperlative(String baseForm) {
//		String morphology = null;
//		if(baseForm != null) {
//			if(baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
//				morphology = baseForm.replaceAll("y\\b", "iest"); //$NON-NLS-1$ //$NON-NLS-2$
//			} else if(baseForm.endsWith("e")) { //$NON-NLS-1$
//				morphology = baseForm + "st"; //$NON-NLS-1$
//			} else {
//				morphology = baseForm + "est"; //$NON-NLS-1$
//			}
//		}
//		return morphology;
//	}

	/**
	 * This method performs the morphology for adverbs.
	 *
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public static NLGElement doAdverbMorphology(InflectedWordElement element, WordElement baseWord) {

		String realised = null;

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);
//
//		if(element.getFeatureAsBoolean(Feature.IS_COMPARATIVE).booleanValue()) {
//			realised = element.getFeatureAsString(LexicalFeature.COMPARATIVE);
//
//			if(realised == null && baseWord != null) {
//				realised = baseWord.getFeatureAsString(LexicalFeature.COMPARATIVE);
//			}
//			if(realised == null) {
//				realised = buildRegularComparative(baseForm);
//			}
//		} else if(element.getFeatureAsBoolean(Feature.IS_SUPERLATIVE).booleanValue()) {
//
//			realised = element.getFeatureAsString(LexicalFeature.SUPERLATIVE);
//
//			if(realised == null && baseWord != null) {
//				realised = baseWord.getFeatureAsString(LexicalFeature.SUPERLATIVE);
//			}
//			if(realised == null) {
//				realised = buildRegularSuperlative(baseForm);
//			}
//		} else {
			realised = baseForm;
//		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
		                           element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * This method performs the morphology for pronouns.
	 * Adpatation for Chinese has been done!
     *
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public static NLGElement doPronounMorphology(InflectedWordElement element) {
		String realised = null;

		if(!element.getFeatureAsBoolean(InternalFeature.NON_MORPH) && !isWHPronoun(element)) {
			Object genderValue = element.getFeature(LexicalFeature.GENDER);
			Object personValue = element.getFeature(Feature.PERSON);
			Object discourseValue = element.getFeature(InternalFeature.DISCOURSE_FUNCTION);

			int numberIndex = element.isPlural() ? 1 : 0;
			int genderIndex = (genderValue instanceof Gender) ? ((Gender) genderValue).ordinal() : 2;
			int personIndex = (personValue instanceof Person) ? ((Person) personValue).ordinal() : 2;

			if(genderIndex == 3) {
			    genderIndex = 0;
            }

			if(personIndex == 2) {
				personIndex += genderIndex;
			}

			int positionIndex = 0;

			if(element.getFeatureAsBoolean(LexicalFeature.REFLEXIVE)) {
				positionIndex = 1;
			} else if(element.getFeatureAsBoolean(Feature.POSSESSIVE)) {
				positionIndex = 2;
			}
			realised = PRONOUNS[numberIndex][positionIndex][personIndex];
		} else {
			realised = element.getBaseForm();
		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
		                           element.getFeature(InternalFeature.DISCOURSE_FUNCTION));

		return realisedElement;
	}

	private static boolean isWHPronoun(InflectedWordElement word) {
		String base = word.getBaseForm();
		boolean wh = false;

		if(base != null) {
			for(int i = 0; i < WH_PRONOUNS.length && !wh; i++) {
				wh = WH_PRONOUNS[i].equals(base);
			}
		}

		return wh;

	}

	/**
	 * This method performs the morphology for determiners.
     * 所有 的 应该出现在这个地方
	 *
	 * @param determiner
	 *            the <code>InflectedWordElement</code>.
	 * @param realisation
	 *            the current realisation of the determiner.
	 */
	public static void doDeterminerMorphology(NLGElement determiner, String realisation) {

		if(realisation != null) {

			if(!(determiner.getRealisation().equals("a"))) {
				if(determiner.isPlural()) {
					// Use default inflection rules:
					if("that".equals(determiner.getRealisation())) {
						determiner.setRealisation("those");
					} else if("this".equals(determiner.getRealisation())) {
						determiner.setRealisation("these");
					}
				} else if(!determiner.isPlural()) {
					// Use default push back to base form rules:
					if("those".equals(determiner.getRealisation())) {
						determiner.setRealisation("that");
					} else if("these".equals(determiner.getRealisation())) {
						determiner.setRealisation("this");
					}

				}
			}

			// Special "a" determiner and perform a/an agreement:
			if(determiner.getRealisation().equals("a")) { //$NON-NLS-1$
				if(determiner.isPlural()) {
					determiner.setRealisation("some");
				} else if(DeterminerAgrHelper.requiresAn(realisation)) {
					determiner.setRealisation("an");
				}
			}

		}
	}
}
