package br.com.alvesfred.util;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import br.com.alvesfred.util.ObjectExplorer.Feature;
import br.com.alvesfred.util.ObjectVisitor.Traversal;

/**
 * Class to show the size of objects
 *
 * @author alvesfred
 *
 */
public class ObjectSizeMemory {
	public static class PrintObj {
		private final int objects;
		private final int references;
		private final ImmutableMultiset<Class<?>> primitives;

		private static final ImmutableSet<Class<?>> primitiveTypes = ImmutableSet
				.<Class<?>> of(
						boolean.class, byte.class, char.class,
						short.class, int.class, float.class, long.class,
						double.class);

		public PrintObj(int objects, int references,
				Multiset<Class<?>> primitives) {
			Preconditions.checkArgument(objects >= 0,
					"Negative objects");
			Preconditions.checkArgument(references >= 0,
					"Negative references");
			Preconditions.checkArgument(
					primitiveTypes.containsAll(primitives.elementSet()),
					"Unexpected primitive class (type)");
			this.objects = objects;
			this.references = references;
			this.primitives = ImmutableMultiset.copyOf(primitives);
		}

		public int getObjects() {
			return objects;
		}

		public int getReferences() {
			return references;
		}

		public ImmutableMultiset<Class<?>> getPrimitives() {
			return primitives;
		}

		@Override
		// https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
		// char -> 16 bits, 2 bytes; int -> 32 bits, 4 bytes; float -> 32 bits, long, double -> 64 bits, boolean -> 1 bit
		public String toString() {
			long valueBytes = getTypeSize(primitives);
			return "[METRICS] " +
					"\n Number of Objects: "     + objects    + 
					"\n References: "            + references +
					"\n Classes (primitives): "  + primitives +
					"\n Size in Bytes: "         + valueBytes +
					"\n Size in MB: "            + (double) valueBytes / (1024 * 1024);
		}

		// Aproximado...
		private static long getTypeSize(ImmutableMultiset<Class<?>> primitives) {
			if (primitives == null) return -1; // desconhecido

			String msg = primitives.toString().trim().replace("[", "");
			msg = msg.replace("]", "");
			StringTokenizer st = new StringTokenizer(msg, ",");
			long total = 0;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				StringTokenizer st2 = new StringTokenizer(token, "x");
				long qtde = 1;
				int valor = 1;
				int count = 0;
				String token2 = "";
				while (st2.hasMoreTokens()) {
					token2 = st2.nextToken().trim();
					count++;
					if (count == 1) {
						if      (token2.contains("byte")  || token2.contains("boolean")) valor = 1;
						else if (token2.contains("short") || token2.contains("char"))    valor = 2;
						else if (token2.contains("int")   || token2.contains("float"))   valor = 4;
						else if (token2.contains("long")  || token2.contains("double"))  valor = 8;
						else valor = 4;
					} else {
						qtde = new Long(token2);
					}
				}
				count = 0;
				total = total + (qtde * valor);
			}

