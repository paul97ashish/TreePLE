package ca.mcgill.ecse321.project6.admin;

import java.io.File;

import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.security.Password;

public class Admin {

	public static void main(String[] args) {
		if (args.length == 0) {
			usage();
			return;
		}
		String action = args[0];
		switch (action) {
		case "help":
			usage();
			break;
		case "add-user":
			if (args.length != 5) {
				usage();
			} else {
				addUser(args[1], args[2], args[3], args[4]);
			}
			break;
		default:
			usage();
			break;
		}
	}

	private static void addUser(String database, String name, String password, String type) {
		if (!type.equals("resident") && !type.equals("municipal")) {
			System.out.println("Valid user types are \"resident\" and \"municipal\".");
			return;
		}
		File data = new File(database);
		if (!data.exists()) {
			System.out.println("File " + database + " does not exist.");
			return;
		}
		AdminPersistence db = new AdminPersistence(database);
		TreePLE tp = (TreePLE) db.loadFromDb();
		if (tp == null) {
			tp = new TreePLE();
			db.saveToDb(tp);
		}
		try {
			User.UserType userType = type.equalsIgnoreCase("resident")
				? User.UserType.Resident
				: User.UserType.Municipal;
			
			User u = new User(name, Password.getSaltedHash(password), userType);
			
			if(!tp.addUser(u)) {
				System.out.println("User already exists!");
				return;
			} else {
				System.out.println("Successfully added user.");
			}
		} catch (Exception e) {
			System.err.println("Error occured: " + e.getMessage());
		}
		
		db.saveToDb(tp);
	}

	private static void usage() {
		System.out.println("TreePLE Admin Tool -- Help");
		System.out.println("Do not use while backend is running.");
		System.out.println("-------------------------------------------");
		System.out.println("java admin [action] [args]");
		System.out.println();
		System.out.println("Actions:");
		System.out.println("  help - Displays this dialog.");
		System.out.println("  add-user [database] [name] [password] [type] - Adds a user to the database.");
	}

}

