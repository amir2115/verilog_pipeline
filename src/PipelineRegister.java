import java.util.HashMap;
import java.util.Map.Entry;

public class PipelineRegister {
	
	private HashMap<String, Register> registers;
	private PipelineRegister tmpRegister;

	public PipelineRegister(String type)
	{
		this(type, false);
	}

	public PipelineRegister(String type, boolean tmp)
	{
		registers = new HashMap<String, Register>();
		switch (type) {
			case "IFtoID":
				registers.put("PC", new Register(32));
				registers.put("Instruction", new Register(32));
				break;
			case "IDtoEx":
				registers.put("RegWrite", new Register(1));
				registers.put("MemToReg", new Register(1));
				registers.put("Branch", new Register(1));
				registers.put("MemRead", new Register(1));
				registers.put("MemWrite", new Register(1));
				registers.put("RegDst", new Register(1));
				registers.put("ALUOp", new Register(2));
				registers.put("ALUSrc", new Register(1));
				registers.put("PC", new Register(32));
				registers.put("ReadData1", new Register(32));
				registers.put("ReadData2", new Register(32));
				registers.put("ImmediateValue", new Register(32));
				registers.put("rs", new Register(5));
				registers.put("Destination1", new Register(5));
				registers.put("Destination2", new Register(5));
				break;
			case "ExToMem":
				registers.put("RegWrite", new Register(1));
				registers.put("MemToReg", new Register(1));
				registers.put("Branch", new Register(1));
				registers.put("MemRead", new Register(1));
				registers.put("MemWrite", new Register(1));
				registers.put("BranchAddress", new Register(32));
				registers.put("Zero", new Register(1));
				registers.put("ALUResult", new Register(32));
				registers.put("ReadData2", new Register(32));
				registers.put("Destination", new Register(5));
				break;
			default:
				registers.put("RegWrite", new Register(1));
				registers.put("MemToReg", new Register(1));
				registers.put("MemoryOutput", new Register(32));
				registers.put("ALUResult", new Register(32));
				registers.put("Destination", new Register(5));
				break;
		}

		if(!tmp)
			tmpRegister = new PipelineRegister(type, true);
	}

	public Register getRegister(String registerName)
	{
		return registers.get(registerName);
	}

	public void setRegister(String registerName, int value)
	{
		tmpRegister.getRegister(registerName).setValue(value);
	}

	public void update()
	{
		for(Entry<String, Register> entry: registers.entrySet())
		{
			String registerName = entry.getKey();
			registers.get(registerName).setValue(tmpRegister.getRegister(registerName).getValue());
			tmpRegister.getRegister(registerName).clear();
		}
	}

	public void selfUpdate()
	{
		for(Entry<String, Register> entry: registers.entrySet())
			setRegister(entry.getKey(), entry.getValue().getValue());
	}

	public String toString()
	{
		StringBuilder r = new StringBuilder();
		for(Entry<String, Register> entry: registers.entrySet())
			r.append(String.format("%s %s\n", entry.getKey(), entry.getValue()));
		return r.toString();
	}
	
}