			return total;
		}
	}

	/**
	 * bytes
	 *
	 * @param obj
	 * @return
	 */
	public static final long getBytesSizeObject(Object obj) {
		return ObjectSizeFetcher.getObjectSize(obj);
	}

	/**
	 * references...
	 *
	 * @param rootObject
	 * @return
	 */
	public static final PrintObj measure(Object rootObject) {
		return measure(rootObject, Predicates.alwaysTrue());
	}
	
	public static String capacityArrayList(Object obj) {
		try {
			Field f = ArrayList.class.getDeclaredField("elementData");
			f.setAccessible(true);
			int capacity = ((Object[]) f.get(obj)).length;

			return String.format("Capacity (array): " + capacity);

		} catch (Exception ex) {
			return "Objeto Indefinido: " + ex.getMessage();
		}
	}

	public static PrintObj measure(Object rootObject,
			Predicate<Object> objectAcceptor) {
		Preconditions.checkNotNull(objectAcceptor, "predicate");

		Predicate<Chain> completePredicate = Predicates.and(ImmutableList.of(
				ObjectExplorer.notEnumFieldsOrClasses, Predicates.compose(
						objectAcceptor, ObjectExplorer.chainToObject),
				new ObjectExplorer.AtMostOncePredicate()));

		return ObjectExplorer.exploreObject(rootObject, new ObjectGraphVisitor(
				completePredicate), EnumSet.of(
						Feature.VISIT_PRIMITIVES,
						Feature.VISIT_NULL));
	}

	private static class ObjectGraphVisitor implements ObjectVisitor<PrintObj> {
		private int objects;
		// -1 to account for the root, which has no reference leading to it
		private int references = -1;
		private final Multiset<Class<?>> primitives = HashMultiset.create();
		private final Predicate<Chain> predicate;

		ObjectGraphVisitor(Predicate<Chain> predicate) {
			this.predicate = predicate;
		}

		public Traversal visit(Chain chain) {
			if (chain.isPrimitive()) {
				primitives.add(chain.getValueType());
				return Traversal.SKIP;
			} else {
				references++;
			}
			if (predicate.apply(chain) && chain.getValue() != null) {
				objects++;
				return Traversal.EXPLORE;
			}
			return Traversal.SKIP;
		}

		public PrintObj result() {
			return new PrintObj(objects, references,
					ImmutableMultiset.copyOf(primitives));
		}
	}

	// Exemplo de Uso
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		Set<Integer> hashset = new HashSet<Integer>();
		Random rng = new Random();
		int n = 10000;
		for (int i = 1; i <= n; i++) {
			hashset.add(rng.nextInt());
		}
		System.out.println(ObjectSizeMemory.measure(hashset));
		System.out.println(ObjectSizeMemory.capacityArrayList(new ArrayList(hashset)));
	}
}

/**
 * sizeof...
 *
 * @author fred
 *
 */
class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}

abstract class Chain {
	private final Object value;
	private final Chain parent;

	Chain(Chain parent, Object value) {
		this.parent = parent;
		this.value = value;
	}

	static Chain root(Object value) {
		return new Chain(null, Preconditions.checkNotNull(value)) {
			@Override
			public Class<?> getValueType() {
				return getValue().getClass();
			}
		};
	}

	FieldChain appendField(Field field, Object value) {
		return new FieldChain(this, Preconditions.checkNotNull(field), value);
	}

	ArrayIndexChain appendArrayIndex(int arrayIndex, Object value) {
		return new ArrayIndexChain(this, arrayIndex, value);
	}

	public boolean hasParent() {
		return parent != null;
	}

	public Chain getParent() {
		Preconditions.checkState(parent != null,
				"This is the root value, it has no parent");
		return parent;
	}

	public Object getValue() {
		return value;
	}

	public abstract Class<?> getValueType();

	public boolean isThroughField() {
		return false;
	}

	public boolean isThroughArrayIndex() {
		return false;
	}

	public boolean isPrimitive() {
		return getValueType().isPrimitive();
	}

	public Object getRoot() {
		Chain current = this;
		while (current.hasParent()) {
			current = current.getParent();
		}
		return current.getValue();
	}

	Deque<Chain> reverse() {
		Deque<Chain> reverseChain = new ArrayDeque<Chain>(8);
		Chain current = this;
		reverseChain.addFirst(current);
		while (current.hasParent()) {
			current = current.getParent();
			reverseChain.addFirst(current);
		}
		return reverseChain;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(32);

		Iterator<Chain> it = reverse().iterator();
		sb.append(it.next().getValue());
		while (it.hasNext()) {
			sb.append("->");
			Chain current = it.next();
			if (current.isThroughField()) {
				sb.append(((FieldChain) current).getField().getName());
			} else if (current.isThroughArrayIndex()) {
				sb.append("[")
						.append(((ArrayIndexChain) current).getArrayIndex())
						.append("]");
			}
		}
		return sb.toString();
	}

	static class FieldChain extends Chain {
		private final Field field;

		FieldChain(Chain parent, Field referringField, Object value) {
			super(parent, value);
			this.field = referringField;
		}

		@Override
		public boolean isThroughField() {
			return true;
		}

		@Override
		public boolean isThroughArrayIndex() {
			return false;
		}

		@Override
		public Class<?> getValueType() {
			return field.getType();
		}

		public Field getField() {
			return field;
		}
	}

	static class ArrayIndexChain extends Chain {
		private final int index;

		ArrayIndexChain(Chain parent, int index, Object value) {
			super(parent, value);
			this.index = index;
		}

