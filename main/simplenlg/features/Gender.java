package simplenlg.features;

/**
 * <p>
 * An enumeration representing the gender of the subject of a noun phrase, or
 * the object or subject of a verb phrase. It is most commonly used with
 * personal pronouns. The gender is recorded in the {@code Feature.GENDER}
 * feature and applies to nouns and pronouns.
 * </p>
 *
 * Adaptation for Chinese has been done
 * Changes:
 *  1. Add a new category: MIXED
 * 
 */

public enum Gender {

	/**
	 * A word or phrase pertaining to a male topic. For example, <em>他</em>,
	 * <em>他 的</em>.
	 */
	MASCULINE,

	/**
	 * A word or phrase pertaining to a female topic. For example, <em>他</em>,
	 * <em>她们</em>.
	 */
	FEMININE,

	/**
	 * A word or phrase pertaining to a neutral or gender-less topic. For
	 * example, <em>它</em>, <em>它的</em>.
	 */
	NEUTER,

    /**
     * A word or phrase pertaining to a mixed gender topic. For example, <em>他</em>,
     * <em>他们</em>.
     */
    MIXED;
}
