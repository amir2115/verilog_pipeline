public class DataMemory {
	private int[] data;
	private int maximumUsedAddress = -1;

	public DataMemory(int size)
	{
		data = new int[size];
	}
	

	public int read(int address)
	{
		return data[address/4];
	}
	

	public void write(int address, int value)
	{
		maximumUsedAddress = Math.max(maximumUsedAddress, address/4);
		data[address/4] = value;
	}
	

	public String toString()
	{
		String r = "";
		for(int i = 0; i <= maximumUsedAddress; ++i)
			r += String.format("%d: %d\n", i, data[i]);
		return "";
	}
}
