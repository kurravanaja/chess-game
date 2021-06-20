import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Game {
	static Board board = new Board();
	static Piece[] pawnW = new Pawn[8];
	static Piece[] pawnB = new Pawn[8];
	static Piece knightW1;
	static Piece knightW2;
	static Piece bishopW1;
	static Piece bishopW2;
	static Piece rookW2;
	static Piece rookW1;
	static Piece queenW;
	static Piece knightB1;
	static Piece knightB2;
	static Piece bishopB1;
	static Piece bishopB2;
	static Piece rookB1;
	static Piece rookB2;
	static Piece queenB;
	static Piece kingW;
	static Piece kingB;

	static boolean player = true;
	Piece active = null;
	boolean isSelected = false;
	static ArrayList<Piece> AllPieces = new ArrayList<Piece>();

	ArrayList<Move> allPossiblesMoves = new ArrayList<Move>();
	public static boolean drag = false;
	
	static List<Move> allPlayersMove = new ArrayList<Move>();
	static List<Move> allEnemysMove = new ArrayList<Move>();
	
	public Game() {
		for (int i = 0; i < 8; i++) {
			pawnW[i] = new Pawn(i, 6, true, board, 1);
			pawnB[i] = new Pawn(i, 1, false, board, -1);
		}
		knightB1 = new Knight(6, 0, false, board, -3);
		knightB2 = new Knight(1, 0, false, board, -3);
		bishopB1 = new Bishop(5, 0, false, board, -3);
		bishopB2 = new Bishop(2, 0, false, board, -3);
		rookB1 = new Rook(7, 0, false, board, -5);
		rookB2 = new Rook(0, 0, false, board, -5);
		queenB = new Queen(3, 0, false, board, -8);
		kingB = new King(4, 0, false, board, -100);

		rookW1 = new Rook(7, 7, true, board, 5);
		rookW2 = new Rook(0, 7, true, board, 5);
		knightW1 = new Knight(1, 7, true, board, 3);
		knightW2 = new Knight(6, 7, true, board, 3);
		bishopW1 = new Bishop(2, 7, true, board, 3);
		bishopW2 = new Bishop(5, 7, true, board, 3);
		queenW = new Queen(3, 7, true, board, 8);
		kingW = new King(4, 7, true, board, 100);
		fillAllPieces();
	}
	
	public void draw(Graphics g, int x, int y) {
		drawBoard(g);
		drawPiece(g);
		drawPossibleMoves(g);
		drag(active,x, y ,g);
	}
	
	public static void generateOnePlayerMoves() {
		allPlayersMove = new ArrayList<Move>();
		for(Piece p: AllPieces) {
			if(p.isWhite() == player) {
				p.fillAllPossibleMoves();
				allPlayersMove.addAll(p.getMoves());
			}
		}
	}
	
	public static void generateAllEnemysMoves() {
		allEnemysMove = new ArrayList<Move>();
		for(Piece p: AllPieces) {
			if(p.isWhite() != player) {
				p.fillAllPossibleMoves();
				allPlayersMove.addAll(p.getMoves());
			}
		}
	}

	public void changeSide() {
		player = !player;
	}
	
	public void selectPiece(int x, int y) {
		if(active == null) {
			active = board.getPiece(x, y);
			if(active!=null && active.isWhite() == player) {
				active.fillAllPossibleMoves();
			}else {
				active = null;
			}
		}
	}
	
	public void drag(Piece piece, int x, int y, Graphics g) {
		if(piece!=null && drag == true) {
			piece.draw2(g, player, x, y);			
		}
	}
	
	public void move(int x, int y) {
		if(active != null) {
			if(active.makeMove(x, y, board)) {
				tryToPromote(active);
				changeSide();
			}
			active  = null;
		}
	}
	
	public void drawBoard(Graphics g) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((i + j) % 2 == 1) {
					g.setColor(new Color(113, 144, 58));
				} else {
					g.setColor(new Color(182, 142, 96));
				}
				g.fillRect(i * Piece.size, j * Piece.size, Piece.size, Piece.size);
			}
		}
	}
	
	
	public void tryToPromote(Piece p) {
		if(p instanceof Pawn) {
			if(((Pawn)p).madeToTheEnd()) {
				popUpPieces(p.pieceColor, p);
			}
		}
	}
	
	public void choosePiece(Piece p, int choice) {
		switch (choice) {
		case 0:
			AllPieces.remove(p);
			p = new Queen(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 8: -8);					
			AllPieces.add(p);
			break;
		case 1:
			AllPieces.remove(p);
			p = new Rook(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 5: -5);
			AllPieces.add(p);
			break;
		case 2:
			AllPieces.remove(p);
			p = new Knight(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 3: -3);
			AllPieces.add(p);
			break;
		case 3: 
			AllPieces.remove(p);
			p = new Bishop(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 3: -3);
			AllPieces.add(p);
			break;
		default:
			AllPieces.remove(p);
			p = new Queen(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 8: -8);
			AllPieces.add(p);
			break;
		}
	}

	public void drawPossibleMoves(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));
		if (active != null) {
			active.showMoves(g2);
		}

	}

	public void drawPiece(Graphics g) {
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Piece.size));
		for (Piece p : AllPieces) {
			p.draw(g, false);
		}

	}
	
	public void  popUpPieces(Color color, Piece p) {
		JFrame frame = new JFrame("Choose piece");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2,2, 1, 1));
		frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		
		JButton rook = new JButton(PieceImages.ROOK);
		rook.setFont(new Font(Font.DIALOG, Font.BOLD, 75));
		rook.setForeground(color);
		rook.setBackground(new Color(182, 142, 96));
		rook.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				choosePiece(p, 1);
				frame.dispose();
			}
		});
		
		
		JButton queen = new JButton(PieceImages.QUEEN);
		queen.setFont(new Font(Font.DIALOG, Font.BOLD, 75));
		queen.setForeground(color);
		queen.setBackground(new Color(182, 142, 96));
		queen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				choosePiece(p, 0);
				frame.dispose();
			}
		});
		
		JButton knight = new JButton(PieceImages.KNIGHT);
		knight.setFont(new Font(Font.DIALOG, Font.BOLD, 75));
		knight.setForeground(color);
		knight.setBackground(new Color(113, 144, 58));
		knight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				choosePiece(p, 2);
				frame.dispose();
			}
		});
		
		JButton bishop = new JButton(PieceImages.BISHOP);
		bishop.setFont(new Font(Font.DIALOG, Font.BOLD, 75));
		bishop.setForeground(color);
		bishop.setBackground(new Color(113, 144, 58));
		bishop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				choosePiece(p, 3);
				frame.dispose();
			}
		});
		
		panel.add(bishop);
		panel.add(queen);
		panel.add(rook);
		panel.add(knight);
		
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public void fillAllPieces() {
		for (int i = 0; i < 8; i++) {
			AllPieces.add(pawnB[i]);
			AllPieces.add(pawnW[i]);
		}
		AllPieces.add(queenW);
		AllPieces.add(queenB);
		AllPieces.add(kingW);
		AllPieces.add(kingB);
		AllPieces.add(rookW1);
		AllPieces.add(rookW2);
		AllPieces.add(rookB1);
		AllPieces.add(rookB2);
		AllPieces.add(bishopW2);
		AllPieces.add(bishopW1);
		AllPieces.add(bishopB1);
		AllPieces.add(bishopB2);
		AllPieces.add(knightB1);
		AllPieces.add(knightB2);
		AllPieces.add(knightW1);
		AllPieces.add(knightW2);
	}

}
