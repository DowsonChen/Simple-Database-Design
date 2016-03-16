import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

/**
 * Simple database design
 * @author Dongsong Chen
 * @date Mar 15, 2016
 * @version Versoin 0
 */
public class SimpleDatabase {
	/*
	 * Set two HashMap to store name-value and value-number
	 * Save value as String in case of overflow
	 * Transaction stack is for transaction use
	 */
	private static HashMap<String, String> nameValue;
	private static HashMap<String, Integer> valueNumber;
	private static Stack<Transaction> transactionStack;
	// Transaction temp variables
	private static Transaction tran;
	private static HashMap<String, String> tranNameValue;
	private static HashMap<String, Integer> tranValueNumber;
	
	// Initilize HashMap and transaction stack
	public SimpleDatabase() {
		nameValue = new HashMap<String, String>();
		valueNumber = new HashMap<String, Integer>();
		transactionStack = new Stack<Transaction>();
	}
	
	/*
	 * Check if current status is within a transaction
	 * If so, initilize current transaction by stack head
	 */
	private static boolean isInTransaction() {
		if (!transactionStack.isEmpty()) {
			tran = transactionStack.peek();
			tranNameValue = tran.getTranNameValue();
			tranValueNumber = tran.getTranValueNumber();
			return true;
		}
		return false;
	}
	
	// SET method
	private void set(String[] parts) {
		// Check if variable number is correct
		if (parts.length != 3) {
			System.out.println("Fail! Variable numbers should equal 2");
			return;
		}
		String name = parts[1];
		String value = parts[2];
		/*
		 * Check if current process is within transaction
		 * If so, use transaction temp variables instead of global ones
		 */
		if (isInTransaction()) {
			setUpdate(value, name, tranValueNumber, tranNameValue);
			tranNameValue.put(name, value);
			return;
		}
		setUpdate(value, name, valueNumber, nameValue);
		nameValue.put(name, value);
	}
	
	/*
	 * If set value to existing name, check if value setting is different
	 * If different, that name's original value number minus one
	 * And set new value number to 1
	 */
	private static void setUpdate(String value, String name,
			HashMap<String, Integer> valueNumber,
			HashMap<String, String> nameValue) {
		if (value != nameValue.get(name)) {
			updateValueNum(valueNumber, nameValue.get(name), -1);
			updateValueNum(valueNumber, value, 1);
		} else {
			// If value is same, do nothing
			return;
		}
	}
	
	// Update valueNumber hashmap, int add means the manipulation on value
	private static void updateValueNum(HashMap<String, Integer> valueNumber, 
									String value, int add) {
		if (valueNumber.get(value) == null) {
			valueNumber.put(value, 1);
		} else {
			int number = valueNumber.get(value);
			valueNumber.put(value, number + add);
		}
	}
	
	// GET method
	private void get(String[] parts) {
		// Check if variable number is correct
		if (parts.length != 2) {
			System.out.println("Fail! Variable numbers should equal 1");
			return;
		}
		// Check if current process is within transaction
		if (isInTransaction()) {
			System.out.println(tranNameValue.get(parts[1]) == null ? "NULL" : tranNameValue.get(parts[1]));
			return;
		}
		System.out.println(nameValue.get(parts[1]) == null ? "NULL" : nameValue.get(parts[1]));
	}
	
	// UNSET method
	private void unset(String[] parts) {
		// Check if variable number is correct
		if (parts.length != 2) {
			System.out.println("Fail! Variable numbers should equal 1");
			return;
		}
		// Check if current process is within transaction
		if (isInTransaction()) {
			updateValueNum(tranValueNumber, tranNameValue.get(parts[1]), -1);
			tranNameValue.put(parts[1], null);
			return;
		}
		updateValueNum(valueNumber, nameValue.get(parts[1]), -1);
		nameValue.put(parts[1], null);
	}
	
	// NUMEQUALTO method
	private void numEqualTo(String[] parts) {
		// Check if variable number is correct
		if (parts.length != 2) {
			System.out.println("Fail! Variable numbers should equal 1");
			return;
		}
		String value = parts[1];
		// Check if current process is within transaction
		if (isInTransaction()) {
			System.out.println(tranValueNumber.get(value) == null ? "0" : tranValueNumber.get(value));
			return;
		}
		System.out.println(valueNumber.get(value) == null ? "0" : valueNumber.get(value));
	}
	
