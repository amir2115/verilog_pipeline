public class RegisterFile {
	Register[] registers;

	public RegisterFile()
	{
		registers = new Register[32];
		for(int i = 0; i < 32; ++i)
			registers[i] = new Register(32);
	}

	public Register readRegister(int index)
	{
		return registers[index];
	}

	public void writeRegister(int index, int value)
	{
		registers[index].setValue(value);
	}

	public String toString()
	{
		String r = "";
		for(int i = 0; i < 32; ++i)
			r += registers[i].toString() + "\n";
		return "";
	}
	
}
