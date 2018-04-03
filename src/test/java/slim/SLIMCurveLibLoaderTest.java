/*
 * #%L
 * SLIM Curve package for exponential curve fitting of spectral lifetime data.
 * %%
 * Copyright (C) 2010 - 2014 Gray Institute University of Oxford and Board of
 * Regents of the University of Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package slim;

import org.junit.Test;

/**
 * Tests {@link NativeLoader}.
 * 
 * @author Dasong Gao
 */
public class SLIMCurveLibLoaderTest {
	/** Tests {@link NativeLoader#loadLibrary}. */
	@Test
	public void testLoader() {
		NativeLoader.loadLibrary("slim-curve");
		NativeLoader.loadLibrary("slim-curve-jni");
	}
}
