package br.com.alvesfred.builder;

/**
 * Builder pattern class
 *
 * @author alvesfred
 *
 */
public class Person {

	private final String name;
	private final int age;
	private final boolean male;
	private final boolean married;

	public Person(PersonBuilder pfb) {
		this.name    = pfb.name;
		this.age     = pfb.age;
		this.male    = pfb.male;
		this.married = pfb.married;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public boolean isMale() {
		return male;
	}

	public boolean isMarried() {
		return married;
	}

	/**
	 * Builder class sample
	 *
	 * @author alvesfred
	 *
	 */
	public static class PersonBuilder extends Builder<Person> {
		private final String name;
		private int age;
		private boolean male;
		private boolean married;

		public PersonBuilder(String name) {
			this.name = name;
		}

		public PersonBuilder defineAge(int age) {
			this.age = age;
			return this;
		}

		public PersonBuilder defineMale(boolean male) {
			this.male = male;
			return this;
		}

		public PersonBuilder defineMarried(boolean married) {
			this.married = married;
			return this;
		}

		public Person build() {
            return new Person(this);
        }
	}

	@Override
	public String toString() {
		return "[PERSON] "        + name 
				+ " - [Age] "     + age 
				+ " - [Male?] "   + male
				+ " - [Married] " + married;
	}

	/**
	 * Main
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Person p = new PersonBuilder("Fred")
			.defineAge(38)
			.defineMale(true)
			.defineMarried(true)
			.build();

		System.out.println(p); 
	}
}
