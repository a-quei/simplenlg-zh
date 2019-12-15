package simplenlg.tuna.mandarin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import simplenlg.features.Feature;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.*;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.mandarin.Realiser;

public class MTunaTest2 {

    protected Realiser realiser;

    protected NLGFactory phraseFactory;

    protected Lexicon lexicon;

    protected CoordinatedPhraseElement conj;

    // colour
    protected NLGElement white, blue, red;

    // adj
    protected NLGElement small, large, old;

    // type
    protected PhraseElement person, person2, clf_person, chair, clf_chair, fan, furniture;

    // components
    protected PhraseElement hair, glasses, smail, beard, have_beard;

    // orientation
    protected PhraseElement orien1, orien2, left;

    @Before
    public void setUp() {
        lexicon = new XMLLexicon();  // built in lexicon

        this.phraseFactory = new NLGFactory(this.lexicon);
        this.realiser = new Realiser(this.lexicon);
        this.conj = new CoordinatedPhraseElement();

        //colour
        this.white = this.phraseFactory.createWord("白", LexicalCategory.ADJECTIVE);
        this.blue = this.phraseFactory.createWord("蓝色", LexicalCategory.ADJECTIVE);
        this.red = this.phraseFactory.createWord("红色", LexicalCategory.ADJECTIVE);

        // size
        this.small = this.phraseFactory.createWord("小", LexicalCategory.ADJECTIVE);
        this.large = this.phraseFactory.createWord("大", LexicalCategory.ADJECTIVE);
        this.old = this.phraseFactory.createWord("老年", LexicalCategory.ADJECTIVE);

        // ren
        this.person = this.phraseFactory.createNounPhrase("人");
        this.person2 = this.phraseFactory.createNounPhrase("人");
        this.clf_person = this.phraseFactory.createNounPhrase("一", "个", "人");
        this.chair = this.phraseFactory.createNounPhrase("椅子");
        this.clf_chair = this.phraseFactory.createNounPhrase("一", "把", "椅子");
        this.fan = this.phraseFactory.createNounPhrase("风扇");
        this.furniture = this.phraseFactory.createNounPhrase("家具");

        // conponents
        this.hair = this.phraseFactory.createNounPhrase("头发");
        this.glasses = this.phraseFactory.createVerbPhrase("戴");
        ((VPPhraseSpec) this.glasses).setObject(this.phraseFactory.createNounPhrase("眼镜"));
        this.smail = this.phraseFactory.createVerbPhrase("微笑");
        this.have_beard = this.phraseFactory.createVerbPhrase("有");
        ((VPPhraseSpec) this.have_beard).setObject(this.phraseFactory.createNounPhrase("胡子"));
        this.beard = this.phraseFactory.createNounPhrase("胡子");

        // orientation
        PhraseElement side = this.phraseFactory.createNounPhrase("侧");
        side.addPreModifier("正面");
        this.orien1 = this.phraseFactory.createClause(side, "放", null);

        PhraseElement front = this.phraseFactory.createNounPhrase("正");
        this.orien2 = this.phraseFactory.createClause(front, "朝向", "我们");
        this.left = this.phraseFactory.createVerbPhrase("朝");
        ((VPPhraseSpec) this.left).setObject("左");
    }

    @After
    public void tearDown() {
        this.realiser = null;

        this.phraseFactory = null;

        if(null != lexicon) {
            lexicon = null;
        }
    }

