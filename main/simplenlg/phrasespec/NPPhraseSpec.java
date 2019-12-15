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

package simplenlg.phrasespec;

import simplenlg.features.*;
import simplenlg.framework.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class defines a noun phrase. It is essentially a wrapper around the
 * <code>PhraseElement</code> class, with methods for setting common
 * constituents such as specifier. For example, the <code>setNoun</code> method
 * in this class sets the head of the element to be the specified noun
 * 
 * From an API perspective, this class is a simplified version of the
 * NPPhraseSpec class in simplenlg V3. It provides an alternative way for
 * creating syntactic structures, compared to directly manipulating a V4
 * <code>PhraseElement</code>.
 * 
 * Methods are provided for setting and getting the following constituents:
 * <UL>
 * <li>Specifier (eg, "the")
 * <LI>PreModifier (eg, "green")
 * <LI>Noun (eg, "apple")
 * <LI>PostModifier (eg, "in the shop")
 * </UL>
 * 
 * NOTE: The setModifier method will attempt to automatically determine whether
 * a modifier should be expressed as a PreModifier, or PostModifier
 * 
 * NOTE: Specifiers are currently pretty basic, this needs more development
 * 
 * Features (such as number) must be accessed via the <code>setFeature</code>
 * and <code>getFeature</code> methods (inherited from <code>NLGElement</code>).
 * Features which are often set on NPPhraseSpec include
 * <UL>
 * <LI>Number (eg, "the apple" vs "the apples")
 * <LI>Possessive (eg, "John" vs "John's")
 * <LI>Pronominal (eg, "the apple" vs "it")
 * </UL>
 * 
 * <code>NPPhraseSpec</code> are produced by the <code>createNounPhrase</code>
 * method of a <code>PhraseFactory</code>
 * </p>
 * @author E. Reiter, University of Aberdeen.
 * @version 4.1
 * 
 */
public class NPPhraseSpec extends PhraseElement {

