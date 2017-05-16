import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;

public class AdminFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminFrame frame = new AdminFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	
	
	public AdminFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 718, 305);
		contentPane = new JPanel();
		contentPane.setToolTipText("");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[133px][57.00px][69px][57px][69px][57px][111px]", "[20px][38px][20px][20px][20px][][][]"));
		
		JLabel lblAdministratorView = new JLabel("Administrator View");
		lblAdministratorView.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblAdministratorView.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblAdministratorView, "cell 0 0 7 1,grow");
		
		JLabel lblPlayer = new JLabel("Green Player");
		lblPlayer.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblPlayer, "cell 0 1,alignx center,aligny top");
		
		JLabel lblPlayer4 = new JLabel("Yellow Player");
		lblPlayer4.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer4.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblPlayer4, "cell 6 1,alignx center,aligny top");
		
		JLabel lblPlayer2 = new JLabel("Red Player");
		lblPlayer2.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer2.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblPlayer2, "cell 2 1,growx,aligny top");
		
		JLabel lblPlayer3 = new JLabel("Blue Player");
		lblPlayer3.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer3.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblPlayer3, "cell 4 1,growx,aligny top");
		
		JLabel lblGreen1 = new JLabel("Pawn 1: Home Row 7");
		lblGreen1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblGreen1.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblGreen1, "cell 0 2,alignx left,aligny bottom");
		
		JLabel lblGreen2 = new JLabel("Pawn 2: Home Row 7");
		lblGreen2.setHorizontalAlignment(SwingConstants.CENTER);
		lblGreen2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblGreen2, "cell 0 3,alignx left,growy");
		
		JLabel lblGreen3 = new JLabel("Pawn 3: Home Row 7");
		lblGreen3.setHorizontalAlignment(SwingConstants.CENTER);
		lblGreen3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblGreen3, "cell 0 4,alignx left,growy");
		
		JLabel lblGreen4 = new JLabel("Pawn 4: Home Row 7");
		lblGreen4.setHorizontalAlignment(SwingConstants.CENTER);
		lblGreen4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblGreen4, "cell 0 5,alignx left,growy");
		
		JLabel lblRed1 = new JLabel("Pawn 1: Home Row 7");
		lblRed1.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed1, "cell 2 2,alignx left");
		
		JLabel lblRed2 = new JLabel("Pawn 2: Home Row 7");
		lblRed2.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed2, "cell 2 3");
		
		JLabel lblRed3 = new JLabel("Pawn 3: Home Row 7");
		lblRed3.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed3, "cell 2 4");
		
		JLabel lblRed4 = new JLabel("Pawn 4: Home Row 7");
		lblRed4.setHorizontalAlignment(SwingConstants.CENTER);
		lblRed4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblRed4, "cell 2 5");
		
		JLabel lblBlue1 = new JLabel("Pawn 1: Home Row 7");
		lblBlue1.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue1, "cell 4 2,alignx left");
		
		JLabel lblBlue2 = new JLabel("Pawn 2: Home Row 7");
		lblBlue2.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue2, "cell 4 3");
		
		JLabel lblBlue3 = new JLabel("Pawn 3: Home Row 7");
		lblBlue3.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue3, "cell 4 4");
		
		JLabel lblBlue4 = new JLabel("Pawn 4: Home Row 7");
		lblBlue4.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlue4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblBlue4, "cell 4 5");
		
		JLabel lblYellow1 = new JLabel("Pawn 1: Home Row 7");
		lblYellow1.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow1, "cell 6 2,alignx left");
		
		JLabel lblYellow2 = new JLabel("Pawn 2: Home Row 7");
		lblYellow2.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow2, "cell 6 3");
		
		JLabel lblYellow3 = new JLabel("Pawn 3: Home Row 7");
		lblYellow3.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow3, "cell 6 4");
		
		JLabel lblYellow4 = new JLabel("Pawn 4: Home Row 7");
		lblYellow4.setHorizontalAlignment(SwingConstants.CENTER);
		lblYellow4.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.add(lblYellow4, "cell 6 5");
		
		JLabel lblNewLabel = new JLabel("Status:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(lblNewLabel, "cell 0 7,alignx right");
		
		JLabel lblStatus = new JLabel("Waiting to Start");
		contentPane.add(lblStatus, "cell 2 7");
	}
	
	public void set_status_label(String status){
		JLabel status_label = (JLabel) this.contentPane.getComponent(this.contentPane.getComponentCount() - 1);
		
		status_label.setText(status);
	}
	
	public void update_pawn_positions(Board board){
		int START_INDEX = 5;
		Pawn[] pawns = {new Pawn(0, "green"), new Pawn(1, "green"), new Pawn(2, "green"), new Pawn(3, "green"), new Pawn(0, "red"), new Pawn(1, "red"), new Pawn(2, "red"), new Pawn(3, "red"), new Pawn(0, "blue"), new Pawn(1, "blue"), new Pawn(2, "blue"), new Pawn(3, "blue"), new Pawn(0, "yellow"), new Pawn(1, "yellow"), new Pawn(2, "yellow"), new Pawn(3, "yellow")};
	
		for(int i = 0; i < 16; i++){
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
				s = "Main " + (loc.get_index() + 1);
			}
			else{
				s = "Home Row " + (loc.get_index() + 1);
			}
			if(loc.get_safe()){
				s = "*" + s + "*";
			}
			l.setText("Pawn " + (p.get_id() + 1) + ": " + s);
		}
	
	}

}
