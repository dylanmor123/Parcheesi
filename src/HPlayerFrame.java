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
import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerListModel;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class HPlayerFrame extends JFrame{

	private JPanel contentPane;
	
	private JLabel lblStatus;
	
	private JLabel lblRolls;
	private JButton btnMakeMove;
	private JButton btnUndoMoves;
	
	private JLabel lblIllegal;
	
	private JList spinner;
	private JList spinnerRollChoice;
	private JLabel lblBoardImage;
	
	private JLabel lblPawn_1;
	private JLabel lblPawn_2;
	private JLabel lblPawn_3;
	private JLabel lblPawn_4;
	
	public JList get_Pawn_Spinner(){
		return this.spinner;
	}
	
	public JList get_Roll_Spinner(){
		return this.spinnerRollChoice;
	}
	
	public JLabel get_Illegal(){
		return this.lblIllegal;
	}
	
	// String indicating if view is of starting board or current board given move updates
	private String state = "start";
	
	public void set_state(String s){
		this.state = s;
	}

	/**
	 * Create the frame.
	 */
	public HPlayerFrame(HPlayer listener) throws Exception{
		// handles GUI initialization
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 718, 697);
		contentPane = new JPanel();
		contentPane.setToolTipText("");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[133px][57.00px][133.00px,grow][57px][133.00px,grow][57px][133.00px]", "[20px][38px,grow][20px][20px][20px][][][][][][][][][][][61.00][]"));
		
		JLabel lblTitle = new JLabel("Parcheesi");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblTitle, "cell 0 0 7 1,grow");
		
		lblBoardImage = new JLabel("");
		lblBoardImage.setIcon(new ImageIcon(ImageIO.read(new File("img/base.png"))));
		contentPane.add(lblBoardImage, "cell 0 1 5 10,alignx center,aligny center");
		
		JLabel lblYourPawns = new JLabel("Your Pawns");
		lblYourPawns.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblYourPawns, "cell 5 1 2 1,alignx center,aligny bottom");
		
		lblPawn_1 = new JLabel("Pawn 1:");
		contentPane.add(lblPawn_1, "cell 5 2 2 1");
		
		lblPawn_2 = new JLabel("Pawn 2:");
		contentPane.add(lblPawn_2, "cell 5 3 2 1");
		
		lblPawn_3 = new JLabel("Pawn 3:");
		contentPane.add(lblPawn_3, "cell 5 4 2 1");
		
		lblPawn_4 = new JLabel("Pawn 4:");
		contentPane.add(lblPawn_4, "cell 5 5 2 1");
		
		JLabel lblPawnIndicesBegin = new JLabel("Pawn indices begin from ");
		lblPawnIndicesBegin.setFont(new Font("Tahoma", Font.ITALIC, 16));
		contentPane.add(lblPawnIndicesBegin, "cell 5 7 2 1,aligny top");
		
		JLabel lblYourEntrySpace = new JLabel("your entry space ");
		lblYourEntrySpace.setFont(new Font("Tahoma", Font.ITALIC, 16));
		contentPane.add(lblYourEntrySpace, "cell 5 8 2 1");
		
		lblStatus = new JLabel("");
		contentPane.add(lblStatus, "cell 2 13");
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.BLACK);
		contentPane.add(separator_1, "cell 0 12 7 1,growx");
		
		JLabel lblNewLabel = new JLabel("Status:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblNewLabel, "cell 0 13 2 1,alignx left");
		
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
		
		spinner = new JList();
		spinner.setEnabled(false);
		spinner.setVisibleRowCount(1);
		spinner.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		spinner.setModel(new AbstractListModel() {
			String[] values = new String[] {"1", "2", "3", "4"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		contentPane.add(spinner, "cell 2 15,grow");
		
		JLabel lblRoll = new JLabel("Roll:");
		contentPane.add(lblRoll, "cell 3 15,alignx right");
		
		spinnerRollChoice = new JList();
		spinnerRollChoice.setVisibleRowCount(1);
		spinnerRollChoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentPane.add(spinnerRollChoice, "cell 4 15,grow");
		
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
	
	public void update_board(Board board, String color) throws Exception{
		String outname = "img/" + this.state;
		board.to_PNG(outname);
		
		lblBoardImage.setIcon(new ImageIcon(ImageIO.read(new File(outname + ".png"))));
		
		// update pawn positions
		int START_INDEX = 3;
		int BOARD_LENGTH = 68;
		int MAIN_ENTRY;
		
		if(color.equals("green")){
			MAIN_ENTRY = 0;
		}
		else if(color.equals("red")){
			MAIN_ENTRY = 17;
		}
		else if(color.equals("blue")){
			MAIN_ENTRY = 34;
		}
		else{
			MAIN_ENTRY = 51;
		}
		
		
		Pawn[] pawns = {new Pawn(0, color), new Pawn(1, color), new Pawn(2, color), new Pawn(3, color)};
	
		for(int i = 0; i < 4; i++){
			Pawn p = pawns[i];
			JLabel l = (JLabel) this.contentPane.getComponent(START_INDEX + i);
			String s;
			PawnLocation loc = board.get_Pawn_Location(p);
			if(loc.get_type().equals("home circle")){
				s =  "Home Circle";
			}
			else if(loc.get_type().equals("home")){
				s = "Home";
			}
			else if(loc.get_type().equals("main")){
				s = "Main " + (((loc.get_index() + 1 - MAIN_ENTRY) % BOARD_LENGTH) + BOARD_LENGTH) % BOARD_LENGTH;
			}
			else{
				s = "Home Row " + (loc.get_index() + 1);
			}
			l.setText("Pawn " + (p.get_id() + 1) + ": " + s);
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
			for_spinner = new String[]{};
		}
		
		
		spinnerRollChoice.setModel(new AbstractListModel() {
			String[] values = for_spinner;
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
	}
	
	public void startGame(String color) throws Exception{
		// update GUI
		this.setVisible(true);
		
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
