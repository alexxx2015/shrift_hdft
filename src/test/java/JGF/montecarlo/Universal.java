/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - Version 2.0             *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         *
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*      Original version of this code by Hon Yau (hwyau@epcc.ed.ac.uk)     *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/


package JGF.montecarlo;
/**
  * Base class for all non-trivial classes.
  * Used as a centralised repository for all the functionalities which
  * all classes written by me, which will be of use.
  *
  * @author H W Yau
  * @version $Revision: 1.7 $ $Date: 1999/02/16 18:53:43 $
  */
public class Universal {
  //------------------------------------------------------------------------
  // Class variables.
  //------------------------------------------------------------------------
  /**
    * Class variable, for whether to print debug messages.  This one is
    * unique to this class, and can hence be set in the one place.
    */
  private static boolean UNIVERSAL_DEBUG;
  //------------------------------------------------------------------------
  // Instance variables.
  //------------------------------------------------------------------------
  /**
    * Variable, for whether to print debug messages.  This one can
    * be set by subsequent child classes.
    */
  private boolean DEBUG;
  /**
    * The prompt to write before any debug messages.
    */
  private String prompt;

  //------------------------------------------------------------------------
  // Constructors.
  //------------------------------------------------------------------------
  /**
    * Default constructor.
    */
  public Universal() {
    super();
    this.DEBUG=true;
    this.UNIVERSAL_DEBUG=true;
    this.prompt="Universal> ";
  }
  //------------------------------------------------------------------------
  // Methods.
  //------------------------------------------------------------------------
  //------------------------------------------------------------------------
  // Accessor methods for class AppDemo/Universal.
  // Generated by 'makeJavaAccessor.pl' script.  HWY.  20th January 1999.
  //------------------------------------------------------------------------
  /**
    * Accessor method for private instance variable <code>DEBUG</code>.
    *
    * @return Value of instance variable <code>DEBUG</code>.
    */
  public boolean get_DEBUG() {
    return(this.DEBUG);
  }
  /**
    * Set method for private instance variable <code>DEBUG</code>.
    *
    * @param DEBUG the value to set for the instance variable <code>DEBUG</code>.
    */
  public void set_DEBUG(boolean DEBUG) {
    this.DEBUG = DEBUG;
  }
  /**
    * Accessor method for private instance variable <code>UNIVERSAL_DEBUG</code>.
    *
    * @return Value of instance variable <code>UNIVERSAL_DEBUG</code>.
    */
  public boolean get_UNIVERSAL_DEBUG() {
    return(this.UNIVERSAL_DEBUG);
  }
  /**
    * Set method for private instance variable <code>DEBUG</code>.
    *
    * @param UNIVERSAL_DEBUG the value to set for the instance
    *        variable <code>UNIVERSAL_DEBUG</code>.
    */
  public void set_UNIVERSAL_DEBUG(boolean UNIVERSAL_DEBUG) {
    this.UNIVERSAL_DEBUG = UNIVERSAL_DEBUG;
  }
  /**
    * Accessor method for private instance variable <code>prompt</code>.
    *
    * @return Value of instance variable <code>prompt</code>.
    */
  public String get_prompt() {
    return(this.prompt);
  }
  /**
    * Set method for private instance variable <code>prompt</code>.
    *
    * @param prompt the value to set for the instance variable <code>prompt</code>.
    */
  public void set_prompt(String prompt) {
    this.prompt = prompt;
  }
  //------------------------------------------------------------------------
  /**
    * Used to print debug messages.
    *
    * @param s The debug message to print out, to PrintStream "out".
    */
  public void dbgPrintln(String s) {
    if( DEBUG || UNIVERSAL_DEBUG ) {
      System.out.println("DBG "+prompt+s);
    }
  }
  /**
    * Used to print debug messages.
    *
    * @param s The debug message to print out, to PrintStream "out".
    */
  public void dbgPrint(String s) {
    if( DEBUG || UNIVERSAL_DEBUG ) {
      System.out.print("DBG "+prompt+s);
    }
  }
  /**
    * Used to print error messages.
    *
    * @param s The error message to print out, to PrintStream "err".
    */
  public void errPrintln(String s) {
    System.err.println(prompt+s);
  }
  /**
    * Used to print error messages.
    *
    * @param s The error message to print out, to PrintStream "err".
    */
  public void errPrint(String s) {
    System.err.print(prompt+s);
  }
}

