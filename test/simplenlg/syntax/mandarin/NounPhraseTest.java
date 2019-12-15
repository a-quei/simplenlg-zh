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

import org.junit.*;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.framework.*;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * Tests for the NPPhraseSpec and CoordinateNPPhraseSpec classes.
 * 
 * @author agatt
 */
public class NounPhraseTest extends SimpleNLG4Test {

	@Override
    @Before
    public void setUp() {super.setUp();}

    @Override
	@After
	public void tearDown() {
		super.tearDown();
	}

	/**
	 * Test the pronominalisation method for full NPs.
     * Chinese Pass
	 */
	@Test
	public void testPronominalisation() {
		// sing
		this.proTest1.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		this.proTest1.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals(
				"她", this.realiser.realise(this.proTest1).getRealisation());

		// sing, possessive
		this.proTest1.setFeature(Feature.POSSESSIVE, true);
		Assert.assertEquals(
				"她 的", this.realiser.realise(this.proTest1).getRealisation());

		// plural pronoun; mixed gender
		this.proTest2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        this.proTest2.setFeature(LexicalFeature.GENDER, Gender.MIXED);
		this.proTest2.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals(
				"他们", this.realiser.realise(this.proTest2).getRealisation());
	}

	/**
	 * Test the pronominalisation method for full NPs (more thorough than above)
	 */
	@Test
	public void testPronominalisation2() {
		// Ehud - added extra pronominalisation tests
		NPPhraseSpec pro = phraseFactory.createNounPhrase("王美丽");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.FIRST);
		SPhraseSpec sent = phraseFactory.createClause(pro, "喜欢", "李四");
		Assert
				.assertEquals("我 喜欢 李四。", this.realiser
						.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("王美丽");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.SECOND);
        sent = phraseFactory.createClause(pro, "喜欢", "李四");
		Assert.assertEquals("你 喜欢 李四。", this.realiser
				.realiseSentence(sent));

