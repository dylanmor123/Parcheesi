import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JSpinner;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerListModel;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HPlayerFrame extends JFrame{

	private JPanel contentPane;
	
	// set of GUI controls that we modify
	private JLabel lblPlayerGreen;
	private JLabel lblPlayerYellow;
	private JLabel lblPlayerBlue;
	private JLabel lblPlayerRed;
	
	private JLabel lblStatus;
	
	private JLabel lblHomeRowEntry;
	private JLabel lblMainEntry;
	
	private JLabel lblRolls;
	private JSpinner spinner;
	private JSpinner spinnerRollChoice;
	private JButton btnMakeMove;
	private JButton btnUndoMoves;
	
	private JLabel lblIllegal;
	
	public JSpinner get_Pawn_Spinner(){
		return this.spinner;
	}
	
	public JSpinner get_Roll_Spinner(){
		return this.spinnerRollChoice;
	}
	
	public JLabel get_Illegal(){
		return this.lblIllegal;
	}

	/**
	 * Create the frame.
	 */
	public HPlayerFrame(HPlayer listener) {
		// handles GUI initialization
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 718, 460);
		contentPane = new JPanel();
		contentPane.setToolTipText("");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[133px][57.00px][133.00px][57px][133.00px,grow][57px][133.00px]", "[20px][38px][20px][20px][20px][][][][][][][][][][][61.00][grow]"));
		
		JLabel lblTitle = new JLabel("Parcheesi");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblTitle, "cell 0 0 7 1,grow");
		
		lblPlayerGreen = new JLabel("Green Player");
		lblPlayerGreen.setFont(new Font("Tahoma", Font.PLAIN, 16));
		contentPane.add(lblPlayerGreen, "cell 0 1,alignx center,aligny top");
		
		lblPlayerYellow = new JLabel("Yellow Player");
		lblPlayerYellow.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayerYellow.setFont(new Font("Tahoma", Font.PLAIN, 16));
		contentPane.add(lblPlayerYellow, "cell 6 1,alignx center,aligny top");
		
		lblPlayerRed = new JLabel("Red Player");
		lblPlayerRed.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayerRed.setFont(new Font("Tahoma", Font.PLAIN, 16));
		contentPane.add(lblPlayerRed, "cell 2 1,growx,aligny top");
		
		lblPlayerBlue = new JLabel("Blue Player");
		lblPlayerBlue.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayerBlue.setFont(new Font("Tahoma", Font.PLAIN, 16));
		contentPane.add(lblPlayerBlue, "cell 4 1,growx,aligny top");
		
		JLabel lblGreen1 = new JLabel("Pawn 1:");
		lblGreen1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGreen1.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblGreen1, "cell 0 2,alignx left,aligny bottom");
		
		JLabel lblGreen2 = new JLabel("Pawn 2:");
		lblGreen2.setHorizontalAlignment(SwingConstants.CENTER);
		lblGreen2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblGreen2, "cell 0 3,alignx left,growy");
		
		JLabel lblGreen3 = new JLabel("Pawn 3:");
		lblGreen3.setHorizontalAlignment(SwingConstants.CENTER);
		lblGreen3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblGreen3, "cell 0 4,alignx left,growy");
		
		JLabel lblGreen4 = new JLabel("Pawn 4:");
		lblGreen4.setHorizontalAlignment(SwingConstants.CENTER);
		lblGreen4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblGreen4, "cell 0 5,alignx left,growy");
		
		JLabel lblRed1 = new JLabel("Pawn 1:");
		lblRed1.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed1, "cell 2 2,alignx left");
		
		JLabel lblRed2 = new JLabel("Pawn 2:");
		lblRed2.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed2, "cell 2 3");
		
		JLabel lblRed3 = new JLabel("Pawn 3:");
		lblRed3.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed3, "cell 2 4");
		
		JLabel lblRed4 = new JLabel("Pawn 4:");
		lblRed4.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed4, "cell 2 5");
		
		JLabel lblBlue1 = new JLabel("Pawn 1:");
		lblBlue1.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue1, "cell 4 2,alignx left");
		
		JLabel lblBlue2 = new JLabel("Pawn 2:");
		lblBlue2.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue2, "cell 4 3");
		
		JLabel lblBlue3 = new JLabel("Pawn 3:");
		lblBlue3.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue3, "cell 4 4");
		
		JLabel lblBlue4 = new JLabel("Pawn 4:");
		lblBlue4.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue4, "cell 4 5");
		
		JLabel lblYellow1 = new JLabel("Pawn 1: ");
		lblYellow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow1, "cell 6 2,alignx left");
		
		JLabel lblYellow2 = new JLabel("Pawn 2: ");
		lblYellow2.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow2, "cell 6 3");
		
		JLabel lblYellow3 = new JLabel("Pawn 3: ");
		lblYellow3.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow3, "cell 6 4");
		
		JLabel lblYellow4 = new JLabel("Pawn 4:");
		lblYellow4.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow4, "cell 6 5");
		
		JLabel lblNewLabel = new JLabel("Status:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblNewLabel, "cell 0 7,alignx right");
		
		lblStatus = new JLabel("");
		contentPane.add(lblStatus, "cell 2 7");
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		contentPane.add(separator, "cell 0 9 7 1,growx");
		
		JLabel lblMainSpaces = new JLabel("68 main ring spaces");
		lblMainSpaces.setFont(new Font("Tahoma", Font.ITALIC, 16));
		lblMainSpaces.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPane.add(lblMainSpaces, "cell 0 10 2 1,alignx center");
		
		JLabel lblHomeRow = new JLabel("7 home row spaces");
		lblHomeRow.setFont(new Font("Tahoma", Font.ITALIC, 16));
		contentPane.add(lblHomeRow, "cell 2 10 2 1,alignx center");
		
		lblHomeRowEntry = new JLabel("Your home row entry space:");
		lblHomeRowEntry.setFont(new Font("Tahoma", Font.ITALIC, 16));
		contentPane.add(lblHomeRowEntry, "cell 4 10 3 1,growx");
		
		lblMainEntry = new JLabel("Your main ring entry space:");
		lblMainEntry.setFont(new Font("Tahoma", Font.ITALIC, 16));
		contentPane.add(lblMainEntry, "cell 4 11 3 1,growx");
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.BLACK);
		contentPane.add(separator_1, "cell 0 13 7 1,growx");
		
		JLabel lblRollsRemaining = new JLabel("Rolls Remaining:");
		lblRollsRemaining.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblRollsRemaining, "cell 0 14 2 1");
		
		lblRolls = new JLabel("");
		contentPane.add(lblRolls, "cell 2 14");
		
		JLabel lblNewLabel_1 = new JLabel("Make a Move:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblNewLabel_1, "cell 0 15");
		
		JLabel lblPawn = new JLabel("Pawn:");
		contentPane.add(lblPawn, "cell 1 15,alignx right");
		
		spinner = new JSpinner();
		spinner.setEnabled(false);
		spinner.setModel(new SpinnerListModel(new String[] {"1", "2", "3", "4"}));
		contentPane.add(spinner, "cell 2 15,growx");
		
		JLabel lblRoll = new JLabel("Roll:");
		contentPane.add(lblRoll, "cell 3 15,alignx right");
		
		spinnerRollChoice = new JSpinner();
		spinnerRollChoice.setEnabled(false);
		spinnerRollChoice.setModel(new SpinnerListModel(new String[] {"  "}));
		contentPane.add(spinnerRollChoice, "cell 4 15,growx");
		
		btnMakeMove = new JButton("Make Move");
		btnMakeMove.addActionListener(listener);
		btnMakeMove.setEnabled(false);
		contentPane.add(btnMakeMove, "cell 6 15,growx");
		
		lblIllegal = new JLabel("");
		lblIllegal.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblIllegal.setForeground(Color.RED);
		contentPane.add(lblIllegal, "cell 0 16 3 1,alignx center");
		
		btnUndoMoves = new JButton("Undo Moves");
		btnUndoMoves.addActionListener(listener);
		btnUndoMoves.setEnabled(false);
		contentPane.add(btnUndoMoves, "cell 6 16,growx,aligny top");
	}
	
	public void set_status_label(String status){
		lblStatus.setText(status);
	}
	
	public void update_pawn_positions(Board board){
		int START_INDEX = 5;
		Pawn[] pawns = {new Pawn(0, "green"), new Pawn(1, "green"), new Pawn(2, "green"), new Pawn(3, "green"), new Pawn(0, "red"), new Pawn(1, "red"), new Pawn(2, "red"), new Pawn(3, "red"), new Pawn(0, "blue"), new Pawn(1, "blue"), new Pawn(2, "blue"), new Pawn(3, "blue"), new Pawn(0, "yellow"), new Pawn(1, "yellow"), new Pawn(2, "yellow"), new Pawn(3, "yellow")};
	
		for(int i = 0; i < 16; i++){
			Pawn p = pawns[i];
			JLabel l = (JLabel) this.contentPane.getComponent(START_INDEX + i);
			
			PawnLocation loc = board.get_Pawn_Location(p);
			if(loc.get_type().equals("home circle")){
				l.setText("Pawn " + (p.get_id() + 1) + ": Home Circle");
			}
			else if(loc.get_type().equals("home")){
				l.setText("Pawn " + (p.get_id() + 1) + ": Home");
			}
			else if(loc.get_type().equals("main")){
				l.setText("Pawn " + (p.get_id() + 1) + ": Main " + (loc.get_index() + 1));
			}
			else{
				l.setText("Pawn " + (p.get_id() + 1) + ": Home Row " + (loc.get_index() + 1));
			}
		}
	
	}
	
	public void update_rolls(int[] rolls){
		// update lblRolls
		String to_show = "";
		if(rolls.length != 0){
			to_show = to_show + rolls[0];
			for (int i = 1; i < rolls.length; i++){
				int r = rolls[i];
				to_show = to_show + ", " + r;
			}
		}
		
		lblRolls.setText(to_show);
		
		// update spinnerRollChoice with options
		String [] for_spinner;
		if(rolls.length != 0){
			for_spinner = new String[rolls.length];
			for(int i = 0; i < rolls.length; i++){
				for_spinner[i] = Integer.toString(rolls[i]);
			}
		}
		else{
			for_spinner = new String[]{" "};
		}
		
		
		spinnerRollChoice.setModel(new SpinnerListModel(for_spinner));
		
	}
	
	public void startGame(String color) throws Exception{
		// update GUI
		this.setVisible(true);
		Font bold = new Font("Tahoma", Font.BOLD, 16);
		int home_row_index = 0;
		int main_ring_index = 0;
		if(color.equals("green")){
			this.lblPlayerGreen.setFont(bold);
			home_row_index = 64;
			main_ring_index = 1;
		}
		else if(color.equals("red")){
			this.lblPlayerRed.setFont(bold);
			home_row_index = 13;
			main_ring_index = 18;
		}
		else if(color.equals("blue")){
			this.lblPlayerBlue.setFont(bold);
			home_row_index = 30;
			main_ring_index = 35;
		}
		else{
			this.lblPlayerYellow.setFont(bold);
			home_row_index = 47;
			main_ring_index = 52;
		}
		
		this.lblHomeRowEntry.setText("Your home row entry space: " + home_row_index);
		this.lblMainEntry.setText("Your main ring entry space: " + main_ring_index);
		
		//set status to waiting
		this.set_status_label("Waiting for turn");
	};
	
	public void ready_for_move(){
		this.btnMakeMove.setEnabled(true);
		this.btnUndoMoves.setEnabled(true);
		this.spinner.setEnabled(true);
		this.spinnerRollChoice.setEnabled(true);
		this.set_status_label("Your move");
	}
	
	public void move_over(){
		// disable form controls
		this.btnMakeMove.setEnabled(false);
		this.btnUndoMoves.setEnabled(false);
		this.spinner.setEnabled(false);
		this.spinnerRollChoice.setEnabled(false);
		
		this.set_status_label("Waiting for turn");
	}
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HPlayer player = new HPlayer("name");
					HPlayerFrame frame = new HPlayerFrame(player);
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
