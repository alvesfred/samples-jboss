package br.com.alvesfred.builder;

import java.io.Serializable;

/**
 * Person simple class information
 *
 * @author alvesfred
 *
 */
public class SimplePerson implements Serializable {

	/**
	 * serial
	 */
	private static final long serialVersionUID = -978731152684129660L;

	private String name;
	private int age;
	private boolean male;
	private boolean married;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}
	
	public boolean isMarried() {
		return married;
	}
	
	public void setMarried(boolean married) {
		this.married = married;
	}
}
