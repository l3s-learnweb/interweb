package de.l3s.interwebj.test;


public class ClassB
    extends ClassA
{
	
	protected String s = "B";
	

	@Override
	public String getS()
	{
		return s;
	}
	

	public static void main(String[] args)
	{
		ClassA a = new ClassB();
		System.out.println(a.getS());
	}
	
}
