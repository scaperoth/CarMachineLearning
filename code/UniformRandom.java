// File: UniformRandom.java
//
// Author: Rahul Simha
// Created: Sept 22, 1998.
//
// Uniform random number generator with different uniform 
// generators.

public class UniformRandom {

  // Basic Lehmer generator - constants
  static final long m = 2147483647L;
  static final long a = 48271L;
  static final long q = 44488L;
  static final long r = 3399L;

  static long r_seed = 12345678L; 

  public static void set_seed (long seed)
  {
    r_seed = seed;
  }

  // Basic Lehmer generator - uniform[0,1]
  // For more information see Knuth, Vol. II.
  public static double uniform ()
  {
    long hi = r_seed / q;
    long lo = r_seed - q * hi;
    long t = a * lo - r * hi;
    if (t > 0)
      r_seed = t;
    else
      r_seed = t + m;
    return ( (double) r_seed / (double) m );
  }

  // U[a,b] generator 
  public static double uniform (double a, double b)
  {
    if (b > a)
      return ( a + (b-a) * uniform() );
    else { 
      System.out.println ("ERR in uniform(double,double):a="+a+"b="+b); 
      return 0;
    }
  }

  // Discrete Uniform random generator - returns an
  // integer between a and b
  public static long uniform (long a, long b)
  {
    if (b > a) {
      double x = uniform ();
      long c = ( a + (long) Math.floor((b-a+1)*x) );
      return c;
    }
    else if (a == b) 
      return a;
    else { 
      System.out.println ("ERR: in uniform(long,long):a="+a+"b="+b); 
      return 0;
    }
  }

  public static int uniform (int a, int b)
  {
    return (int) uniform ((long) a, (long) b);
  }
  

} // End of class "UniformRandom"
