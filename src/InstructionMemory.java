public class InstructionMemory {
	
	int[] instructions;
	int numberOfInstructions;

	public InstructionMemory(int size)
	{
		instructions = new int[size];
	}

	public int getInstruction(int index)
	{
		return instructions[index/4];
	}

	public void setInstruction(int index, int value)
	{
		instructions[index/4] = value;
		numberOfInstructions++;
	}

	public int getNumberOfInstructions()
	{
		return numberOfInstructions;
	}
}
	
