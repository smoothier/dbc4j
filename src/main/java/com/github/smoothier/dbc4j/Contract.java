package com.github.smoothier.dbc4j;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Contract<T, M> implements AutoCloseable {
	private static final ThreadLocal<Map<Object, Contract<?, ?>>> CONTRACTS = ThreadLocal.withInitial(IdentityHashMap::new);
	private static final ThreadLocal<Deque<Contract<?, ?>>> CONTRACT_STACK = ThreadLocal.withInitial(LinkedList::new);
	
	protected final T obj;
	protected final M original;
	private final Invariant<T> invariant;
	private boolean locked = false;
	
	public static <T, M> Contract<T, M> Contract(final T obj, final Invariant<T> invariant) {
		return Contract(obj, invariant, it -> null);
	}
	
	public static <T, M> Contract<T, M> Contract(final T obj, final Invariant<T> invariant, final Function<T, M> original) {
		final @SuppressWarnings("unchecked") var result = (Contract<T, M>) CONTRACTS.get().computeIfAbsent(obj, k -> new Contract<>(obj, invariant,original));
		CONTRACT_STACK.get().push(result);
		return result;
	}
	
	private Contract(final T obj, final Invariant<T> invariant, final Function<T, M> original) {
		this.obj = Objects.requireNonNull(obj);
		this.original = Objects.requireNonNull(original).apply(obj);
		this.invariant = Objects.requireNonNull(invariant);
		
		Invariant(invariant);
	}
	
	@Override
	public void close() {
		Invariant(invariant);
		
		CONTRACT_STACK.get().pop();
		if (CONTRACT_STACK.get().isEmpty()) {
			CONTRACT_STACK.remove();
		}
		if (!CONTRACT_STACK.get().contains(this)) {
			CONTRACTS.get().remove(obj);
			if (CONTRACTS.get().isEmpty()) {
				CONTRACTS.remove();
			}
		}
	}
	
	private static <T> void Invariant(final Invariant<T> invariant) {
		try {
			final @SuppressWarnings("unchecked") var contract = (Contract<T,?>) CONTRACT_STACK.get().peek();
			if (contract != null && !contract.locked) {
				contract.locked = true;
				try {
					invariant.check((T) contract.obj);
				} finally {
					contract.locked = false;
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new InvariantViolatedException(e);
		} catch (final IllegalStateException e) {
			throw new InvariantViolatedException(e);
		} catch (final ContractViolationException e) {
			throw e;
		} catch (final Exception e) {
			throw new ContractNotVerifiableException(e);
		}
	}
	
	public static void Precondition(final Precondition precondition) {
		try {
			final var contract = CONTRACT_STACK.get().peek();
			if (contract != null && !contract.locked) {
				contract.locked = true;
				try {
					precondition.check();
				} finally {
					contract.locked = false;
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new PostconditionViolatedException(e);
		} catch (final IllegalStateException e) {
			throw new PostconditionViolatedException(e);
		} catch (final ContractViolationException e) {
			throw e;
		} catch (final Exception e) {
			throw new ContractNotVerifiableException(e);
		}
	}
	
	
	public static <M> void Postcondition(final Postcondition<M> postcondition) {
		try {
			final var contract = CONTRACT_STACK.get().peek();
			if (contract != null && !contract.locked) {
				contract.locked = true;
				try {
					//noinspection unchecked
					postcondition.check((M) contract.original);
				} finally {
					contract.locked = false;
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new PostconditionViolatedException(e);
		} catch (final IllegalStateException e) {
			throw new PostconditionViolatedException(e);
		} catch (final ContractViolationException e) {
			throw e;
		} catch (final Exception e) {
			throw new ContractNotVerifiableException(e);
		}
	}
	
	public static final boolean NOTHING = true;
	
	@org.jetbrains.annotations.Contract("false,_ -> fail")
	public static void require(boolean condition, Supplier<String> msg) {
		if (!condition) {
			throw new IllegalArgumentException(msg.get());
		}
	}
	
	@org.jetbrains.annotations.Contract("false,_ -> fail")
	public static void ensure(boolean condition, Supplier<String> msg) {
		if (!condition) {
			throw new IllegalStateException(msg.get());
		}
	}
	
	@org.jetbrains.annotations.Contract("false,_ -> fail")
	public static void check(boolean condition, Supplier<String> msg) {
		if (!condition) {
			throw new IllegalStateException(msg.get());
		}
	}
	
	@org.jetbrains.annotations.Contract("false -> fail")
	public static void require(boolean condition) {
		if (!condition) {
			throw new IllegalArgumentException();
		}
	}
	
	@org.jetbrains.annotations.Contract("false -> fail")
	public static void ensure(boolean condition) {
		if (!condition) {
			throw new IllegalStateException();
		}
	}
	
	@org.jetbrains.annotations.Contract("false -> fail")
	public static void check(boolean condition) {
		if (!condition) {
			throw new IllegalStateException();
		}
	}
	
	public interface Condition {}
	
	@FunctionalInterface
	public interface Invariant<T> extends Condition {
		void check(T object) throws Exception;
	}
	
	@FunctionalInterface
	public interface Precondition extends Condition {
		void check() throws Exception;
	}
	
	@FunctionalInterface
	public interface Postcondition<M> extends Condition {
		void check(M original) throws Exception;
	}
	
}
