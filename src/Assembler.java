import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Assembler {

	private HashMap<String, Integer> registerMap;
	private HashMap<String, Character> instructionFormat;
	private HashMap<String, Integer> commandMap;
	

	public Assembler() throws FileNotFoundException
	{
		initializeRegisterMap();
		initializeCommands();
	}

	private void initializeRegisterMap()
	{
		registerMap = new HashMap<String, Integer>();
		registerMap.put("$0", 0); registerMap.put("$zero", 0); registerMap.put("$at", 1);
		registerMap.put("$gp", 28); registerMap.put("$sp", 29); registerMap.put("$fp", 30);
		registerMap.put("$ra", 31);
		initializeRegisterMap('v', 2, 0, 2); initializeRegisterMap('a', 4, 0, 4);
		initializeRegisterMap('t', 8, 0, 8); initializeRegisterMap('s', 8, 0, 16);
		initializeRegisterMap('t', 2, 8, 24); initializeRegisterMap('k', 2, 0, 26);
	}

	public void setRegistersNames(Simulator simulator)
	{
		for(Entry<String, Integer> entry: registerMap.entrySet())
			simulator.getRegisterFile().readRegister(entry.getValue()).setName(entry.getKey());
	}

	public int getRegisterNumber(String name)
	{
		return registerMap.get(name);
	}

	private void initializeRegisterMap(char symbol, int count, int start, int base)
	{
		for(int i = 0; i < count; ++i)
			registerMap.put("$"+symbol+(start+i), base+i);
	}

	private String[] instruction(){
		String[] instructions = new String[14];
		instructions[0] = "ADD,R,0,20";
		instructions[1] = "SUB,R,0,22";
		instructions[2] = "SLL,R,0,0";
		instructions[3] = "SRL,R,0,2";
		instructions[4] = "SLT,R,0,2A";
		instructions[5] = "AND,R,0,24";
		instructions[6] = "OR,R,0,25";
		instructions[7] = "ADDI,I,8,";
		instructions[8] = "LUI,I,F,";
		instructions[9] = "LW,I,23,";
		instructions[10] = "SW,I,2B,";
		instructions[11] = "BEQ,I,4,";
		instructions[12] = "BNE,I,5,";
		instructions[13] = "J,J,2,";
		return instructions;
	}
	private void initializeCommands() throws FileNotFoundException
	{
		String[] instructions = instruction();
		instructionFormat = new HashMap<String, Character>();
		commandMap = new HashMap<String, Integer>();
		for (int i = 0;i < 14; i++)
		{
			String[] instruction = instructions[i].split(",");
			instructionFormat.put(instruction[0], instruction[1].charAt(0));
			int command = Integer.parseInt(instruction[2], 16)<<26;
			if(instruction[1].charAt(0) == 'R')
				command |= Integer.parseInt(instruction[3], 16);
			commandMap.put(instruction[0], command);
		}
	}

	private int parseRegister(String register, int shift) throws Exception
	{
		if(!registerMap.containsKey(register))
			throw new Exception("Register " + register + " is not valid.");
		return registerMap.get(register) << shift;
	}

	private int parseConstant(String constant, int shift, int size)
	{
		if(constant.length() > 2 && constant.charAt(1) == 'x')
			return (Integer.parseInt(constant.substring(2), 16) & ((1<<size) - 1)) << shift;
		return (Integer.parseInt(constant) & ((1<<size) - 1)) << shift;
	}

	private String[] splitAddress(String rsAndOffset) throws Exception
	{
		String offset = "";
		for(int i = 0; i < rsAndOffset.length(); ++i)
			if(rsAndOffset.charAt(i) == '(')
				return new String[] { rsAndOffset.substring(i+1, rsAndOffset.length()-1),  offset};
			else
				offset += rsAndOffset.charAt(i);
		throw new Exception("Bad I-Format");
	}

	private String[] filterTokens(String[] in)
	{
		int i = 0, j = 0;
		while(i < in.length)
		{
			if(in[i] != null && !in[i].trim().isEmpty())
				in[j++] = in[i];
			++i;
		}
		return Arrays.copyOf(in, j);
	}

	private int parse(String[] assembly) throws Exception
	{
		Character format = instructionFormat.get(assembly[0]);
		if(format == null)
			throw new Exception("Bad command: "+assembly[0]);
		
		int instruction = 0;
		if(format == 'R')
		{
			if(assembly.length != 4)
				throw new Exception("Bad R-format instruction");
			
			instruction |= commandMap.get(assembly[0]);					//opcode and funct
			instruction |= parseRegister(assembly[2], 21); 				//rs - source1
			instruction |= parseRegister(assembly[1], 11);		 		//rd - destination
			if(assembly[0].equals("SLL") || assembly[0].equals("SRL"))
				instruction |= parseConstant(assembly[3], 6, 5);		//shamt - shift amount
			else
				instruction |= parseRegister(assembly[3], 16); 			//rt - source2
		}
		else if(format == 'I')
		{
			instruction |= commandMap.get(assembly[0]);					//opcode
			if(assembly[0].equals("LW") || assembly[0].equals("SW") || assembly[0].equals("LUI"))
			{
				if(assembly.length != 3)
					throw new Exception("Bad I-format instruction: " + assembly[0]);
				if(assembly[0].equals("LUI"))
				{
					instruction |= parseConstant(assembly[1], 0, 16);	//constant
				}
				else
				{
					instruction |= parseRegister(assembly[1], 16);		//rt - source/destination
					String[] rsAndOffset = splitAddress(assembly[2]);
					instruction |= parseRegister(rsAndOffset[0], 21);	//rs - base address
					instruction |= parseConstant(rsAndOffset[1], 0, 16);//constant - offset
				}
				
			}
			else
			{
				if(assembly.length != 4)
					throw new Exception("Bad I-format instruction: " + assembly[0]);
				int rs = assembly[0].equals("BEQ") || assembly[0].equals("BNE") ? 1 : 2, rt = (rs-1^1)+1;			
				instruction |= parseRegister(assembly[rs], 21);			//rs
				instruction |= parseRegister(assembly[rt], 16);			//rt
				instruction |= parseConstant(assembly[3], 0, 16);		//address or constant
			}
		}
		else if(format == 'J')
		{
			if(assembly.length != 2)
				throw new Exception("Bad J-format instruction");
			instruction |= commandMap.get(assembly[0]);					//opcode
			instruction |= parseConstant(assembly[1], 0, 16);			//address
		}
			
		return instruction;
	}

	public void parse(String filePath, Simulator simulator) throws Exception
	{
		Scanner sc = new Scanner(new FileReader(filePath));
		int counter = 0;
		while(sc.hasNextLine())
		{		
			String[] assembly = filterTokens(sc.nextLine().trim().split(" |,"));
			if(assembly.length == 0)
				continue;
			assembly[0] = assembly[0].toUpperCase();
			simulator.getInstructionMemory().setInstruction(counter, parse(assembly));
			counter += 4;
		}
		
		sc.close();
	}
	
}
