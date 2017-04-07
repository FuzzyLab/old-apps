package com.fuzzylabs.fuzzywords;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import com.fuzzylabs.fuzzywords.R;

public class AboutApp extends Activity{
	String about;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		Typeface fonti = Typeface.createFromAsset(getAssets(), "Filmcrypob.ttf");
		TextView label = (TextView) findViewById(R.id.item_about);
		label.setTypeface(fonti);
		label.setText("Fuzzy Words is a collection of interesting words, words that can express a situation or a feeling, lucidly, doing away with the explanatory descriptions. Words that are self-sufficient, self-explanatory. Some of them are easy on your lips, you will love to utter them every now and then. Some of them, simply unspeakable! Try them all out. Besides, it&apos;s always better to know that little bit more than others. Just to satiate our appetite of being better. So plunge in, make friends with new words, words like Dysania, Accismus, Torschlusspanik and a lot more!\nP.S. We would be more than happy to receive your suggestions and feedbacks to improve this app. Do write at contact.fuzzylabs@gmail.com");
		
	}
}