    @Test
    public void testFurnitureSg() {

        ((SPhraseSpec) this.orien1).setParticle("着");
        this.blue.setFeature(LexicalFeature.NO_DE, true);
        this.clf_chair.addPreModifier(this.blue);
        this.clf_chair.addPreModifier(this.orien1);
        System.out.println(this.realiser.realise(this.clf_chair));

        setUp();

        this.furniture.addPreModifier(this.left);
        this.furniture.addPreModifier(this.red);
        this.red.setFeature(LexicalFeature.NO_DE, true);
        System.out.println(this.realiser.realise(this.furniture));

        setUp();
        this.chair.addPreModifier(this.red);
        conj.addCoordinate(this.chair);
        NPPhraseSpec n2 = this.phraseFactory.createNounPhrase();
        n2.addPreModifier("椅子背在左边");
        n2.addPreModifier("大尺寸");
        conj.addCoordinate(n2);
        conj.topicalise();
        System.out.println(this.realiser.realise(this.conj));

        setUp();
        VPPhraseSpec set = this.phraseFactory.createVerbPhrase("放置");
        set.addPreModifier(this.orien2);
        this.orien2.setFeature(LexicalFeature.NO_DE, true);
        this.chair.addPreModifier(set);
        System.out.println(this.realiser.realise(this.chair));

        setUp();
        this.chair.addPreModifier(this.red);
        this.chair.addComplement(this.left);
        System.out.println(this.realiser.realise(this.chair));
    }

    @Test
    public void testFurniturePl() {
        this.chair.addPreModifier(this.orien2);
        this.chair.addPreModifier(this.small);
        this.fan.addPreModifier(this.orien2);
        this.fan.addPreModifier(this.large);
        conj.addCoordinate(this.chair);
        conj.addCoordinate(this.fan);
        System.out.println(this.realiser.realise(this.conj));
    }

    @Test
    public void testPeopleSg() {
        this.beard.addPreModifier(this.white);
        this.white.setFeature(LexicalFeature.NO_DE, true);
        this.beard.setFeature(Feature.ASSOCIATIVE, true);
        this.person.addPreModifier(this.beard);
        System.out.println(this.realiser.realise(this.person));

        setUp();
        this.person.addPreModifier("西装");
        this.person.addPreModifier("领带");
        System.out.println(this.realiser.realise(this.person));

        setUp();
        SPhraseSpec longbeard =
                this.phraseFactory.createClause("胡子", this.phraseFactory.createAdjectivePhrase("很长"));
        this.person.addPreModifier(longbeard);
        System.out.println(this.realiser.realise(this.person));

        setUp();
        ((VPPhraseSpec) this.glasses).setFeature(Feature.NEGATED, true);
        this.person.addPreModifier(this.glasses);
        System.out.println(this.realiser.realise(this.person));

        setUp();
        VPPhraseSpec is = this.phraseFactory.createVerbPhrase("是");
        is.addPreModifier(this.phraseFactory.createWord("也", LexicalCategory.ADVERB));
        this.person.addPreModifier(this.phraseFactory.createClause("胡子", is, "白"));
        System.out.println(this.realiser.realise(this.person));
    }

    @Test
    public void testPeoplePl() {

        this.hair.addPreModifier(this.white);
        this.white.setFeature(LexicalFeature.NO_DE, true);
        this.person.addPreModifier(this.hair);
        this.hair.setFeature(Feature.ASSOCIATIVE, true);
        this.hair.setFeature(LexicalFeature.NO_DE, true);
        this.person.addPreModifier(this.smail);
        conj.addCoordinate(this.person);
        this.clf_person.addPreModifier(this.glasses);
        conj.addCoordinate(this.clf_person);
        System.out.println(this.realiser.realise(this.conj));

        setUp();
        this.hair.addPreModifier(this.white);
        this.white.setFeature(LexicalFeature.NO_DE, true);
        this.hair.setFeature(Feature.ASSOCIATIVE, true);
        this.person.addPreModifier(this.hair);
        conj.addCoordinate(this.person);
        this.person2.addPreModifier(this.have_beard);
        conj.addCoordinate(this.person2);
        System.out.println(this.realiser.realise(this.conj));

        setUp();
        this.person.addPreModifier(this.glasses);
        conj.addCoordinate(this.phraseFactory.createClause("第一位", "是", this.person));
        this.old.setFeature(LexicalFeature.NO_DE, true);
        this.person2.addPreModifier(this.old);
        this.hair.addPreModifier(this.white);
        this.white.setFeature(LexicalFeature.NO_DE, true);
        this.hair.setFeature(Feature.ASSOCIATIVE, true);
        this.person2.addPreModifier(this.hair);
        conj.addCoordinate(this.phraseFactory.createClause("第二位", "是", this.person2));
        System.out.println(this.realiser.realise(this.conj));
    }

}
