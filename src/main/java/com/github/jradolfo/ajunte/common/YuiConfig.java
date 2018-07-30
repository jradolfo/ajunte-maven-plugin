package com.github.jradolfo.ajunte.common;


/**
 * <a href="http://yui.github.io/yuicompressor/">YUI Compressor</a> configuration.
 */
public class YuiConfig {
	
	private final int lineBreak;

	private final boolean munge;

	private final boolean preserveSemicolons;

	private final boolean disableOptimizations;

	/**
	 * Init YuiConfig values.
	 *
	 * @param lineBreak
	 *            split long lines after a specific column
	 * @param munge
	 *            obfuscate local symbols
	 * @param preserveSemicolons
	 *            preserve unnecessary semicolons
	 * @param disableOptimizations
	 *            disable all the built-in micro-optimizations
	 */
	public YuiConfig(int lineBreak, boolean munge, boolean preserveSemicolons, boolean disableOptimizations) {
		this.lineBreak = lineBreak;
		this.munge = munge;
		this.preserveSemicolons = preserveSemicolons;
		this.disableOptimizations = disableOptimizations;
	}

	/**
	 * Gets the lineBreak.
	 *
	 * @return the lineBreak
	 */
	public int getLineBreak() {
		return lineBreak;
	}

	/**
	 * Gets the munge.
	 *
	 * @return the munge
	 */
	public boolean isMunge() {
		return munge;
	}

	/**
	 * Gets the preserveSemicolons.
	 *
	 * @return the preserveSemicolons
	 */
	public boolean isPreserveSemicolons() {
		return preserveSemicolons;
	}

	/**
	 * Gets the disableOptimizations.
	 *
	 * @return the disableOptimizations
	 */
	public boolean isDisableOptimizations() {
		return disableOptimizations;
	}
	
}
