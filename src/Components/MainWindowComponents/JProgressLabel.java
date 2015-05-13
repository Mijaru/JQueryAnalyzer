package Components.MainWindowComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class JProgressLabel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JLabel label_1 = null;
		private JLabel label_2 = null;
		private ImageIcon icon_2 = null;
		
		public JProgressLabel() {
			super();
			this.setBounds(10,430,475,30);
			this.setLayout(null);
			// texto
			label_1 = new JLabel();
			label_1.setOpaque(false);
			label_1.setFont(new Font("Verdana", Font.ROMAN_BASELINE, 11));
			label_1.setForeground(Color.BLACK);
			label_1.setHorizontalAlignment(JLabel.CENTER);
			label_1.setVerticalAlignment(JLabel.CENTER);
			label_1.setBounds(0,0,475,26);
			
			// barra de progresso maior.
			label_2 = new JLabel();
			label_2.setBounds(10,430,475,30);
			label_2.setBounds(1,1,0,26);
			label_2.setDoubleBuffered(false);
			label_2.setOpaque(false);
			
			this.add(label_1);
			this.add(label_2);
		}
		
		private long _paint_time = 0L;
		public void updateProgress(final String text, final float main, final float item) {
			if (_paint_time + (40000000) <= System.nanoTime() || main >= 100 || item >= 100) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							setMainProgress(main);
							setItemProgress(item);
							setText(text);
						}
					});
				}
				catch (Exception e) { e.printStackTrace(); }
				_paint_time = System.nanoTime();
			}
			
		}
		
		public void setPanelBackground(Color c) {
			this.setBackground(c);
		}

		public void setPanelBounds(int x, int y, int w, int h) {
			this.setBounds(x, y, w, h);
			label_1.setBounds(0, 0, w, h);
			label_2.setBounds(0, 0, w, h);
			label_2.setBorder(BorderFactory.createLineBorder(Color.GRAY));			
			this.setMainProgress(0.f);
			this.setItemProgress(0.f);
		}
		
		public void setText(final String t) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					label_1.setText(t);
					label_1.repaint();
				}
			});
			
		}
		
		public void setMainProgress(float perc) {
			Dimension size = label_2.getSize();
			size.width  = Math.max(size.width, 1);
			size.height = Math.max(size.height, 1);
			ImageIcon high = new ImageIcon(ClassLoader.getSystemResource("progress.png"));
			ImageIcon low = new ImageIcon(ClassLoader.getSystemResource("progress_gray.png"));
			low.setImage(low.getImage().getScaledInstance(size.width, size.height, 100));
			int x = (int)(size.width * perc) / 100;
			high.setImage(high.getImage().getScaledInstance(x <= 0 ? 1 : x, size.height + 2, 100));
			low.setImage(low.getImage().getScaledInstance(size.width,size.height, 100));
			BufferedImage progress = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
			progress.setAccelerationPriority(1.f);
			Graphics g = progress.getGraphics();
			g.drawImage(low.getImage(), 0, 0, null);
			g.drawImage(high.getImage(), 0, -1, null);
			g.dispose();
			icon_2 = new ImageIcon(progress);
			label_2.setIcon(icon_2);
		}
		
		public void setItemProgress(float perc) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					Dimension size = label_2.getSize();
					size.width  = Math.max(size.width, 1);
					size.height = Math.max(size.height, 1);
					ImageIcon high = new ImageIcon(ClassLoader.getSystemResource("progress_green.png"));
					int x = (int)(size.width * perc) / 100;
					high.setImage(high.getImage().getScaledInstance(x <= 0 ? 1 : x, 3, 100));
					BufferedImage progress = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
					progress.setAccelerationPriority(1.f);
					Graphics g = progress.getGraphics();
					g.drawImage(icon_2.getImage(), 0, 0, null);
					g.drawImage(high.getImage(), 0, size.height - 3, null);
					g.dispose();
					label_2.setIcon(new ImageIcon(progress));
				}
			});
		}
	}