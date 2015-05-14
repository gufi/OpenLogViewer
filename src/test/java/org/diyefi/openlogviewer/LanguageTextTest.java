package org.diyefi.openlogviewer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.diyefi.openlogviewer.Text;

import static org.junit.Assert.*;

/**
 * A set of tests to shake down the translations setup. It won't catch defaults pasted into the non-English file, but that shouldn't be done anyway.
 *
 * @author Fred Cooke
 */
public class LanguageTextTest {

	private final Field[] keys = Text.class.getDeclaredFields();
	private final Set<String> keySet = new HashSet<String>();

	/**
	 * Ensure that no keys are themselves blank, forgotten or duplicated.
	 *
	 * Doing it here gets us a line number and blatant stack traces dumped.
	 */
	public LanguageTextTest() {
		Validate.isTrue(keys.length > 0);
		// System.out.println("Text lookups: " + keys.length);
		for (Field k : keys) {
			String key = null;
			try {
				key = (String)k.get(null);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				Validate.isTrue(false); // Should never happen
			}
			Validate.notBlank(key); // No empties/blanks/nulls
			Validate.isTrue(keySet.add(key)); // No dupes
		}
	}

	// Individual tests below so as to see what went wrong, at a glance

	@Test
	public void testEnglishComplete() {
		testLanguageComplete(Locale.ENGLISH, "");
	}

	@Test
	public void testEnglishNoUnused() {
		testNoUnusedText(Locale.ENGLISH, "");
	}

	@Test
	@Ignore("Not yet complete!")
	public void testSpanishComplete() {
		testLanguageComplete(new Locale("es"), "_es");
	}

	@Test
	public void testSpanishNoUnused() {
		testNoUnusedText(new Locale("es"), "_es");
	}

	/**
	 * Ensure that all keys for this language have a valid label
	 *
	 * @param locale
	 * @param suffix
	 */
	private void testLanguageComplete(Locale locale, String suffix) {
		ResourceBundle labels = getLabels(locale, suffix);

		for (String key : keySet) {
			String value = labels.getString(key);
			// System.out.println("Key/value: " + key + " = \"" + value + "\""); // Useful while debugging
			assertTrue(StringUtils.isNotBlank(value));
		}
	}

	/**
	 * Ensure that everything in the properties files is referred to by a field in the text class.
	 *
	 * @param locale
	 * @param suffix
	 */
	private void testNoUnusedText(Locale locale, String suffix) {
		ResourceBundle labels = getLabels(locale, suffix);

		for (String label : labels.keySet()) {
			// System.out.println("Label: " + label); // Useful while debugging
			assertTrue(keySet.contains(label));
		}
	}

	private ResourceBundle getLabels(Locale locale, String suffix) {
		return ResourceBundle.getBundle(OpenLogViewer.class.getPackage().getName() + ".Labels" + suffix , locale);
	}
}