	// BEGIN method
	private void begin(String[] parts) {
		// Check if variable number is correct
		if (parts.length != 1) {
			System.out.println("Fail! There should be no variables for BEGIN");
			return;
		}
		/*
		 * Add a new transaction to stack, the current nameValue and valueNumber
		 * should be original one or one after previouse commit
		 */
		Transaction tran = new Transaction();
		tran.copyNameValue(nameValue);
		tran.copyValueNumber(valueNumber);
		transactionStack.add(tran);
	}
	
	// ROLLBACK method
	private void rollback(String[] parts) {
		// Check if variable number is correct
		if (parts.length != 1) {
			System.out.println("Fail! There should be no variables for ROLLBACK");
			return;
		}
		if (transactionStack.isEmpty()) {
			System.out.println("NO TRANSACTION");
			return;
		}
		// Remove the mose recent transaction
		transactionStack.pop();
	}
	
	// COMMIT method
	private void commit(String[] parts) {
		// Check if variable number is correct
		if (parts.length != 1) {
			System.out.println("Fail! There should be no variables for COMMIT");
			return;
		}
		if (transactionStack.isEmpty()) {
			System.out.println("NO TRANSACTION");
			return;
		}
		/*
		 * Update by most recent transaction, clear oringinal HashMap and put new
		 * value into them
		 */
		nameValue = new HashMap<String, String>();
		valueNumber = new HashMap<String, Integer>();
		// User iterator to traverse all elements and assign them to gloable one
		Iterator<String> iterNameVal = tranNameValue.keySet().iterator();
		Iterator<String> iterValNum = tranValueNumber.keySet().iterator();
		while (iterNameVal.hasNext()) {
			String name = iterNameVal.next();
			nameValue.put(name, tranNameValue.get(name));
		}
		while (iterValNum.hasNext()) {
			String value = iterValNum.next();
			nameValue.put(value, tranNameValue.get(value));
		}
		// Clear transation stack after commit
		while (!transactionStack.isEmpty()) {
			transactionStack.pop();
		}
	}
	
	public static void main(String[] args) {
		SimpleDatabase sd = new SimpleDatabase();
		// Use sanner to get commands from command line
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please type your command");
		System.out.println("Available choices: SET/GET/UNSET/NUMEQUALTO/END/BEGIN/ROLLBACK/COMMIT");
		while (scanner.hasNext()) {
			String input = scanner.nextLine();
			// Split command line with any number of whitespaces
			String[] parts = input.split("\\s+");
			String command = parts[0];
			// Switch to choose corresponding execution
			switch (command) {
			case "SET":
				sd.set(parts);
				break;
			case "GET":
				sd.get(parts);
				break;
			case "UNSET":
				sd.unset(parts);
				break;
			case "NUMEQUALTO":
				sd.numEqualTo(parts);
				break;
			case "BEGIN":
				sd.begin(parts);
				break;
			case "ROLLBACK":
				sd.rollback(parts);
				break;
			case "COMMIT":
				sd.commit(parts);
				break;
			case "":
				break;
			case "END":
				// Check if variable number is correct
				if (parts.length != 1) {
					System.out.println("There should be no variables for END");
					break;
				}
				// Only if end command is correct, exit programm
				return;
			default:
				System.out.println("Invalid command!");
			}
		}
		scanner.close();
	}
}

class Transaction {
	private HashMap<String, String> tranNameValue;
	private HashMap<String, Integer> tranValueNumber;
	// Initialize temp hashmap
	public Transaction() {
		tranNameValue = new HashMap<String, String>();
		tranValueNumber = new HashMap<String, Integer>();
	}
	
	// Return value of name in current transaction
	public HashMap<String, String> getTranNameValue() {
		return tranNameValue;
	}
	public HashMap<String, Integer> getTranValueNumber() {
		return tranValueNumber;
	}
	
	// Copy current global map
	public void copyNameValue(HashMap<String, String> nameValue) {
		Iterator<String> iterator = nameValue.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			tranNameValue.put(name, nameValue.get(name));
		}
	}
	public void copyValueNumber(HashMap<String, Integer> valueNumber) {
		Iterator<String> iterator = valueNumber.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			tranValueNumber.put(name, valueNumber.get(name));
		}
	}
}