		@Override
		public boolean isThroughField() {
			return false;
		}

		@Override
		public boolean isThroughArrayIndex() {
			return true;
		}

		@Override
		public Class<?> getValueType() {
			return getParent().getValue().getClass().getComponentType();
		}

		public int getArrayIndex() {
			return index;
		}
	}
	
	public static void main(String[] args) {
		Set<Integer> hashset = new HashSet<Integer>();
	    Random rng = new Random();
	    int n = 10000;
	    for (int i = 1; i <= n; i++) {
	      hashset.add(rng.nextInt());
	    }
	    System.out.println(ObjectSizeMemory.measure(hashset));
	}
}

class ObjectExplorer {
	private ObjectExplorer() {
	}

	public static <T> T exploreObject(Object rootObject,
			ObjectVisitor<T> visitor) {
		return exploreObject(rootObject, visitor, EnumSet.noneOf(Feature.class));
	}

	public static <T> T exploreObject(Object rootObject,
			ObjectVisitor<T> visitor, EnumSet<Feature> features) {
		Deque<Chain> stack = new ArrayDeque<Chain>(32);
		if (rootObject != null)
			stack.push(Chain.root(rootObject));

		while (!stack.isEmpty()) {
			Chain chain = stack.pop();
			Traversal traversal = visitor.visit(chain);
			switch (traversal) {
				case SKIP:
					continue;
				case EXPLORE:
					break;
			}

			Object value = chain.getValue();
			Class<?> valueClass = value.getClass();
			if (valueClass.isArray()) {
				boolean isPrimitive = valueClass.getComponentType()
						.isPrimitive();
				for (int i = Array.getLength(value) - 1; i >= 0; i--) {
					Object childValue = Array.get(value, i);
					if (isPrimitive) {
						if (features.contains(Feature.VISIT_PRIMITIVES))
							visitor.visit(chain.appendArrayIndex(i, childValue));
						continue;
					}
					if (childValue == null) {
						if (features.contains(Feature.VISIT_NULL))
							visitor.visit(chain.appendArrayIndex(i, childValue));
						continue;
					}
					stack.push(chain.appendArrayIndex(i, childValue));
				}
			} else {
				for (Field field : getAllFields(value)) {
					if (Modifier.isStatic(field.getModifiers()))
						continue;
					Object childValue = null;
					try {
						childValue = field.get(value);
					} catch (Exception e) {
						throw new AssertionError(e);
					}
					if (childValue == null) {
						if (features.contains(Feature.VISIT_NULL))
							visitor.visit(chain.appendField(field, childValue));
						continue;
					}
					boolean isPrimitive = field.getType().isPrimitive();
					Chain extendedChain = chain.appendField(field, childValue);
					if (isPrimitive) {
						if (features.contains(Feature.VISIT_PRIMITIVES))
							visitor.visit(extendedChain);
						continue;
					} else {
						stack.push(extendedChain);
					}
				}
			}
		}
		return visitor.result();
	}

	public static class AtMostOncePredicate implements Predicate<Chain> {
		private final Set<Object> interner = Collections.newSetFromMap(
				new IdentityHashMap<Object, Boolean>());

		public boolean apply(Chain chain) {
			Object o = chain.getValue();
			return o instanceof Class<?> || interner.add(o);
		}
	}

	static final Predicate<Chain> notEnumFieldsOrClasses = new Predicate<Chain>() {
		public boolean apply(Chain chain) {
			return !(Enum.class.isAssignableFrom(chain.getValueType()) || chain
					.getValue() instanceof Class<?>);
		}
	};

	static final Function<Chain, Object> chainToObject = new Function<Chain, Object>() {
		public Object apply(Chain chain) {
			return chain.getValue();
		}
	};

	private static Iterable<Field> getAllFields(Object o) {
		List<Field> fields = Lists.newArrayListWithCapacity(8);
		Class<?> clazz = o.getClass();
		while (clazz != null) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}

		AccessibleObject.setAccessible(
				fields.toArray(new AccessibleObject[fields.size()]), true);
		return fields;
	}

	public enum Feature {
		VISIT_NULL,
		VISIT_PRIMITIVES
	}
}

interface ObjectVisitor<T> {
	Traversal visit(Chain chain);
	T result();

	enum Traversal {
		EXPLORE,
		SKIP
	}
}