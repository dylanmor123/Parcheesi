
class Pawn {
  protected int /* 0-n */ id;
  protected String color;
  Pawn (int id, String color) {
    this.id=id;
    this.color=color;
  }
  
  @Override
  public boolean equals(Object p){
	  if(p == null){
		  return false;
	  }
	  if (!Pawn.class.isAssignableFrom(p.getClass())) {
	        return false;
	    }
	  Pawn pawn = (Pawn) p;
	  return (this.color.equals(pawn.color)) && (this.id == pawn.id);
  }
  
  public String get_color(){
	  return this.color;
  }
  
  static Pawn p1;
  static Pawn p2;
  static Pawn p3;
  static Pawn p4;
  
  static void createExamples(){
	  if(p1 == null){
		  p1 = new Pawn(0, "green");
		  p2 = new Pawn(0, "green");
		  p3 = new Pawn(1, "green");
		  p4 = new Pawn(0, "blue");
	  }
  }
  
  public static void main(String argv[]){
	  Tester.check(p1.equals(p2), "equal pawns");
	  Tester.check(!p1.equals(p3), "different id pawns");
	  Tester.check(!p1.equals(p4), "different color pawns");
  }
}