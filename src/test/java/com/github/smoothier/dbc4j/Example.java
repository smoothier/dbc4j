package com.github.smoothier.dbc4j;


import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

import static com.github.smoothier.dbc4j.Contract.Contract;
import static com.github.smoothier.dbc4j.Contract.*;
import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.YEARS;

public class Example {
	
	public static void main(final String... args) {
		final Person me = new Person("Max", "Mustermann", LocalDate.parse("2022-01-01"));
		
		me.changeName("", "");
	}
	
	public static class Person {
		public static final String SHOULD_NOT_BE_BLANK = "%s should not be blank but was '%s'";
		
		private @NotNull String firstname;
		private @NotNull String lastname;
		private final @NotNull LocalDate dateOfBirth;
		
		
		public Person(final @NotNull String firstname, final @NotNull String lastname, final @NotNull LocalDate dateOfBirth) {
			try (final var contract = Contract(this, Person::invariant)) {
				Precondition(() -> {
					final var now = now();
					require(!firstname.isBlank(), () -> "");
					require(!lastname.isBlank(), () -> "");
					require(dateOfBirth.isBefore(now) || dateOfBirth.isEqual(now), () -> "");
				});
				
				this.firstname = Objects.requireNonNull(firstname);
				this.lastname = Objects.requireNonNull(lastname);
				this.dateOfBirth = Objects.requireNonNull(dateOfBirth);
				
				Postcondition((Memento original) -> {
					ensure(this.firstname().equals(firstname), () -> "");
					ensure(this.lastname().equals(lastname), () -> "");
					ensure(this.dateOfBirth().equals(dateOfBirth), () -> "");
				});
			}
		}
		
		private void invariant() {
			check(!this.firstname().isBlank(), () -> "");
			check(!this.lastname().isBlank(), () -> "");
			check(this.dateOfBirth().isBefore(now()) || this.dateOfBirth().isEqual(now()), () -> "");
			check(this.age() >= 0, () -> "");
		}
		
		public @NotNull String firstname() {
			try (final var contract = Contract(this, Person::invariant, Person::saveToMemento)) {
				Precondition(() -> {
					require(NOTHING);
				});
				
				final var result = Objects.requireNonNull(firstname);
				
				Postcondition((Memento original) -> {
					ensure(this.equates(original), () -> "");
					ensure(!result.isBlank(), () -> "");
				});
				return result;
			}
		}
		
		public @NotNull String lastname() {
			try (final var contract = Contract(this, Person::invariant, Person::saveToMemento)) {
				Precondition(() -> {
					require(NOTHING);
				});
				
				final var result = Objects.requireNonNull(lastname);
				
				Postcondition((Memento original) -> {
					ensure(this.equates(original), () -> "");
					ensure(!result.isBlank(), () -> "");
				});
				return result;
			}
		}
		
		public @NotNull LocalDate dateOfBirth() {
			try (final var contract = Contract(this, Person::invariant, Person::saveToMemento)) {
				Precondition(() -> {
					require(NOTHING);
				});
				
				final var result = Objects.requireNonNull(dateOfBirth);
				
				Postcondition((Memento original) -> {
					ensure(this.equates(original), () -> "");
					ensure(result.isBefore(now()) || result.isEqual(now()), () -> "");
				});
				return result;
			}
		}
		
		public long age() {
			try (final var contract = Contract(this, Person::invariant, Person::saveToMemento)) {
				Precondition(() -> {
					require(NOTHING);
				});
				
				final var result = YEARS.between(dateOfBirth, now());
				
				Postcondition((Memento original) -> {
					ensure(this.equates(original), () -> "");
					ensure(result >= 0, () -> "");
				});
				return result;
			}
		}
		
		public void changeName(final @NotNull String newFirstname, final @NotNull String newLastname) {
			
			try (final var contract = Contract(this, Person::invariant, Person::saveToMemento)) {
				Precondition(() -> {
					require(!newFirstname.isBlank(), () -> SHOULD_NOT_BE_BLANK.formatted("newFirstname", newFirstname));
					require(!newLastname.isBlank(), () -> SHOULD_NOT_BE_BLANK.formatted("newLastname", newLastname));
				});
				
				this.firstname = Objects.requireNonNull(newFirstname);
				this.lastname = Objects.requireNonNull(newLastname);
				
				Postcondition((Memento original) -> {
					ensure(firstname().equals(newFirstname), () -> "");
					ensure(lastname().equals(newLastname), () -> "");
				});
			}
		}
		
		public record Memento(@NotNull String firstname, @NotNull String lastname, @NotNull LocalDate dateOfBirth) {
			public Memento {
				Objects.requireNonNull(firstname);
				Objects.requireNonNull(lastname);
				Objects.requireNonNull(dateOfBirth);
			}
		}
		
		public @NotNull Memento saveToMemento() {
			return new Memento(firstname, lastname, dateOfBirth);
		}
		
		public void restoreFromMemento(final @NotNull Memento memento) {
			this.firstname = Objects.requireNonNull(memento.firstname);
			this.lastname = Objects.requireNonNull(memento.lastname);
		}
		
		public boolean equates(final @NotNull Memento memento) {
			return this.saveToMemento().equals(memento);
		}
		
		@Override
		public @NotNull String toString() {
			return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
				.add("firstname='" + firstname + "'")
				.add("lastname='" + lastname + "'")
				.add("dateOfBirth=" + dateOfBirth)
				.toString();
		}
	}
}
