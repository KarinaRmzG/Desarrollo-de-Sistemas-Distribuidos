class Threads extends Thread
{
  static long n;
  public void run()
  {
    for (int i = 0; i < 100000; i++)
        n++;
  }
  public static void main(String[] args) throws Exception
  {
    Threads t1 = new Threads();
    Threads t2 = new Threads();
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    System.out.println(n);
  }
}