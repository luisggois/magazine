package com.luisgois.magazine.exception.enumeration;

public enum DataIntegrity {

	DATABASE("A database error has occured."),
	DUPLICATE_USER("This user already exists."),
	NONEXISTENT_USER("This user does not exist"),
	NONEXISTENT_AUTHOR("This author does not exist"),
	NONEXISTENT_ARTICLE("This article does not exist");

	private final String description;

	private DataIntegrity(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return description;
	}

}
