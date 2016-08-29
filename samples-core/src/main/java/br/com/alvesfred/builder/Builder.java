package br.com.alvesfred.builder;

/**
 * Builder pattern
 *
 * @author alvesfred
 *
 */
public abstract class Builder<T> {
	/**
	 * Build
	 *
	 * @return
	 */
	public abstract T build();
}
