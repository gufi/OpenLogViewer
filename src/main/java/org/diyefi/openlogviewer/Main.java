/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.diyefi.openlogviewer;

import java.awt.EventQueue;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import org.diyefi.openlogviewer.filefilters.FreeEMSFileFilter;
import org.diyefi.openlogviewer.filefilters.MSTypeFileFilter;

/**
 *
 * @author Bryan
 */




public class Main {
    /**
	 * The entry point of OLV!
	 *
	 * @param args the command line arguments
	 */
    	private static final String OS_NAME = System.getProperty(Keys.OS_NAME);
	private static final boolean IS_MAC_OS_X = OS_NAME.contains("OS X"); // TN2110
	private static final boolean IS_WINDOWS = OS_NAME.contains("Windows");
	private static final boolean IS_LINUX = OS_NAME.contains("Linux");
    
        private static ResourceBundle labels;
    
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				final Locale currentLocale = Locale.getDefault();

				labels = ResourceBundle.getBundle(OpenLogViewer.class.getPackage().getName() + ".Labels", currentLocale);

				final String lookAndFeel;
				final String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
				if (IS_MAC_OS_X) {
					System.setProperty(Keys.APPLE_LAF_USE_SCREEN_MENU_BAR, Boolean.TRUE.toString());
				}
				lookAndFeel = systemLookAndFeel;

				try {
					UIManager.setLookAndFeel(lookAndFeel);
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
					System.out.println(labels.getString(Text.LOOK_AND_FEEL_EXCEPTION_MESSAGE_ONE));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println(labels.getString(Text.LOOK_AND_FEEL_EXCEPTION_MESSAGE_TWO));
				} catch (InstantiationException e) {
					e.printStackTrace();
					System.out.println(labels.getString(Text.LOOK_AND_FEEL_EXCEPTION_MESSAGE_THREE));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					System.out.println(labels.getString(Text.LOOK_AND_FEEL_EXCEPTION_MESSAGE_FOUR));
				}

				mainAppRef = new OpenLogViewer();

				if (args.length > 0) {
					final File toOpen = new File(args[0]).getAbsoluteFile();
					if (toOpen.exists() && toOpen.isFile()) {
						if (args.length > 1) {
							System.out.println(args.length + labels.getString(Text.TOO_MANY_ARGUMENTS) + args[0]);
						} else {
							System.out.println(labels.getString(Text.ATTEMPTING_TO_OPEN_FILE) + args[0]);
						}
						final FileFilter ms = new MSTypeFileFilter(labels);
						final FileFilter fe = new FreeEMSFileFilter(labels);
						if (fe.accept(toOpen) || ms.accept(toOpen)) {
							mainAppRef.openFile(toOpen, mainAppRef.generateChooser());
						} else {
							System.out.println(labels.getString(Text.FILE_TYPE_NOT_SUPPORTED) + args[0]);
							mainAppRef.quit();
						}
					} else {
						System.out.println(labels.getString(Text.FILE_ARGUMENT_NOT_GOOD) + args[0]);
						mainAppRef.quit();
					}
				}
			}
		});
	}
}
