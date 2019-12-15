package simplenlg.syntax.mandarin;

import org.junit.*;

import simplenlg.features.Feature;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.mandarin.Realiser;

/**
 * JUnit test case for interrogatives.
 * 
 * @author agatt
 */
public class InterrogativeTest extends SimpleNLG4Test {

	// set up a few more fixtures
	/** The s5. */
	SPhraseSpec s1, s2, s3, s4;


	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.test.SimplenlgTest#setUp()
	 */
	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	/**
	 * Tests a couple of fairly simple questions.
	 */
	@Test
	public void testSimpleQuestions() {

		// simple present
		this.s1 = this.phraseFactory.createClause("你", "是", "学生");
		this.s1.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);

		DocumentElement sent = phraseFactory.createSentence(this.s1);
		Assert.assertEquals("你 是 学生 吗？", this.realiser //$NON-NLS-1$
				.realise(sent).getRealisation());

		// copular/existential: be-fronting
		// sentence = "there is the dog on the rock"
		this.s2 = this.phraseFactory.createClause("那", "有", this.dog); //$NON-NLS-1$ //$NON-NLS-2$
		this.s2.addPostModifier(this.onTheRock);
		this.s2.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("那 有 一 条 狗 在 一 块 石头 上 吗", this.realiser //$NON-NLS-1$
				.realise(this.s2).getRealisation());

		// progressive
		// sentence: "the man was giving the woman John's flower"
		PhraseElement john = this.phraseFactory.createNounPhrase
                (phraseFactory.createWord("小明", LexicalCategory.NOUN)); //$NON-NLS-1$
		john.setFeature(Feature.POSSESSIVE, true);
		PhraseElement flower = this.phraseFactory.createNounPhrase(john, "花");
		PhraseElement _woman = this.phraseFactory.createNounPhrase(
				"那", "个", "女人");
		this.s3 = this.phraseFactory.createClause(this.man, this.give, flower);
		this.s3.setIndirectObject(_woman);
		this.s3.setFeature(Feature.PROGRESSIVE, true);
		this.s3.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		this.s3.setParticle("了");
		NLGElement realised = this.realiser.realise(this.s3);
		Assert.assertEquals("男人 给 那 个 女人 小明 的 花 了 吗", //$NON-NLS-1$
				realised.getRealisation());

