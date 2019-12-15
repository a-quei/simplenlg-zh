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
 * Contributor(s): Ehud Reiter, Albert Gatt, Dave Wewstwater, Roman Kutlak, Margaret Mitchell, Saad Mahamood.
 */
package simplenlg.syntax.mandarin;

import org.junit.*;

import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.*;
import simplenlg.phrasespec.*;

/**
 * This class incorporates a few tests for adjectival phrases. Also tests for
 * adverbial phrase specs, which are very similar
 *
 * @author agatt
 */
public class AdjectivePhraseTest extends SimpleNLG4Test {

	@Override
	@Before
	public void setUp() {super.setUp();}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}


	/************************************************************
     * Adjective phrase as pre-modifier of nouns
	 ************************************************************/


	/**
	 * Test premodification & coordination of Adjective Phrases (Not much else
	 * to simplenlg.test)
     *
     * Just a test for functionality the meaning of these test cases are meaningless
	 */
	@Test
	public void testAdj() {

		// form the adjphrase "incredibly salacious"
		this.salacious.addPreModifier(this.phraseFactory
				.createAdverbPhrase("特别")); //$NON-NLS-1$
		Assert.assertEquals("特别 好色", this.realiser //$NON-NLS-1$
				.realise(this.salacious).getRealisation());

		// form the adjphrase "incredibly beautiful"
		this.beautiful.addPreModifier("十分"); //$NON-NLS-1$
		Assert.assertEquals("十分 美丽", this.realiser //$NON-NLS-1$
				.realise(this.beautiful).getRealisation());

		// coordinate the two aps
		CoordinatedPhraseElement coordap = new CoordinatedPhraseElement(
				this.salacious, this.beautiful);
		Assert.assertEquals("特别 好色 和 十分 美丽", //$NON-NLS-1$
				this.realiser.realise(coordap).getRealisation());

		// changing the inner conjunction
		coordap.setFeature(Feature.CONJUNCTION, "或"); //$NON-NLS-1$
		Assert.assertEquals("特别 好色 或 十分 美丽", //$NON-NLS-1$
				this.realiser.realise(coordap).getRealisation());

		// coordinate this with a new AdjPhraseSpec
		CoordinatedPhraseElement coord2 = new CoordinatedPhraseElement(coordap,
				this.cute);
		Assert.assertEquals(
				"特别 好色 或 十分 美丽 和 可爱", //$NON-NLS-1$
				this.realiser.realise(coord2).getRealisation());

		// add a premodifier the coordinate phrase, yielding
		// "seriously and undeniably incredibly salacious or amazingly beautiful
		// and stunning"
		CoordinatedPhraseElement preMod = new CoordinatedPhraseElement(
				new StringElement("严肃"), new StringElement("无疑"));

		coord2.addPreModifier(preMod);
		Assert.assertEquals(
				"严肃 和 无疑 特别 好色 或 十分 美丽 和 可爱",
				this.realiser.realise(coord2).getRealisation());

	}

	@Test
    public void testObjectPosition() {
	    this.man.setFeature(Feature.PRONOMINAL, true);
	    this.man.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
        AdjPhraseSpec high = this.phraseFactory.createAdjectivePhrase(this.phraseFactory
                .createWord("高", LexicalCategory.ADJECTIVE));
        high.addPreModifier(this.phraseFactory.createAdverbPhrase("很"));
        SPhraseSpec sent1 = this.phraseFactory.createClause(this.man, high);
        Assert.assertEquals("他 很 高", this.realiser.realise(sent1).getRealisation());

        SPhraseSpec sent2 = this.phraseFactory.createClause();
        this.man.setPlural(true);
        this.man.setFeature(LexicalFeature.NO_DE, true);
        NPPhraseSpec c = this.phraseFactory.createNounPhrase("班");
        c.addPreModifier(this.man);
        PPPhraseSpec prep = this.phraseFactory.createPrepositionPhrase("比");
        NPPhraseSpec xiaoming = this.phraseFactory.createNounPhrase("小明");
        prep.addComplement(xiaoming);
        high = this.phraseFactory.createAdjectivePhrase(this.phraseFactory
                .createWord("高", LexicalCategory.ADJECTIVE));
        high.addPreModifier(this.phraseFactory.createAdverbPhrase("更"));
        high.addPreModifier(prep);
        NPPhraseSpec object = this.phraseFactory.createNounPhrase();
        object.addPreModifier(high);
        sent2.setSubject(c);
        sent2.setVerb("没有");
        sent2.setObject(object);
        Assert.assertEquals("他们 班 没有 比 小明 更 高 的。", this.realiser.realiseSentence(sent2));
    }

    @Test
    public void testNonPredicate() {
        AdjPhraseSpec male = this.phraseFactory.createAdjectivePhrase(this.phraseFactory
                .createWord("男", LexicalCategory.ADJECTIVE));
        NPPhraseSpec person = this.phraseFactory.createNounPhrase(this.phraseFactory
                .createWord("人", LexicalCategory.NOUN));
        person.addPreModifier(male);
        Assert.assertEquals("男 人", this.realiser.realise(person).getRealisation());

        person = this.phraseFactory.createNounPhrase();
		male = this.phraseFactory.createAdjectivePhrase(this.phraseFactory
				.createWord("男", LexicalCategory.ADJECTIVE));
        person.addPreModifier(male);
        SPhraseSpec sent1 = this.phraseFactory.createClause("他", "是", person);
        Assert.assertEquals("他 是 男 的", this.realiser.realise(sent1).getRealisation());
    }


    /************************************************************
     * Adjective phrase as pre-modifier or complement of verbs
     ************************************************************/


	@Test
	public void testModal() {
		this.man.setFeature(Feature.PRONOMINAL, true);
		this.man.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
		AdjPhraseSpec high = this.phraseFactory.createAdjectivePhrase(this.phraseFactory
				.createWord("高", LexicalCategory.ADJECTIVE));
		high.addPreModifier(this.phraseFactory.createAdverbPhrase("很"));
		SPhraseSpec sent1 = this.phraseFactory.createClause(this.man, high);
		sent1.setFeature(Feature.MODAL, "应该");
		Assert.assertEquals("他 应该 很 高", this.realiser.realise(sent1).getRealisation());

		// set the modal word to the adjective
		high.setFeature(Feature.MODAL, "应该");
        sent1 = this.phraseFactory.createClause(this.man, high);
        Assert.assertEquals("他 应该 很 高", this.realiser.realise(sent1).getRealisation());
	}

    @Test
    public void testNegation() {
        this.man.setFeature(Feature.PRONOMINAL, true);
        this.man.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
        AdjPhraseSpec high = this.phraseFactory.createAdjectivePhrase(this.phraseFactory
                .createWord("高", LexicalCategory.ADJECTIVE));
        SPhraseSpec sent1 = this.phraseFactory.createClause(this.man, high);
        sent1.setFeature(Feature.NEGATED, true);
        Assert.assertEquals("他 不 高", this.realiser.realise(sent1).getRealisation());

        // set the modal negation to the adjective
        high.setFeature(Feature.NEGATED, true);
        sent1 = this.phraseFactory.createClause(this.man, high);
        Assert.assertEquals("他 不 高", this.realiser.realise(sent1).getRealisation());
    }

	/**
	 * Test participles as adjectives
	 */
	@Test
	public void testParticipleAdj() {
		PhraseElement ap = this.phraseFactory
				.createAdjectivePhrase(this.lexicon.getWord("associated",
						LexicalCategory.ADJECTIVE));
		Assert.assertEquals("associated", this.realiser.realise(ap)
				.getRealisation());
	}

	/**
	 * Test for multiple adjective modifiers with comma-separation. Example courtesy of William Bradshaw (Data2Text Ltd).
	 */
	@Test
	public void testMultipleModifiers() {
		PhraseElement np = this.phraseFactory
				.createNounPhrase(this.lexicon.getWord("message",
						LexicalCategory.NOUN));
		np.addPreModifier(this.lexicon.getWord("active",
						LexicalCategory.ADJECTIVE));
		np.addPreModifier(this.lexicon.getWord("temperature",
						LexicalCategory.ADJECTIVE));
		Assert.assertEquals("active, temperature message", this.realiser.realise(np).getRealisation());

		//now we set the realiser not to separate using commas
		this.realiser.setCommaSepPremodifiers(false);
		Assert.assertEquals("active temperature message", this.realiser.realise(np).getRealisation());

	}

}
