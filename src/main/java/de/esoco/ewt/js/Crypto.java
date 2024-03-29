//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// chaintag source file
// Copyright (c) 2018 by Elmar Sonnenschein / esoco GmbH
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.ewt.js;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.ArrayBufferView;

/**
 * JSInterop wrapper for the <a
 * href="https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API">main
 * object of the WebCrypto API</a>.
 *
 * @author eso
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "crypto")
public class Crypto {

	/**
	 * Property for access to the {@link SubtleCrypto} instance.
	 */
	public static SubtleCrypto subtle;

	/**
	 * JSInterop wrapper for the <a
	 * href="https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto">
	 * SuptleCrypto object of the WebCrypto API</a>.
	 *
	 * @author eso
	 */
	@JsType(isNative = true, namespace = "Crypto")
	public static class SubtleCrypto {

		/**
		 * @see <a href="https://developer.mozilla
		 * .org/en-US/docs/Web/API/SubtleCrypto/digest">
		 * SubtleCrypto.digest()</a>
		 */
		@SuppressWarnings("unusable-by-js")
		public native Promise<ArrayBuffer> digest(String algorithm,
			ArrayBuffer buffer);

		/**
		 * @see <a href="https://developer.mozilla
		 * .org/en-US/docs/Web/API/SubtleCrypto/digest">
		 * SubtleCrypto.digest()</a>
		 */
		@SuppressWarnings("unusable-by-js")
		public native Promise<ArrayBuffer> digest(String algorithm,
			ArrayBufferView buffer);
	}
}
