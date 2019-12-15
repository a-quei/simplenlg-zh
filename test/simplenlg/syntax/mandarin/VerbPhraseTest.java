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

import java.util.List;

import org.junit.*;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.InternalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.framework.*;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * These are tests for the verb phrase and coordinate VP classes.
 * @author agatt
 */
public class VerbPhraseTest extends SimpleNLG4Test {

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}


    @Override
    @Before
    public void setUp() {super.setUp();}

	/**
	 * Some tests to check for an early bug which resulted in reduplication of
	 * verb particles in the past tense e.g. "fall down down" or "creep up up"
	 */
	@Test
	public void testVerbParticle() {
		VPPhraseSpec v = this.phraseFactory.createVerbPhrase("fall down"); //$NON-NLS-1$

		Assert.assertEquals(
				"down", v.getFeatureAsString(Feature.PARTICLE)); //$NON-NLS-1$

		Assert.assertEquals(
				"fall", ((WordElement) v.getVerb()).getBaseForm()); //$NON-NLS-1$

		v.setFeature(Feature.TENSE,Tense.PAST);
		v.setFeature(Feature.PERSON, Person.THIRD);
		v.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);

		Assert.assertEquals(
				"fell down", this.realiser.realise(v).getRealisation()); //$NON-NLS-1$

		v.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
		Assert.assertEquals(
				"fallen down", this.realiser.realise(v).getRealisation()); //$NON-NLS-1$
	}

	/**
	 * Tests for the tense and aspect.
	 */
	@Test
	public void simplePastTest() {
		// "fell down"
		this.fallDown.setFeature(Feature.TENSE,Tense.PAST);
		Assert.assertEquals(
						"fell down", this.realiser.realise(this.fallDown).getRealisation()); //$NON-NLS-1$

	}

	/**
	 * Test tense aspect.
	 */
	@Test
	public void tenseAspectTest() {
		// had fallen down
		this.realiser.setLexicon(this.lexicon);
		this.fallDown.setFeature(Feature.TENSE,Tense.PAST);
		this.fallDown.setFeature(Feature.PERFECT, true);

		Assert.assertEquals("had fallen down", this.realiser.realise( //$NON-NLS-1$
				this.fallDown).getRealisation());

		// had been falling down
		this.fallDown.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("had been falling down", this.realiser.realise( //$NON-NLS-1$
				this.fallDown).getRealisation());

		// will have been kicked
		this.kick.setFeature(Feature.PASSIVE, true);
		this.kick.setFeature(Feature.PERFECT, true);
		this.kick.setFeature(Feature.TENSE,Tense.FUTURE);
		Assert.assertEquals("will have been kicked", this.realiser.realise( //$NON-NLS-1$
				this.kick).getRealisation());

		// will have been being kicked
		this.kick.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("will have been being kicked", this.realiser //$NON-NLS-1$
				.realise(this.kick).getRealisation());

		// will not have been being kicked
		this.kick.setFeature(Feature.NEGATED, true);
		Assert.assertEquals("will not have been being kicked", this.realiser //$NON-NLS-1$
				.realise(this.kick).getRealisation());

		// passivisation should suppress the complement
		this.kick.clearComplements();
		this.kick.addComplement(this.man);
		Assert.assertEquals("will not have been being kicked", this.realiser //$NON-NLS-1$
				.realise(this.kick).getRealisation());

		// de-passivisation should now give us "will have been kicking the man"
		this.kick.setFeature(Feature.PASSIVE, false);
		Assert.assertEquals("will not have been kicking the man", this.realiser //$NON-NLS-1$
				.realise(this.kick).getRealisation());

		// remove the future tense --
		// this is a test of an earlier bug that would still realise "will"
		this.kick.setFeature(Feature.TENSE,Tense.PRESENT);
		Assert.assertEquals("has not been kicking the man", this.realiser //$NON-NLS-1$
				.realise(this.kick).getRealisation());
	}

	/**
	 * Test for realisation of VP complements.
	 */
	@Test
	public void complementationTest() {

		// was kissing Mary
		PhraseElement mary = this.phraseFactory.createNounPhrase("玛丽"); //$NON-NLS-1$
		mary.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
		this.kiss.clearComplements();
		this.kiss.addComplement(mary);

		Assert.assertEquals("亲 玛丽", this.realiser //$NON-NLS-1$
				.realise(this.kiss).getRealisation());

		CoordinatedPhraseElement mary2 = new CoordinatedPhraseElement(mary,
				this.phraseFactory.createNounPhrase("苏珊")); //$NON-NLS-1$
		// add another complement -- should come out as "Mary and Susan"
		this.kiss.clearComplements();
		this.kiss.addComplement(mary2);
		Assert.assertEquals("亲 玛丽 和 苏珊", this.realiser //$NON-NLS-1$
				.realise(this.kiss).getRealisation());
	}

	/**
	 * This tests for the default complement ordering, relative to pre and
	 * postmodifiers.
	 */
	@Test
	public void DoubleObjectsTest() {
        // give the woman the dog
        this.woman.setFeature(InternalFeature.DISCOURSE_FUNCTION,
                DiscourseFunction.INDIRECT_OBJECT);
        this.dog.setFeature(InternalFeature.DISCOURSE_FUNCTION,
                DiscourseFunction.OBJECT);
        this.give.clearComplements();
        this.give.addComplement(this.dog);
        this.give.addComplement(this.woman);
        Assert.assertEquals("给 一 个 女人 一 条 狗", this.realiser.realise( //$NON-NLS-1$
                this.give).getRealisation());
    }

    @Test
    public void PreModifierTest() {

		NLGElement quick = this.phraseFactory.createWord("快速", LexicalCategory.ADJECTIVE);
        this.walk.addPreModifier(quick);
		Assert.assertEquals("快速 地 走",
						this.realiser.realise(this.walk).getRealisation());

        quick = this.phraseFactory.createWord("快", LexicalCategory.ADJECTIVE);
        this.walk = this.phraseFactory.createVerbPhrase("走");
        this.walk.addPreModifier(quick);
        Assert.assertEquals("快 走",
                this.realiser.realise(this.walk).getRealisation());

        quick = this.phraseFactory.createAdjectivePhrase
                (phraseFactory.createWord("快", LexicalCategory.ADJECTIVE));
        this.walk = this.phraseFactory.createVerbPhrase("走");
        this.walk.addPreModifier(quick);
        Assert.assertEquals("快 走",
                this.realiser.realise(this.walk).getRealisation());

        ((AdjPhraseSpec) quick).addPreModifier(this.phraseFactory.createWord("特别", LexicalCategory.ADVERB));
        this.walk = this.phraseFactory.createVerbPhrase("走");
        this.walk.addPreModifier(quick);
        Assert.assertEquals("特别 快 地 走",
                this.realiser.realise(this.walk).getRealisation());

	}


	@Test
    public void postModifierTest() {
        NLGElement quick = this.phraseFactory.createWord("快", LexicalCategory.ADJECTIVE);
        this.walk.addPostModifier(quick);
        Assert.assertEquals("走 得 快",
                this.realiser.realise(this.walk).getRealisation());

        quick = this.phraseFactory.createAdjectivePhrase
                (phraseFactory.createWord("快", LexicalCategory.ADJECTIVE));
        ((AdjPhraseSpec) quick).addPreModifier(this.phraseFactory.createWord("特别", LexicalCategory.ADVERB));
        this.walk = this.phraseFactory.createVerbPhrase("走");
        this.walk.addPostModifier(quick);
        Assert.assertEquals("走 得 特别 快",
                this.realiser.realise(this.walk).getRealisation());
    }

	/**
	 * Test for complements raised in the passive case.
	 */
	@Test
	public void passiveComplementTest() {
		// add some arguments
        VPPhraseSpec beat = this.phraseFactory.createVerbPhrase
                (phraseFactory.createWord("打", LexicalCategory.VERB));
        SPhraseSpec beaten = this.phraseFactory.createClause("他", beat, "小明");
		Assert.assertEquals("他 打 小明", this.realiser.realise(beaten).getRealisation());

		// add a few premodifiers and postmodifiers
		beat.addPreModifier(phraseFactory.createWord("重重", LexicalCategory.ADJECTIVE));
		Assert.assertEquals("他 重重 地 打 小明",
						this.realiser.realise(beaten).getRealisation());

        beat.setFeature(Feature.PASSIVE, true);

		Assert.assertEquals(
				"小明 被 他 重重 地 打", //$NON-NLS-1$
				this.realiser.realise(beaten).getRealisation());

        beaten = this.phraseFactory.createClause(null, beat, "小明");
        Assert.assertEquals("小明 被 重重 地 打",
                this.realiser.realise(beaten).getRealisation());
	}

    /**
     * Test for complements raised in the passive case.
     */
    @Test
    public void particleTest() {
        // add some arguments
        VPPhraseSpec beat = this.phraseFactory.createVerbPhrase
                ("打 了");
        SPhraseSpec beaten = this.phraseFactory.createClause("他", beat, "小明");
        Assert.assertEquals("他 打 了 小明", this.realiser.realise(beaten).getRealisation());

        // add a few premodifiers and postmodifiers
        beat = this.phraseFactory.createVerbPhrase("打 了");
        beaten = this.phraseFactory.createClause("他", beat, "小明");
        beat.addPreModifier(phraseFactory.createWord("重重", LexicalCategory.ADJECTIVE));
        Assert.assertEquals("他 重重 地 打 了 小明",
                this.realiser.realise(beaten).getRealisation());

        beat.setFeature(Feature.PASSIVE, true);

        Assert.assertEquals(
                "小明 被 他 重重 地 打 了", //$NON-NLS-1$
                this.realiser.realise(beaten).getRealisation());

        beaten = this.phraseFactory.createClause(null, beat, "小明");
        Assert.assertEquals("小明 被 重重 地 打 了",
                this.realiser.realise(beaten).getRealisation());

        beat.setParticle("过");
        Assert.assertEquals("小明 被 重重 地 打 过",
                this.realiser.realise(beaten).getRealisation());

        beat = this.phraseFactory.createVerbPhrase("打");
        beaten = this.phraseFactory.createClause("他", beat, "小明");
        beaten.setParticle("了");
        Assert.assertEquals("他 打 小明 了", this.realiser.realise(beaten).getRealisation());
    }

	@Test
    public void modalTest() {
        VPPhraseSpec beat = this.phraseFactory.createVerbPhrase
                (phraseFactory.createWord("打", LexicalCategory.VERB));
        SPhraseSpec beaten = this.phraseFactory.createClause("他", beat, "小明");
        beaten.setFeature(Feature.MODAL, "应该");
        Assert.assertEquals("他 应该 打 小明",
                this.realiser.realise(beaten).getRealisation());

        beat.setFeature(Feature.PASSIVE, true);
        Assert.assertEquals("小明 应该 被 他 打",
                this.realiser.realise(beaten).getRealisation());
    }


    @Test
    public void negationTest() {
        VPPhraseSpec beat = this.phraseFactory.createVerbPhrase
                (phraseFactory.createWord("打", LexicalCategory.VERB));
        SPhraseSpec beaten = this.phraseFactory.createClause("他", beat, "小明");
        beaten.setFeature(Feature.NEGATED, true);
        Assert.assertEquals("他 不 打 小明",
                this.realiser.realise(beaten).getRealisation());

        beaten.setFeature(Feature.MODAL, "应该");
        beaten.setFeature(Feature.NEGATIVE_WORD, "没");
        Assert.assertEquals("他 应该 没 打 小明",
                this.realiser.realise(beaten).getRealisation());

        beaten.setFeature(Feature.PASSIVE, true);
        Assert.assertEquals("小明 应该 没 被 他 打",
                this.realiser.realise(beaten).getRealisation());

        beaten.setFeature(Feature.PASSIVE, false);
        beaten.setFeature(Feature.BA, true);
        Assert.assertEquals("他 应该 没 把 小明 打",
                this.realiser.realise(beaten).getRealisation());
    }

	@Test
	public void baTest() {
        VPPhraseSpec beat = this.phraseFactory.createVerbPhrase
                (phraseFactory.createWord("打", LexicalCategory.VERB));
        SPhraseSpec beaten = this.phraseFactory.createClause("他", beat, "小明");
        beat.addPreModifier(phraseFactory.createWord("重重", LexicalCategory.ADJECTIVE));


        Assert.assertEquals("他 重重 地 打 小明",
                this.realiser.realise(beaten).getRealisation());

        beaten.setFeature(Feature.BA, true);

        Assert.assertEquals(
                "他 把 小明 重重 地 打", //$NON-NLS-1$
                this.realiser.realise(beaten).getRealisation());
    }

	/**
	 * Test VP with sentential complements. This tests for structures like "said
	 * that John was walking"
	 */
	@Test
	public void clausalComplementTest() {
		this.phraseFactory.setLexicon(this.lexicon);
		SPhraseSpec s = this.phraseFactory.createClause();

		s.setSubject(this.phraseFactory
				.createNounPhrase("John")); //$NON-NLS-1$

		// Create a sentence first
		CoordinatedPhraseElement maryAndSusan = new CoordinatedPhraseElement(
				this.phraseFactory.createNounPhrase("Mary"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Susan")); //$NON-NLS-1$

		this.kiss.clearComplements();
		s.setVerbPhrase(this.kiss);
		s.setObject(maryAndSusan);
		s.setFeature(Feature.PROGRESSIVE, true);
		s.setFeature(Feature.TENSE,Tense.PAST);
		s.addPostModifier(this.inTheRoom);
		Assert.assertEquals("John was kissing Mary and Susan in the room", //$NON-NLS-1$
				this.realiser.realise(s).getRealisation());

		// make the main VP past
		this.say.setFeature(Feature.TENSE,Tense.PAST);
		Assert.assertEquals("said", this.realiser.realise(this.say) //$NON-NLS-1$
				.getRealisation());

		// now add the sentence as complement of "say". Should make the sentence
		// subordinate
		// note that sentential punctuation is suppressed
		this.say.addComplement(s);
		Assert.assertEquals(
				"said that John was kissing Mary and Susan in the room", //$NON-NLS-1$
				this.realiser.realise(this.say).getRealisation());

		// add a postModifier to the main VP
		// yields [says [that John was kissing Mary and Susan in the room]
		// [behind the curtain]]
		this.say.addPostModifier(this.behindTheCurtain);
		Assert.assertEquals(
						"said that John was kissing Mary and Susan in the room behind the curtain", //$NON-NLS-1$
						this.realiser.realise(this.say).getRealisation());

		// create a new sentential complement
		PhraseElement s2 = this.phraseFactory.createClause(this.phraseFactory
				.createNounPhrase("all"), //$NON-NLS-1$
				"be", //$NON-NLS-1$
				this.phraseFactory.createAdjectivePhrase("fine")); //$NON-NLS-1$

		s2.setFeature(Feature.TENSE,Tense.FUTURE);
		Assert.assertEquals("all will be fine", this.realiser.realise(s2) //$NON-NLS-1$
				.getRealisation());

		// add the new complement to the VP
		// yields [said [that John was kissing Mary and Susan in the room and
		// all will be fine] [behind the curtain]]
		CoordinatedPhraseElement s3 = new CoordinatedPhraseElement(s, s2);
		this.say.clearComplements();
		this.say.addComplement(s3);

		// first with outer complementiser suppressed
		s3.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
		Assert.assertEquals(
				"said that John was kissing Mary and Susan in the room " //$NON-NLS-1$
						+ "and all will be fine behind the curtain", //$NON-NLS-1$
				this.realiser.realise(this.say).getRealisation());

		setUp();
		s = this.phraseFactory.createClause();

		s.setSubject(this.phraseFactory
				.createNounPhrase("John")); //$NON-NLS-1$

		// Create a sentence first
		maryAndSusan = new CoordinatedPhraseElement(
				this.phraseFactory.createNounPhrase("Mary"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Susan")); //$NON-NLS-1$

		s.setVerbPhrase(this.kiss);
		s.setObject(maryAndSusan);
		s.setFeature(Feature.PROGRESSIVE, true);
		s.setFeature(Feature.TENSE,Tense.PAST);
		s.addPostModifier(this.inTheRoom);
		s2 = this.phraseFactory.createClause(this.phraseFactory
				.createNounPhrase("all"), //$NON-NLS-1$
				"be", //$NON-NLS-1$
				this.phraseFactory.createAdjectivePhrase("fine")); //$NON-NLS-1$

		s2.setFeature(Feature.TENSE,Tense.FUTURE);
		// then with complementiser not suppressed and not aggregated
		s3 = new CoordinatedPhraseElement(s, s2);
		this.say.addComplement(s3);
		this.say.setFeature(Feature.TENSE,Tense.PAST);
		this.say.addPostModifier(this.behindTheCurtain);
		
		Assert.assertEquals(
				"said that John was kissing Mary and Susan in the room and " //$NON-NLS-1$
						+ "that all will be fine behind the curtain", //$NON-NLS-1$
				this.realiser.realise(this.say).getRealisation());

	}

	/**
	 * Test VP coordination and aggregation:
	 * <OL>
	 * <LI>If the simplenlg.features of a coordinate VP are set, they should be
	 * inherited by its daughter VP;</LI>
	 * <LI>2. We can aggregate the coordinate VP so it's realised with one
	 * wide-scope auxiliary</LI>
	 */
	@Test
	public void coordinationTest() {
		// simple case
		this.kiss.addComplement(this.dog);
		this.kick.addComplement(this.boy);

		CoordinatedPhraseElement coord1 = new CoordinatedPhraseElement(
				this.kiss, this.kick);

		coord1.setFeature(Feature.PERSON, Person.THIRD);
		coord1.setFeature(Feature.TENSE,Tense.PAST);
		Assert.assertEquals("kissed the dog and kicked the boy", this.realiser //$NON-NLS-1$
				.realise(coord1).getRealisation());

		// with negation: should be inherited by all components
		coord1.setFeature(Feature.NEGATED, true);
		this.realiser.setLexicon(this.lexicon);
		Assert.assertEquals("did not kiss the dog and did not kick the boy", //$NON-NLS-1$
				this.realiser.realise(coord1).getRealisation());

		// set a modal
		coord1.setFeature(Feature.MODAL, "could"); //$NON-NLS-1$
		Assert.assertEquals(
						"could not have kissed the dog and could not have kicked the boy", //$NON-NLS-1$
						this.realiser.realise(coord1).getRealisation());

		// set perfect and progressive
		coord1.setFeature(Feature.PERFECT, true);
		coord1.setFeature(Feature.PROGRESSIVE, true);
		Assert.assertEquals("could not have been kissing the dog and " //$NON-NLS-1$
				+ "could not have been kicking the boy", this.realiser.realise( //$NON-NLS-1$
				coord1).getRealisation());

		// now aggregate
		coord1.setFeature(Feature.AGGREGATE_AUXILIARY, true);
		Assert.assertEquals(
				"could not have been kissing the dog and kicking the boy", //$NON-NLS-1$
				this.realiser.realise(coord1).getRealisation());
	}
}
