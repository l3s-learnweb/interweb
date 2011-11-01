package de.l3s.profileMatcher;

import com.wcohen.ss.SmithWaterman;

import de.l3s.profileMatcher.StringDistance;

public class JaccardDistance extends StringDistance {

	private SmithWaterman jaccard;

	public JaccardDistance(String string1) {
		super(string1);
		
		jaccard = new SmithWaterman();
		System.out.println(jaccard.prepare(string1));
	}

	@Override
	public double getDistance(String string2) 
	{
		return jaccard.score(string1, string2);
	}

}
