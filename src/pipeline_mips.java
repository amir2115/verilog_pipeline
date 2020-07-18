import java.io.FileNotFoundException;

public class pipeline_mips {
	
	private Simulator simulator;
	private Assembler parser;
	

	public pipeline_mips() throws FileNotFoundException
	{
		simulator = new Simulator();
		parser = new Assembler();
	}

	public Simulator getSimulator() { return simulator; }

	public Assembler getParser() { return parser; }

	private void initializeDataMemory()
	{
		this.getSimulator().getDataMemory().write(0, 32);
	}
	

	public void initializeRegisterFile()
	{
		this.getSimulator().getRegisterFile().writeRegister(this.getParser().getRegisterNumber("$s0"), 31);
	}
	

	public static void main(String[] args) throws Exception 
	{
		pipeline_mips controller = new pipeline_mips();
		while(true)
		{
			try{
				controller.getParser().parse("file.txt", controller.getSimulator());
				controller.getParser().setRegistersNames(controller.getSimulator());
			}
			catch(Exception e)
			{
				System.out.println("Error: " + e.getMessage());
				continue;
			}
			controller.initializeDataMemory();
			controller.initializeRegisterFile();

			controller.getSimulator().run();
			break;
		}
	}

}
