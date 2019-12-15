package simplenlg.tuna.mandarin;

import org.junit.*;
import simplenlg.features.Feature;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.mandarin.Realiser;

public class MTunaTest {

    protected Realiser realiser;

    protected NLGFactory phraseFactory;

    protected Lexicon lexicon;

    @Before
    public void setUp() {
        lexicon = new XMLLexicon();  // built in lexicon

        this.phraseFactory = new NLGFactory(this.lexicon);
        this.realiser = new Realiser(this.lexicon);
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
    public void testFurniture() {
        // simple pre-modifier
        NPPhraseSpec chair = this.phraseFactory.createNounPhrase("椅子");
        AdjPhraseSpec green = this.phraseFactory.createAdjectivePhrase
                (this.phraseFactory.createWord("绿色", LexicalCategory.ADJECTIVE));
        chair.addPreModifier(green);
        Assert.assertEquals("绿色 的 椅子", this.realiser //$NON-NLS-1$
                .realise(chair).getRealisation());

        // without de and ordering the pre-modifiers
        AdjPhraseSpec large = this.phraseFactory.createAdjectivePhrase
                (this.phraseFactory.createWord("大号", LexicalCategory.ADJECTIVE));
        green.setFeature(LexicalFeature.NO_DE, true);
        chair.addPreModifier(large);
        Assert.assertEquals("大号 的 绿色 椅子", this.realiser //$NON-NLS-1$
                .realise(chair).getRealisation());

        chair = this.phraseFactory.createNounPhrase("椅子");
        NLGElement localiser = phraseFactory.createWord("左边", LexicalCategory.NOUN);
        localiser.setFeature(LexicalFeature.LOCATIVE, true);
        chair.addPreModifier(green);
        chair.addPostModifier(localiser);
        Assert.assertEquals("绿色 椅子 的 左边", this.realiser //$NON-NLS-1$
                .realise(chair).getRealisation());

        VPPhraseSpec orientation = this.phraseFactory
                .createVerbPhrase(phraseFactory.createWord("朝", LexicalCategory.VERB));
        orientation.setObject(localiser);
        chair = this.phraseFactory.createNounPhrase("椅子");
        chair.addPreModifier(green);
        chair.addPreModifier(orientation);
        Assert.assertEquals("朝 左边 的 绿色 椅子", this.realiser //$NON-NLS-1$
                .realise(chair).getRealisation());

        chair = this.phraseFactory.createNounPhrase("那", "椅子"); // illegal
        NLGElement classifier = this.phraseFactory.createWord("把", LexicalCategory.CLASSIFIER);
        green = this.phraseFactory.createAdjectivePhrase
                (this.phraseFactory.createWord("绿色", LexicalCategory.ADJECTIVE));
        chair.addPreModifier(green);
        chair.addSpecifier(classifier);
        Assert.assertEquals("那 把 绿色 的 椅子", this.realiser //$NON-NLS-1$
                .realise(chair).getRealisation());

        chair = this.phraseFactory.createNounPhrase("一", "把","椅子");
        chair.addPreModifier(green);
        Assert.assertEquals("一 把 绿色 的 椅子", this.realiser //$NON-NLS-1$
                .realise(chair).getRealisation());

        chair = this.phraseFactory.createNounPhrase("一", "把","椅子");
        chair.addPreModifier(green);

        NPPhraseSpec null_object = this.phraseFactory.createNounPhrase(null);
        null_object.addSpecifier(classifier);
        null_object.addSpecifier("那");
        null_object.addPreModifier(large);
        CoordinatedPhraseElement cnp = new CoordinatedPhraseElement(chair, null_object);
        cnp.topicalise();
        Assert.assertEquals("绿色 的 椅子， 那 把 大号 的。", this.realiser
                .realiseSentence(cnp));
    }

    @Test
    public void testPeople() {
        NPPhraseSpec person = this.phraseFactory.createNounPhrase("人");
        person.addPreModifier(phraseFactory.createWord("男", LexicalCategory.ADJECTIVE));
        AdjPhraseSpec high = this.phraseFactory.createAdjectivePhrase
                (phraseFactory.createWord("高", LexicalCategory.ADJECTIVE));
        person.addPreModifier(high);
        Assert.assertEquals("高 的 男 人", this.realiser //$NON-NLS-1$
                .realise(person).getRealisation());

        person = this.phraseFactory.createNounPhrase();
        person.addPreModifier(phraseFactory.createWord("男", LexicalCategory.ADJECTIVE));
        person.addPreModifier(high);
        Assert.assertEquals("高 的 男 的", this.realiser //$NON-NLS-1$
                .realise(person).getRealisation());

        NPPhraseSpec black = this.phraseFactory.createNounPhrase("黑框");
        NPPhraseSpec glasses = this.phraseFactory.createNounPhrase("眼镜");
        glasses.addPreModifier(black);
        Assert.assertEquals("黑框 眼镜", this.realiser //$NON-NLS-1$
                .realise(glasses).getRealisation());

        person = this.phraseFactory.createNounPhrase("人");
        person.addPreModifier(phraseFactory.createWord("男", LexicalCategory.ADJECTIVE));
        NPPhraseSpec hair = this.phraseFactory.createNounPhrase("头发");
        NLGElement white = this.phraseFactory.createWord("白", LexicalCategory.ADJECTIVE);
        white.setFeature(LexicalFeature.NO_DE, true);
        hair.addPreModifier(white);
        hair.setFeature(Feature.ASSOCIATIVE, true); // setting the feature manually
        person.addPreModifier(hair);
        Assert.assertEquals("白 头发 的 男 人", this.realiser //$NON-NLS-1$
                .realise(person).getRealisation());
    }
}
