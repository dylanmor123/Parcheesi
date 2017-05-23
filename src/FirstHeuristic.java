import java.util.ArrayList;

public class FirstHeuristic implements IHeuristic{
	
	
	public int eval(Board brd, String color){
		return 9999*pawns_in_home(brd, color) + 150*pawns_in_homerow(brd, color) + 200*count_safe_pawns(brd, color) 
		+ 50*count_main_pawns(brd, color) + 10*num_blockades(brd, color) + total_distance(brd, color) 
		- total_opp_distances(brd, color) - 200*count_opp_pawns(brd, color);
	}
	
	
	//Counts number of pawns on the board that belong to the 
	//player corresponding to 'color'
	public int count_main_pawns(Board brd, String color){
		int num_main_pawns = 0;
		
		for (Space s: brd.get_Spaces()){
			ArrayList<Pawn> pawns = s.get_pawns();
			if(pawns.size() > 0){
				for (Pawn p: pawns){
					if (p.get_color().equals(color)){
						num_main_pawns++;
					}
				}
			}
		}
		return num_main_pawns;
	}
	
	//Counts number of pawns on the board that 
	//belong to the opponents of the 
	//player corresponding to 'color'
	public int count_opp_pawns(Board brd, String color){
		int num_opp_pawns = 0;
		
		for (Space s: brd.get_Spaces()){
			ArrayList<Pawn> pawns = s.get_pawns();
			if(pawns.size() > 0){
				for (Pawn p: pawns){
					if (!p.get_color().equals(color)){
						num_opp_pawns++;
					}
				}
			}
		}
		return num_opp_pawns;
	}
	
	//Return the number of pawns in home of
	//player corresponding to 'color'
	public int pawns_in_home(Board brd, String color){
		return brd.get_Home(color).get_pawns().size();
	}
	
	
	//Return the number of pawns in the homerow of
	//player corresponding to 'color'
	
	public int pawns_in_homerow(Board brd, String color){
		int num_homerow_pawns = 0;
		
		for (Space s: brd.get_HomeRow(color)){
			num_homerow_pawns += s.get_pawns().size();
			}
		
		return num_homerow_pawns;
	}
	
	//Return the number of blockades formed by
	//player corresponding to 'color'
	public int num_blockades(Board brd, String color){
		int num_blockades = 0;
		for (Space s: brd.get_Spaces()){
			ArrayList<Pawn> pawns = s.get_pawns();
			if(pawns.size() > 1){
				if(pawns.get(0).get_color().equals(color)){
					num_blockades++;
				}
			}
		}
		return num_blockades;
		
	}
	

	//Counts number of pawns on the board that belong to the 
	//player corresponding to 'color' that are currently on safe spaces
	public int count_safe_pawns(Board brd, String color){
		int num_safe_pawns = 0;
		
		for (Space s: brd.get_Spaces()){
			ArrayList<Pawn> pawns = s.get_pawns();
			if(pawns.size() > 0 && s.get_safe() && !(s instanceof Entry && color.equals(s.get_color()))){
				for (Pawn p: pawns){
					if (p.get_color().equals(color)){
						num_safe_pawns++;
					}
				}
			}
		}
		return num_safe_pawns;
	}
	
	
	//Calculates sum of distance of all pawns from start
	//of the player corresponding to 'color'
	public int total_distance(Board brd, String color){
		int total_distance = 0;
		int start = 0;
		
		if(color.equals("green")){
			start = 0;
		}
		else if(color.equals("red")){
			start = 17;
		}
		else if(color.equals("blue")){
			start = 34;
		}
		else{
			start = 51;
		}
		
		for (int i = 0; i < brd.get_Spaces().length; i++){
			ArrayList<Pawn> pawns = brd.get_Spaces()[i].get_pawns();
			if(pawns.size() > 0){
				for (Pawn p: pawns){
					if (p.get_color().equals(color)){
						if(i < start){
							total_distance += (68-start) + i;
						}
						else{
							total_distance += (i - start);
						}
					}
				}
			}
		}
		return total_distance;
	}
	
	//Calculates sum of distance of all pawns from start
	//of all opponents to player corresponding to 'color'
	public int total_opp_distances(Board brd, String color){
		if(color.equals("yellow")){
			return total_distance(brd, "green") + total_distance(brd, "red") + total_distance(brd, "blue");
		}
		else if(color.equals("red")){
			return total_distance(brd, "green") + total_distance(brd, "yellow") + total_distance(brd, "blue");
		}
		else if(color.equals("blue")){
			return total_distance(brd, "green") + total_distance(brd, "yellow") + total_distance(brd, "red");
		}
		else{
			return total_distance(brd, "blue") + total_distance(brd, "yellow") + total_distance(brd, "red");
		}
	}
	
	
	
}