		// This test case is ensure that no 's' will be added
        pro = phraseFactory.createNounPhrase("王美丽");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.THIRD);
		pro.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
        sent = phraseFactory.createClause(pro, "喜欢", "李四");
		Assert.assertEquals("她 喜欢 李四。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("王美丽");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.FIRST);
		pro.setPlural(true);
        sent = phraseFactory.createClause(pro, "喜欢", "李四");
		Assert.assertEquals("我们 喜欢 李四。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("王美丽");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.SECOND);
		pro.setPlural(true);
        sent = phraseFactory.createClause(pro, "喜欢", "李四");
		Assert.assertEquals("你们 喜欢 李四。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("王美丽");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.THIRD);
		pro.setPlural(true);
		pro.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
        sent = phraseFactory.createClause(pro, "喜欢", "李四");
		Assert.assertEquals("她们 喜欢 李四。", this.realiser
				.realiseSentence(sent));

		pro = phraseFactory.createNounPhrase("李四");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.FIRST);
		sent = phraseFactory.createClause("王美丽", "喜欢", pro);
		Assert.assertEquals("王美丽 喜欢 我。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("李四");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.SECOND);
        sent = phraseFactory.createClause("王美丽", "喜欢", pro);
		Assert.assertEquals("王美丽 喜欢 你。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("李四");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.THIRD);
		pro.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
        sent = phraseFactory.createClause("王美丽", "喜欢", pro);
		Assert.assertEquals("王美丽 喜欢 他。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("李四");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.FIRST);
		pro.setPlural(true);
        sent = phraseFactory.createClause("王美丽", "喜欢", pro);
		Assert.assertEquals("王美丽 喜欢 我们。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("李四");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.SECOND);
		pro.setPlural(true);
        sent = phraseFactory.createClause("王美丽", "喜欢", pro);
		Assert.assertEquals("王美丽 喜欢 你们。", this.realiser
				.realiseSentence(sent));

        pro = phraseFactory.createNounPhrase("李四");
		pro.setFeature(Feature.PRONOMINAL, true);
		pro.setFeature(Feature.PERSON, Person.THIRD);
		pro.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
		pro.setPlural(true);
        sent = phraseFactory.createClause("王美丽", "喜欢", pro);
		Assert.assertEquals("王美丽 喜欢 他们。", this.realiser
				.realiseSentence(sent));
	}

    /**
     * Test the pronominalisation method for full NPs (more thorough than above)
     */
    @Test
    public void testSpecification() {
        NLGElement pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);

        NPPhraseSpec np = phraseFactory.createNounPhrase("学生");

        np.addSpecifier(pronoun);
        Assert.assertEquals("学生", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());

        np = phraseFactory.createNounPhrase("学生");
        pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);
        np.addSpecifier(pronoun);
        np.setPlural(true);
        Assert.assertEquals("他们 学生", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());

        np = phraseFactory.createNounPhrase("椅子");
        np.addPreModifier(phraseFactory.createWord("绿色", LexicalCategory.ADJECTIVE));
        System.out.println(this.realiser.realise(np));


        np = phraseFactory.createNounPhrase("学生");
        pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);
        NLGElement demonstrative = phraseFactory.createWord("那", LexicalCategory.DEMONSTRATIVE);
        np.addSpecifier(pronoun);
        np.setPlural(true);
        np.addSpecifier(demonstrative);
        Assert.assertEquals("他们 学生", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());

        np = phraseFactory.createNounPhrase("学生");
        pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);
        demonstrative = phraseFactory.createWord("那", LexicalCategory.DEMONSTRATIVE);
        NLGElement classifier2 = phraseFactory.createWord("些", LexicalCategory.CLASSIFIER);
        np.addSpecifier(pronoun);
        np.setPlural(true);
        np.addSpecifier(demonstrative);
        np.addSpecifier(classifier2);
        Assert.assertEquals("他们 那 些 学生", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());

        np = phraseFactory.createNounPhrase("学生");
        pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);
        demonstrative = phraseFactory.createWord("那", LexicalCategory.DEMONSTRATIVE);
        NLGElement numeral = phraseFactory.createWord("两", LexicalCategory.NUMERAL);
        NLGElement classifier = phraseFactory.createWord("个", LexicalCategory.CLASSIFIER);
        np.addSpecifier(pronoun);
        np.setPlural(true);
        np.addSpecifier(demonstrative);
        np.addSpecifier(classifier);
        np.addSpecifier(numeral);
        Assert.assertEquals("他们 那 两 个 学生", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());

        np = phraseFactory.createNounPhrase("学生");
        pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);
        NLGElement properName = phraseFactory.createWord("王富贵", LexicalCategory.NOUN);
        numeral = phraseFactory.createWord("两", LexicalCategory.NUMERAL);
        classifier = phraseFactory.createWord("个", LexicalCategory.CLASSIFIER);
        properName.setFeature(LexicalFeature.PROPER, true);
        np.addSpecifier(pronoun);
        np.addSpecifier(properName);
        np.addSpecifier(classifier);
        np.addSpecifier(numeral);
        np.setPlural(true);
        Assert.assertEquals("王富贵 他们 两 个 学生", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());


    }

    /**
     * Test the pronominalisation method for full NPs (more thorough than above)
     */
    @Test
    public void testMEN() {
        NPPhraseSpec np = phraseFactory.createNounPhrase("学生");
        NLGElement pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);
        NLGElement properName = phraseFactory.createWord("王富贵", LexicalCategory.NOUN);
        NLGElement numeral = phraseFactory.createWord("两", LexicalCategory.NUMERAL);
        NLGElement classifier = phraseFactory.createWord("个", LexicalCategory.CLASSIFIER);
        properName.setFeature(LexicalFeature.PROPER, true);
        properName.setMEN(true);
        np.getHead().setMEN(true);
        np.addSpecifier(pronoun);
        np.addSpecifier(properName);
        np.addSpecifier(classifier);
        np.addSpecifier(numeral);
        np.setPlural(true);
        Assert.assertEquals("王富贵们 他们 两 个 学生", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());

        np = phraseFactory.createNounPhrase("学生");
        pronoun = phraseFactory.createWord("他", LexicalCategory.PRONOUN);
        np.addSpecifier(pronoun);
        np.setPlural(true);
        np.getHead().setMEN(true);
        Assert.assertEquals("他们 学生们", this.realiser.realise(np) //$NON-NLS-1$
                .getRealisation());
    }

	/**
	 * Test the pronominalisation method for full NPs (more thorough than above)
     *
	 */
	@Test
	public void testLocaliser() {
	    NLGElement localiser1 = phraseFactory.createWord("里", LexicalCategory.NOUN);
        NLGElement localiser2 = phraseFactory.createWord("里面", LexicalCategory.NOUN);
        NLGElement localiser3 = phraseFactory.createWord("里面", LexicalCategory.NOUN);

        localiser3.setFeature(LexicalFeature.NO_DE, true);

		this.np6.addComplement(localiser1);
		Assert.assertEquals("房间 里", this.realiser.realise(this.np6)
				.getRealisation());

		this.np6.clearComplements();
        this.np6.addPostModifier(localiser1);
        Assert.assertEquals("房间 里", this.realiser.realise(this.np6)
                .getRealisation());

        this.np6.setPostModifier(localiser2);
        Assert.assertEquals("房间 的 里面", this.realiser.realise(this.np6)
                .getRealisation());

        NPPhraseSpec dog = phraseFactory.createNounPhrase("狗");
        dog.addPreModifier(this.np6);
        Assert.assertEquals("房间 里面 的 狗", this.realiser.realise(dog)
                .getRealisation());

        this.np6.setPostModifier(localiser3);
        Assert.assertEquals("房间 里面", this.realiser.realise(this.np6)
                .getRealisation());
    }


	/**
	 * Test premodification in NPS.
	 */
	@Test
	public void testPremodification() {
		this.man.addPreModifier(this.salacious);
		Assert.assertEquals("一 个 好色 的 男人", this.realiser
				.realise(this.man).getRealisation());

		this.woman.addPreModifier(this.beautiful);
		Assert.assertEquals("一 个 美丽 的 女人", this.realiser.realise(
				this.woman).getRealisation());

		this.dog.addPreModifier(this.cute);
		Assert.assertEquals("一 条 可爱 的 狗", this.realiser.realise(this.dog)
				.getRealisation());

		// premodification with a WordElement
		this.man.setPreModifier(this.phraseFactory.createWord("愚蠢",
				LexicalCategory.ADJECTIVE));
		Assert.assertEquals("一 个 愚蠢 的 男人", this.realiser
				.realise(this.man).getRealisation());

		PhraseElement np = this.phraseFactory.createNounPhrase("教育");
		NLGElement noun = this.phraseFactory.createWord("大学",
				LexicalCategory.NOUN);
		np.addPreModifier(noun);
		Assert.assertEquals("大学 教育", this.realiser
				.realise(np).getRealisation());

		np.addPreModifier(this.phraseFactory.createWord("愚蠢",
				LexicalCategory.ADJECTIVE));
		Assert.assertEquals("愚蠢 的 大学 教育", this.realiser
				.realise(np).getRealisation());
	}

	/**
	 * Test possessive constructions.
	 */
	@Test
	public void testPossessive() {

		// simple possessive 's: 'a man's'
        NPPhraseSpec possNP = this.phraseFactory.createNounPhrase("男人");
		possNP.addSpecifier(phraseFactory.createWord("那", LexicalCategory.DEMONSTRATIVE));
        possNP.addSpecifier(phraseFactory.createWord("个", LexicalCategory.CLASSIFIER));
		possNP.setFeature(Feature.POSSESSIVE, true);
        ((NPPhraseSpec) this.dog).addSpecifier(possNP);
        Assert.assertEquals("那 个 男人 的 一 条 狗", this.realiser.realise(this.dog) //$NON-NLS-1$
                .getRealisation());

		possNP.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
        ((NPPhraseSpec) this.dog).setSpecifier(possNP);
		possNP.setFeature(Feature.PRONOMINAL, true);
		Assert.assertEquals("他 的 狗", this.realiser.realise(this.dog)
				.getRealisation());

		// make it slightly more complicated: "his dog's rock"
		this.dog.setFeature(Feature.POSSESSIVE, true); // his dog's
        ((NPPhraseSpec) this.np4).addSpecifier(this.dog);
		Assert.assertEquals("他 的 狗 的 一 块 石头", this.realiser.realise(this.np4) //$NON-NLS-1$
				.getRealisation());

        ((NPPhraseSpec) this.np4).setSpecifier(this.dog);
        Assert.assertEquals("他 的 狗 的 石头", this.realiser.realise(this.np4) //$NON-NLS-1$
                .getRealisation());
	}

	/**
	 * Test NP coordination.
	 */
	@Test
	public void testCoordination() {

		CoordinatedPhraseElement cnp1 = new CoordinatedPhraseElement(this.dog,
				this.woman);
		// simple coordination
		Assert.assertEquals("一 条 狗 和 一 个 女人", this.realiser //$NON-NLS-1$
				.realise(cnp1).getRealisation());

		// simple coordination with complementation of entire coordinate NP
		cnp1.addComplement(this.behindTheCurtain);
		Assert.assertEquals("一 条 狗 和 一 个 女人 在 窗帘 的 后面", //$NON-NLS-1$
				this.realiser.realise(cnp1).getRealisation());
	}

	/**
	 * Another battery of tests for NP coordination.
	 */
	@Test
	public void testCoordination2() {

		// simple coordination of complementised nps
		this.dog.clearComplements();
		this.woman.clearComplements();

		CoordinatedPhraseElement cnp1 = new CoordinatedPhraseElement(this.man,
				this.woman);
		cnp1.setFeature(Feature.RAISE_SPECIFIER, true);
		NLGElement realised = this.realiser.realise(cnp1);
		Assert.assertEquals("一 个 男人 和 一 个 女人", realised.getRealisation()); //这个地方有问题...

        NPPhraseSpec possNP = this.phraseFactory.createNounPhrase("男人");
        possNP.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
        possNP.setFeature(Feature.PRONOMINAL, true);
        possNP.setFeature(Feature.POSSESSIVE, true);
        ((NPPhraseSpec) this.dog).setSpecifier(possNP);
        ((NPPhraseSpec) this.boy).setSpecifier(possNP);
        CoordinatedPhraseElement cnp2 = new CoordinatedPhraseElement(this.dog, this.boy);
        cnp2.setFeature(Feature.RAISE_SPECIFIER, true);
        realised = this.realiser.realise(cnp2);
        Assert.assertEquals("他 的 狗 和 男孩", realised.getRealisation());



        this.dog = this.phraseFactory.createNounPhrase("一", "条","狗");
        this.boy = this.phraseFactory.createNounPhrase("一", "个", "男孩");
        cnp2 = new CoordinatedPhraseElement(this.dog, this.boy);
        cnp2.addSpecifier(possNP);
        realised = this.realiser.realise(cnp2);
        Assert.assertEquals("他 的 狗 和 男孩", realised.getRealisation());


        this.dog = this.phraseFactory.createNounPhrase("狗");
        this.woman = this.phraseFactory.createNounPhrase("女人");
        PhraseElement rock = this.phraseFactory.createNounPhrase("石头");
        rock.addPostModifier(this.localiser1);
        this.dog.addPreModifier(rock);

        PhraseElement curtain = this.phraseFactory.createNounPhrase("窗帘");
        curtain.addPostModifier(this.localiser2);
		this.woman.addPreModifier(curtain);

        CoordinatedPhraseElement cnp3 = new CoordinatedPhraseElement(this.dog,
                this.woman);
		Assert.assertEquals(
				"石头 上 的 狗 和 窗帘 后面 的 女人", //$NON-NLS-1$
				this.realiser.realise(cnp3).getRealisation());



		// pronominalise one of the constituents
		this.dog.setFeature(Feature.PRONOMINAL, true);

		// CNP should be realised with pronominal internal const
		Assert.assertEquals(
				"它 和 窗帘 后面 的 女人",
				this.realiser.realise(cnp3).getRealisation());
	}

	/**
	 * Test possessives in coordinate NPs.
	 */
	@Test
	public void testPossessiveCoordinate() {
		// simple coordination
		CoordinatedPhraseElement cnp2 = new CoordinatedPhraseElement(this.dog,
				this.woman);
		Assert.assertEquals("一 条 狗 和 一 个 女人", this.realiser
				.realise(cnp2).getRealisation());

		// set possessive -- wide-scope by default
		cnp2.setFeature(Feature.POSSESSIVE, true);
		Assert.assertEquals("一 条 狗 和 一 个 女人 的", this.realiser.realise(
				cnp2).getRealisation());

		// set possessive with pronoun
		this.dog.setFeature(Feature.PRONOMINAL, true);
		this.dog.setFeature(Feature.POSSESSIVE, true);
		cnp2.setFeature(Feature.POSSESSIVE, true);
		Assert.assertEquals("它 的 和 一 个 女人 的", this.realiser.realise(cnp2)
				.getRealisation());

	}


//	/**
//	 * Test for appositive postmodifiers
//	 * Still not whether this can appear in Chinese
//	 */
//	@Test
//	public void testAppositivePostmodifier() {
//		PhraseElement _dog = this.phraseFactory.createNounPhrase("the", "dog");
//		PhraseElement _rott = this.phraseFactory.createNounPhrase("a", "rottweiler");
//		_rott.setFeature(Feature.APPOSITIVE, true);
//		_dog.addPostModifier(_rott);
//		SPhraseSpec _sent = this.phraseFactory.createClause(_dog, "run");
//		Assert.assertEquals("The dog, a rottweiler, runs.", this.realiser.realiseSentence(_sent));
//	}
}
