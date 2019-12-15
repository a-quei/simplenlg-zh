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


import simplenlg.features.LexicalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.mandarin.Realiser;

/**
 * This class is the base class for all JUnit simplenlg.test cases for
 * simplenlg. It sets up a a JUnit fixture, i.e. the basic objects (basic
 * constituents) that all other tests can use.
 * @author agatt
 */
public abstract class SimpleNLG4Test {

	/** The realiser. */
	protected Realiser realiser;

	protected NLGFactory phraseFactory;
	
	protected Lexicon lexicon;
	
	/** The pro test2. */
	protected PhraseElement man, woman, dog, boy, np4, np5, np6, np7, np8, np9, proTest1, proTest2;

	/** The salacious. */
	protected PhraseElement beautiful, cute, salacious;

	protected NLGElement localiser1, localiser2, localiser3;

	/** The under the table. */
	protected PhraseElement onTheRock, behindTheCurtain, inTheRoom, underTheTable;

	/** The say. */
	protected VPPhraseSpec kick, kiss, walk, talk, getUp, fallDown, give, say;

	/**
	 * Set up the variables we'll need for this simplenlg.test to run (Called
	 * automatically by JUnit)
	 */
    @Before
	protected void setUp() {
        lexicon = new XMLLexicon();  // built in lexicon

		this.phraseFactory = new NLGFactory(this.lexicon);
		this.realiser = new Realiser(this.lexicon);
		
		this.man = this.phraseFactory.createNounPhrase("一", "个", "男人");
		this.woman = this.phraseFactory.createNounPhrase("一", "个", "女人");
		this.dog = this.phraseFactory.createNounPhrase("一", "条","狗");
		this.boy = this.phraseFactory.createNounPhrase("一", "个", "男孩");

		this.beautiful = this.phraseFactory.createAdjectivePhrase("美丽");
		this.cute = this.phraseFactory.createAdjectivePhrase("可爱");
		this.salacious = this.phraseFactory.createAdjectivePhrase("好色");

		this.onTheRock = this.phraseFactory.createPrepositionPhrase("在");
		this.np4 = this.phraseFactory.createNounPhrase("一", "块", "石头");

        this.np8 = this.phraseFactory.createNounPhrase("一", "块", "石头");
		this.localiser1 = phraseFactory.createWord("上", LexicalCategory.NOUN);
        this.np8.addPostModifier(localiser1);
        this.localiser1.setFeature(LexicalFeature.LOCATIVE, true);
		this.onTheRock.addComplement(this.np8);

		this.behindTheCurtain = this.phraseFactory.createPrepositionPhrase("在");
		this.np5 = this.phraseFactory.createNounPhrase("窗帘");

        this.np9 = this.phraseFactory.createNounPhrase("窗帘");
        this.localiser2 = phraseFactory.createWord("后面", LexicalCategory.NOUN);
		this.np9.addPostModifier(localiser2);
		this.behindTheCurtain.addComplement(this.np9);

		this.inTheRoom = this.phraseFactory.createPrepositionPhrase("在");
		this.np6 = this.phraseFactory.createNounPhrase("房间");

		this.np7 = this.phraseFactory.createNounPhrase("房间");
		this.localiser3 = phraseFactory.createWord("里", LexicalCategory.NOUN);
		this.np7.addPostModifier(this.localiser3);
		this.inTheRoom.addComplement(this.np7);

		this.underTheTable = this.phraseFactory.createPrepositionPhrase("在");
		this.underTheTable.addComplement(this.phraseFactory.createNounPhrase("一", "张", "桌子"));

		this.proTest1 = this.phraseFactory.createNounPhrase("一", "位", "歌手");
		this.proTest2 = this.phraseFactory.createNounPhrase("一", "群", "人");

		this.kick = this.phraseFactory.createVerbPhrase("kick"); //$NON-NLS-1$
		this.kiss = this.phraseFactory.createVerbPhrase("亲"); //$NON-NLS-1$
		this.walk = this.phraseFactory.createVerbPhrase("走"); //$NON-NLS-1$
		this.talk = this.phraseFactory.createVerbPhrase("talk"); //$NON-NLS-1$
		this.getUp = this.phraseFactory.createVerbPhrase("get up"); //$NON-NLS-1$
		this.fallDown = this.phraseFactory.createVerbPhrase("fall down"); //$NON-NLS-1$
		this.give = this.phraseFactory.createVerbPhrase("给"); //$NON-NLS-1$
		this.say = this.phraseFactory.createVerbPhrase("say"); //$NON-NLS-1$
	}

    @After
	public void tearDown() {
		this.realiser = null;

		this.phraseFactory = null;
		
		if(null != lexicon) {
			lexicon = null;
		}
		
		this.man = null; this.woman = null; this.dog = null; this.boy = null; 
		this.np4 = null; this.np5 = null; this.np6 = null; this.proTest1 = null; 
		this.proTest2 = null;
		
		this.beautiful = null; this.cute = null; this.salacious = null;

		this.onTheRock = null; this.behindTheCurtain= null; 
		this.inTheRoom = null; this.underTheTable = null;

		this.kick = null; this.kiss = null;  this.walk = null; this.talk = null; 
		this.getUp = null; this.fallDown = null; this.give = null; this.say = null;
	}
	
	
}