        this.s3 = this.phraseFactory.createClause(this.man, this.give, flower);
        this.s3.setIndirectObject(_woman);
        this.s3.setFeature(Feature.PROGRESSIVE, true);
        this.s3.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		this.s3.setFeature(Feature.MODAL, "应该"); //$NON-NLS-1$
		Assert.assertEquals(
				"男人 应该 给 那 个 女人 小明 的 花 吗", //$NON-NLS-1$
				this.realiser.realise(this.s3).getRealisation());
	}

	/**
	 * Test for sentences with negation.
	 */
	@Test
	public void testNegatedQuestions() {
		setUp();
		this.phraseFactory.setLexicon(this.lexicon);
		this.realiser.setLexicon(this.lexicon);

		// sentence: "the woman did not kiss the man"
		this.s1 = this.phraseFactory.createClause(this.woman, "kiss", this.man);
		this.s1.setFeature(Feature.TENSE, Tense.PAST);
		this.s1.setFeature(Feature.NEGATED, true);
		this.s1.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("did the woman not kiss the man", this.realiser //$NON-NLS-1$
				.realise(this.s1).getRealisation());

		// sentence: however, tomorrow, Jane and Andrew will not pick up the
		// balls in the shop
		CoordinatedPhraseElement subjects = new CoordinatedPhraseElement(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.s4 = this.phraseFactory.createClause(subjects, "pick up", //$NON-NLS-1$
				"the balls"); //$NON-NLS-1$
		this.s4.addPostModifier("in the shop"); //$NON-NLS-1$
		this.s4.setFeature(Feature.CUE_PHRASE, "however,"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.NEGATED, true);
		this.s4.setFeature(Feature.TENSE, Tense.FUTURE);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals(
				"however, will Jane and Andrew not pick up the balls in the shop tomorrow", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());
	}

	/**
	 * Tests for coordinate VPs in question form.
	 */
	@Test
	public void testCoordinateVPQuestions() {

		// create a complex vp: "kiss the dog and walk in the room"
		setUp();
		CoordinatedPhraseElement complex = new CoordinatedPhraseElement(
				this.kiss, this.walk);
		this.kiss.addComplement(this.dog);
		this.walk.addComplement(this.inTheRoom);

		// sentence: "However, tomorrow, Jane and Andrew will kiss the dog and
		// will walk in the room"
		CoordinatedPhraseElement subjects = new CoordinatedPhraseElement(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.s4 = this.phraseFactory.createClause(subjects, complex);
		this.s4.setFeature(Feature.CUE_PHRASE, "however"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE, Tense.FUTURE);

		Assert.assertEquals(
				"however tomorrow Jane and Andrew will kiss the dog and will walk in the room", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());

		// setting to interrogative should automatically give us a single,
		// wide-scope aux
		setUp();
		subjects = new CoordinatedPhraseElement(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		this.kiss.addComplement(this.dog);
		this.walk.addComplement(this.inTheRoom);
		complex = new CoordinatedPhraseElement(this.kiss, this.walk);
		this.s4 = this.phraseFactory.createClause(subjects, complex);
		this.s4.setFeature(Feature.CUE_PHRASE, "however"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE, Tense.FUTURE);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);

		Assert.assertEquals(
				"however will Jane and Andrew kiss the dog and walk in the room tomorrow", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());

		// slightly more complex -- perfective
		setUp();
		this.realiser.setLexicon(this.lexicon);
		subjects = new CoordinatedPhraseElement(
				this.phraseFactory.createNounPhrase("Jane"), //$NON-NLS-1$
				this.phraseFactory.createNounPhrase("Andrew")); //$NON-NLS-1$
		complex = new CoordinatedPhraseElement(this.kiss, this.walk);
		this.kiss.addComplement(this.dog);
		this.walk.addComplement(this.inTheRoom);
		this.s4 = this.phraseFactory.createClause(subjects, complex);
		this.s4.setFeature(Feature.CUE_PHRASE, "however"); //$NON-NLS-1$
		this.s4.addFrontModifier("tomorrow"); //$NON-NLS-1$
		this.s4.setFeature(Feature.TENSE, Tense.FUTURE);
		this.s4.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		this.s4.setFeature(Feature.PERFECT, true);

		Assert.assertEquals(
				"however will Jane and Andrew have kissed the dog and walked in the room tomorrow", //$NON-NLS-1$
				this.realiser.realise(this.s4).getRealisation());
	}

	/**
	 * Test for simple WH questions in present tense.
	 */
	@Test
	public void testWho() {

		PhraseElement john = this.phraseFactory.createNounPhrase
				(phraseFactory.createWord("小明", LexicalCategory.NOUN));

		SPhraseSpec s = this.phraseFactory.createClause(john, "亲 了", "王美丽");
		s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("谁 亲 了 王美丽", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause(john, "亲", "王美丽");
		s.setFeature(Feature.NEGATED, true);
		s.setFeature(Feature.NEGATIVE_WORD, "没");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
        Assert.assertEquals("谁 没 亲 王美丽", this.realiser.realise(s).getRealisation());


        s = this.phraseFactory.createClause(john, "亲 了", "王美丽");
		s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("小明 亲 了 谁", this.realiser //$NON-NLS-1$
				.realise(s).getRealisation());

		// subject interrogative with passive
        s = this.phraseFactory.createClause(john, "亲 了", "王美丽");
		s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		s.setFeature(Feature.PASSIVE, true);
		Assert.assertEquals("王美丽 被 谁 亲 了", this.realiser //$NON-NLS-1$
				.realise(s).getRealisation());

        s = this.phraseFactory.createClause(john, "亲", "王美丽");
        s.setFeature(Feature.NEGATED, true);
        s.setFeature(Feature.NEGATIVE_WORD, "没");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
        s.setFeature(Feature.PASSIVE, true);
        Assert.assertEquals("王美丽 没 被 谁 亲", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause(john, "给 了", phraseFactory.createNounPhrase("一", "朵", "花"));
        s.setIndirectObject("王美丽");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_INDIRECT_OBJECT);
        Assert.assertEquals("小明 给 了 谁 一 朵 花", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause(john, "给 了", phraseFactory.createNounPhrase("那", "朵", "花"));
        s.setIndirectObject("王美丽");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_INDIRECT_OBJECT);
        s.setFeature(Feature.PASSIVE, true);
        Assert.assertEquals("那 朵 花 被 小明 给 了 谁", this.realiser.realise(s).getRealisation());
	}

	/**
	 * Test for wh questions.
	 */
	@Test
	public void testWhat() {
        PhraseElement john = this.phraseFactory.createNounPhrase
                (phraseFactory.createWord("小明", LexicalCategory.NOUN));

        SPhraseSpec s = this.phraseFactory.createClause(john, "买 了", "房子");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("小明 买 了 什么", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause("台风", "摧毁 了", "房子");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
        Assert.assertEquals("什么 摧毁 了 房子", this.realiser.realise(s).getRealisation());
	}

	@Test
    public void testHow() {
        PhraseElement john = this.phraseFactory.createNounPhrase
                (phraseFactory.createWord("小明", LexicalCategory.NOUN));
        SPhraseSpec s = this.phraseFactory.createClause(john, "亲 了", "王美丽");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
        Assert.assertEquals("小明 怎么 亲 了 王美丽", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause(john, "亲 了", "王美丽");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW_PREDICATE);
        Assert.assertEquals("小明 怎么 了", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause(john, phraseFactory.createAdjectivePhrase("好"));
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW_PREDICATE);
        Assert.assertEquals("小明 怎么 了", this.realiser.realise(s).getRealisation());
    }

    @Test
    public void testWhere() {
        PhraseElement john = this.phraseFactory.createNounPhrase
                (phraseFactory.createWord("小明", LexicalCategory.NOUN));
        SPhraseSpec s = this.phraseFactory.createClause(john, "去 了", "王家屯");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE_OBJECT);
        Assert.assertEquals("小明 去 了 哪里", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause("王家屯", "是", "家");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHERE_SUBJECT);
        Assert.assertEquals("哪里 是 家", this.realiser.realise(s).getRealisation());
    }

    @Test
    public void testHowMany() {
        PhraseElement john = this.phraseFactory.createNounPhrase
                (phraseFactory.createWord("小明", LexicalCategory.NOUN));
        SPhraseSpec s = this.phraseFactory.createClause(john, "打 了", "人");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW_MANY_OBJECT);
        Assert.assertEquals("小明 打 了 多少 人", this.realiser.realise(s).getRealisation());

        s.setFeature(Feature.PASSIVE, true);
        Assert.assertEquals("多少 人 被 小明 打 了", this.realiser.realise(s).getRealisation());

        s = this.phraseFactory.createClause("人", "打 了", john);
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW_MANY_SUBJECT);
        Assert.assertEquals("多少 人 打 了 小明", this.realiser.realise(s).getRealisation());
    }

    @Test
    public void testWhich() {
        PhraseElement john = this.phraseFactory.createNounPhrase
                (phraseFactory.createWord("小明", LexicalCategory.NOUN));
        SPhraseSpec s = this.phraseFactory.createClause(john, "要", "狗");
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHICH_OBJECT);
        Assert.assertEquals("小明 要 哪个", this.realiser.realise(s).getRealisation());

        john.setFeature(Feature.POSSESSIVE, true);

        s = this.phraseFactory.createClause("二胖", "是", john);
        s.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHICH_SUBJECT);
        Assert.assertEquals("哪个 是 小明 的", this.realiser.realise(s).getRealisation());
    }

	/**
	 * WH movement in the progressive
	 */
	@Test
	public void testProgrssiveWHSubjectQuestions() {
		SPhraseSpec p = this.phraseFactory.createClause();
		p.setSubject("Mary");
		p.setVerb("eat");
		p.setObject(this.phraseFactory.createNounPhrase("the", "pie"));
		p.setFeature(Feature.PROGRESSIVE, true);
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who is eating the pie", //$NON-NLS-1$
				this.realiser.realise(p).getRealisation());
	}

	/**
	 * WH movement in the progressive
	 */
	@Test
	public void testProgrssiveWHObjectQuestions() {
		SPhraseSpec p = this.phraseFactory.createClause();
		p.setSubject("Mary");
		p.setVerb("eat");
		p.setObject(this.phraseFactory.createNounPhrase("the", "pie"));
		p.setFeature(Feature.PROGRESSIVE, true);
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what is Mary eating", //$NON-NLS-1$
				this.realiser.realise(p).getRealisation());

		// AG -- need to check this; it doesn't work
		// p.setFeature(Feature.NEGATED, true);
		//		Assert.assertEquals("what is Mary not eating", //$NON-NLS-1$
		// this.realiser.realise(p).getRealisation());

	}

	/**
	 * Negation with WH movement for subject
	 */
	@Test
	public void testNegatedWHSubjQuestions() {
		SPhraseSpec p = this.phraseFactory.createClause();
		p.setSubject("Mary");
		p.setVerb("eat");
		p.setObject(this.phraseFactory.createNounPhrase("the", "pie"));
		p.setFeature(Feature.NEGATED, true);
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who does not eat the pie", //$NON-NLS-1$
				this.realiser.realise(p).getRealisation());
	}

	/**
	 * Negation with WH movement for object
	 */
	@Test
	public void testNegatedWHObjQuestions() {
		SPhraseSpec p = this.phraseFactory.createClause();
		p.setSubject("Mary");
		p.setVerb("eat");
		p.setObject(this.phraseFactory.createNounPhrase("the", "pie"));
		p.setFeature(Feature.NEGATED, true);

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		NLGElement realisation = this.realiser.realise(p);
		Assert.assertEquals("what does Mary not eat", //$NON-NLS-1$
				realisation.getRealisation());
	}

	/**
	 * Test questyions in the tutorial.
	 */
	@Test
	public void testTutorialQuestions() {
		setUp();
		this.realiser.setLexicon(this.lexicon);

		PhraseElement p = this.phraseFactory.createClause("Mary", "chase", //$NON-NLS-1$ //$NON-NLS-2$
				"George"); //$NON-NLS-1$
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("does Mary chase George", this.realiser.realise(p) //$NON-NLS-1$
				.getRealisation());

		p = this.phraseFactory.createClause("Mary", "chase", //$NON-NLS-1$ //$NON-NLS-2$
				"George"); //$NON-NLS-1$
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("who does Mary chase", this.realiser.realise(p) //$NON-NLS-1$
				.getRealisation());

	}

	/**
	 * Subject WH Questions with modals
	 */
	@Test
	public void testModalWHSubjectQuestion() {
		SPhraseSpec p = this.phraseFactory.createClause(this.dog, "upset",
				this.man);
		p.setFeature(Feature.TENSE, Tense.PAST);
		Assert.assertEquals("the dog upset the man", this.realiser.realise(p)
				.getRealisation());

		// first without modal
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who upset the man", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
		Assert.assertEquals("what upset the man", this.realiser.realise(p)
				.getRealisation());

		// now with modal auxiliary
		p.setFeature(Feature.MODAL, "may");

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who may have upset the man", this.realiser
				.realise(p).getRealisation());

		p.setFeature(Feature.TENSE, Tense.FUTURE);
		Assert.assertEquals("who may upset the man", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.TENSE, Tense.PAST);
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
		Assert.assertEquals("what may have upset the man", this.realiser
				.realise(p).getRealisation());

		p.setFeature(Feature.TENSE, Tense.FUTURE);
		Assert.assertEquals("what may upset the man", this.realiser.realise(p)
				.getRealisation());
	}

	/**
	 * Subject WH Questions with modals
	 */
	@Test
	public void testModalWHObjectQuestion() {
		SPhraseSpec p = this.phraseFactory.createClause(this.dog, "upset",
				this.man);
		p.setFeature(Feature.TENSE, Tense.PAST);
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);

		Assert.assertEquals("who did the dog upset", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.MODAL, "may");
		Assert.assertEquals("who may the dog have upset", this.realiser
				.realise(p).getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what may the dog have upset", this.realiser
				.realise(p).getRealisation());

		p.setFeature(Feature.TENSE, Tense.FUTURE);
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("who may the dog upset", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what may the dog upset", this.realiser.realise(p)
				.getRealisation());
	}

	/**
	 * Questions with tenses requiring auxiliaries + subject WH
	 */
	@Test
	public void testAuxWHSubjectQuestion() {
		SPhraseSpec p = this.phraseFactory.createClause(this.dog, "upset",
				this.man);
		p.setFeature(Feature.TENSE, Tense.PRESENT);
		p.setFeature(Feature.PERFECT, true);
		Assert.assertEquals("the dog has upset the man",
				this.realiser.realise(p).getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who has upset the man", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
		Assert.assertEquals("what has upset the man", this.realiser.realise(p)
				.getRealisation());
	}

	/**
	 * Questions with tenses requiring auxiliaries + subject WH
	 */
	@Test
	public void testAuxWHObjectQuestion() {
		SPhraseSpec p = this.phraseFactory.createClause(this.dog, "upset",
				this.man);

		// first without any aux
		p.setFeature(Feature.TENSE, Tense.PAST);
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what did the dog upset", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("who did the dog upset", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.TENSE, Tense.PRESENT);
		p.setFeature(Feature.PERFECT, true);

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("who has the dog upset", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what has the dog upset", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.TENSE, Tense.FUTURE);
		p.setFeature(Feature.PERFECT, true);

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_OBJECT);
		Assert.assertEquals("who will the dog have upset", this.realiser
				.realise(p).getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what will the dog have upset", this.realiser
				.realise(p).getRealisation());

	}

	/**
	 * Test for questions with "be"
	 */
	@Test
	public void testBeQuestions() {
		SPhraseSpec p = this.phraseFactory.createClause(
				this.phraseFactory.createNounPhrase("a", "ball"),
				this.phraseFactory.createWord("be", LexicalCategory.VERB),
				this.phraseFactory.createNounPhrase("a", "toy"));

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what is a ball", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("is a ball a toy", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
		Assert.assertEquals("what is a toy", this.realiser.realise(p)
				.getRealisation());

		SPhraseSpec p2 = this.phraseFactory.createClause("Mary", "be",
				"beautiful");
		p2.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);
		Assert.assertEquals("why is Mary beautiful", this.realiser.realise(p2)
				.getRealisation());

		
		p2.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who is beautiful", this.realiser.realise(p2)
				.getRealisation());					
	}

	/**
	 * Test for questions with "be" in future tense
	 */
	@Test
	public void testBeQuestionsFuture() {
		SPhraseSpec p = this.phraseFactory.createClause(
				this.phraseFactory.createNounPhrase("a", "ball"),
				this.phraseFactory.createWord("be", LexicalCategory.VERB),
				this.phraseFactory.createNounPhrase("a", "toy"));
		p.setFeature(Feature.TENSE, Tense.FUTURE);

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what will a ball be", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("will a ball be a toy", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
		Assert.assertEquals("what will be a toy", this.realiser.realise(p)
				.getRealisation());

		SPhraseSpec p2 = this.phraseFactory.createClause("Mary", "be",
				"beautiful");
		p2.setFeature(Feature.TENSE, Tense.FUTURE);
		p2.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);
		Assert.assertEquals("why will Mary be beautiful", this.realiser
				.realise(p2).getRealisation());

		p2.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who will be beautiful", this.realiser.realise(p2)
				.getRealisation());
	}

	/**
	 * Tests for WH questions with be in past tense
	 */
	@Test
	public void testBeQuestionsPast() {
		SPhraseSpec p = this.phraseFactory.createClause(
				this.phraseFactory.createNounPhrase("a", "ball"),
				this.phraseFactory.createWord("be", LexicalCategory.VERB),
				this.phraseFactory.createNounPhrase("a", "toy"));
		p.setFeature(Feature.TENSE, Tense.PAST);

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		Assert.assertEquals("what was a ball", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
		Assert.assertEquals("was a ball a toy", this.realiser.realise(p)
				.getRealisation());

		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
		Assert.assertEquals("what was a toy", this.realiser.realise(p)
				.getRealisation());

		SPhraseSpec p2 = this.phraseFactory.createClause("Mary", "be",
				"beautiful");
		p2.setFeature(Feature.TENSE, Tense.PAST);
		p2.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);
		Assert.assertEquals("why was Mary beautiful", this.realiser.realise(p2)
				.getRealisation());

		p2.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
		Assert.assertEquals("who was beautiful", this.realiser.realise(p2)
				.getRealisation());
	}


	/**
	 * Test WHERE, HOW and WHY questions, with copular predicate "be"
	 */
	public void testSimpleBeWHQuestions() {
		SPhraseSpec p = this.phraseFactory.createClause("I", "be");
		
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHY);		
		Assert.assertEquals("Why am I?", realiser.realiseSentence(p));
		
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);		
		Assert.assertEquals("How am I?", realiser.realiseSentence(p));

	}

	/**
	 * Test a simple "how" question, based on query from Albi Oxa
	 */
	@Test
	public void testHowPredicateQuestion() {
		SPhraseSpec test = this.phraseFactory.createClause();
		NPPhraseSpec subject = this.phraseFactory.createNounPhrase("You");

		subject.setFeature(Feature.PRONOMINAL, true);
		subject.setFeature(Feature.PERSON, Person.SECOND);
		test.setSubject(subject);
		test.setVerb("be");

		test.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.HOW_PREDICATE);
		test.setFeature(Feature.TENSE, Tense.PRESENT);

		String result = realiser.realiseSentence(test);
		Assert.assertEquals("How are you?", result);

	}
	
	/**
	 * Case 1 checks that "What do you think about John?" can be generated.
	 * 
	 * Case 2 checks that the same clause is generated, even when an object is
	 * declared.
	 */
	@Test
	public void testWhatObjectInterrogative() {
		Lexicon lexicon = Lexicon.getDefaultLexicon();
		NLGFactory nlg = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);

		// Case 1, no object is explicitly given:
		SPhraseSpec clause = nlg.createClause("you", "think");
		PPPhraseSpec aboutJohn = nlg.createPrepositionPhrase("about", "John");
		clause.addPostModifier(aboutJohn);
		clause.setFeature(Feature.INTERROGATIVE_TYPE,
				InterrogativeType.WHAT_OBJECT);
		String realisation = realiser.realiseSentence(clause);
		System.out.println(realisation);
		Assert.assertEquals("What do you think about John?", realisation);
		
		// Case 2:
		// Add "bad things" as the object so the object doesn't remain null:
		clause.setObject("bad things");
		realisation = realiser.realiseSentence(clause);
		System.out.println(realiser.realiseSentence(clause));
		Assert.assertEquals("What do you think about John?", realisation);
	}
}
