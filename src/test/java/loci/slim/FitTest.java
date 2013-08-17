//
// FitTest.java
//

/*
Java layer for SLIM Curve library for fitting exponential decay curves.

Copyright (c) 2010-2013, UW-Madison LOCI
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package loci.slim;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aivar Grislis
 */
public class FitTest {
	private static final int PROMPT_BASELINE = 0;
	private static final int TRANSIENT_START = 1;
	private static final int DATA_START = 2;
	private static final int TRANSIENT_END = 3;
	private static final int PROMPT_DELTA = 4;
	private static final int PROMPT_WIDTH = 5;
	
	private SLIMCurve slimCurve;
	
	public FitTest() {
		slimCurve = new SLIMCurve();
	}
	
	private double[] getTransient1() {
		return new double[] { 0, 3, 1, 1, 1, 0, 2, 0, 0, 0, 1, 0, 2, 0, 1, 0,
			                  2, 1, 1, 0, 1, 1, 0, 2, 1, 3, 0, 2, 1, 0, 0, 1,
							  2, 1, 3, 0, 2, 0, 2, 0, 1, 1, 0, 2, 2, 1, 2, 1,
							  0, 2, 2, 1, 6, 10, 6, 5, 2, 7, 8, 4, 13, 8, 9, 3,
							  6, 2, 6, 2, 8, 7, 5, 4, 3, 4, 6, 4, 4, 7, 2, 9,
							  6, 6, 1, 3, 2, 4, 6, 2, 5, 4, 1, 2, 4, 2, 2, 4,
							  4, 2, 6, 4, 1, 2, 5, 6, 4, 3, 1, 2, 4, 3, 6, 2,
							  5, 4, 1, 3, 5, 4, 3, 3, 0, 2, 5, 1, 3, 3, 0, 5,
							  2, 3, 0, 2, 2, 2, 4, 2, 5, 1, 3, 0, 2, 0, 3, 2,
							  1, 1, 1, 3, 1, 3, 4, 2, 2, 4, 5, 1, 0, 0, 3, 1,
							  1, 4, 1, 2, 1, 2, 1, 0, 3, 1, 4, 1, 1, 1, 3, 2,
							  0, 1, 0, 1, 1, 2, 1, 0, 1, 1, 1, 3, 2, 1, 3, 0,
							  4, 2, 1, 2, 0, 2, 3, 2, 2, 2, 3, 0, 0, 0, 2, 3,
							  1, 2, 3, 0, 3, 0, 1, 0, 1, 1, 1, 2, 0, 2, 1, 1,
							  1, 0, 2, 1, 2, 1, 4, 2, 2, 0, 1, 2, 2, 1, 2, 0,
							  1, 0, 1, 0, 1, 1, 1, 3, 0, 0, 0, 1, 0, 0, 0, 2 };
	}
	
	private double[] getPrompt1() {
		return new double[] { 0.013579, 0.0494665, 0.123181, 0.20999, 0.245635, 0.197381, 0.108875, 0.0412221, 0.0106693 };
	}

	/**
	 * Returns cursors in the following order:<p>
	 * prompt baseline<p>
	 * transient start<p>
	 * data start<p>
	 * transient end<p>
	 * prompt delta<p>
	 * prompt width<p>
	 * 
	 * @return 
	 */
	private double[] getCursors1() {
		return new double[] { 5.079646, 2.403800, 2.931400, 13.484600, -0.820800, 0.410400 };
	}
	
	private double getXInc1() {
		return 0.1;
	}
	
	private double[] getSig1() {
		return null;
	}
	
	private double[] getInitZ1() {
		return new double[] { 0.0 };
	}
	
	private double[] getInitA1() {
		return new double[] { 1000.0 };
	}
	
	private double[] getInitTau1() {
		return new double[] { 2.0 };
	}
	
	private double getChiSquareTarget() {
		return 1.25;
	}
	
    public double[] getAdjustedTransient(double[] trans, int transStartIndex, int transEndIndex) {
        int size = transEndIndex - transStartIndex;
        double[] adjusted = new double[size];
        for (int i = 0; i < size; ++i) {
            adjusted[i] = trans[i + transStartIndex];
        }
        return adjusted;
    }

	@Test
	public void doFit1() {
		double[] cursors = getCursors1();
		double xInc = getXInc1();
		int transStartIndex = (int)(cursors[TRANSIENT_START] / xInc);
		int dataStartIndex  = (int)(cursors[DATA_START]      / xInc);
		int transEndIndex   = (int)(cursors[TRANSIENT_END]   / xInc);
		int fit_start = dataStartIndex - transStartIndex;
		int fit_end   = transEndIndex  - transStartIndex;
		
		double[] prompt = getPrompt1();
		int nInstr = prompt.length;
		
		double[] trans = getTransient1();
		trans = getAdjustedTransient(trans, transStartIndex, transEndIndex);
		
		int noise = 1;
		
		double[] fitted = new double[trans.length];
		double[] chiSquare = new double[1];
		double chiSquareTarget = 1.25;
		int chiSquareAdjust = fit_end - fit_start - 3;
		
		double[] a = getInitA1();
		double[] tau = getInitTau1();
		double[] z = getInitZ1();
		
		int returnCode = slimCurve.fitRLD(
				xInc,
				trans,
				fit_start,
				fit_end,
				prompt,
				nInstr,
				noise,
				getSig1(),
				z,
				a,
				tau,
				fitted,
				chiSquare,
				chiSquareTarget * chiSquareAdjust);
		
		System.out.println("return code is " + returnCode);
		System.out.println("A " + a[0] + " T " + tau[0] + " Z " + z[0]);
		System.out.println("chisquare " + chiSquare[0] / chiSquareAdjust);
	}
}
