package console;

import java.io.*;
import java.util.*;

import ast.Program;
import ast.ProgramImpl;
import exceptions.InitializationError;
import exceptions.SimulationException;
import interpret.Critter;
import interpret.Food;
import interpret.Hex;
import interpret.World;
import model.Constants;
import parse.Parser;
import parse.ParserFactory;

/**
 * The console user interface for Assignment 5.
 */
public class Console {
	private Scanner scan;
	public boolean done;
	public PrintStream out;
	public static World w;
	private LineNumberReader lnr;
	private LineNumberReader clnr;
	public int maxColumn = 0;
	public int maxRow = 0;

	/* =========================== */
	/* DO NOT EDIT ABOVE THIS LINE */
	/* (except imports...) */
	/* =========================== */

	/**
	 * Constructs a new Console capable of reading the standard input.
	 */
	public Console() {
		this(System.in, System.out);
	}
	
	/**
	 * Constructs a new Console capable of reading a given input.
	 */
	public Console(InputStream in, PrintStream out) {
		this.out = out;
		scan = new Scanner(in);
		done = false;
	}

	/**
	 * Processes a single console command provided by the user.
	 */
	public void handleCommand() {
		out.print("Enter a command or \"help\" for a list of commands.\n> ");
		String command = scan.next();
		try {
			if (command.equals("new")) {
				newWorld();
			}
			else if (command.equals("load")) {
				String filename = scan.next();
				loadWorld(filename);
			}
			else if (command.equals("critters")) {
				String filename = scan.next();
				int n = scan.nextInt();
				loadCritters(filename, n);
			}
			else if (command.equals("step")) {
				int n = scan.nextInt();
				advanceTime(n);
			}
			else if (command.equals("info")) {
				worldInfo();
			}
			else if (command.equals("hex")) {
				int c = scan.nextInt();
				int r = scan.nextInt();
				hexInfo(c, r);
			}
			else if (command.equals("help")) {
				printHelp();
			}
			else if (command.equals("exit")) {
				done = true;
			}
			else 
				out.println(command + " is not a valid command.");
		} catch (Exception e) {
			System.err.println("This command is: " + command);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts new random world simulation.
	 */
	public void newWorld() {
		w = new World();
	}

	/**
	 * Starts new simulation with world specified in filename.
	 *
	 * @param fileName
	 */
	public void loadWorld(String fileName) {
		String name = null;
		try {
			File f = new File(fileName);
			String parent = f.getAbsoluteFile().getParent();
			FileReader r = new FileReader(f);
			lnr = new LineNumberReader(r);
			for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
				try {
					String[] tokens = line.split("\\h+");
					if (tokens.length < 1) continue;
					line = tokens[0].trim();
					if(line.equals("") || line.startsWith("//")) continue;
					switch (line) {
					case "rock":
						if (tokens.length != 3) {
							warning("Invalid rock placement");
							break;
						}
						Hex h = checkOccupancy(tokens[1], tokens[2]);
						if (h == null) break;
						w.getHexAt(h.getCol(), h.getRow()).isRock = true;
						break;
					case "food":
						if (tokens.length != 4) {
							warning("Invalid food placement info");
							break;
						}
						h = checkOccupancy(tokens[1], tokens[2]);
						if (h == null) break;
						try {
							int amt = Integer.parseInt(tokens[3]);
							if (amt < 1) {
								warning("Food amount must be positive");
								break;
							}
							w.getHexAt(h.getCol(), h.getRow()).add(new Food(amt, w));
						} catch (NumberFormatException e) {
							warning("Invalid food amount");
						}
						break;
					case "size":
						if (maxColumn >= 1 || maxRow >= 1) {
							warning("Duplicate world size");
							break;
						}
						if (tokens.length != 3) {
							warning("Invalid world size info", "using defaults");
							maxColumn = Constants.columns;
							maxRow = Constants.rows;
							break;
						}
						try {
							maxColumn = Integer.parseInt(tokens[1]);
							maxRow = Integer.parseInt(tokens[2]);
							if (maxColumn < 1 || maxRow < 1) {
								warning("World dimensions must be positive", "using defaults");
								maxColumn = Constants.columns;
								maxRow = Constants.rows;
							}
						} catch (NumberFormatException e) {
							warning("Invalid world size info", "using defaults");
							maxColumn = Constants.columns;
							maxRow = Constants.rows;
						}
						w = new World(name, maxColumn, maxRow);
						break;
					case "critter":
						if (tokens.length != 5) {
							warning("Invalid critter info");
							break;
						}
						String critFileName = parent + File.separator + tokens[1];
						h = checkOccupancy(tokens[2], tokens[3]);
						if (h == null) break;
						int dir = 0;
						try {
							dir = Integer.parseInt(tokens[4]);
						} catch (NumberFormatException e) {
							warning("Invalid critter direction", "using default");
						}
						try (Reader cr = new FileReader(critFileName)){
							Critter c = loadCritterFile(cr, h, dir);
							w.getHexAt(h.getCol(), h.getRow()).add(c);
						} catch (FileNotFoundException e) {
							throw new InitializationError("File not found: " + critFileName);
						} catch (InitializationError ie) {
							warning("Errors reading critter file " + critFileName);
						}
						break;
					case "name":
						if (name != null) {
							warning("Duplicate name");
							break;
						}
						name = "";
						for (int i = 1; i < tokens.length; i++) {
							name += tokens[i].trim() + " ";
						}
						name = name.trim();
						break;
					default:
						throw new InitializationError("Invalid world file format at line " + 
								lnr.getLineNumber());
					}
				} catch (NumberFormatException e) {
					throw new InitializationError("Expected a number at line " + lnr.getLineNumber());
				} catch (SimulationException e) {
					throw new InitializationError("Invalid location at line " + lnr.getLineNumber());
				}		
			}
		} catch (FileNotFoundException e) {
			out.println("World file " + fileName + " not found");
		} catch (IOException e) {
			out.println("Error initializing world");
			done = true;
		} catch (InitializationError e) {
			out.println("Initialization error " + e.getMessage());
		}
			/*Scanner scan = new Scanner(f);
			scan.next(); // scan past name
			String name = scan.nextLine();
			scan.next(); // scan past size
			int cols = scan.nextInt();
			int rows = scan.nextInt(); 
			scan.nextLine();
			w = new World(name, cols, rows);
			do {
				String line = scan.nextLine();
				Scanner in = new Scanner(line);
				if (in.hasNext()) {
					String category = in.next();
					if (category.equals("rock")) {
						int col = in.nextInt();
						int row = in.nextInt();
						if (w.getHexAt(col, row) != null
								&& w.getHexAt(col, row).getStatus() == 0)
							w.getHexAt(col, row).isRock = true;
					}
					else if (category.equals("food")) {
						int col = in.nextInt();
						int row = in.nextInt();
						int amount = in.nextInt();
						if (w.getHexAt(col, row) != null
								&& w.getHexAt(col, row).getStatus() == 0)
							w.getHexAt(col,row).add(new Food(amount, w));
					}
					else if (category.equals("critter")) {
						String critterFile = in.next();
						int col = in.nextInt();
						int row = in.nextInt();
						int dir = in.nextInt();
						if (w.getHexAt(col, row) != null 
								&& w.getHexAt(col, row).getStatus() == 0) {
							Critter c = loadCritterFile
									(parent + "/" + 
											critterFile, col, row, dir);
							w.getHexAt(col, row).add(c);
						}
					}
				}
				in.close();
			} while (scan.hasNextLine());
			scan.close();
		} catch (FileNotFoundException e) {
			out.println("World file " + fileName + " not found");
		} catch (IOException e) {
			out.println("Error initializing world");
		}*/
	}

	public Critter loadCritterFile(Reader r, Hex h, int dir) throws IOException {
		String name = "";
		
		String[] fileFormat = new String[] {
				"memsize", "defense", "offense", "size", "energy", "posture" };
		
		int[] values = { 8, 1, 1, 1, 1, 0 }; // default values
		
		clnr = new LineNumberReader(r);
		
		for (int i = 0; i < fileFormat.length; i++) {
			String line = nextLine();
			if (i == 0 && line.startsWith("species:")) {
				name = line.substring(8).trim();
				line = nextLine();
			}
			String s = fileFormat[i];
			if (!line.startsWith(s + ":")) error("Expected " + s);
			try {
				line = line.substring(s.length() + 1).trim();
				values[i] = Integer.parseInt(line);
				if (values[i] < 0) error("Value must be positive");
			} catch (NumberFormatException e) {
				error("Expected action number");
			}
		}
		
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(clnr);
		if (prog == null) error("Could not parse critter program");
		
		//check consistency of values
		if (values[0] < Constants.minMemory) error("Memory length too small");
		int[] mem = new int[values[0]];
		if (values[5] > Constants.maxPosture) error("Posture value too large");
		mem[0] = values[0];
		mem[1] = values[1];
		mem[2] = values[2];
		mem[3] = values[3];
		mem[4] = values[4];
		mem[5] = 1;
		mem[6] = 0;
		mem[7] = values[5];
		Critter c = new Critter(name, h, dir, mem, (ProgramImpl) prog);
		
		return c;
		
		/*
		try {
			int[] memory = new int[8];
			Scanner scan = new Scanner(new File(filename));
			scan.next(); // scans past species:
			String name = scan.nextLine();
			scan.next(); // scans past memsize:
			int memSize = scan.nextInt(); scan.nextLine();
			memory[0] = memSize;
			scan.next(); // scans past defense:
			int defense = scan.nextInt(); scan.nextLine();
			memory[1] = defense;
			scan.next(); // scans past offense:
			int offense = scan.nextInt(); scan.nextLine();
			memory[2] = offense;
			scan.next(); // scan past size:
			int size = scan.nextInt(); scan.nextLine();
			memory[3] = size;
			scan.next(); // scan past energy:
			int energy = scan.nextInt(); scan.nextLine();
			memory[4] = energy;
			memory[5] = 0;
			memory[6] = 0;
			scan.next(); // scan past posture:
			int posture = scan.nextInt(); scan.nextLine();
			memory[7] = posture;
			StringBuilder sb = new StringBuilder();
			while(scan.hasNextLine()) {
				sb.append(scan.nextLine() + "\n");		
			}
			String restOfFile = sb.toString();
			StringReader sr = new StringReader(restOfFile);
			Parser parser = ParserFactory.getParser();
			ProgramImpl pr = (ProgramImpl) parser.parse(sr);
			scan.close();
			Critter c = new Critter(h, dir, memory, pr);
			return c;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}*/
	}
	
	/**
	 * Loads critter definition from filename and randomly places n critters with
	 * that definition into the world.
	 *
	 * @param filename
	 * @param n
	 */
	public void loadCritters(String fileName, int n) {
		try {
			if (w == null) {
				out.println("Please initialize world.");
				return;
			}
			for (int ix = 0; ix < n; ix++) {
				int col, row;
				do {
					col = ((new Random()).nextInt(w.getCols() - 1));
					row = ((new Random()).nextInt(w.getRows() - 1));
				} while (!(w.getHexAt(col, row) != null && 
						w.getHexAt(col, row).getStatus() == 0));
				Critter c = loadCritterFile(new FileReader(fileName), w.getHexAt(col, row) , 
						((new Random()).nextInt(6)));
				/*w.getHexAt(col, row).add(c);*/
			}
		} catch (FileNotFoundException e) {
			System.out.println("Critter file " + fileName + " not found.");
		} catch (InitializationError e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Error reading critter file.");
		}
	}

	/**
	 * Advances the world by n time steps.
	 *
	 * @param n
	 */
	public void advanceTime(int n) {
		if (w == null) {
			out.println("Please initialize world.");
			return;
		}
		for (int ix = 0; ix < n; ix++) {
			w.advanceTime();
		}
	}

	/**
	 * Prints current time step, number of critters, and world map of the
	 * simulation.
	 */
	public void worldInfo() {
		if (w == null) {
			out.println("Please initialize the world");
			return;
		}
		System.out.println(w.toString());
		worldInfo(w.getTime(), w.getCritters().size());
	}

	/**
	 * Prints description of the contents of hex (c,r).
	 *
	 * @param c
	 *           column of hex
	 * @param r
	 *           row of hex
	 */
	public void hexInfo(int c, int r) {
		if (w == null) {
			out.println("Please initialize the world");
			return;
		}
		Hex h = w.getHexAt(c, r);
		out.println(h.toString());

		critterInfo(null, null, null, null);
		// OR
		// terrainInfo(0);
	}

	/* =========================== */
	/* DO NOT EDIT BELOW THIS LINE */
	/* =========================== */

	/**
	 * Be sure to call this function, we will override it to grade.
	 *
	 * @param numSteps
	 *           The number of steps that have passed in the world.
	 * @param crittersAlive
	 *           The number of critters currently alive.
	 */
	protected void worldInfo(int numSteps, int crittersAlive) {
		out.println("steps: " + numSteps);
		out.println("critters: " + crittersAlive);
	}

	/**
	 * Be sure to call this function, we will override it to grade.
	 *
	 * @param species
	 *           The species of the critter.
	 * @param mem
	 *           The memory of the critter.
	 * @param program
	 *           The program of the critter pretty printed as a String. This
	 *           should be able to be parsed back to the same AST.
	 * @param lastrule
	 *           The last rule executed by the critter pretty printed as a
	 *           String. This should be able to be parsed back to the same AST.
	 *           If no rule has been executed, this parameter should be null.
	 */
	protected void critterInfo(String species, int[] mem, String program, String lastrule) {
		out.println("Species: " + species);
		StringBuilder sbmem = new StringBuilder();
		for (int i : mem) {
			sbmem.append(" ").append(i);
		}
		out.println("Memory:" + sbmem.toString());
		out.println("Program: " + program);
		out.println("Last rule: " + lastrule);
	}

	/**
	 * Be sure to call this function, we will override it to grade.
	 *
	 * @param terrain
	 *           0 is empty, -1 is rock, -X is (X-1) food
	 */
	protected void terrainInfo(int terrain) {
		if (terrain == 0) {
			out.println("Empty");
		} else if (terrain == -1) {
			out.println("Rock");
		} else {
			out.println("Food: " + (-terrain - 1));
		}
	}

	/**
	 * Prints a list of possible commands to the standard output.
	 */
	public void printHelp() {
		out.println("new: start a new simulation with a random world");
		out.println("load <world_file>: start a new simulation with the world loaded from world_file");
		out.println("critters <critter_file> <n>: add n critters defined by critter_file randomly into the world");
		out.println("step <n>: advance the world by n timesteps");
		out.println("info: print current timestep, number of critters living, and map of world");
		out.println("hex <c> <r>: print contents of hex at column c, row r");
		out.println("exit: exit the program");
	}
	
	public World getWorld() {
		return w;
	}

	private void warning(String message, String resolution) {
		System.err.println(message + " at line " + lnr.getLineNumber() + " -- " + resolution);
	}

	private void warning(String message) {
		warning(message, "ignoring");
	}

	private Hex checkOccupancy(String c, String r) {
		try {
			int col = Integer.parseInt(c);
			int row = Integer.parseInt(r);
			if (w.getHexAt(col, row) != null
					&& w.getHexAt(col, row).getStatus() == 0) {
				Hex loc = new Hex(col, row, false, w);
				return loc;
			}
			else {
				warning("Cell is already occupied");
				return null;
			}
		} catch (NumberFormatException e) {
			warning("Illegal location " + c + " " + r);
			return null;
		}
	}
	
	private String nextLine() throws IOException {
	      String line = clnr.readLine();
	      while (true) {
	    	 if (line == null) error("Unexpected end of file");
	         line = line.trim();
	         if (line.length() > 0 && !line.startsWith("//")) return line;
	         line = clnr.readLine();
	      }
	   }
	
	private void error(String message) {
		   throw new InitializationError(message + " at line " + clnr.getLineNumber());
	   }

	public static void main(String[] args) {
		Console console = new Console();
		while (!console.done) {
			console.handleCommand();
		}
	}

}