	public NPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.NOUN_PHRASE);
		this.setFactory(phraseFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.framework.PhraseElement#setHead(java.lang.Object) This
	 * version sets NP default features from the head
	 */
	@Override
	public void setHead(Object newHead) {
		super.setHead(newHead);
		setNounPhraseFeatures(getFeatureAsElement(InternalFeature.HEAD));
	}

	/**
	 * A helper method to set the features required for noun phrases, from the
	 * head noun
     * Changes:
     *  1. add a new feature: specifier_ordering
	 * 
	 * @param nounElement
	 *            the element representing the noun.
	 */
	private void setNounPhraseFeatures(NLGElement nounElement) {
		if (nounElement == null)
			return;		if (nounElement == null)
			return;

		setFeature(Feature.POSSESSIVE, nounElement != null ? nounElement
				.getFeatureAsBoolean(Feature.POSSESSIVE) : Boolean.FALSE);
		setFeature(InternalFeature.RAISED, false);
		setFeature(InternalFeature.ACRONYM, false);

		if (nounElement != null && nounElement.hasFeature(Feature.NUMBER)) {

			setFeature(Feature.NUMBER, nounElement.getFeature(Feature.NUMBER));
		} else {
			setPlural(false);
		}
		if (nounElement != null && nounElement.hasFeature(Feature.PERSON)) {

			setFeature(Feature.PERSON, nounElement.getFeature(Feature.PERSON));
		} else {
			setFeature(Feature.PERSON, Person.THIRD);
		}
		if (nounElement != null
				&& nounElement.hasFeature(LexicalFeature.GENDER)) {

			setFeature(LexicalFeature.GENDER, nounElement
					.getFeature(LexicalFeature.GENDER));
		} else {
			setFeature(LexicalFeature.GENDER, Gender.NEUTER);
		}

		if (nounElement != null
				&& nounElement.hasFeature(LexicalFeature.EXPLETIVE_SUBJECT)) {

			setFeature(LexicalFeature.EXPLETIVE_SUBJECT, nounElement
					.getFeature(LexicalFeature.EXPLETIVE_SUBJECT));
		}

		setFeature(Feature.ADJECTIVE_ORDERING, true);
		setFeature(Feature.SPECIFIER_ORDERING, true);
	}

	/**
	 * sets the noun (head) of a noun phrase
	 * 
	 * @param noun
	 */
	public void setNoun(Object noun) {
		NLGElement nounElement = getFactory().createNLGElement(noun,
				LexicalCategory.NOUN);
		setHead(nounElement);
	}

	/**
	 * @return noun (head) of noun phrase
	 */
	public NLGElement getNoun() {
		return getHead();
	}

	
	/**
	 * setDeterminer - Convenience method for when a person tries to set 
	 *                 a determiner (e.g. "the") to a NPPhraseSpec.
	 */
	public void setDeterminer(Object determiner) {
		setSpecifier(determiner);
	}
	
	/**
	 * getDeterminer - Convenience method for when a person tries to get a
	 *                 determiner (e.g. "the") from a NPPhraseSpec.
	 */
	public List<NLGElement> getDeterminer() {
		return getSpecifier();
	}
	
	/**
	 * sets the specifier of a noun phrase.
     * Adaptation has been done for Chinese for supporting the
     *  multiple specifier for the nouns in Chinese.
	 * 
	 * @param specifier
	 */
	public void setSpecifier(Object specifier) {
		setFeature(InternalFeature.SPECIFIERS, null);
		addSpecifier(specifier);
	}

	/**
	 * add the specifier of a noun phrase to the specifier list.
     * New Method for Chinese!
	 *
	 * @param specifier
	 */
	public void addSpecifier(Object specifier) {
		List<NLGElement> specifiers = getFeatureAsElementList(InternalFeature.SPECIFIERS);
		if(specifiers == null) {
			specifiers = new ArrayList<NLGElement>();
		}
		if (specifier instanceof NLGElement) {
			specifiers.add((NLGElement) specifier);
			setFeature(InternalFeature.SPECIFIERS, specifiers);
		} else {
			// create specifier as word (assume determiner)
			NLGElement specifierElement = getFactory().createWord(specifier,
					LexicalCategory.DEMONSTRATIVE);

			// set specifier feature
			if (specifierElement != null) {
				specifiers.add(specifierElement);
				setFeature(InternalFeature.SPECIFIERS, specifiers);
			}
		};
	}


	/**
	 * Adds a new pre-modifier to the phrase element.
	 *
	 * @param newPreModifier
	 *            the new pre-modifier as an <code>NLGElement</code>.
	 */
	@Override
	public void addPreModifier(NLGElement newPreModifier) {
        List<NLGElement> preModifiers = getFeatureAsElementList(InternalFeature.PREMODIFIERS);
        if (preModifiers == null) {
            preModifiers = new ArrayList<NLGElement>();
        }

        if (newPreModifier instanceof  SPhraseSpec) {
            if (((SPhraseSpec) newPreModifier).getVerb() != null) {
                ((SPhraseSpec) newPreModifier).getVerb().setFeature(LexicalFeature.PREMODIFIER, true);
            } else {
                ((SPhraseSpec) newPreModifier).getObject().setFeature(LexicalFeature.PREMODIFIER, true);
            }
            newPreModifier.setFeature(Feature.ASSOCIATIVE, true);
		} else if (newPreModifier instanceof PhraseElement) {
            ((PhraseElement) newPreModifier).getHead().setFeature(LexicalFeature.PREMODIFIER, true);
        } else if (newPreModifier instanceof WordElement) {
            newPreModifier.setFeature(LexicalFeature.PREMODIFIER, true);
        }
        preModifiers.add(newPreModifier);
        setFeature(InternalFeature.PREMODIFIERS, preModifiers);

        if (newPreModifier.isA(LexicalCategory.ADJECTIVE)
                || newPreModifier.isA(PhraseCategory.ADJECTIVE_PHRASE)
                || newPreModifier.isA(PhraseCategory.VERB_PHRASE)
                || newPreModifier.isA(LexicalCategory.VERB)
                || newPreModifier.getFeatureAsElement(InternalFeature.LOCATIVE) != null) {
        	if (!newPreModifier.getFeatureAsBoolean(LexicalFeature.NONPREDICATE) || getHead() == null) {
                newPreModifier.setFeature(Feature.ASSOCIATIVE, true);
            }
            if (newPreModifier.getFeatureAsElement(InternalFeature.LOCATIVE) != null) {
                newPreModifier.getFeatureAsElement(InternalFeature.LOCATIVE).setFeature(Feature.PREASSOCIATIVE, false);
            }
        }
	}

    /**
     * <p>
     * Adds a new complement to the phrase element. Complements will be realised
     * in the syntax after the head element of the phrase. Complements differ
     * from post-modifiers in that complements are crucial to the understanding
     * of a phrase whereas post-modifiers are optional.
     * </p>
     *
     * <p>
     * If the new complement being added is a <em>clause</em> or a
     * <code>CoordinatedPhraseElement</code> then its clause status feature is
     * set to <code>ClauseStatus.SUBORDINATE</code> and it's discourse function
     * is set to <code>DiscourseFunction.OBJECT</code> by default unless an
     * existing discourse function exists on the complement.
     * </p>
     *
     * <p>
     * Complements can have different functions. For example, the phrase <I>John
     * gave Mary a flower</I> has two complements, one a direct object and one
     * indirect. If a complement is not specified for its discourse function,
     * then this is automatically set to <code>DiscourseFunction.OBJECT</code>.
     * </p>
     *
     * @param newComplement
     *            the new complement as an <code>NLGElement</code>.
     */
    @Override
    public void addComplement(NLGElement newComplement) {
        List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
        if (complements == null) {
            complements = new ArrayList<NLGElement>();
        }

        // check if the new complement has a discourse function; if not, assume
        // object
        if(!newComplement.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
            newComplement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
        }

        complements.add(newComplement);
        setFeature(InternalFeature.COMPLEMENTS, complements);
        if (newComplement.isA(PhraseCategory.CLAUSE)
                || newComplement instanceof CoordinatedPhraseElement) {
            newComplement.setFeature(InternalFeature.CLAUSE_STATUS,
                    ClauseStatus.SUBORDINATE);

            if (!newComplement.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
                newComplement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
                        DiscourseFunction.OBJECT);
            }
        }
        if (newComplement.getFeatureAsBoolean(LexicalFeature.LOCATIVE)) {
            if (newComplement instanceof WordElement
                    && ((WordElement) newComplement).getBaseForm().length() > 1) {
                newComplement.setFeature(Feature.PREASSOCIATIVE, true);
            }
            setFeature(InternalFeature.LOCATIVE, newComplement);
        }
    }

    /**
     * Removes all existing complements on the phrase.
     */
    @Override
    public void clearComplements() {
        removeFeature(InternalFeature.COMPLEMENTS);
        setFeature(InternalFeature.LOCATIVE, false);
    }

    /**
     * Adds a new post-modifier to the phrase element. Post-modifiers will be
     * realised in the syntax after the complements.
     *
     * @param newPostModifier
     *            the new post-modifier as an <code>NLGElement</code>.
     */
    @Override
    public void addPostModifier(NLGElement newPostModifier) {
        List<NLGElement> postModifiers = getFeatureAsElementList(InternalFeature.POSTMODIFIERS);
        if (postModifiers == null) {
            postModifiers = new ArrayList<NLGElement>();
        }
        newPostModifier.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.POST_MODIFIER);
        postModifiers.add(newPostModifier);
        setFeature(InternalFeature.POSTMODIFIERS, postModifiers);
        if (newPostModifier.getFeatureAsBoolean(LexicalFeature.LOCATIVE)) {
            if (newPostModifier instanceof WordElement
                    && ((WordElement) newPostModifier).getBaseForm().length() > 1) {
                newPostModifier.setFeature(Feature.PREASSOCIATIVE, true);
            }
            setFeature(InternalFeature.LOCATIVE, newPostModifier);
        }
    }

	/**
	 * @return specifier (eg, determiner) of noun phrase
	 */
	public List<NLGElement> getSpecifier() {
		return getFeatureAsElementList(InternalFeature.SPECIFIERS);
	}

	public void clearSpecifiers() {removeFeature(InternalFeature.SPECIFIERS);}

	/**
	 * Add a modifier to an NP Use heuristics to decide where it goes
	 * 
	 * @param modifier
	 */
	@Override
	public void addModifier(Object modifier) {
		// string which is one lexicographic word is looked up in lexicon,
		// adjective is preModifier
		// Everything else is postModifier
		if (modifier == null)
			return;

		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String) modifier;
			if (modifierString.length() > 0 && !modifierString.contains(" "))
				modifierElement = getFactory().createWord(modifier,
						LexicalCategory.ANY);
		}

		// if no modifier element, must be a complex string, add as postModifier
		if (modifierElement == null) {
			addPostModifier((String) modifier);
			return;
		}

		// AdjP is premodifer
		if (modifierElement instanceof AdjPhraseSpec) {
			addPreModifier(modifierElement);
			return;
		}

		// else extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null
				&& modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement)
					.getBaseWord();

		// check if modifier is an adjective
		if (modifierWord != null
				&& modifierWord.getCategory() == LexicalCategory.ADJECTIVE) {
			addPreModifier(modifierWord);
			return;
		}

		// default case
		addPostModifier(modifierElement);
	}
}
