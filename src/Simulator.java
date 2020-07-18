import java.util.Arrays;

public class Simulator {

	PipelineRegister IFtoID;
	PipelineRegister IDtoEx;
	PipelineRegister ExtoMem;
	PipelineRegister MemtoWb;
	
	InstructionMemory instructionMemory;
	RegisterFile registerFile;
	DataMemory dataMemory;
	
	InstructionFetch instructionFetchStage;
	InstructionDecode instructionDecodeStage;
	Execution executionStage;
	Memory memoryStage;
	WriteBack writeBackStage;
	
	int[] instructionsNumbers = new int[5];
	int[] tmpInstructionsNumbers = new int[5];

	public Simulator()
	{
		IFtoID = new PipelineRegister(0);
		IDtoEx = new PipelineRegister(1);
		ExtoMem = new PipelineRegister(2);
		MemtoWb = new PipelineRegister(3);
		
		instructionMemory = new InstructionMemory(1000);	//1000 instructions, 32 KB
		registerFile = new RegisterFile();
		dataMemory = new DataMemory(16000);					//64 KB
		
		instructionFetchStage = new InstructionFetch(this);
		instructionDecodeStage = new InstructionDecode(this);
		executionStage = new Execution(this);
		memoryStage = new Memory(this);
		writeBackStage = new WriteBack(this);
		
		Arrays.fill(instructionsNumbers, -2);
		Arrays.fill(tmpInstructionsNumbers, -2);
		instructionsNumbers[0] = 0;
	}
	

	void run()
	{
		int clockCycle = 1;

		while(isBusy())
		{
			//Run Stages
			instructionFetchStage.run();
			instructionDecodeStage.run();
			executionStage.run();
			memoryStage.run();
			writeBackStage.run();
			
			// Update Pipleine registers
			updatePipelines();
			
			// Print
			print(clockCycle++);
			
			// Update instructions numbers
			updateInstructionNumbers();
		}
	}
	
	public PipelineRegister getIFtoID() { return IFtoID; }

	public PipelineRegister getIDtoEx() { return IDtoEx; }

	public PipelineRegister getExtoMem() { return ExtoMem;}

	public PipelineRegister getMemtoWb() { return MemtoWb; }

	public InstructionMemory getInstructionMemory() { return instructionMemory; }

	public RegisterFile getRegisterFile() {	return registerFile; }

	public DataMemory getDataMemory() {	return dataMemory; }

	public InstructionFetch getInstructionFetchStage() { return instructionFetchStage; }

	public InstructionDecode getInstructionDecodeStage() {	return instructionDecodeStage; }

	public Execution getExecutionStage() {	return executionStage; }

	public Memory getMemoryStage() { return memoryStage; }

	public WriteBack getWriteBackStage() { return writeBackStage; }

	public int getInstructionNumber(int stageID) { return instructionsNumbers[stageID]; }
	

	public void setInstructionNumber(int stageID, int instructionNumber)
	{
		tmpInstructionsNumbers[stageID] = instructionNumber;
	}

	public void updateInstructionNumbers()
	{
		for(int i = 0; i < 5; ++i)
		{
			instructionsNumbers[i] = tmpInstructionsNumbers[i];
			tmpInstructionsNumbers[i] = -2;
		}
	}

	private void updatePipelines()
	{
		IFtoID.update();
		IDtoEx.update();
		ExtoMem.update();
		MemtoWb.update();
	}

	private boolean isBusy()
	{
		for(int instructionNumber: instructionsNumbers)
			if(instructionNumber != -2)
				return true;
		return false;
	}

	private void print(int clockCycle)
	{
		if(clockCycle > 1)
		System.out.println("##############################################\n");
		System.out.print("Clock Cycle " + clockCycle + "\n");
		String[] instructionAction = new String[] { "fetched", "decoded", "executed", "in memory stage", "in write back stage" };
		for(int i = 0; i < 5; ++i)
			if(instructionsNumbers[i] >= 0)
				System.out.printf("Instruction %d %s\n", instructionsNumbers[i] + 1, instructionAction[i]);
		System.out.printf("PCSrc = %d\n", instructionFetchStage.getPCSrc());
		System.out.printf("Pipepline IF/ID\n%s================================================\n", IFtoID);
		System.out.printf("Pipepline ID/EX\n%s================================================\n", IDtoEx);
		System.out.printf("Pipepline EX/MEM\n%s================================================\n", ExtoMem);
		System.out.printf("Pipepline MEM/WB\n%s================================================\n", MemtoWb);
	}
}