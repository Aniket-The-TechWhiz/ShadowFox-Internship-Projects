import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

class Contact {
    private String name;
    private String phoneNumber;
    private String email;

    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Phone: " + phoneNumber + ", Email: " + email;
    }
}

class ContactManager {
    private ArrayList<Contact> contacts;

    public ContactManager() {
        this.contacts = new ArrayList<>();
    }

    public void addContact(String name, String phoneNumber, String email) {
        if (isDuplicate(name)) {
            System.out.println("Error: A contact with the name '" + name + "' already exists.");
            return;
        }
        contacts.add(new Contact(name, phoneNumber, email));
        System.out.println("Contact added successfully.");
    }

    public void viewAllContacts() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts to display.");
            return;
        }
        System.out.println("\n--- Contact List ---");
        for (Contact contact : contacts) {
            System.out.println(contact);
        }
    }

    public void updateContact(String name, String newPhoneNumber, String newEmail) {
        Contact contactToUpdate = findContact(name);
        if (contactToUpdate != null) {
            contactToUpdate.setPhoneNumber(newPhoneNumber);
            contactToUpdate.setEmail(newEmail);
            System.out.println("Contact updated successfully.");
        } else {
            System.out.println("Contact not found.");
        }
    }

    public void deleteContact(String name) {
        Contact contactToDelete = findContact(name);
        if (contactToDelete != null) {
            contacts.remove(contactToDelete);
            System.out.println("Contact deleted successfully.");
        } else {
            System.out.println("Contact not found.");
        }
    }

    public void searchContacts(String keyword) {
        ArrayList<Contact> foundContacts = new ArrayList<>();
        String lowerCaseKeyword = keyword.toLowerCase();
        for (Contact contact : contacts) {
            if (contact.getName().toLowerCase().contains(lowerCaseKeyword) ||
                contact.getPhoneNumber().contains(lowerCaseKeyword) ||
                contact.getEmail().toLowerCase().contains(lowerCaseKeyword)) {
                foundContacts.add(contact);
            }
        }

        if (foundContacts.isEmpty()) {
            System.out.println("No contacts found for the keyword '" + keyword + "'.");
        } else {
            System.out.println("\n--- Search Results ---");
            for (Contact contact : foundContacts) {
                System.out.println(contact);
            }
        }
    }

    private Contact findContact(String name) {
        for (Contact contact : contacts) {
            if (contact.getName().equalsIgnoreCase(name)) {
                return contact;
            }
        }
        return null;
    }

    private boolean isDuplicate(String name) {
        return findContact(name) != null;
    }
}

public class SimpleContactManagementSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static ContactManager contactManager = new ContactManager();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Contact Management System ---");
            System.out.println("1. Add a new contact");
            System.out.println("2. View all contacts");
            System.out.println("3. Update an existing contact");
            System.out.println("4. Delete a contact");
            System.out.println("5. Search for a contact");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                switch (choice) {
                    case 1:
                        addContactMenu();
                        break;
                    case 2:
                        contactManager.viewAllContacts();
                        break;
                    case 3:
                        updateContactMenu();
                        break;
                    case 4:
                        deleteContactMenu();
                        break;
                    case 5:
                        searchContactMenu();
                        break;
                    case 6:
                        running = false;
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
        scanner.close();
    }

    private static void addContactMenu() {
        System.out.println("\n--- Add New Contact ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter email address: ");
        String email = scanner.nextLine();
        contactManager.addContact(name, phoneNumber, email);
    }

    private static void updateContactMenu() {
        System.out.println("\n--- Update Contact ---");
        System.out.print("Enter the name of the contact to update: ");
        String name = scanner.nextLine();
        System.out.print("Enter new phone number: ");
        String newPhoneNumber = scanner.nextLine();
        System.out.print("Enter new email address: ");
        String newEmail = scanner.nextLine();
        contactManager.updateContact(name, newPhoneNumber, newEmail);
    }

    private static void deleteContactMenu() {
        System.out.println("\n--- Delete Contact ---");
        System.out.print("Enter the name of the contact to delete: ");
        String name = scanner.nextLine();
        contactManager.deleteContact(name);
    }

    private static void searchContactMenu() {
        System.out.println("\n--- Search Contact ---");
        System.out.print("Enter a keyword to search: ");
        String keyword = scanner.nextLine();
        contactManager.searchContacts(keyword);
    }
}