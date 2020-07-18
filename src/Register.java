public class Register {

	private String name;
	private int size;
	private int value;

	public Register(int size)
	{
		this.size = size;
	}


	public int getSegment(int left, int right)
	{
		return (value >> right) & ((1 << left - right + 1)-1);
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	public int getSize()
	{
		return size;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public void clear()
	{
		value = 0;
	}

	public String toString()
	{
		String r = Integer.toBinaryString(value);
		while(r.length() < size)
			r = "0" + r;
		if(name != null)
			r = name + ": " + r;
		return r + " <==> " + value;
	}
}
